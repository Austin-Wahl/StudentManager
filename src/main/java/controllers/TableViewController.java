package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Database;
import models.Student;
import models.StudentTableRecord;

public class TableViewController implements Initializable {
    /**
     * Students table
     */
    @FXML
    private TableView<StudentTableRecord> leftHandSideTable;
    @FXML
    private TableColumn<StudentTableRecord, String> lhs_uuid;
    @FXML
    private TableColumn<StudentTableRecord, String> lhs_name;
    @FXML
    private TableColumn<StudentTableRecord, String> lhs_email;
    @FXML
    private TableColumn<StudentTableRecord, Double> lhs_gpa;
    
    /**
     * GPA table
     */
    @FXML
    private TableView<StudentTableRecord> rightHandSideTable;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_uuid;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_name;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_email;
    @FXML
    private TableColumn<StudentTableRecord, Double> rhs_gpa;

    /**
     * Misc components
     */
    @FXML
    private Button aboutButton;
    @FXML
    private Button addStudentButton;
    @FXML
    private TextField searchBar;
    @FXML
    private TabPane tabs;
    private Tab activeTab;

    /**
     * Lists of data pulled in from the database
     */
    private ObservableList<StudentTableRecord> students = FXCollections.observableList(Database.getInstance().getStudentsAsList());
    private ObservableList<StudentTableRecord> gpaSortedStudends = FXCollections.observableList(Database.getInstance().getStudentsSortedByGpaAsList());

    /**
     * Search string set by user as they type
     */
    private String searchQuery = null;

    /**
     * Searching thread
     */
    private Thread searchDataThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup tables with initial values
        LoadInitialStudentDataset();
        LoadInitialGPADataset();

        // Setup row factories for context menu stuff
        LHSTableRowFactory();
        RHSTableRowFactory();
        
        // EH for buttons
        aboutButton.setOnAction(event -> RenderAboutWindow(event));
        addStudentButton.setOnAction(event -> RenderAddStudentWindow(event));

        // Get the active tab. This is so we know what table to search on
        activeTab = tabs.getSelectionModel().getSelectedItem();
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            activeTab = nv;
            if(searchDataThread != null) {  
                searchDataThread.interrupt();
            }

            // Start a new thread for search
            searchDataThread = SearchDataset(searchQuery);
        });

        // Set up the on change listner for searching the dataset loaded in the table
        searchBar.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // Cancel current search task
            if(searchDataThread != null) searchDataThread.interrupt();

            // dont search if its just empty 
            if(newValue.trim().length() > 0) {
                // take the search value and auto search the dataset by the values
                searchQuery = newValue;
                searchDataThread = SearchDataset(searchQuery);
            } else {
                // Otherwise just reset the table
                LoadInitialStudentDataset();
                LoadInitialGPADataset();
                searchQuery = null;
            }
        });
    }

    /**
     * Helper that just maps student table object to column for Students table
     */
    private void setLHSTableData() {
        lhs_uuid.setCellValueFactory(data -> data.getValue().uuidProperty());
        lhs_name.setCellValueFactory(data -> data.getValue().nameProperty());
        lhs_email.setCellValueFactory(data -> data.getValue().emailProperty());
        lhs_gpa.setCellValueFactory(data -> data.getValue().gpaProperty().asObject());
    }

    /**
     * Helper that just maps student table object to column for GPA table
     */
    private void setRHSTableData() {
        rhs_uuid.setCellValueFactory(data -> data.getValue().uuidProperty());
        rhs_name.setCellValueFactory(data -> data.getValue().nameProperty());
        rhs_email.setCellValueFactory(data -> data.getValue().emailProperty());
        rhs_gpa.setCellValueFactory(data -> data.getValue().gpaProperty().asObject());
    }

    /**
     * Renders the about project window
     */
    private void RenderAboutWindow(ActionEvent event) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/Views/About.fxml")
            );
            Scene scene = new Scene(root);

            stage.setTitle("About this project");
            stage.setScene(scene);
            stage.initOwner(aboutButton.getScene().getWindow());
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.show();
        } catch (IOException ex) {
            System.out.println("Failed to load About window");
        }
    }

    /**
     * Renders the add student window and sets up on save callback
     */
    private void RenderAddStudentWindow(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AddStudent.fxml"));
            Parent root = loader.load();

            ((AddStudentViewController)loader.getController()).setTableViewController(this);

            ((AddStudentViewController)loader.getController()).SetOnSaveCallback((Student student) -> {
                StudentTableRecord temp = new StudentTableRecord(student);

                Database.getInstance().createStudent(temp);
            
                students.add(temp);
                gpaSortedStudends.add(temp);
            });

            Scene scene = new Scene(root);
            

            stage.setTitle("Add Student");
            stage.setScene(scene);
            stage.initOwner(aboutButton.getScene().getWindow());
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.show();
        } catch (IOException ex) {
            System.out.println("Failed to load Add Student window");
        }
    }

    /**
     * Renders the edit student window (same fxml as add student) and sets the save callback
     */
    private void RenderEditStudentView(StudentTableRecord studentTableRecord) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AddStudent.fxml"));
            Parent root = loader.load();

            ((AddStudentViewController)loader.getController()).setTableViewController(this);
            ((AddStudentViewController)loader.getController()).setDefaultValues(studentTableRecord);
            ((AddStudentViewController)loader.getController()).SetOnSaveCallback((Student student) -> {
                UpdateStudent(studentTableRecord, student);
            });

            Scene scene = new Scene(root);

            stage.setTitle("Edit Student - " + studentTableRecord.getName());
            stage.setScene(scene);
            stage.initOwner(aboutButton.getScene().getWindow());
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.show();
        } catch (IOException ex) {
            System.out.println("Failed to load Add Student window");
        }
    }

    /**
     * Creates a new thread that then searches the active tabs dataset for the value inside the search bar.
     * A new thread is created to prevent the main UI from freezing
     */
    private Thread SearchDataset(String valueToSearch) {
        Task<ObservableList<StudentTableRecord>> task = new Task<ObservableList<StudentTableRecord>>() {
            @Override protected ObservableList<StudentTableRecord> call() throws Exception {
                // Check if thread is canceled
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("Canceling Thread");
                    if(activeTab.getText().equals("Students")) {
                        LoadInitialStudentDataset();
                    } else {
                        LoadInitialGPADataset();
                    }
                }

                if(activeTab.getText().equals("Students")) {
                    SearchStudentsTable(valueToSearch);
                } else {
                    SearchGPATable(valueToSearch);
                }

                // Get the current data in the table
                ObservableList<StudentTableRecord> data =  FXCollections.observableArrayList();
                return data;
            }
        };
        
        Thread thread = new Thread(task);
        thread.start();

        return thread;
    }

    /**
     * Helper that specifically searches the data loaded into the students table and then updates the FX table with data matching the keyword search
     */
    private void SearchStudentsTable(String value) {
        ObservableList<StudentTableRecord> data = FXCollections.observableArrayList();
        for(StudentTableRecord student : leftHandSideTable.getItems()) {
            if(student.getName().contains(value)) data.add(student);
            else if(student.getEmailProp().contains(value)) data.add(student);
            else if(student.getUUID().toString().contains(value)) data.add(student);
            else if(Double.toString(student.getGPA()).contains(value)) data.add(student);
        }
        leftHandSideTable.setItems(data);
    }

    /**
     * Helper that specifically searches the data loaded into the GPA table and then updates the FX table with data matching the keyword search
     */
    private void SearchGPATable(String value) {
        ObservableList<StudentTableRecord> data = FXCollections.observableArrayList();
        for(StudentTableRecord student : rightHandSideTable.getItems()) {
            if(student.getName().contains(value)) data.add(student);
            else if(student.getEmailProp().contains(value)) data.add(student);
            else if(student.getUUID().toString().contains(value)) data.add(student);
            else if(Double.toString(student.getGPA()).contains(value)) data.add(student);
        }
        rightHandSideTable.setItems(data);   
    }

    /**
     * Helper that loads the student dataset into the students table
     */
    private void LoadInitialStudentDataset() {
        // ObservableList<StudentTableRecord> students = FXCollections.observableList(Database.getInstance().getStudentsAsList());
        leftHandSideTable.setItems(students);
        setLHSTableData();
    }

     /**
     * Helper that loads the gpa dataset into the gpa table
     */
    private void LoadInitialGPADataset() {
        rightHandSideTable.setItems(gpaSortedStudends);
        setRHSTableData();
    }

    /**
     * Helper to add a new student into the database
     */
    public void AddNewStudent(Student student) {
        students.add(new StudentTableRecord(student));
        gpaSortedStudends.add(new StudentTableRecord(student));
    }

    /**
     * Helper to update a student in the databse
     */
    public void UpdateStudent(StudentTableRecord studentTableRecord, Student newStudent) {
        // Generate a new temp record
        StudentTableRecord temp = new StudentTableRecord(newStudent);

        // Update said record in the db
        Database.getInstance().UpdateStudent(temp);
        
        // Remove the original record from the temp
        students.remove(studentTableRecord);
        gpaSortedStudends.remove(studentTableRecord);

        rightHandSideTable.getItems().remove(studentTableRecord);
        leftHandSideTable.getItems().remove(studentTableRecord);

        students.add(temp);
        gpaSortedStudends.add(temp);
        
        // rightHandSideTable.getItems().add(temp);
        // leftHandSideTable.getItems().add(temp);
    }

    /**
     * Handles the context menu and the respective on click events for the Students table
     */
    public void LHSTableRowFactory() {
        leftHandSideTable.setRowFactory(new Callback<TableView<StudentTableRecord>, TableRow<StudentTableRecord>>() {
            @Override
            public TableRow<StudentTableRecord> call(TableView<StudentTableRecord> tableView) {
                final TableRow<StudentTableRecord> row = new TableRow<>();
                final ContextMenu rowMenu = new ContextMenu();

                MenuItem editItem = new MenuItem("Edit");
                MenuItem removeItem = new MenuItem("Delete");
                
                removeItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        HandleRemoveStudent(row);
                    }
                });

                editItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        RenderEditStudentView(row.getItem());
                    }
                });

                rowMenu.getItems().addAll(editItem, removeItem);

                row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu)null));
                    return row;
                }
        });
    }

    /**
     * Handles the context menu and the respective on click events for the GPA table
     */
    public void RHSTableRowFactory() {
        rightHandSideTable.setRowFactory(new Callback<TableView<StudentTableRecord>, TableRow<StudentTableRecord>>() {
            @Override
            public TableRow<StudentTableRecord> call(TableView<StudentTableRecord> tableView) {
                final TableRow<StudentTableRecord> row = new TableRow<>();
                final ContextMenu rowMenu = new ContextMenu();

                MenuItem editItem = new MenuItem("Edit");
                MenuItem removeItem = new MenuItem("Delete");
                
                removeItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        HandleRemoveStudent(row);
                    }
                });

                editItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        RenderEditStudentView(row.getItem());
                    }
                });

                rowMenu.getItems().addAll(editItem, removeItem);

                row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                    .then(rowMenu)
                    .otherwise((ContextMenu)null));
                    return row;
                }
        });
    }

    /**
     * Helper to remove a student from the database
     */
    private void HandleRemoveStudent(TableRow<StudentTableRecord> row) {
        students.remove(row.getItem());
        gpaSortedStudends.remove(row.getItem());

        rightHandSideTable.getItems().remove(row.getItem());
        leftHandSideTable.getItems().remove(row.getItem());

        Database.getInstance().DeleteStudent(row.getItem());
    }
}
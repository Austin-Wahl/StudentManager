package controllers;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Student;
import models.StudentTableRecord;

public class AddStudentViewController implements Initializable {
    /**
     * Originally, this class was designed fro just adding a student but it makes more sense to make it handle edits too since its just the same code
     * except for what the save button does. This interface is a callback that takes a student. The execute() method runs when the save button is clicked
     */
    public interface CB {
        void execute(Student tableRecord);
    }

    /**
     * Input fields
     */
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField gpa;
    @FXML
    private TextField id;
    @FXML
    private Button cancel;
    @FXML
    private Button save;

    private final UUID uuid = UUID.randomUUID();

    private CB onSaveClick = null;

    private TableViewController tvc = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       ConfigureButtonCallbacks();
       ValidateUserInput();
       
       id.textProperty().set(uuid.toString());
       save.setDisable(true);
    }

    public void setTableViewController(TableViewController tvc) {
        this.tvc = tvc;
    }

    /**
     * Prepopulates the input fields with external data
     */
    public void setDefaultValues(StudentTableRecord studentTableRecord) {
        this.email.textProperty().setValue(studentTableRecord.getEmail());
        this.id.textProperty().setValue(studentTableRecord.getUUID().toString());
        this.name.textProperty().setValue(studentTableRecord.getName());
        this.gpa.textProperty().setValue(Double.toString(studentTableRecord.getGPA()));
    }

    /**
     * Sets the functions to run when the respective Cancel and Save buttons are clicked
     */
    private void ConfigureButtonCallbacks() {
        cancel.setOnAction(event -> {
            ResetFields();
        });
        save.setOnAction(event -> {
            Save();
        });
    }

    /**
     * Sets listners to check on every input that the fields are valid (i.e., not empty)
     */
    private void ValidateUserInput() {
        name.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            save.setDisable(!AllInputsValid());
        });
        email.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            save.setDisable(!AllInputsValid());
        });
        gpa.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            save.setDisable(!AllInputsValid());
        });
    }

    /**
     * Valideates each input
     */
    private boolean AllInputsValid() {
        boolean okName = name.textProperty().length().get() > 0;
        boolean okEmail = email.textProperty().length().get() > 0;
        boolean okGpa = false;
        try {
            double gpaValue = Double.parseDouble(gpa.textProperty().getValue());
            okGpa = gpa.textProperty().length().get() > 0 && !Double.isNaN(gpaValue);
        } catch (NumberFormatException e) {
            okGpa = false;
        }
        System.out.println(okName && okEmail && okGpa);
        return okName && okEmail && okGpa;
    }

    /**
     * Sets are fields to empty except UUID
     */
    private void ResetFields() {
        name.textProperty().set("");
        email.textProperty().set("");
        gpa.textProperty().set("");

        CloseWindow();
    }

    /**
     * Runs when the save button is clicked
     */
    private void Save() {
        Student student = new Student(
            id.textProperty().getValue().isEmpty() ? uuid : UUID.fromString(id.textProperty().getValue()), 
            name.textProperty().getValue(), 
            email.textProperty().getValue(), 
            Double.parseDouble(gpa.textProperty().getValue())
        );

        if(onSaveClick != null) {
            // This is the callback that runs and is set outside this view controller
            onSaveClick.execute(student);
        }
        
        CloseWindow();
    }

    /**
     * Helper to close the window
     */
    private void CloseWindow() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }   

    /**
     * Sets the value of the save button callbak
     */
    public void SetOnSaveCallback(CB callback) {
        this.onSaveClick = callback;
    }
}
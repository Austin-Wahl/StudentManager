package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Database;
import models.StudentTableRecord;

public class TableViewController implements Initializable {

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
    
    @FXML
    private TableView<StudentTableRecord> rightHandSideTable;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_uuid;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_name;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_email;
    @FXML
    private TableColumn<StudentTableRecord, String> rhs_gpa;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<StudentTableRecord> students = FXCollections.observableList(Database.getInstance().getStudentsAsList());
        leftHandSideTable.setItems(students);
        setLHSTableData();
    }

    private void setLHSTableData() {
        // nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        lhs_uuid.setCellValueFactory(data -> data.getValue().uuidProperty());
        lhs_name.setCellValueFactory(data -> data.getValue().nameProperty());
        lhs_email.setCellValueFactory(data -> data.getValue().emailProperty());
        lhs_gpa.setCellValueFactory(data -> data.getValue().gpaProperty().asObject());
    }
 
}
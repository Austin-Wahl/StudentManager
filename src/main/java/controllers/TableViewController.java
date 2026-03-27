package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private TableColumn<StudentTableRecord, Double> rhs_gpa;

    @FXML
    private Button aboutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<StudentTableRecord> students = FXCollections.observableList(Database.getInstance().getStudentsAsList());
        leftHandSideTable.setItems(students);
        setLHSTableData();

        ObservableList<StudentTableRecord> gpaSortedStudends = FXCollections.observableList(Database.getInstance().getStudentsSortedByGpaAsList());
        rightHandSideTable.setItems(gpaSortedStudends);
        setRHSTableData();

        aboutButton.setOnAction(event -> RenderAboutWindow(event));
    }

    private void setLHSTableData() {
        lhs_uuid.setCellValueFactory(data -> data.getValue().uuidProperty());
        lhs_name.setCellValueFactory(data -> data.getValue().nameProperty());
        lhs_email.setCellValueFactory(data -> data.getValue().emailProperty());
        lhs_gpa.setCellValueFactory(data -> data.getValue().gpaProperty().asObject());
    }

     private void setRHSTableData() {
        rhs_uuid.setCellValueFactory(data -> data.getValue().uuidProperty());
        rhs_name.setCellValueFactory(data -> data.getValue().nameProperty());
        rhs_email.setCellValueFactory(data -> data.getValue().emailProperty());
        rhs_gpa.setCellValueFactory(data -> data.getValue().gpaProperty().asObject());
    }

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
            stage.initModality(Modality.WINDOW_MODAL);

            stage.show();
        } catch (IOException ex) {
            System.out.println("Failed to load About window");
        }
    }

 
}
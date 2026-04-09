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

public class AddStudentViewController implements Initializable {
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

    private void ConfigureButtonCallbacks() {
        cancel.setOnAction(event -> {
            ResetFields();
        });
        save.setOnAction(event -> {
            Save();
        });
    }

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

    private void ResetFields() {
        name.textProperty().set("");
        email.textProperty().set("");
        gpa.textProperty().set("");

        CloseWindow();
    }

    private void Save() {
        Student student = new Student(uuid, name.textProperty().getValue(), email.textProperty().getValue(), Double.parseDouble(gpa.textProperty().getValue()));

        tvc.AddNewStudent(student);
        
        CloseWindow();
    }

    private void CloseWindow() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}
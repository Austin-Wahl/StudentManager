package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;


public class ErrorViewController implements Initializable {
    @FXML
    private Text error;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setErrorMessage(String errorMsg) {
        error.setText(errorMsg);
    }
    
}
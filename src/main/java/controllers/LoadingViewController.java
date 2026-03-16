package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class LoadingViewController implements Initializable {
    
    @FXML
    private ImageView loadingSpinner;
    private double rotation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Rotating");
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), loadingSpinner);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotate.play();
    }

    
}
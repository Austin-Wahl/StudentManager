import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import atlantafx.base.theme.PrimerDark;
import controllers.ErrorViewController;
import controllers.TableViewController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Database;
import utils.DataParser;

// Main entry point of the application
public class StudentManager extends Application {

    static final String APPLICATION_TITLE = "Student Manager";
    InputStream fileStream;
    private static final Path DATA_PATH = Paths.get(
        System.getProperty("user.home"), "StudentManager", "data.txt"
    );

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // Load parent FXML resource
        Parent root = FXMLLoader.load(
            getClass().getResource("Views/Loading.fxml")
        );

        // Views for program startup
        FXMLLoader errorLoader = new FXMLLoader(
            getClass().getResource("Views/ErrorScreen.fxml")
        );
        Parent errorParent = errorLoader.load();

        // Group group = new Group();
        Scene scene = new Scene(root);
        scene
            .getStylesheets()
            .add(getClass().getResource("styles/main.css").toExternalForm());

        // Set Stage Title
        stage.setTitle(APPLICATION_TITLE);

        // Create little program icon
        Image icon = new Image("/assets/images/student-svgrepo-com.png");
        stage.getIcons().add(icon);

        // Set scene
        stage.setScene(scene);

        // Set default size
        stage.setMinWidth(700);
        stage.setWidth(1200);
        stage.setMinHeight(500);
        stage.setHeight(900);
        stage.show();

        // Use a background thread to read in the data from le test file.
        // According to JavaFX docs, running processes in the main UI thread causes it to freeze so this fixes it.
        Task<Void> readTask = new Task<Void>() {
            @Override
            protected Void call() throws IOException {
                initializeDataFromFile();
                return null;
            }
        };

        // If the data loading process fails, just show error screen
        readTask.setOnFailed(event -> {
            ErrorViewController evc = errorLoader.getController();
            evc.setErrorMessage(readTask.getException().toString());
            stage.setScene(new Scene(errorParent));
        });

        // Otherwise, show the main UI
        readTask.setOnSucceeded(event -> {
            try {
                FXMLLoader tableLoader = new FXMLLoader(
                    getClass().getResource("Views/StudentManager.fxml")
                );
                Parent tableParent = tableLoader.load();
                tableParent
                .getStylesheets()
                .add(getClass().getResource("styles/main.css").toExternalForm());

                TableViewController tvc = tableLoader.getController();
                // We dont need to pass any data directly to the VC cuz the Database class uses the Singleton design pattern.
                stage.setScene(new Scene(tableParent));
            } catch (Exception e) {
                ErrorViewController evc = errorLoader.getController();
                evc.setErrorMessage(e.getMessage());
                stage.setScene(new Scene(errorParent));
            }
        });

        // Run the thread task
        new Thread(readTask).start();
    }

    /**
     * Reads in the test data from data.txt and passes it to data structures. Additionally,
     * this copies the content to a new file because i guess u cant write to bundled resources
     */
    private void initializeDataFromFile() throws IOException {
        // If the file doesnt exist, just copy the resource to the new location
        if (!Files.exists(DATA_PATH)) {
            Files.createDirectories(DATA_PATH.getParent());

            try (InputStream in = getClass().getResourceAsStream("data.txt")) {
                Files.copy(in, DATA_PATH);
            }
        }
        BufferedReader bufferedDataFileReader;
        bufferedDataFileReader = new BufferedReader(
                new FileReader(new File(DATA_PATH.toAbsolutePath().toString()))
        );
        // DATA_PATH.
        DataParser.createDatabaseInstanceAndWriteData(bufferedDataFileReader);
    }

    public static void main(String[] args) {
        // Run via CMD with 'mvn clean javafx:run'
        launch();
    }

    @Override
    public void stop() {
        try {
            Database.getInstance().save(DATA_PATH.toAbsolutePath().toString());
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}

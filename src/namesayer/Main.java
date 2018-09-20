package namesayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        initialise();
        launch(args);

    }

    public static void initialise() {
        File archiveFolder = new File("user.dir" + "/Creations");
        if (!archiveFolder.exists()) {
            ProcessBuilder createNameDir = new ProcessBuilder("/bin/bash", "-c", "mkdir ./Creations");
            try {
                createNameDir.start();
            } catch (IOException e) {
            }
        }
    }
}

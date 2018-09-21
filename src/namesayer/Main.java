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
		File archiveFolder = new File("Creations");
		if (!archiveFolder.exists()) {            
			try {
				archiveFolder.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		File ratingFile = new File("Bad_Ratings.txt");
		if(!ratingFile.exists()) {
			try {
				ratingFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

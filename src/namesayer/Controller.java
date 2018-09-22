package namesayer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

	private List<String> listOfNamesNotSelected;
	private static List<String> listOfNamesSelected;
	private static List<NameFile> namesListArray = new ArrayList<NameFile>();
	
	private String fileSelectedFromDatabase;
	private String fileSelectedFromSelected;

	Stage practiceStage = new Stage();

	@FXML
	private ListView<String> namesListView;
	@FXML
	private ListView<String> selectedListView;

	@FXML
	private Button addButton;
	@FXML
	private Button addAllButton;
	
	@FXML
	private Button removeButton;
	@FXML
	private Button removeAllButton;

	@FXML
	private Button practiceButton;

	@FXML
	private CheckBox randomBox = new CheckBox();
	private boolean isRandom = randomBox.isSelected();

	private final Tooltip namesListTooltip = new Tooltip("Double-click to add to chosen names");
	private final Tooltip selectedListTooltip = new Tooltip("Double-click to remove from chosen names");


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listOfNamesSelected = new ArrayList<>();
		initialiseListNotSelected();
		updateListNotSelected();
		hackTooltipStartTiming(namesListTooltip);
		namesListView.setTooltip(namesListTooltip);
		selectedListView.setTooltip(selectedListTooltip);
		practiceStage.initModality(Modality.APPLICATION_MODAL);
	}


	// Taken from https://stackoverflow.com/questions/26854301/how-to-control-the-javafx-tooltips-delay
	// Makes tooltips appear/disappear faster
	public static void hackTooltipStartTiming(Tooltip tooltip) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);

			Field actTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			actTimer.setAccessible(true);
			Timeline objActTimer = (Timeline) actTimer.get(objBehavior);

			objActTimer.getKeyFrames().clear();
			objActTimer.getKeyFrames().add(new KeyFrame(new Duration(200)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//Initialises the database list
	public void initialiseListNotSelected() {
		listOfNamesNotSelected = new ArrayList<>();
		
		File nameFolder = new File("names");
		List<String> listOfNamesInDatabase = new ArrayList<String>(Arrays.asList(nameFolder.list()));

		for (String currentFile : listOfNamesInDatabase) {
			System.out.println(currentFile);
			String justName = currentFile.substring((currentFile.lastIndexOf("_")+1), currentFile.lastIndexOf("."));
			String listName = justName;
			int attempt = 0;
			
			// Handle duplicate names by numbering them
			while (listOfNamesNotSelected.contains(listName)) {
				attempt++;
				listName = justName + "_" + attempt;
			}
			listOfNamesNotSelected.add(listName);
			
			NameFile name = new NameFile(currentFile, listName);
			if(name.checkIfBadRating()) {
				name.setBadRatingField(true);
			}
			namesListArray.add(name);
		}
		
		System.out.println(listOfNamesNotSelected);
		Collections.sort(listOfNamesNotSelected);
	}


	//Opens the practice window with the names selected
	public void onPracticeAction(ActionEvent actionEvent) {

		if (listOfNamesSelected.isEmpty()) {
			Alert nonSelectedAlert = new Alert(Alert.AlertType.INFORMATION);
			nonSelectedAlert.setTitle("ERROR");
			nonSelectedAlert.setHeaderText(null);
			nonSelectedAlert.setContentText("No name(s) have been selected. Please select at least one name to practice");
			nonSelectedAlert.showAndWait();
		} else {
			if (isRandom) {
				Collections.shuffle(listOfNamesSelected);
			}

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/namesayer/practiceMenu.fxml"));
				Parent root = fxmlLoader.load();
				//Stage stage = new Stage();
				practiceStage.setTitle("Practice selected names");
				practiceStage.setScene(new Scene(root, 600, 400));
				practiceStage.show();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed to open practice menu");
			}

		}
	}


	// Handles what to do when a name from the database is clicked
	public void handleUnselectedListClicked(MouseEvent mouseEvent) {
		fileSelectedFromDatabase = namesListView.getSelectionModel().getSelectedItem();
		System.out.println("LEFT LIST: " + fileSelectedFromDatabase);
		selectedListView.getSelectionModel().clearSelection();

		if (mouseEvent.getClickCount() == 2) {
			addToSelected();
		}
	}

	
	// Handles what to do when a name from the selected list is clicked
	public void handleSelectedListClicked(MouseEvent mouseEvent) {
		fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();
		System.out.println("RIGHT LIST: " + fileSelectedFromSelected);
		namesListView.getSelectionModel().clearSelection();

		if (mouseEvent.getClickCount() == 2) {
			removeFromSelected();
		}
	}

	
	// Closes the current window
	public static void closeCurrentStage(Button button) {
		Stage currentStage = (Stage) button.getScene().getWindow();
		currentStage.close();
	}

	
	// Getter method for the list of selected names
	public static List<String> getSelectedList() {
		System.out.println(listOfNamesSelected);
		return listOfNamesSelected;
	}


	// Handle when randomise toggle button is clicked
	public void toggleRandom() {
		isRandom = randomBox.isSelected();
	}

	
	// Adds a name from the database to the selected list
	public void addToSelected() {
		fileSelectedFromDatabase = namesListView.getSelectionModel().getSelectedItem();

		if (fileSelectedFromDatabase != null) {
			listOfNamesNotSelected.remove(fileSelectedFromDatabase);
			listOfNamesSelected.add(fileSelectedFromDatabase);
			Collections.sort(listOfNamesNotSelected);
			Collections.sort(listOfNamesSelected);
			updateListNotSelected();
			updateListSelected();
		}
	}
	
	
	// Adds all names from database to the selected list
	public void addAllToSelected() {
		listOfNamesSelected.addAll(listOfNamesNotSelected);
		listOfNamesNotSelected.removeAll(listOfNamesNotSelected);
		Collections.sort(listOfNamesSelected);
		updateListNotSelected();
		updateListSelected();
	}

	
	// Removes a name from the selected list
	public void removeFromSelected() {
		fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();

		if (fileSelectedFromSelected != null) {
			listOfNamesSelected.remove(fileSelectedFromSelected);
			listOfNamesNotSelected.add(fileSelectedFromSelected);
			Collections.sort(listOfNamesNotSelected);
			Collections.sort(listOfNamesSelected);
			updateBothLists();
		}
	}
	
	
	// Removes all names from the selected list
	public void removeAllFromSelected() {
		listOfNamesNotSelected.addAll(listOfNamesSelected);
		listOfNamesSelected.removeAll(listOfNamesSelected);
		Collections.sort(listOfNamesNotSelected);
		updateBothLists();
	}
	
	private void updateBothLists() {
		updateListNotSelected();
		updateListSelected();
	}

	
	// Updates the ListView with names in the database (that are not selected for practicing)
	private void updateListNotSelected() {
		ObservableList<String> listToView = FXCollections.observableArrayList(listOfNamesNotSelected);
		namesListView.setItems(listToView);
		namesListView.getSelectionModel().clearSelection();
	}
	
	
	// Updates the ListView with names selected for practicing
	private void updateListSelected() {
		ObservableList<String> listToView = FXCollections.observableArrayList(listOfNamesSelected);
		selectedListView.setItems(listToView);
		selectedListView.getSelectionModel().clearSelection();
	}


	public static List<NameFile> getAddedNames() {
		return namesListArray;
	}


}

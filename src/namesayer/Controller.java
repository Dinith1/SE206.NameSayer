package namesayer;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

	private List<String> listOfNamesNotSelected;
	private static List<String> listOfNamesSelected = new ArrayList<String>();
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

	private final ListViewHandler lvHandler = new ListViewHandler();
	private final TooltipHandler ttHandler = new TooltipHandler();

	private static Parent controllerRoot;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listOfNamesSelected = new ArrayList<>();
		initialiseListNotSelected();
		lvHandler.updateList(namesListView, listOfNamesNotSelected);
		ttHandler.hackTooltipStartTiming(namesListTooltip);
		namesListView.setTooltip(namesListTooltip);
		selectedListView.setTooltip(selectedListTooltip);
		practiceStage.initModality(Modality.APPLICATION_MODAL);
	}


	//Initialises the database list
	public void initialiseListNotSelected() {
		listOfNamesNotSelected = new ArrayList<>();

		File nameFolder = new File("names");
		List<String> listOfNamesInDatabase = new ArrayList<String>(Arrays.asList(nameFolder.list()));

		for (String currentFile : listOfNamesInDatabase) {
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
				namesListView.getScene().setRoot(root);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed to open practice menu");
			}

		}
	}


	// Handles what to do when a name from the database is clicked
	public void handleUnselectedListClicked(MouseEvent mouseEvent) {
		fileSelectedFromDatabase = namesListView.getSelectionModel().getSelectedItem();
		selectedListView.getSelectionModel().clearSelection();

		if (mouseEvent.getClickCount() >= 2) {
			addToSelected();
		}
	}


	// Handles what to do when a name from the selected list is clicked
	public void handleSelectedListClicked(MouseEvent mouseEvent) {
		fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();
		namesListView.getSelectionModel().clearSelection();

		if (mouseEvent.getClickCount() >= 2) {
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
		return listOfNamesSelected;
	}


	// Handle when randomise toggle button is clicked
	public void toggleRandom() {
		isRandom = randomBox.isSelected();
	}


	// Adds a name from the database to the selected list
	public void addToSelected() {
		if (fileSelectedFromDatabase != null) {
			if(!listOfNamesSelected.contains(fileSelectedFromDatabase)) {
				lvHandler.moveName(fileSelectedFromDatabase, listOfNamesNotSelected, listOfNamesSelected);
				lvHandler.updateBothLists(namesListView, listOfNamesNotSelected, selectedListView, listOfNamesSelected);
			}
		}

		// This is here to allow changing of scenes in the same window
		controllerRoot = addButton.getScene().getRoot();
	}


	// Adds all names from database to the selected list
	public void addAllToSelected() {
		lvHandler.moveWholeList(namesListView, listOfNamesNotSelected, selectedListView, listOfNamesSelected);
		lvHandler.updateBothLists(namesListView, listOfNamesNotSelected, selectedListView, listOfNamesSelected);

		// This is here to allow changing of scenes in the same window
		controllerRoot = addButton.getScene().getRoot();
	}


	// Removes a name from the selected list
	public void removeFromSelected() {
		if (fileSelectedFromSelected != null) {
			if(!listOfNamesNotSelected.contains(fileSelectedFromSelected)) {
				lvHandler.moveName(fileSelectedFromSelected, listOfNamesSelected, listOfNamesNotSelected);
				lvHandler.updateBothLists(namesListView, listOfNamesNotSelected, selectedListView, listOfNamesSelected);
			}
		}
	}


	public void removeAllFromSelected() {
		lvHandler.moveWholeList(selectedListView, listOfNamesSelected, namesListView, listOfNamesNotSelected);
		lvHandler.updateBothLists(namesListView, listOfNamesNotSelected, selectedListView, listOfNamesSelected);
	}


	public static List<NameFile> getAddedNames() {
		return namesListArray;
	}


	public Parent getControllerRoot() {
		return controllerRoot;
	}


}

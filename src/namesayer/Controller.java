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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
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
	private static List<String> listOfNamesSelected = new ArrayList<String>();

	private String fileSelected;
	private String fileSelectedFromSelected;

	@FXML
	private ListView<String> namesListView;

	@FXML
	private ListView<String> selectedListView;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	@FXML
	private Button practiceButton;

	@FXML
	private CheckBox randomBox = new CheckBox();
	private boolean isRandom = randomBox.isSelected();

	@FXML
	private ListView<String> archiveList;
	
	
	private final Tooltip namesListTooltip = new Tooltip("Double-click to add to chosen names");
	private final Tooltip selectedListTooltip = new Tooltip("Double-click to remove from chosen names");


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialiseListNotSelected();
		updateListNotSelected();
		hackTooltipStartTiming(namesListTooltip);
		namesListView.setTooltip(namesListTooltip);
		selectedListView.setTooltip(selectedListTooltip);
	}
	
	
	public static void hackTooltipStartTiming(Tooltip tooltip) {
	    try {
	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(tooltip);

	        Field actTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	        Field hideTimer = objBehavior.getClass().getDeclaredField("hideTimer");
	        actTimer.setAccessible(true);
	        hideTimer.setAccessible(true);
	        Timeline objActTimer = (Timeline) actTimer.get(objBehavior);
	        Timeline objHideTimer = (Timeline) actTimer.get(objBehavior);

	        objActTimer.getKeyFrames().clear();
	        objActTimer.getKeyFrames().add(new KeyFrame(new Duration(200)));
	        objHideTimer.getKeyFrames().clear();
	        objHideTimer.getKeyFrames().add(new KeyFrame(new Duration(200)));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	// ********* WHAT HAPPENS WHEN YOU CLOSE THE PRACTICE MENU - SHOULD SELECTED LIST BE EMPTY - OR SHOULD IT STAY FILLED
	// WITH WHAT WAS PREVIOUSLY SELECTED, AND THE UNSELECTED LIST UPDATED WITH THE EXTRA CREATED FILES??????????
	public void initialiseListNotSelected() {
		File nameFolder = new File(System.getProperty("user.dir"));
		listOfNamesNotSelected = new ArrayList<String>(Arrays.asList(nameFolder.list()));
		//ArrayList<File> files = new ArrayList<File>(Arrays.asList(nameFolder.listFiles()));
	}


	// Called when program is launched to fill the ListView with files
	public void updateListNotSelected() {
		ObservableList<String> listToView = FXCollections.observableArrayList(listOfNamesNotSelected);
		namesListView.setItems(listToView);
		namesListView.getSelectionModel().clearSelection();
	}


	public void onPracticeAction(ActionEvent actionEvent) {
		if (isRandom) {
			Collections.shuffle(listOfNamesSelected);
		}

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/namesayer/practiceMenu.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = new Stage();
			stage.setTitle("Practice selected names");
			stage.setScene(new Scene(root, 600, 400));
			stage.show();
		} catch (IOException e){
			// DO SOMETHING?????
		}

		closeCurrentStage(practiceButton);
	}


	public void handleUnselectedListClicked(MouseEvent mouseEvent) {
		fileSelected = namesListView.getSelectionModel().getSelectedItem();
		System.out.println("LEFT LIST: " + fileSelected);
		selectedListView.getSelectionModel().clearSelection();
		
		if (mouseEvent.getClickCount() == 2) {
			addToSelected();
		}
	}
	
	
	public void handleSelectedListClicked(MouseEvent mouseEvent) {
		fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();
		System.out.println("RIGHT LIST: " + fileSelectedFromSelected);
		namesListView.getSelectionModel().clearSelection();
		
		if (mouseEvent.getClickCount() == 2) {
			removeFromSelected();
		}
	}


	public static void closeCurrentStage(Button button) {
		Stage currentStage = (Stage)button.getScene().getWindow();
		currentStage.close();
	}


	public static List<String> getSelectedList(){
		return listOfNamesSelected;
	}


	// Handle when randomise toggle button is clicked
	public void toggleRandom() {
		isRandom = randomBox.isSelected();
	}


	public void addToSelected() {
		fileSelected = namesListView.getSelectionModel().getSelectedItem();

		if (fileSelected != null) {
			listOfNamesNotSelected.remove(fileSelected);
			listOfNamesSelected.add(fileSelected);
			updateListNotSelected();
			updateListSelected();
		}
	}


	public void removeFromSelected() {
		fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();

		if (fileSelectedFromSelected != null) {
			listOfNamesSelected.remove(fileSelectedFromSelected);
			listOfNamesNotSelected.add(fileSelectedFromSelected);
			updateListNotSelected();
			updateListSelected();
		}
	}


	public void updateListSelected() {
		ObservableList<String> listToView = FXCollections.observableArrayList(listOfNamesSelected);
		selectedListView.setItems(listToView);
		selectedListView.getSelectionModel().clearSelection();
	}


}

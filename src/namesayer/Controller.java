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
import javafx.scene.control.*;
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
    private static List<String> listOfNamesSelected;
    private List<String> listOfArchive;

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
    private ListView<String> archiveListView;


    private final Tooltip namesListTooltip = new Tooltip("Double-click to add to chosen names");
    private final Tooltip selectedListTooltip = new Tooltip("Double-click to remove from chosen names");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseListNotSelected();
        updateListNotSelected();
        initialiseArchive();
        hackTooltipStartTiming(namesListTooltip);
        namesListView.setTooltip(namesListTooltip);
        selectedListView.setTooltip(selectedListTooltip);
        listOfNamesSelected = new ArrayList<>();
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


    //Initialises the database list
    public void initialiseListNotSelected() {
        File nameFolder = new File(System.getProperty("user.dir"));
        listOfNamesNotSelected = new ArrayList<String>(Arrays.asList(nameFolder.list()));
        //ArrayList<File> files = new ArrayList<File>(Arrays.asList(nameFolder.listFiles()));
    }

    //Initialises the archive list and adds to listview
    public void initialiseArchive() {
        File archiveFolder = new File(System.getProperty("user.dir"));
        listOfArchive = new ArrayList<String>(Arrays.asList(archiveFolder.list()));
        ObservableList<String> archiveToView = FXCollections.observableArrayList(listOfArchive);
        archiveListView.setItems(archiveToView);
        archiveListView.getSelectionModel().clearSelection();
    }


    // Called when program is launched to fill the ListView with files
    public void updateListNotSelected() {
        ObservableList<String> listToView = FXCollections.observableArrayList(listOfNamesNotSelected);
        namesListView.setItems(listToView);
        namesListView.getSelectionModel().clearSelection();
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
                Stage stage = new Stage();
                stage.setTitle("Practice selected names");
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
            } catch (IOException e) {
                // DO SOMETHING?????
            }

            closeCurrentStage(practiceButton);
        }
    }


    //Handles what to do when a name from the database is clicked
    public void handleUnselectedListClicked(MouseEvent mouseEvent) {
        fileSelected = namesListView.getSelectionModel().getSelectedItem();
        System.out.println("LEFT LIST: " + fileSelected);
        selectedListView.getSelectionModel().clearSelection();

        if (mouseEvent.getClickCount() == 2) {
            addToSelected();
        }
    }

    //Handles what to do when a name from the selected list is clicked
    public void handleSelectedListClicked(MouseEvent mouseEvent) {
        fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();
        System.out.println("RIGHT LIST: " + fileSelectedFromSelected);
        namesListView.getSelectionModel().clearSelection();

        if (mouseEvent.getClickCount() == 2) {
            removeFromSelected();
        }
    }

    //Closes the current window
    public static void closeCurrentStage(Button button) {
        Stage currentStage = (Stage) button.getScene().getWindow();
        currentStage.close();
    }

    //Getter method for the list of selected names
    public static List<String> getSelectedList() {
        return listOfNamesSelected;
    }


    // Handle when randomise toggle button is clicked
    public void toggleRandom() {
        isRandom = randomBox.isSelected();
    }

    //Adds a name from the database to the selected list
    public void addToSelected() {
        fileSelected = namesListView.getSelectionModel().getSelectedItem();

        if (fileSelected != null) {
            listOfNamesNotSelected.remove(fileSelected);
            listOfNamesSelected.add(fileSelected);
            updateListNotSelected();
            updateListSelected();
        }
    }

    //Removes a name from the selected list
    public void removeFromSelected() {
        fileSelectedFromSelected = selectedListView.getSelectionModel().getSelectedItem();

        if (fileSelectedFromSelected != null) {
            listOfNamesSelected.remove(fileSelectedFromSelected);
            listOfNamesNotSelected.add(fileSelectedFromSelected);
            updateListNotSelected();
            updateListSelected();
        }
    }

    //Updates the selected Listview
    public void updateListSelected() {
        ObservableList<String> listToView = FXCollections.observableArrayList(listOfNamesSelected);
        selectedListView.setItems(listToView);
        selectedListView.getSelectionModel().clearSelection();
    }


}

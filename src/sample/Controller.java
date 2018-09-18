package sample;

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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private List<String> listOfNames;

    private String fileSelected;

    @FXML
    private ListView<String> namesListView;

    @FXML
    private Button practiceButton;

    @FXML
    private CheckBox randomBox = new CheckBox();
    private boolean isRandom = randomBox.isSelected();

    @FXML
    private ListView<String> archiveList;
    
    
    public static List<String> selectedList = new ArrayList<>();

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        namesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        updateList();
    }
    
    
    // Called when program is launched to fill the ListView with files
    public void updateList() {
        ObservableList<String> listToView = FXCollections.observableArrayList(getListOfFiles());
        namesListView.setItems(listToView);
        namesListView.getSelectionModel().clearSelection();
    }
    
    
    public List<String> getListOfFiles() {
        File nameFolder = new File(System.getProperty("user.dir"));
        listOfNames = new ArrayList<String>(Arrays.asList(nameFolder.list()));
        //ArrayList<File> files = new ArrayList<File>(Arrays.asList(nameFolder.listFiles()));
        return listOfNames;
    }
    
    
    public void onPracticeAction(ActionEvent actionEvent) {
        if (isRandom) {
            Collections.shuffle(selectedList);
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample/practiceMenu.fxml"));
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


    public void handleListClicked(MouseEvent mouseEvent) {
        fileSelected = namesListView.getSelectionModel().getSelectedItem();

        if (fileSelected == null) {
        	// Do nothing
        } else {
            selectedList.add(fileSelected);
        }
    }

    
    public static void closeCurrentStage(Button button) {
        Stage currentStage = (Stage)button.getScene().getWindow();
        currentStage.close();
    }


    public static List<String> getSelectedList(){
        return selectedList;
    }
    
    // Handle when randomise toggle button is clicked
    public void toggleRandom() {
    	isRandom = randomBox.isSelected();
    }
    
    
}

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
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private List<String> listOfNames;

    private String _fileSelected;

    @FXML
    private ListView<String> namesList;

    @FXML
    private Button practiceButton;

    @FXML
    private CheckBox randomBox;

    public static List<String> selectedList = new ArrayList<>();

    private boolean isRandom = randomBox.isSelected();

    public void onPracticeAction(ActionEvent actionEvent) {

        if (isRandom) {
            Collections.shuffle(selectedList);
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample/practiceMenu.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Create New Name");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e){
        }

        closeCurrentStage(practiceButton);
    }


    public List<String> getListOfFiles() {
        listOfNames = new ArrayList<>();

        String checkNoFilesCommand = "find . -type f -name \"*.mp4\" | wc -l";
        ProcessBuilder checkNoFilesPB = new ProcessBuilder("/bin/bash", "-c", checkNoFilesCommand);


        try {
            Process checkNoFilesProcess = checkNoFilesPB.start();
            InputStream fileCountOut = checkNoFilesProcess.getInputStream();
            InputStreamReader fileCountReader = new InputStreamReader(fileCountOut);
            BufferedReader fileCountBuffered = new BufferedReader(fileCountReader);
            String line = fileCountBuffered.readLine();

            namesList.setMouseTransparent(false);
            namesList.setFocusTraversable(true);
            String command = "for f in *.mp4; do printf '%s\\n' \"${f%.mp4}\"; done;\n";
            ProcessBuilder listPB = new ProcessBuilder("/bin/bash", "-c", command);

            try {
                Process listProcess = listPB.start();

                InputStream stdout = listProcess.getInputStream();
                InputStreamReader inputReader = new InputStreamReader(stdout);
                BufferedReader stdoutBuffered = new BufferedReader(inputReader);

                String fileRead;

                while ((fileRead = stdoutBuffered.readLine()) != null) {
                    listOfNames.add(fileRead);

                }
            } catch (IOException e) {
            }


        } catch (IOException e) {
        }

        return listOfNames;
    }

    public void updateList() {
        ObservableList<String> listToView = FXCollections.observableArrayList(getListOfFiles());
        namesList.setItems(listToView);
        namesList.getSelectionModel().clearSelection();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        namesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void handleListClicked(MouseEvent mouseEvent) {

        _fileSelected = namesList.getSelectionModel().getSelectedItem();

        if (_fileSelected == null) {
        } else {
            selectedList.add(_fileSelected);
        }
    }

    public static void closeCurrentStage(Button button) {
        Stage currentStage = (Stage) button.getScene().getWindow();
        currentStage.close();
    }


    public static List<String> getSelectedList(){
        return selectedList;
    }
}

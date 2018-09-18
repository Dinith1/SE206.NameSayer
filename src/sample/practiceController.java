package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class practiceController implements Initializable {

    private String selectedName;

    private int selectedIndex = 0;

    @FXML
    private Button prevButton;

    @FXML
    private Button playButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button playRecButton;

    @FXML
    private Button recordButton;

    @FXML
    private Button saveRecButton;

    @FXML
    private Button micTestButton;

    @FXML
    private ListView<String> archiveList;

    @FXML
    private ListView<String> displayList;



    public void handlePrevButton(ActionEvent actionEvent) {

    }

    public void handlePlayButton(ActionEvent actionEvent) {
    }

    public void handleNextButton(ActionEvent actionEvent) {
    }

    public void handleRecordAction(ActionEvent actionEvent) {
        String recordCommand = "ffmpeg -f alsa -ac 1 -ar 44100 -i default -t 5 \"" + selectedName + "\".wav";
        ProcessBuilder recordAudio = new ProcessBuilder("/bin/bash", "-c", recordCommand);
//        recordAudio.directory(namesFile);

        try {
            recordAudio.start();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
        }
    }

    public void handlePlayRec(ActionEvent actionEvent) {
    }

    public void handleSaveRec(ActionEvent actionEvent) {
    }

    public void handleArcListClicked(MouseEvent mouseEvent) {
    }

    public void handlePlayArc(ActionEvent actionEvent) {
    }

    public void handleDeleteArc(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

            ObservableList<String> listToView = FXCollections.observableArrayList(Controller.getSelectedList());
            displayList.setItems(listToView);
            displayList.getSelectionModel().clearSelection();
            displayList.getSelectionModel().selectFirst();

            selectedName = displayList.getSelectionModel().getSelectedItem();

    }
}

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class practiceController implements Initializable {

    private String selectedName;

    private int selectedIndex = 0;

    private ObservableList<String> listToPlay;

    private ObservableList<String> recordedList;

    private String selectedArchive;

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
    private ListView<String> availableList;

    @FXML
    private ListView<String> displayList;

    @FXML
    private ProgressBar recordingIndicator;


    public void handlePrevButton(ActionEvent actionEvent) {
        if(selectedIndex == 0){
            displayList.scrollTo(selectedIndex);
            displayList.getSelectionModel().selectFirst();
        } else {
            selectedIndex--;
            displayList.scrollTo(selectedIndex);
            displayList.getSelectionModel().select(selectedIndex);
        }
        selectedName = displayList.getSelectionModel().getSelectedItem();

    }

    public void handlePlayButton(ActionEvent actionEvent) {
        Service<Void> background = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        File recording = new File("Names/" + selectedName + ".wav");
                        String path = recording.toString();
                        Media media = new Media(new File(path).toURI().toString());
                        MediaPlayer audioPlayer = new MediaPlayer(media);
                        audioPlayer.play();
                        return null;
                    }
                };
            }
        };

        background.start();
    }

    public void handleNextButton(ActionEvent actionEvent) {
        if(selectedIndex == listToPlay.size() -1){
            displayList.scrollTo(selectedIndex);
            displayList.getSelectionModel().selectLast();

        } else {
            selectedIndex++;
            displayList.scrollTo(selectedIndex);
            displayList.getSelectionModel().select(selectedIndex);
        }
        selectedName = displayList.getSelectionModel().getSelectedItem();

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

    public void handleDeleteRec(ActionEvent actionEvent) {
    }

    public void handleArcListClicked(MouseEvent mouseEvent) {
        selectedArchive = availableList.getSelectionModel().getSelectedItem();
    }

    public void handlePlayArc(ActionEvent actionEvent) {
        if (selectedArchive == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("No file selected");
            alert.showAndWait();
        } else {

        }
    }

    public void handleDeleteArc(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

            listToPlay = FXCollections.observableArrayList(Controller.getSelectedList());
            displayList.setItems(listToPlay);
            displayList.getSelectionModel().clearSelection();
            displayList.getSelectionModel().selectFirst();
            selectedIndex = 0;

            selectedName = displayList.getSelectionModel().getSelectedItem();

    }

    public void handleMicTest(ActionEvent actionEvent) {
    }
}

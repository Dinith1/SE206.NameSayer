package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;


public class practiceController implements Initializable {

    private String selectedName;

    private int selectedIndex = 0;

    private ObservableList<String> listToPlay;

    private ObservableList<String> recordedList;

    private String selectedArchive;

    private boolean contains;

    @FXML
    private Button prevButton;

    @FXML
    private Button playButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button playArcButton;

    @FXML
    private Button deleteArcButton;

    @FXML
    private Button recordButton;

    @FXML
    private Button micTestButton;

    @FXML
    private ListView<String> availableList;

    @FXML
    private ListView<String> displayList;

    @FXML
    private ProgressBar recordingIndicator;

    @FXML
    private Button exitMicButton;



    public void handlePrevButton(ActionEvent actionEvent) {
        if (selectedIndex == 0) {
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
        playButton.setDisable(true);
        prevButton.setDisable(true);
        nextButton.setDisable(true);

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
        background.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                playButton.setDisable(false);
                prevButton.setDisable(false);
                nextButton.setDisable(false);
            }
        });
    }

    public void handleNextButton(ActionEvent actionEvent) {
        if (selectedIndex == listToPlay.size() - 1) {
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
        int attempt = 1;

        File nameRecording = new File("/Attempts/" + selectedName + attempt);
        while (nameRecording.exists()) {
            attempt++;
            nameRecording = new File("/Attempts/" + selectedName + attempt);
        }


        String recordCommand = "ffmpeg -f alsa -ac 1 -ar 44100 -i default -t 5 \"" + selectedName + "\"attempt_" + attempt + ".wav";
        ProcessBuilder recordAudio = new ProcessBuilder("/bin/bash", "-c", recordCommand);

        try {
            recordAudio.start();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
        }
    }

    public void handlePlayArc(ActionEvent actionEvent) {
        if (selectedArchive == null) {
            noFileAlert();
        } else {
            playArcButton.setDisable(true);
            deleteArcButton.setDisable(true);
            Service<Void> background = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            File recording = new File("Names/" + selectedArchive + ".wav");
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
            background.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    playArcButton.setDisable(false);
                    deleteArcButton.setDisable(false);
                }
            });
        }
    }


    public void handleArcListClicked(MouseEvent mouseEvent) {

        selectedArchive = availableList.getSelectionModel().getSelectedItem();
    }


    public void handleDeleteArc(ActionEvent actionEvent) {

        if (selectedArchive == null) {
            noFileAlert();
        } else {

            File file = new File("Names/" + selectedArchive + ".mp4");
            if (file.exists()) {

                Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete:" + selectedArchive + "?", ButtonType.YES, ButtonType.NO);
                deleteConfirm.showAndWait();
                if (deleteConfirm.getResult() == ButtonType.YES) {

                    File toDelete = new File("Names/" + selectedArchive + ".mp4");
                    try {
                        Files.deleteIfExists(toDelete.toPath());
                    } catch (IOException e) {

                    }

                    updateArchive();
                    availableList.getSelectionModel().clearSelection();

                }
            } else {
                if (!contains) {
                    availableList.setMouseTransparent(true);
                    availableList.setFocusTraversable(false);
                }
            }
        }
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

    public void updateArchive() {

    }

    public void handleMicTest(ActionEvent actionEvent) {
    }

    public void noFileAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText("No file selected");
        alert.showAndWait();
    }

    public void handleExitMic(ActionEvent actionEvent) {
        Stage currentStage = (Stage) exitMicButton.getScene().getWindow();
        currentStage.close();
    }
}

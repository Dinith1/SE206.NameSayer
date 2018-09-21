package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class practiceController implements Initializable {

	private String selectedName;

	private int selectedIndex = 0;

	private ObservableList<String> listToPlay;

	private ObservableList<String> recordedList;

	private String selectedArchive;

	private boolean contains;

	private String toPlay;

	@FXML
	private Button returnButton;

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

	private Stage listeningStage;

	@FXML
	private Button micTestButton;

	@FXML
	private ListView<String> availableListView;

	@FXML
	private ListView<String> displayListView;

	@FXML
	private ProgressBar recordingIndicator;

	@FXML
	private Button exitMicButton;

	@FXML
	private ProgressBar micBar = new ProgressBar();

	@FXML
	private Label playingLabel;

	private Service<Void> bgThread;

	private List<NameFile> nameDatabase;

	private File creations = new File("./Creations");

	private NameFile currentName;




	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameDatabase = Controller.getAddedNames();
		listToPlay = FXCollections.observableArrayList(Controller.getSelectedList());
		displayListView.setItems(listToPlay);
		displayListView.getSelectionModel().clearSelection();
		displayListView.getSelectionModel().selectFirst();
		selectedIndex = 0;

		selectedName = displayListView.getSelectionModel().getSelectedItem();
		playingLabel.setText(selectedName);
		getCurrentName();
		updateArchive();


		// Microphone level
		new Thread() {
			@Override
			public void run() {
				micBar.setStyle("-fx-accent: red;");
				// Open a TargetDataLine for getting microphone input & sound level
				TargetDataLine line = null;
				AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); //     format is an AudioFormat object
				if (!AudioSystem.isLineSupported(info)) {
					System.out.println("The line is not supported.");
				}
				// Obtain and open the line.
				try {
					line = (TargetDataLine) AudioSystem.getLine(info);
					line.open(format);
					line.start();
				} catch (LineUnavailableException ex) {
					System.out.println("The TargetDataLine is Unavailable.");
				}

				while(true) {
					byte[] bytes = new byte[line.getBufferSize() / 5];
					line.read(bytes, 0, bytes.length);
					System.out.println("RMS Level: " + calculateRMSLevel(bytes));
					double prog = (double)calculateRMSLevel(bytes) / 70;
					micBar.setProgress(prog);

					if (!micBar.getScene().getWindow().isShowing()) {
						line.close();
						return;
					}
				}
			}
		}.start();

	}


	public void handlePrevButton(ActionEvent actionEvent) {
		if (selectedIndex == 0) {
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().selectFirst();
		} else {
			selectedIndex--;
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().select(selectedIndex);
		}
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		playingLabel.setText(selectedName);
		getCurrentName();
		updateArchive();

	}



	public void handleNextButton(ActionEvent actionEvent) {
		if (selectedIndex == listToPlay.size() - 1) {
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().selectLast();

		} else {
			selectedIndex++;
			displayListView.scrollTo(selectedIndex);
			displayListView.getSelectionModel().select(selectedIndex);
		}
		selectedName = displayListView.getSelectionModel().getSelectedItem();
		playingLabel.setText(selectedName);
		getCurrentName();
		updateArchive();


	}


	public void handlePlayButton(ActionEvent actionEvent) {
		//        showListeningStage();
		//        playButton.setDisable(true);
		//        prevButton.setDisable(true);
		//        nextButton.setDisable(true);

		toPlay = currentName.getFileName();
		System.out.println(toPlay);

		//        Service<Void> background = new Service<Void>() {
		//            @Override
		//            protected Task<Void> createTask() {
		//                return new Task<Void>() {
		//                    @Override
		//                    protected Void call() throws Exception {
		//                        File recording = new File("names/" + toPlay);
		//                        String path = recording.toString();
		//                        Media media = new Media(new File(path).toURI().toString());
		//                        MediaPlayer audioPlayer = new MediaPlayer(media);
		//                        audioPlayer.play();
		//                        return null;
		//                    }
		//                };
		//            }
		//        };
		//
		//        background.start();
		//        background.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		//            @Override
		//            public void handle(WorkerStateEvent event) {
		//                playButton.setDisable(false);
		//                prevButton.setDisable(false);
		//                nextButton.setDisable(false);
		//                listeningStage.close();
		//            }
		//        });
	}



	public void handleArcListClicked(MouseEvent mouseEvent) {

		selectedArchive = availableListView.getSelectionModel().getSelectedItem();
	}


	public void handleDeleteArc(ActionEvent actionEvent) {

		if (selectedArchive == null) {
			noFileAlert();
		} else {

			String fileString = "Creations/"+selectedArchive;
			File toDelete = new File(fileString +".wav");
			if (toDelete.exists()) {

				Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete:" + selectedArchive + "?", ButtonType.YES, ButtonType.NO);
				deleteConfirm.showAndWait();
				if (deleteConfirm.getResult() == ButtonType.YES) {

					try {
						Files.deleteIfExists(toDelete.toPath());
					} catch (IOException e) {
					}

					currentName.deleteAttempt(fileString);
					updateArchive();
					availableListView.getSelectionModel().clearSelection();

				}
			} else {
				if (!contains) {
					availableListView.setMouseTransparent(true);
					availableListView.setFocusTraversable(false);
				}
			}
		}
	}

	public void handlePlayArc(ActionEvent actionEvent) {
		if (selectedArchive == null) {
			noFileAlert();
		} else {
			showListeningStage();
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
					listeningStage.close();
				}
			});
		}
	}


	// Update attempts list
	public void updateArchive() {
		recordedList = FXCollections.observableArrayList(currentName.getAttemptList());
		if(recordedList.size() == 0){
			contains = false;
			availableListView.setMouseTransparent(true);
			availableListView.setFocusTraversable(false);

		} else {
			contains = true;
			availableListView.setMouseTransparent(false);
			availableListView.setFocusTraversable(true);
		}
		availableListView.setItems(recordedList);
		availableListView.getSelectionModel().clearSelection();
	}

	public void handleRecordAction(ActionEvent actionEvent) {
		int attempt = 0;

		File nameRecording = new File("./Creations/" + selectedName +"_attempt"+ attempt);
		while (nameRecording.exists()) {
			attempt++;
			nameRecording = new File("./Creations/" + selectedName + "_attempt"+ attempt);
		}

		String recordingName = selectedName+"_attempt"+attempt;
		String recordCommand = "ffmpeg -f alsa -ac 1 -ar 44100 -i default -t 5 \"" + recordingName + "\".wav";
		ProcessBuilder recordAudio = new ProcessBuilder("/bin/bash", "-c", recordCommand);
		recordAudio.directory(creations);
		
		// Time 5 seconds and set progress bar accordingly 
		new Thread() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				recordButton.setDisable(true);
				while (System.currentTimeMillis() < (startTime+5000)) {
					double recordingProgress = (System.currentTimeMillis() - startTime) / 5000.0;
					System.out.println("..........." + recordingProgress);
					recordingIndicator.setProgress(recordingProgress);
				}
				recordButton.setDisable(false);
				return;
			}
		}.start();
		
		currentName.addAttempt(recordingName);
		updateArchive();
	}


	public void noFileAlert() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("ERROR");
		alert.setHeaderText(null);
		alert.setContentText("No file selected");
		alert.showAndWait();
	}

	// https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
	protected static int calculateRMSLevel(byte[] audioData) {
		// audioData might be buffered data read from a data line
		long lSum = 0;
		for(int i = 0; i<audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;

		double sumMeanSquare = 0d;
		for(int j=0; j<audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
	}

	public void showListeningStage() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/namesayer/listeningWindow.fxml"));
			Parent root = fxmlLoader.load();
			listeningStage = new Stage();
			listeningStage.setTitle("Please wait");
			listeningStage.setScene(new Scene(root, 200, 100));
			listeningStage.show();
		} catch (IOException e){

		}
	}

	public void getCurrentName() {
		for (NameFile n : nameDatabase) {
			if (n.getListName().equals(selectedName)) {
				currentName = n;
			}
		}
	}


	public void returnToMain() {
		returnButton.getScene().getWindow().hide();
	}


}

package namesayer;

import javafx.animation.PauseTransition;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
	private ListView<String> availableListView;

	@FXML
	private ListView<String> displayListView;

	@FXML
	private ProgressBar recordingIndicator;

	@FXML
	private Button rateButton;

	@FXML
	private ProgressBar micBar = new ProgressBar();

	@FXML
	private Label playingLabel;

	private List<NameFile> nameDatabase;

	private File creations = new File("./Creations");

	private NameFile currentName;


	private List<String> listOfAttempts;

	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy_HHmmss");
	private Date date;


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
		newNameSelected();
		setRatingButton();


		// Microphone level
		new Thread() {
			@Override
			public void run() {
				micBar.setStyle("-fx-accent: red;");
				// Open a TargetDataLine for getting microphone input & sound level
				TargetDataLine line = null;
				AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 	// format is an AudioFormat object
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

				while (true) {
					byte[] bytes = new byte[line.getBufferSize() / 5];
					line.read(bytes, 0, bytes.length);
					double prog = (double) calculateRMSLevel(bytes) / 65;
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
		newNameSelected();
		setRatingButton();
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
		newNameSelected();
		setRatingButton();

	}


	public void handlePlayButton(ActionEvent actionEvent) {
		showListeningStage();

		toPlay = currentName.getFileName();
		System.out.println(toPlay);

		// Taken from https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
		try {
			File yourFile = new File("names/" + toPlay);
			AudioInputStream stream = AudioSystem.getAudioInputStream(yourFile);
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			long frames = stream.getFrameLength();
			long durationInSeconds = (frames / (long)format.getFrameRate());
			System.out.println(durationInSeconds);
			setAllButtonsDisabled(true);
			PauseTransition pause = new PauseTransition(Duration.seconds(durationInSeconds));
			pause.setOnFinished(event -> setAllButtonsDisabled(false));
			pause.play();
		}
		catch (Exception e) {
			System.out.println("FAILED TO PLAY FILE");
		}


	}


	private void setAllButtonsDisabled(boolean b) {
		playButton.setDisable(b);
		prevButton.setDisable(b);
		nextButton.setDisable(b);
		recordButton.setDisable(b);
		playArcButton.setDisable(b);
		deleteArcButton.setDisable(b);
	}



	public void handleArcListClicked(MouseEvent mouseEvent) {
		selectedArchive = availableListView.getSelectionModel().getSelectedItem();
		System.out.println(selectedArchive);
	}


	public void handleDeleteArc(ActionEvent actionEvent) {

		if (selectedArchive == null) {
			noFileAlert();
		} else {

			String fileString = "./Creations/" + selectedArchive;
			File toDelete = new File(fileString + ".wav");
			if (toDelete.exists()) {

				Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete:" + selectedArchive + "?", ButtonType.YES, ButtonType.NO);
				deleteConfirm.showAndWait();
				if (deleteConfirm.getResult() == ButtonType.YES) {

					try {
						Files.deleteIfExists(toDelete.toPath());
					} catch (IOException e) {
					}

					currentName.deleteAttempt(selectedArchive);
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
							File recording = new File("./Names/" + selectedArchive + ".wav");
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
		if (recordedList.size() == 0) {
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
		date = new Date();
		String currentDate = formatter.format(date);

		String recordingName = currentDate + "_" + selectedName;
		String recordCommand = "ffmpeg -f alsa -ac 1 -ar 44100 -i default -t 5 \"" + recordingName + "\".wav";
		ProcessBuilder recordAudio = new ProcessBuilder("/bin/bash", "-c", recordCommand);
		recordAudio.directory(creations);

		try{
			recordAudio.start();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		setAllButtonsDisabled(true);
		// Time 5 seconds and set progress bar accordingly
		new Thread() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				recordButton.setDisable(true);
				while (System.currentTimeMillis() < (startTime + 5000)) {
					double recordingProgress = (System.currentTimeMillis() - startTime) / 5000.0;
					recordingIndicator.setProgress(recordingProgress);
				}
				recordButton.setDisable(false);
				setAllButtonsDisabled(false);
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
	

	// Taken from https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
	protected static int calculateRMSLevel(byte[] audioData) {
		// audioData might be buffered data read from a data line
		long lSum = 0;
		for (int i = 0; i < audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;

		double sumMeanSquare = 0d;
		for (int j = 0; j < audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
	}



	public void showListeningStage() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/namesayer/listeningWindow.fxml"));
			Parent root = fxmlLoader.load();
			listeningStage = new Stage();
			listeningStage.setTitle("Please wait");
			listeningStage.setScene(new Scene(root, 200, 100));
			listeningStage.show();
		} catch (IOException e) {

		}
	}

	
	public void getCurrentName() {
		for (NameFile n : nameDatabase) {
			if (n.toString().equals(selectedName)) {
				currentName = n;
			}
		}
	}
	

	public void fillAttemptList() {
		for (String s : listOfAttempts) {
			int place = s.lastIndexOf("_") + 1;
			int place2 = s.lastIndexOf(".");
			String nameMatch = s.substring(place, place2);

			if (currentName.toString().equals(nameMatch)) {
				String toAddtoList = s.substring(0, place2);
				currentName.addAttempt(toAddtoList);
			}
		}
	}
	

	public void initialiseListOfAttempts() {
		listOfAttempts = new ArrayList<String>(Arrays.asList(creations.list()));
	}


	public void handleRateAction(ActionEvent actionEvent) {
		Alert rateConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Change " + selectedName + "'s rating?", ButtonType.YES, ButtonType.NO);
		rateConfirm.showAndWait();
		
		if (rateConfirm.getResult() == ButtonType.YES) {
			System.out.println(currentName.getFileName());
			toPlay = currentName.getFileName();
			
			if(!currentName.checkIfBadRating()) {
				currentName.setBadRating(true);
			} else {
				currentName.setBadRating(false);
			}
			setRatingButton();
		}
		
	}


	private void setRatingButton() {
		if (currentName.checkIfBadRating()) {
			rateButton.setText("Rate Good");
			rateButton.setStyle("-fx-background-color: red;");
		} else {
			rateButton.setText("Rate Bad");
			rateButton.setStyle("-fx-background-color: green;");
		}
	}


	public void newNameSelected() {
		getCurrentName();
		initialiseListOfAttempts();
		fillAttemptList();
		updateArchive();
	}


	public void returnToMain() {
		returnButton.getScene().getWindow().hide();
	}


}


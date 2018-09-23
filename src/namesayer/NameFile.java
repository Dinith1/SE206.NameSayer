package namesayer;

import javafx.beans.property.SimpleStringProperty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameFile {

	private SimpleStringProperty _fileName;
	private SimpleStringProperty _listName;
	private boolean _rating = false;
	private List<String> attemptList = new ArrayList<String>();
	private List<String> attemptListNameOnly = new ArrayList<String>();

	
	public NameFile(String fileName, String listName) {
		_fileName= new SimpleStringProperty(fileName);
		_listName = new SimpleStringProperty(listName);
	}


	public String getName() {
		int first = _fileName.get().indexOf("_") + 1;
		int point = _fileName.get().lastIndexOf("_");
		return (_fileName.get().substring(first, point)) + (_listName.get());
	}

	
	public String getFileName() {
		return _fileName.get();
	}
	
	
	public String getFileNameWithoutWAV() {
		return _fileName.get().substring(0, _fileName.get().length()-4);
	}

	
	public boolean getRating() {
		return _rating;
	}
	
	
	// Checks Bad_Ratings.txt file
	public boolean checkIfBadRating() {
		List<String> ratingsList = new ArrayList<String>();
		try {
			FileInputStream stream = new FileInputStream("Bad_Ratings.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String ratedFile;

			while ((ratedFile = br.readLine()) != null)   {
				ratingsList.add(ratedFile);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ratingsList.contains(this.getFileName());
	}
	
	
	public void setBadRatingField(boolean rating) {
		_rating = rating;
	}

	// This method sets rating field as well as writes to Bad_ratings.txt
	public void setBadRating(boolean rating) {
		_rating = rating;
		if (rating) {
			addBadRating();
		} else {
			removeBadRating();
		}
	}


	private void addBadRating() {
		try {
			FileWriter fw = new FileWriter("Bad_Ratings.txt", true);
			fw.write(this.getFileName() + "\n");
			fw.close();
			System.out.println("**************** ADDED BAD RATING ****************");
		} catch(IOException e){
			e.printStackTrace();
		}
	}


	private void removeBadRating() {
		try {
			File ratingsFile = new File("Bad_Ratings.txt");
			File tempFile = new File("temp.txt");
			tempFile.createNewFile();

			BufferedReader reader = new BufferedReader(new FileReader(ratingsFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String lineToRemove = this.getFileName();
			String currentLine;

			while((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();
				if(!trimmedLine.equals(lineToRemove)) {
					writer.write(currentLine + System.getProperty("line.separator"));
				}
			}

			writer.close(); 
			reader.close(); 
			ratingsFile.delete();
			tempFile.renameTo(ratingsFile);
			System.out.println("**************** REMOVED BAD RATING ****************");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public String toString(){
		return _listName.get();
	}

	
	public void addAttempt(String attemptFileName) {
		attemptList.add(attemptFileName);
		attemptListNameOnly.add(attemptFileName.substring(attemptFileName.lastIndexOf("_")+1, attemptFileName.length()));
	}

	
	public List<String> getAttemptList() {
		if(!attemptList.isEmpty()) {
			Collections.sort(attemptList);
		}
		return attemptList;
	}
	
	
	public List<String> getAttemptListNameOnly() {
		if(!attemptListNameOnly.isEmpty()) {
			Collections.sort(attemptListNameOnly);
		}
		return attemptListNameOnly;
	}
	

	public void deleteAttempt(String attemptFileName) {
		attemptList.remove(attemptFileName);
		attemptListNameOnly.remove(attemptFileName.substring(attemptFileName.lastIndexOf("_")+2, attemptFileName.length()));
	}

	
}

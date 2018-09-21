package namesayer;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameFile {

    private SimpleStringProperty _fileName;
    private SimpleStringProperty _listName;
    private boolean _rating = false;
    private List<String> attemptList = new ArrayList<>();

    public NameFile(String fileName, String listName) {
        _fileName= new SimpleStringProperty(fileName);
        _listName = new SimpleStringProperty(listName);
    }

    public String getName() {
        int point = _fileName.get().lastIndexOf("_");
        return (_fileName.get().substring(0, point)) + (_listName.get());
    }

    public String getFileName() {
        return _fileName.get();
    }

    public boolean getRating() {
        return _rating;
    }

    public void setRating(boolean rating) {
        _rating = rating;
    }

    public String toString(){
        return _listName.get();
    }

    public void addAttempt(String attemptFileName) {
        attemptList.add(attemptFileName);
    }

    public List<String> getAttemptList() {
        if(!attemptList.isEmpty()) {
            Collections.sort(attemptList);
        }
        return attemptList;
    }

    public void deleteAttempt(String attemptFileName) {
        attemptList.remove(attemptFileName);
    }

}

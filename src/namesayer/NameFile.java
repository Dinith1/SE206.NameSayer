package namesayer;

import javafx.beans.property.SimpleStringProperty;

import java.util.Collections;
import java.util.List;

public class NameFile {

    private SimpleStringProperty _fileName;
    private SimpleStringProperty _listName;
    private SimpleStringProperty _rating;
    private List<String> attemptList;

    public NameFile(String fileName, String listName, String rating) {
        _fileName= new SimpleStringProperty(fileName);
        _listName = new SimpleStringProperty(listName);
        _rating = new SimpleStringProperty(rating);
    }

    public String getListName() {
        return _listName.get();
    }

    public String getFileName(){
        return _fileName.get();
    }

    public String getRating() {
        return _rating.get();
    }

    public String toString(){
        return _listName.get();
    }

    public void addAttempt(String attemptFileName){
        attemptList.add(attemptFileName);
    }

    public List<String> getAttemptList(){
        Collections.sort(attemptList);
        return attemptList;
    }

}

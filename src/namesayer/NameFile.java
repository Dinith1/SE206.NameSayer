package namesayer;

public class NameFile {

    private String _fileName;
    private String _listName;
    private boolean _rating;

    public NameFile(String fileName, String listName, boolean rating) {
        _fileName= fileName;
        _listName = listName;
        _rating = rating;
    }

    public String getListName() {
        return _listName;
    }

    public String getFileName(){
        return _fileName;
    }

    public boolean getRating() {
        return _rating;
    }

}

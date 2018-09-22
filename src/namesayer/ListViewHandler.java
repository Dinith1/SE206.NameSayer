package namesayer;

import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

// Class to handle clicking and updating ListViews
public class ListViewHandler {

	// Updates the specified ListView with the specified list
	public void updateList(ListView<String> lv, List<String> l) {
		ObservableList<String> listToView = FXCollections.observableArrayList(l);
		lv.setItems(listToView);
		lv.getSelectionModel().clearSelection();
	}


	public void updateBothLists(ListView<String> lv1, List<String> l1, ListView<String> lv2, List<String> l2) {
		updateList(lv1, l1);
		updateList(lv2, l2);
	}


	// Removes all names from the selected list.
	// From lv1/l1 to lv2/l2
	public void moveWholeList(ListView<String> fromLV, List<String> fromList, ListView<String> toLV, List<String> toList) {
		toList.addAll(fromList);
		fromList.removeAll(fromList);
		Collections.sort(toList);
	}
	
	
	public void moveName(String name, List<String> fromList, List<String> toList) {
		fromList.remove(name);
		toList.add(name);
		Collections.sort(fromList);
		Collections.sort(toList);
	}
	
	
}

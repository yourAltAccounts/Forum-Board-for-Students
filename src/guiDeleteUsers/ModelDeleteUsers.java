package guiDeleteUsers;

import javafx.collections.FXCollections;

import java.util.List;
import database.Database;


public class ModelDeleteUsers {
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	protected static void setupNewUserArrays() {
		List<String> userList = theDatabase.getUserList();
	    ViewDeleteUsers.combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
	    ViewDeleteUsers.combobox_SelectUser.getSelectionModel().select(0);
	}
		
}

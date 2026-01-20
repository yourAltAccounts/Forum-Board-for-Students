package guiManageInvitation;

import javafx.collections.FXCollections;

import java.util.List;
import database.Database;
import java.util.HashMap;
import java.util.Map;

public class ModelManageInvitation {
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	protected static void setupNewInvitationArrays() {
		List<String> invitationList = theDatabase.getInvitationList();
		if (!invitationList.contains("<Select an Email>")) {
		    invitationList.add(0, "<Select an Email>");
		}
		ViewManageInvitation.combobox_SelectUser.setItems(FXCollections.observableArrayList(invitationList));
		ViewManageInvitation.combobox_SelectUser.getSelectionModel().select(0);
		ViewManageInvitation.theSelectedUser = "<Select an Email>";
	}
	
	public static long getDeadline(String code) {
	    try {
	        return theDatabase.getDeadlineFromDatabase(code); // delegate to DB
	    } catch (Exception e) {
	        System.err.println("Error fetching deadline: " + e.getMessage());
	        return 0L;
	    }
	}
	
	private static Map<String, Long> deadlineMap = new HashMap<>();

    public static void setDeadline(String code, long deadline) {
        deadlineMap.put(code, deadline);
    }

    public static boolean isExpired(String code) {
        long deadline = getDeadline(code);
        return deadline > 0 && System.currentTimeMillis() > deadline;
    }


    public static void removeDeadline(String code) {
        deadlineMap.remove(code);
    }


    public static void clearAll() {
        deadlineMap.clear();
    }
}

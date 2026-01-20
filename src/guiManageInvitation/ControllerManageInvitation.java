package guiManageInvitation;

import database.Database;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;

import java.util.List;
import javafx.beans.value.ChangeListener;

public class ControllerManageInvitation {
	
	public static final ChangeListener<String> userSelectionListener = 
		    (observable, oldValue, newValue) -> doSelectUser();

	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static void doSelectUser() {
		 String selected = ViewManageInvitation.combobox_SelectUser.getValue();

		    if (selected == null || selected.trim().isEmpty() || selected.equals("<Select an Email>")) {
		        ViewManageInvitation.theSelectedUser = "<Select an Email>";
		    } else {
		        ViewManageInvitation.theSelectedUser = selected;
		    }

		    setupSelectedUser();
	}

	protected static void repaintTheWindow() {

		ViewManageInvitation.theRootPane.getChildren().clear();
		
		if ("<Select an Email>".equals(ViewManageInvitation.theSelectedUser)) {
			ViewManageInvitation.theRootPane.getChildren().addAll(
					ViewManageInvitation.label_PageTitle, ViewManageInvitation.label_UserDetails, 
				
					ViewManageInvitation.line_Separator1,
					ViewManageInvitation.label_SelectUser, ViewManageInvitation.combobox_SelectUser, 
					ViewManageInvitation.line_Separator4, ViewManageInvitation.button_Return,
					ViewManageInvitation.button_Logout, ViewManageInvitation.button_Quit);
		}
		else {
			ViewManageInvitation.theRootPane.getChildren().addAll(
					ViewManageInvitation.label_PageTitle, ViewManageInvitation.label_UserDetails,
					
					ViewManageInvitation.label_SelectUser,
					ViewManageInvitation.combobox_SelectUser, 
					ViewManageInvitation.label_CurrentRoles,
					ViewManageInvitation.label_SelectRoleToBeAdded,
					ViewManageInvitation.combobox_SelectRoleToAdd,
					ViewManageInvitation.button_deadline,
					ViewManageInvitation.label_SelectRoleToBeRemoved,
					ViewManageInvitation.combobox_SelectRoleToRemove,
					ViewManageInvitation.button_RemoveRole,
					ViewManageInvitation.line_Separator4, 
					ViewManageInvitation.button_Return,
					ViewManageInvitation.button_Logout,
					ViewManageInvitation.button_Quit);
		}
		
		ViewManageInvitation.theStage.setTitle("CSE 360 Foundation Code: Admin Opertaions Page");
		ViewManageInvitation.theStage.setScene(ViewManageInvitation.theAddRemoveRolesScene);
		ViewManageInvitation.theStage.show();
	}

	private static void setupSelectedUser() {
		System.out.println("*** Entering setupSelectedUser");

		 String selectedEmail = ViewManageInvitation.theSelectedUser;

		    String code = theDatabase.getCodebyEmail(selectedEmail);

		    if (ModelManageInvitation.isExpired(code)) {
		    	
		    	showAlert("Expired Invitation", "This invitation has expired and has been removed.");
		    	
		    	theDatabase.removeInvitationAfterUse(code);
		        ModelManageInvitation.removeDeadline(code); 
		        ModelManageInvitation.setupNewInvitationArrays();

		        List<String> updatedList = theDatabase.getInvitationList();
		        if (updatedList == null || updatedList.isEmpty()) {
		            updatedList = List.of("<Select an Email>");
		        }

		        ViewManageInvitation.theSelectedUser = "<Select an Email>";
		        updateComboBoxItems(updatedList, "<Select an Email>");
		        
		        ViewManageInvitation.label_CurrentRoles.setText("This invitation has expired and was removed.");
		        repaintTheWindow();
		        return; 
		    }
		    long deadline = theDatabase.getDeadlineFromDatabase(code);

		    String deadlineText;
		    if (deadline == 0L) {
		        deadlineText = "No deadline set";
		    } else {
		        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
		        deadlineText = formatter.format(new java.util.Date(deadline));
		    }

		ViewManageInvitation.label_CurrentRoles.setText("This code deadline: " + 
				deadlineText);		
		ViewManageInvitation.setupComboBoxUI(ViewManageInvitation.combobox_SelectRoleToAdd, "Dialog",
				16, 150, 280, 205);
		ViewManageInvitation.combobox_SelectRoleToAdd.setItems(FXCollections.
				observableArrayList("<Set Deadline>","30 minutes","1 hour","1 day","1 week"));
		ViewManageInvitation.combobox_SelectRoleToAdd.getSelectionModel().clearAndSelect(0);		
		ViewManageInvitation.setupButtonUI(ViewManageInvitation.button_deadline, "Dialog", 16, 150, 
				Pos.CENTER, 460, 205);
		ViewManageInvitation.setupComboBoxUI(ViewManageInvitation.combobox_SelectRoleToRemove, "Dialog",
				16, 150, 280, 275);
		ViewManageInvitation.combobox_SelectRoleToRemove.setItems(FXCollections.
				observableArrayList("<Confirm?>","Yes","No"));
		ViewManageInvitation.combobox_SelectRoleToRemove.getSelectionModel().select(0);

		repaintTheWindow();

	}

	protected static void performDeadline() {
		
		String selectedEmail = ViewManageInvitation.theSelectedUser;
	    String selectedTime = ViewManageInvitation.combobox_SelectRoleToAdd.getValue();

	    if (selectedEmail == null || selectedEmail.equals("<Select an Email>")) {
	        return;
	    }

	    String code = theDatabase.getCodebyEmail(selectedEmail);
	    long now = System.currentTimeMillis();
	    long deadline = 0L;

	    switch (selectedTime) {
	        case "30 minutes": deadline = now + 30 * 60 * 1000; break;
	        case "1 hour": deadline = now + 60 * 60 * 1000; break;
	        case "1 day": deadline = now + 24 * 60 * 60 * 1000; break;
	        case "1 week": deadline = now + 7 * 24 * 60 * 60 * 1000; break;
	        default: deadline = 0L; break; // ∞
	    }

	    theDatabase.setDeadlineInDatabase(code, deadline);

	    setupSelectedUser(); 
	}

	protected static void performRemoveInvitation() {
		
		ViewManageInvitation.theRemoveRole = (String) ViewManageInvitation.
				combobox_SelectRoleToRemove.getValue();
		
		
		if (ViewManageInvitation.theRemoveRole.compareTo("Yes") == 0) {
			
			String email = ViewManageInvitation.theSelectedUser;
			
			if (theDatabase.emailaddressHasBeenUsed(email)) {
				
				String code = theDatabase.getCodebyEmail(email);
				theDatabase.removeInvitationAfterUse(code);
	            ModelManageInvitation.setupNewInvitationArrays();

	            List<String> invitationList = theDatabase.getInvitationList();
	            if (invitationList == null || invitationList.isEmpty()) {
	                invitationList = List.of("<Select an Email>");
	            }

	            ViewManageInvitation.theSelectedUser = "<Select an Email>";
	            updateComboBoxItems(invitationList, ViewManageInvitation.theSelectedUser);
	            repaintTheWindow();			
			}			
			
			showAlert("Remove Alert", "Code was deleted.");
			
		}
	}

	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewManageInvitation.theStage,
				ViewManageInvitation.theUser);
	}

	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewManageInvitation.theStage);
	}

	protected static void performQuit() {
		System.exit(0);
	}
	
	public static void updateComboBoxItems(List<String> newItems, String selectValue) {

	    ViewManageInvitation.combobox_SelectUser.getSelectionModel().selectedItemProperty()
	        .removeListener(userSelectionListener);

	    ViewManageInvitation.combobox_SelectUser.setItems(FXCollections.observableArrayList(newItems));

	    if (selectValue != null && newItems.contains(selectValue)) {
	        ViewManageInvitation.combobox_SelectUser.getSelectionModel().select(selectValue);
	    } else if (!newItems.isEmpty()) {
	        ViewManageInvitation.combobox_SelectUser.getSelectionModel().select(0);
	    }

	    ViewManageInvitation.combobox_SelectUser.getSelectionModel().selectedItemProperty()
	        .addListener(userSelectionListener);
	}
	
	private static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}

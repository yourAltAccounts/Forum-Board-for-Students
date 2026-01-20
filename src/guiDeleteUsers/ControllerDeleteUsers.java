package guiDeleteUsers;

import database.Database;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

public class ControllerDeleteUsers {

	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static void doSelectUser() {
		ViewDeleteUsers.theSelectedUser = 
				(String) ViewDeleteUsers.combobox_SelectUser.getValue();
		theDatabase.getUserAccountDetails(ViewDeleteUsers.theSelectedUser);
		setupSelectedUser();
	}

	protected static void repaintTheWindow() {

		ViewDeleteUsers.theRootPane.getChildren().clear();

		if (ViewDeleteUsers.theSelectedUser == null ||
		        "<Select a User>".equals(ViewDeleteUsers.theSelectedUser)) {

			ViewDeleteUsers.theRootPane.getChildren().addAll(
					ViewDeleteUsers.label_PageTitle, ViewDeleteUsers.label_UserDetails, 
					ViewDeleteUsers.button_UpdateThisUser, ViewDeleteUsers.line_Separator1,
					ViewDeleteUsers.label_SelectUser, ViewDeleteUsers.combobox_SelectUser, 
					ViewDeleteUsers.line_Separator4, ViewDeleteUsers.button_Return,
					ViewDeleteUsers.button_Logout, ViewDeleteUsers.button_Quit);
		}
		else {
			
			ViewDeleteUsers.theRootPane.getChildren().addAll(
					ViewDeleteUsers.label_PageTitle, ViewDeleteUsers.label_UserDetails,
					ViewDeleteUsers.button_UpdateThisUser, ViewDeleteUsers.line_Separator1,
					ViewDeleteUsers.label_SelectUser,
					ViewDeleteUsers.combobox_SelectUser, 
					ViewDeleteUsers.label_CurrentRoles,
					ViewDeleteUsers.label_SelectRoleToBeRemoved,
					ViewDeleteUsers.combobox_SelectRoleToRemove,
					ViewDeleteUsers.button_RemoveRole,
					ViewDeleteUsers.line_Separator4, 
					ViewDeleteUsers.button_Return,
					ViewDeleteUsers.button_Logout,
					ViewDeleteUsers.button_Quit);
		}

		ViewDeleteUsers.theStage.setTitle("CSE 360 Foundation Code: Admin Opertaions Page");
		ViewDeleteUsers.theStage.setScene(ViewDeleteUsers.theAddRemoveRolesScene);
		ViewDeleteUsers.theStage.show();
	}
	
	
	private static void setupSelectedUser() {
		System.out.println("*** Entering setupSelectedUser");

		boolean notTheFirst = false;
		String theCurrentRoles = "";
		
		if (theDatabase.getCurrentAdminRole()) {
			theCurrentRoles += "Admin";
			notTheFirst = true;
		}
		
		if (theDatabase.getCurrentNewRole1()) {
			if (notTheFirst)
				theCurrentRoles += ", Staff"; 
			else {
				theCurrentRoles += "Staff";
				notTheFirst = true;
			}
		}


		if (theDatabase.getCurrentNewRole2()) {
			if (notTheFirst)
				theCurrentRoles += ", Student"; 
			else {
				theCurrentRoles += "Student";
				notTheFirst = true;
			}
		}

		
		ViewDeleteUsers.label_CurrentRoles.setText("This user's current roles: " + 
				theCurrentRoles);		
		ViewDeleteUsers.setupComboBoxUI(ViewDeleteUsers.combobox_SelectRoleToRemove, "Dialog",
				16, 150, 280, 275);
		ViewDeleteUsers.combobox_SelectRoleToRemove.setItems(FXCollections.
				observableArrayList("<Confirm?>","Yes","No"));
		ViewDeleteUsers.combobox_SelectRoleToRemove.getSelectionModel().select(0);

		repaintTheWindow();

	}
	
	protected static void performRemoveUser() {

		ViewDeleteUsers.theRemoveUser = (String) ViewDeleteUsers.
				combobox_SelectRoleToRemove.getValue();
		
		if (ViewDeleteUsers.theRemoveUser.compareTo("Yes") == 0) {
			String selectedUser = ViewDeleteUsers.theSelectedUser == null ? "" : ViewDeleteUsers.theSelectedUser.trim();
			theDatabase.getUserAccountDetails(selectedUser); 

			if (theDatabase.getCurrentAdminRole()) {
				showAlert("Deletion Blocked", "Cannot delete admin user.");
				return;
			}
			if (theDatabase.DeleteUser(ViewDeleteUsers.theSelectedUser)) {
				
	            ModelDeleteUsers.setupNewUserArrays();

	            var userList = theDatabase.getUserList();
	            ViewDeleteUsers.combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));

	            if (!userList.isEmpty()) {
	                ViewDeleteUsers.combobox_SelectUser.getSelectionModel().select(0);
	                ViewDeleteUsers.theSelectedUser = userList.get(0);
	            } else {
	                ViewDeleteUsers.theSelectedUser = "<Select a User>";
	            }
	            
	            showAlert("Remove Alert", "User already delete.");

	            repaintTheWindow();	
	            
			}
		}
	}

	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUsers.theStage,
				ViewDeleteUsers.theUser);
	}

	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUsers.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}
	
	private static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
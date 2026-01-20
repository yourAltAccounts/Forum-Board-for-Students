package guiAdminHome;

import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import guiNewAccount.ModelNewAccount;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import guiNewAccount.ModelNewAccount; 
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import java.util.Optional;
import java.util.List;

import java.util.ArrayList; //added
import entityClasses.User; //added


/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	//Added
	//Reference for the Model class using the database reference so the Model Class have access to the data.
	private static ModelAdminHome theModel = new ModelAdminHome(theDatabase);
	
	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		String validEmailAddressMessage = 
				guiAdminHome.ModelAdminHome.checkForValidEmailAddress(emailAddress);
		if (validEmailAddressMessage != "") {
			Label emailAddressValidCheck = new Label(validEmailAddressMessage);
			emailAddressValidCheck.setWrapText(true);
			ViewAdminHome.alertEmailError.setTitle("Email Address");
			ViewAdminHome.alertEmailError.setHeaderText("Please enter a valid email address");
			ViewAdminHome.alertEmailError.getDialogPane().setContent(emailAddressValidCheck);
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.checkEmailAddress(emailAddress)) {
			ViewAdminHome.alertEmailError.setTitle("Email Address");
			ViewAdminHome.alertEmailError.setHeaderText("Please enter a different email address");
			ViewAdminHome.alertEmailError.setContentText(
					"This email address has already accepted an invitation, please enter a different email.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		// Convert UI role name to database role name
		String dbRole = theSelectedRole.equals("Student") ? "Role1" : 
						theSelectedRole.equals("Staff") ? "Role2" : theSelectedRole;
		String invitationCode = theDatabase.generateInvitationCode(emailAddress, dbRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		guiManageInvitation.ViewManageInvitation.displayManageInvitation(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword() {
		List<String> userList = theDatabase.getUserList();
		
		if (userList == null || userList.size() <= 1) {
			ViewAdminHome.alertNotImplemented.setContentText("No users available to set a temporary password for.");
			ViewAdminHome.alertNotImplemented.showAndWait();
			return;
		}
		
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Set Temporary Password");
		dialog.setHeaderText("Select a user and enter a temporary password");
		
		ButtonType setButtonType = new ButtonType("Set Password", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(setButtonType, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		ComboBox<String> userCombo = new ComboBox<>();
		userCombo.getItems().addAll(userList);
		userCombo.getSelectionModel().select(0);
		
		TextField tempPassword = new TextField();
		tempPassword.setPromptText("Temporary Password");
		
		Label instructions = new Label(
			"The selected user will be required to change\n" +
			"this password on their next login."
		);
		instructions.setStyle("-fx-font-size: 12px;");
		
		grid.add(new Label("Select User:"), 0, 0);
		grid.add(userCombo, 1, 0);
		grid.add(new Label("Temporary Password:"), 0, 1);
		grid.add(tempPassword, 1, 1);
		grid.add(instructions, 0, 2, 2, 1);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == setButtonType) {
				return new Pair<>(userCombo.getValue(), tempPassword.getText());
			}
			return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();
		
		result.ifPresent(usernamePassword -> {
			String selectedUser = usernamePassword.getKey();
			String password = usernamePassword.getValue();
			
			if (selectedUser == null || selectedUser.equals("<Select a User>")) {
				ViewAdminHome.alertNotImplemented.setContentText("Please select a valid user.");
				ViewAdminHome.alertNotImplemented.showAndWait();
				return;
			}
			
			if (password.trim().isEmpty()) {
				ViewAdminHome.alertNotImplemented.setContentText("Please enter a temporary password.");
				ViewAdminHome.alertNotImplemented.showAndWait();
				return;
			}
			
			theDatabase.setTemporaryPassword(selectedUser, password);
			
			Alert success = new Alert(AlertType.INFORMATION);
			success.setTitle("Success");
			success.setHeaderText("Temporary Password Set");
			success.setContentText("A temporary password has been set for " + selectedUser + 
				".\nThey will be required to change it on their next login.");
			success.showAndWait();
		});
	}
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		guiDeleteUsers.ViewDeleteUsers.displayDeleteUsers(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * UPDATED
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers()
	{
		System.out.println("Displaying list of users...");
		
		ArrayList<User> listOfUsers = theModel.getAllUsers();
		//Pass the listOfUsers to communicate with the view class.
		ViewAdminHome.displayUsers(listOfUsers);
	}
	/**********
	 * <p> 
	 * ADDED 
	 * Title: togglleUserList () Method. </p>
	 * 
	 * <p> Description: Protected method that allows the User List button to show or not show by clicking on the 
	 * List User button. Toggling will update the list to the current information when changes to the list are made</p>
	 */
	protected static void toggleUserList()
	{
		//Base Case If the list is shown in the GUI then turn it off(Don't show)
		if(ViewAdminHome.label_UserList.isVisible())
		{
			ViewAdminHome.label_UserList.setVisible(false);
			
		}
		//Otherwise display the Users and set the list to on(Show)
		else
		{
			ArrayList<User> listOfUsers= theModel.getAllUsers();
			ViewAdminHome.displayUsers(listOfUsers);
			ViewAdminHome.label_UserList.setVisible(true);
		}
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}


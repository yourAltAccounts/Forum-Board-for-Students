package guiUserUpdate;

import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

/*******
 * <p> Title: ViewUserUpdate Class. </p>
 * 
 * <p> Description: The Java/FX-based User Update Page.  This page enables the user to update the
 * attributes about the user held by the system.  Currently, this page does not provide a mechanism
 * to change the Username and not all of the functions on this page are implemented.
 * 
 * Currently the following attributes can be updated:
 * 		- First Name
 * 		- Middle Name
 * 		- Last Name
 * 		- Preferred First Name
 * 		- Email Address
 * The page uses dialog boxes for updating these items.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01		2025-08-19 Initial version plus new internal documentation
 *  
 */

public class ViewUserUpdate {

	/*-********************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// Unlike may of the other pages, the GUI on this page is not organized into areas and the user
	// is not able to logout, return, or quit from this page
	
	// These widgets display the purpose of the page and guide the user.
	private static Label label_ApplicationTitle = new Label("Update a User's Account Details");
    private static Label label_Purpose = 
    		new Label(" Use this page to define or update your account information."); 
    
    // These are static output labels and do not change during execution
	private static Label label_Username = new Label("Username:");
	private static Label label_Password = new Label("Password:");
	private static Label label_FirstName = new Label("First Name:");
	private static Label label_MiddleName = new Label("Middle Name:");
	private static Label label_LastName = new Label("Last Name:");
	private static Label label_PreferredFirstName = new Label("Preferred First Name:");
	private static Label label_EmailAddress = new Label("Email Address:");
	
	// These are dynamic labels and they change based on the user and user interactions.
	private static Label label_CurrentUsername = new Label();
	private static Label label_CurrentPassword = new Label();
	private static Label label_CurrentFirstName = new Label();
	private static Label label_CurrentMiddleName = new Label();
	private static Label label_CurrentLastName = new Label();
	private static Label label_CurrentPreferredFirstName = new Label();
	private static Label label_CurrentEmailAddress = new Label();
	
	// These buttons enable the user to edit the various dynamic fields.  The username and the
	// passwords for a user are currently not editable.
	private static Button button_UpdateUsername = new Button("Update Username");
	private static Button button_UpdatePassword = new Button("Update Password");
	private static Button button_UpdateFirstName = new Button("Update First Name");
	private static Button button_UpdateMiddleName = new Button("Update Middle Name");
	private static Button button_UpdateLastName = new Button("Update Last Name");
	private static Button button_UpdatePreferredFirstName = new Button("Update Preferred First Name");
	private static Button button_UpdateEmailAddress = new Button("Update Email Address");

	// This button enables the user to finish working on this page and proceed to the user's home
	// page determined by the user's role at the time of log in.
	private static Button button_ProceedToUserHomePage = new Button("Proceed to the User Home Page");
	
	// This button enables the user to logout and return to the login page
	private static Button button_Logout = new Button("Logout");
	
	// This is the end of the GUI widgets for this page.
	
	// These are the set of pop-up dialog boxes that are used to enable the user to change the
	// the values of the various account detail items.
	private static TextInputDialog dialogUpdateUsername;
	private static TextInputDialog dialogUpdatePassword;
	private static TextInputDialog dialogUpdatePasswordConfirmation;
	private static TextInputDialog dialogUpdateFirstName;
	private static TextInputDialog dialogUpdateMiddleName;
	private static TextInputDialog dialogUpdateLastName;
	private static TextInputDialog dialogUpdatePreferredFirstName;
	private static TextInputDialog dialogUpdateEmailAddresss;
	
	private static PasswordField passField = new PasswordField();
	private static PasswordField passFieldConfirmation = new PasswordField();
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewUserUpdate theView;	// Used to determine if instantiation of the class
											// is needed

	// This enables access to the application's database
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	private static User theUser;				// The current user of the application

	public static Scene theUserUpdateScene = null;	// The Scene each invocation populates

	private static Optional<String> result;		// The result from a pop-up dialog

	/*-********************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayUserUpdate(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the UserUpdate page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayUserUpdate(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theUser = user;
		theStage = ps;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewUserUpdate();
		
		// Set the widget values that change from use of page to another use of the page.
		String s = "";
		
		// Set the dynamic aspects of the window based on the user logged in and the current state
		// of the various account elements.
		s = theUser.getUserName();
		System.out.println("*** Fetching account data for user: " + s);
    	if (s == null || s.length() < 1)label_CurrentUsername.setText("<none; required>");
    	else label_CurrentUsername.setText(s);
		
		s = theUser.getPassword();
    	if (s == null || s.length() < 1)label_CurrentPassword.setText("<none; required>");
    	else label_CurrentPassword.setText(s);
    	
		s = theUser.getFirstName();
    	if (s == null || s.length() < 1)label_CurrentFirstName.setText("<none, required>");
    	else label_CurrentFirstName.setText(s);
       
        s = theUser.getMiddleName();
    	if (s == null || s.length() < 1)label_CurrentMiddleName.setText("<none>");
    	else label_CurrentMiddleName.setText(s);
        
        s = theUser.getLastName();
    	if (s == null || s.length() < 1)label_CurrentLastName.setText("<none>");
    	else label_CurrentLastName.setText(s);
        
		s = theUser.getPreferredFirstName();
    	if (s == null || s.length() < 1)label_CurrentPreferredFirstName.setText("<none>");
    	else label_CurrentPreferredFirstName.setText(s);
        
		s = theUser.getEmailAddress();
    	if (s == null || s.length() < 1)label_CurrentEmailAddress.setText("<none, required>");
    	else label_CurrentEmailAddress.setText(s);

		// Set the title for the window, display the page, and wait for the Admin to do something
    	theStage.setTitle("CSE 360 Foundation Code: Update User Account Details");
        theStage.setScene(theUserUpdateScene);
		theStage.show();
	}

	
	/**********
	 * <p> Method: ViewUserUpdate() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayUserUpdate method.</p>
	 * 
	 */
	
	private ViewUserUpdate() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserUpdateScene = new Scene(theRootPane, width, height);

		// Initialize the pop-up dialogs to an empty text filed.
		dialogUpdateUsername = new TextInputDialog("");
		dialogUpdatePassword = new TextInputDialog("");
		dialogUpdatePasswordConfirmation = new TextInputDialog("");
		dialogUpdateFirstName = new TextInputDialog("");
		dialogUpdateMiddleName = new TextInputDialog("");
		dialogUpdateLastName = new TextInputDialog("");
		dialogUpdatePreferredFirstName = new TextInputDialog("");
		dialogUpdateEmailAddresss = new TextInputDialog("");
		
		
		Alert ErrorMessage = new Alert(AlertType.INFORMATION);
		
		// Formatting for the password input widget
		passField.setPromptText("");
		passFieldConfirmation.setPromptText("");
		HBox passwordBox1 = new HBox(passField);
		HBox passwordBox2 = new HBox(passFieldConfirmation);
		passwordBox1.setSpacing(10);
		passwordBox2.setSpacing(10);
		passwordBox1.setAlignment(Pos.CENTER);
		passwordBox2.setAlignment(Pos.CENTER);

		// Establish the label for each of the dialogs.
		dialogUpdateUsername.setTitle("Update Username");
		dialogUpdateUsername.setHeaderText("Update your Username");
		
		dialogUpdatePassword.setTitle("Update Password");
		dialogUpdatePassword.setHeaderText("Update your Password");
		dialogUpdatePassword.getDialogPane().setContent(passwordBox1);
		
		dialogUpdatePasswordConfirmation.setTitle("Confirm Updated Password");
		dialogUpdatePasswordConfirmation.setHeaderText("Confirm your updated password");
		dialogUpdatePasswordConfirmation.getDialogPane().setContent(passwordBox2);
		
		dialogUpdateFirstName.setTitle("Update First Name");
		dialogUpdateFirstName.setHeaderText("Update your First Name");
		
		dialogUpdateMiddleName.setTitle("Update Middle Name");
		dialogUpdateMiddleName.setHeaderText("Update your Middle Name");
		
		dialogUpdateLastName.setTitle("Update Last Name");
		dialogUpdateLastName.setHeaderText("Update your Last Name");
		
		dialogUpdatePreferredFirstName.setTitle("Update Preferred First Name");
		dialogUpdatePreferredFirstName.setHeaderText("Update your Preferred First Name");
		
		dialogUpdateEmailAddresss.setTitle("Update Email Address");
		dialogUpdateEmailAddresss.setHeaderText("Update your Email Address");

		// Label theScene with the name of the startup screen, centered at the top of the pane
		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // Label to display the welcome message for the first theUser
        setupLabelUI(label_Purpose, "Arial", 20, width, Pos.CENTER, 0, 50);
        
        theRootPane.getChildren().add(button_UpdateUsername);
        // Display the titles, values, and update buttons for the various admin account attributes.
        // If the attributes is null or empty, display "<none>".
        
        // Username
        setupLabelUI(label_Username, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 110);
        setupLabelUI(label_CurrentUsername, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 110);
        setupButtonUI(button_UpdateUsername, "Dialog", 18, 275, Pos.CENTER, 500, 100);
        button_UpdateUsername.setOnAction((event) -> {
        	result = dialogUpdateUsername.showAndWait();
        	String oldUsername = theUser.getUserName();
         	String newUsername = result.get();
         	boolean isUsernameTaken = theDatabase.doesUserExist(newUsername);
         	// Input Validation
         	String validCheck = guiUserUpdate.Model.checkForValidUserName(newUsername);
         	if(validCheck == "" && !isUsernameTaken) {
         		result.ifPresent(name -> theDatabase.updateUsername(theUser.getUserName(), result.get()));
         		theDatabase.getUserAccountDetails(theUser.getUserName());
	         	theUser.setUserName(newUsername);
	        	label_CurrentUsername.setText(newUsername);
         	} else if (isUsernameTaken) {
         		theDatabase.updateUsername(newUsername, oldUsername);
         		Label usernameError = new Label("That username has already been taken, try another one.");
         		usernameError.setWrapText(true);
         		ErrorMessage.setTitle("Username");
         		ErrorMessage.setHeaderText("That username is taken");
         		ErrorMessage.getDialogPane().setContent(usernameError);
         		ErrorMessage.showAndWait();
         	} else {
         		theDatabase.updateUsername(newUsername, oldUsername);
         		Label usernameError = new Label(validCheck);
         		usernameError.setWrapText(true);
         		ErrorMessage.setTitle("Username");
         		ErrorMessage.setHeaderText("Please enter a valid username");
         		ErrorMessage.getDialogPane().setContent(usernameError);
         		ErrorMessage.showAndWait();
         	}
         });
       
        // Password
        setupLabelUI(label_Password, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 160);
        setupLabelUI(label_CurrentPassword, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 160);
        setupButtonUI(button_UpdatePassword, "Dialog", 18, 275, Pos.CENTER, 500, 150);
        button_UpdatePassword.setOnAction((event) -> {
        	dialogUpdatePassword.setResultConverter(button_UpdatePassword -> {
        		if (button_UpdatePassword == dialogUpdatePassword.getDialogPane().getButtonTypes().get(0)) {
        			return passField.getText();
        		}
        		return null;
        	});
        	result = dialogUpdatePassword.showAndWait();
	    	result.ifPresent(name -> theDatabase.updatePassword(theUser.getUserName(), result.get()));
	    	theDatabase.getUserAccountDetails(theUser.getUserName());
	    	String oldPassword = theUser.getPassword();
	     	String newPassword = theDatabase.getCurrentPassword();
	     	
	     	// Password Confirmation
        	dialogUpdatePasswordConfirmation.setResultConverter(button_UpdatePassword -> {
        		if (button_UpdatePassword == 
        				dialogUpdatePasswordConfirmation.getDialogPane().getButtonTypes().get(0)) {
        			return passFieldConfirmation.getText();
        		}
        		return null;
        	});
        	result = dialogUpdatePasswordConfirmation.showAndWait();
        	
	     	// Input Validation
	     	String validCheck = guiUserUpdate.Model.checkForValidPassword(newPassword);
	     	if(validCheck == "") {
		       	theUser.setPassword(newPassword);
		    	label_CurrentPassword.setText(newPassword);
	     	} else if(!passField.getText().equals(passFieldConfirmation.getText())) {
	     		theDatabase.updatePassword(theUser.getUserName(), oldPassword);
        		Label passwordError = new Label("Confirm your new password by re-entering it.");
        		passwordError.setWrapText(true);
         		ErrorMessage.setTitle("Password");
         		ErrorMessage.setHeaderText("Please re-enter your password");
         		ErrorMessage.getDialogPane().setContent(passwordError);
         		ErrorMessage.showAndWait();
	     	} else {
         		theDatabase.updatePassword(theUser.getUserName(), oldPassword);
        		Label passwordError = new Label(validCheck);
        		passwordError.setWrapText(true);
         		ErrorMessage.setTitle("Password");
         		ErrorMessage.setHeaderText("Please enter a valid password");
         		ErrorMessage.getDialogPane().setContent(passwordError);
         		ErrorMessage.showAndWait();
         	}
	     });
        
        // First Name
        setupLabelUI(label_FirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 210);
        setupLabelUI(label_CurrentFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 210);
        setupButtonUI(button_UpdateFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 200);
        button_UpdateFirstName.setOnAction((event) -> {
        	result = dialogUpdateFirstName.showAndWait();
        	result.ifPresent(name -> theDatabase.updateFirstName(theUser.getUserName(), result.get()));
        	theDatabase.getUserAccountDetails(theUser.getUserName());
        	String oldName = theUser.getFirstName();
         	String newName = theDatabase.getCurrentFirstName();
         	System.out.println(oldName + ", " + newName);
         	// Input Validation
         	String validCheck = guiUserUpdate.Model.checkForValidNewName(newName);
         	if(validCheck == "") {
	         	theUser.setFirstName(newName);
	        	label_CurrentFirstName.setText(newName);
         	} else {
         		theDatabase.updateFirstName(theUser.getUserName(), oldName);
        		Label firstNameError = new Label(validCheck);
        		firstNameError.setWrapText(true);
         		ErrorMessage.setTitle("First Name");
         		ErrorMessage.setHeaderText("Please enter a valid first name");
         		ErrorMessage.getDialogPane().setContent(firstNameError);
         		ErrorMessage.showAndWait();
         	}
         });
               
        // Middle Name
        setupLabelUI(label_MiddleName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 260);
        setupLabelUI(label_CurrentMiddleName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 260);
        setupButtonUI(button_UpdateMiddleName, "Dialog", 18, 275, Pos.CENTER, 500, 250);
        button_UpdateMiddleName.setOnAction((event) -> {
        	result = dialogUpdateMiddleName.showAndWait();
    		result.ifPresent(name -> theDatabase.updateMiddleName(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String oldName = theUser.getMiddleName();
    		String newName = theDatabase.getCurrentMiddleName();
         	// Input Validation
         	String validCheck = guiUserUpdate.Model.checkForValidNewName(newName);
         	if(validCheck == "" || validCheck.contains("You must enter a name.")) {
	           	theUser.setMiddleName(newName);
	        	if (newName == null || newName.length() < 1)label_CurrentMiddleName.setText("<none>");
	        	else label_CurrentMiddleName.setText(newName);
        	} else {
        		theDatabase.updateMiddleName(theUser.getUserName(), oldName);
        		Label middleNameError = new Label(validCheck);
        		middleNameError.setWrapText(true);
         		ErrorMessage.setTitle("Middle Name");
         		ErrorMessage.setHeaderText("Please enter a valid middle name");
         		ErrorMessage.getDialogPane().setContent(middleNameError);
         		ErrorMessage.showAndWait();
         	}
    	});
        
        // Last Name
        setupLabelUI(label_LastName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 310);
        setupLabelUI(label_CurrentLastName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 310);
        setupButtonUI(button_UpdateLastName, "Dialog", 18, 275, Pos.CENTER, 500, 300);
        button_UpdateLastName.setOnAction((event) -> {
        	result = dialogUpdateLastName.showAndWait();
    		result.ifPresent(name -> theDatabase.updateLastName(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String oldName = theUser.getLastName();
    		String newName = theDatabase.getCurrentLastName();
         	// Input Validation
         	String validCheck = guiUserUpdate.Model.checkForValidNewName(newName);
         	if(validCheck == "") {
	           	theUser.setLastName(newName);
	           	if (newName == null || newName.length() < 1)label_CurrentLastName.setText("<none>");
	        	else label_CurrentLastName.setText(newName);
         	} else {
        		theDatabase.updateLastName(theUser.getUserName(), oldName);
        		Label lastNameError = new Label(validCheck);
        		lastNameError.setWrapText(true);
         		ErrorMessage.setTitle("Last Name");
         		ErrorMessage.setHeaderText("Please enter a valid last name");
         		ErrorMessage.getDialogPane().setContent(lastNameError);
         		ErrorMessage.showAndWait();
         	}
    	});
        
        // Preferred First Name
        setupLabelUI(label_PreferredFirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 360);
        setupLabelUI(label_CurrentPreferredFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 360);
        setupButtonUI(button_UpdatePreferredFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 350);
        button_UpdatePreferredFirstName.setOnAction((event) -> {
        	result = dialogUpdatePreferredFirstName.showAndWait();
    		result.ifPresent(name -> theDatabase.updatePreferredFirstName(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String oldName = theUser.getPreferredFirstName();
    		String newName = theDatabase.getCurrentPreferredFirstName();
         	// Input Validation
         	String validCheck = guiUserUpdate.Model.checkForValidNewName(newName);
         	if(validCheck == "") {
	           	theUser.setPreferredFirstName(newName);
	         	if (newName == null || newName.length() < 1)label_CurrentPreferredFirstName.setText("<none>");
	        	else label_CurrentPreferredFirstName.setText(newName);
         	} else {
        		theDatabase.updatePreferredFirstName(theUser.getUserName(), oldName);
        		Label preferredNameError = new Label(validCheck);
        		preferredNameError.setWrapText(true);
         		ErrorMessage.setTitle("Preferred First Name");
         		ErrorMessage.setHeaderText("Please enter a valid preferred first name");
         		ErrorMessage.getDialogPane().setContent(preferredNameError);
         		ErrorMessage.showAndWait();
         	}
     	});
        
        // Email Address
        setupLabelUI(label_EmailAddress, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 410);
        setupLabelUI(label_CurrentEmailAddress, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 410);
        setupButtonUI(button_UpdateEmailAddress, "Dialog", 18, 275, Pos.CENTER, 500, 400);
        button_UpdateEmailAddress.setOnAction((event) -> {
        	result = dialogUpdateEmailAddresss.showAndWait();
    		result.ifPresent(name -> theDatabase.updateEmailAddress(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String oldEmail = theUser.getEmailAddress();
    		String newEmail = theDatabase.getCurrentEmailAddress();
    		// Input Validation
    		String validCheck = guiUserUpdate.Model.checkForValidEmailAddress(newEmail);
    		if(validCheck == "" && !theDatabase.emailaddressHasBeenUsed(newEmail)) {
	           	theUser.setEmailAddress(newEmail);
	        	label_CurrentEmailAddress.setText(newEmail);
    		} else if (theDatabase.emailaddressHasBeenUsed(newEmail)) {
    			theDatabase.updateEmailAddress(theUser.getUserName(), oldEmail);
        		Label emailError = new Label("This email address is already being used, please try again.");
        		emailError.setWrapText(true);
         		ErrorMessage.setTitle("Email Address");
         		ErrorMessage.setHeaderText("Please enter a different email address");
         		ErrorMessage.getDialogPane().setContent(emailError);
         		ErrorMessage.showAndWait();
    		} else {
        		theDatabase.updateEmailAddress(theUser.getUserName(), oldEmail);
        		Label emailError = new Label(validCheck);
        		emailError.setWrapText(true);
         		ErrorMessage.setTitle("Email Address");
         		ErrorMessage.setHeaderText("Please enter a valid email address");
         		ErrorMessage.getDialogPane().setContent(emailError);
         		ErrorMessage.showAndWait();
         	}
 		});
        
        	
        // Set up the button to proceed to this user's home page
	    setupButtonUI(button_ProceedToUserHomePage, "Dialog", 18, 300, Pos.CENTER, width/2-150, 450);
	    button_ProceedToUserHomePage.setOnAction((event) -> {
	    	if(label_CurrentFirstName.getText() != "<none, required>" && 
	    			label_CurrentEmailAddress.getText() != "<none, required>") {
	        	ControllerUserUpdate.goToUserHomePage(theStage, theUser);
	        } else {
	        	ErrorMessage.setTitle("Finish Account Setup");
	        	ErrorMessage.setHeaderText("Finish setting up your account");
	        	if(label_CurrentFirstName.getText() == "<none, required>") {
	        		Label missingFirstName = new Label("Please enter a first name.");
	        		missingFirstName.setWrapText(true);
	        		ErrorMessage.getDialogPane().setContent(missingFirstName);
	        		ErrorMessage.showAndWait();
	        	}
	        	if(label_CurrentEmailAddress.getText() == "<none, required>") {
	        		Label missingEmailAddress = new Label("Please enter an email address.");
	        		missingEmailAddress.setWrapText(true);
	        		ErrorMessage.getDialogPane().setContent(missingEmailAddress);
	        		ErrorMessage.showAndWait();
	        	}	
	        }
	    });
	        
	    // Set up the logout button
	    setupButtonUI(button_Logout, "Dialog", 18, 200, Pos.CENTER, width/2-100, 500);
	    button_Logout.setOnAction((event) -> {
	    	if(label_CurrentFirstName.getText() != "<none, required>" && 
	    			label_CurrentEmailAddress.getText() != "<none, required>") {
	    		ControllerUserUpdate.performLogout();
	        } else {
	        	ErrorMessage.setTitle("Finish Account Setup");
	        	ErrorMessage.setHeaderText("Finish setting up your account");
	        	if(label_CurrentFirstName.getText() == "<none, required>") {
	        		Label missingFirstName = new Label("Please enter a first name.");
	        		missingFirstName.setWrapText(true);
	        		ErrorMessage.getDialogPane().setContent(missingFirstName);
	        		ErrorMessage.showAndWait();
	        	}
	        	if(label_CurrentEmailAddress.getText() == "<none, required>") {
	        		Label missingEmailAddress = new Label("Please enter an email address.");
	        		missingEmailAddress.setWrapText(true);
	        		ErrorMessage.getDialogPane().setContent(missingEmailAddress);
	        		ErrorMessage.showAndWait();
	        	}
	        }
	    	
	    });        
    	
        // Populate the Pane's list of children widgets
        theRootPane.getChildren().addAll(
        		label_ApplicationTitle, label_Purpose, label_Username,
        		label_CurrentUsername, 
        		label_Password, label_CurrentPassword, 
        		button_UpdatePassword, 
        		label_FirstName, label_CurrentFirstName, button_UpdateFirstName,
        		label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName,
        		label_LastName, label_CurrentLastName, button_UpdateLastName,
        		label_PreferredFirstName, label_CurrentPreferredFirstName,
        		button_UpdatePreferredFirstName, button_UpdateEmailAddress,
        		label_EmailAddress, label_CurrentEmailAddress, 
        		button_ProceedToUserHomePage, button_Logout);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}

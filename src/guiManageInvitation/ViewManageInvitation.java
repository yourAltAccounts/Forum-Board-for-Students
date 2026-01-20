package guiManageInvitation;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;



public class ViewManageInvitation {
	

	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();

	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	protected static Label label_SelectUser = new Label("Select an email invitation to be updated:");
	protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();
	
	protected static List<String> addList = new ArrayList<String>();
	protected static Button button_deadline = new Button("Set Deadline");
	protected static List<String> removeList = new ArrayList<String>();
	protected static Button button_RemoveRole = new Button("Remove Invitation");
	protected static Label label_CurrentRoles = new Label("This invitaion deadline:");
	protected static Label label_SelectRoleToBeAdded = new Label("Set deadline of invitation:");
	protected static ComboBox <String> combobox_SelectRoleToAdd = new ComboBox <String>();	
	protected static Label label_SelectRoleToBeRemoved = new Label("Confirm remove invitaion:");
	protected static ComboBox <String> combobox_SelectRoleToRemove = new ComboBox <String>();

	protected static Line line_Separator4 = new Line(20, 525, width-20,525);

	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewManageInvitation theView;	
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theAddRemoveRolesScene = null;	// The Scene each invocation populates
	protected static String theSelectedUser = "";	// The user whose roles are being updated
	protected static String theAddRole = "";		// The role being added
	protected static String theRemoveRole = "";		// The roles being removed

	public static void displayManageInvitation(Stage ps, User user) {
		theStage = ps;
	    theUser = user;

	    if (theView == null) theView = new ViewManageInvitation();

	    label_UserDetails.setText("User: " + theUser.getUserName()); 
	    ModelManageInvitation.setupNewInvitationArrays(); 

	    ControllerManageInvitation.doSelectUser();
	    ControllerManageInvitation.repaintTheWindow();
	}

	public ViewManageInvitation() {
		
		theRootPane = new Pane();
		theAddRemoveRolesScene = new Scene(theRootPane, width, height);

		label_PageTitle.setText("Invitation Management Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		
		// GUI Area 2a
		setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		
		setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 380, 125);
		List<String> invitationList = theDatabase.getInvitationList();
		combobox_SelectUser.setItems(FXCollections.observableArrayList(invitationList));
		combobox_SelectUser.getSelectionModel().select(0);
		
		combobox_SelectUser.getSelectionModel().selectedItemProperty()
	    .addListener(ControllerManageInvitation.userSelectionListener);
		
		// GUI Area 2b
		setupLabelUI(label_CurrentRoles, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 170);	
		setupLabelUI(label_SelectRoleToBeAdded, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 210);
		setupComboBoxUI(combobox_SelectRoleToAdd, "Dialog", 16, 150, 280, 205);
		setupButtonUI(button_deadline, "Dialog", 16, 150, Pos.CENTER, 460, 205);
		ViewManageInvitation.button_deadline.setOnAction((event) -> 
			{ControllerManageInvitation.performDeadline(); });
		setupButtonUI(button_RemoveRole, "Dialog", 16, 150, Pos.CENTER, 460, 275);			
		ViewManageInvitation.button_RemoveRole.setOnAction((event) -> 
			{ControllerManageInvitation.performRemoveInvitation(); });
		setupLabelUI(label_SelectRoleToBeRemoved, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 280);	
		setupComboBoxUI(combobox_SelectRoleToRemove, "Dialog", 16, 150, 280, 275);	
		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerManageInvitation.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((event) -> {ControllerManageInvitation.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((event) -> {ControllerManageInvitation.performQuit(); });
		
	}	

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}

	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}

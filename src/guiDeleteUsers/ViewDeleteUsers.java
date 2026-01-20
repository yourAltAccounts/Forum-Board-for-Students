package guiDeleteUsers;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
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

public class ViewDeleteUsers {
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	protected static Label label_SelectUser = new Label("Select a user to be delete:");
	protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();

	protected static List<String> removeList = new ArrayList<String>();
	protected static Button button_RemoveRole = new Button("Remove User");
	protected static Label label_CurrentRoles = new Label("This user's current roles:");
	
	protected static Label label_SelectRoleToBeRemoved = new Label("Confirm removed the user:");
	protected static ComboBox <String> combobox_SelectRoleToRemove = new ComboBox <String>();

	protected static Line line_Separator4 = new Line(20, 525, width-20,525);

	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewDeleteUsers theView;	

	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			
	protected static Pane theRootPane;			
	protected static User theUser;				
	
	public static Scene theAddRemoveRolesScene = null;	
	protected static String theSelectedUser = "";	
	protected static String theRemoveUser = "";		

	public static void displayDeleteUsers(Stage ps, User user) {

		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewDeleteUsers();
		
		ControllerDeleteUsers.repaintTheWindow();
		ControllerDeleteUsers.doSelectUser();
	}

	
	public ViewDeleteUsers() {

		theRootPane = new Pane();
		theAddRemoveRolesScene = new Scene(theRootPane, width, height);

		label_PageTitle.setText("Delete User Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		
		setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);
		List<String> userList = theDatabase.getUserList();	
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);
		combobox_SelectUser.getSelectionModel().selectedItemProperty()
    	.addListener((ObservableValue<? extends String> observable, 
    		String oldvalue, String newValue) -> {ControllerDeleteUsers.doSelectUser();});

		setupLabelUI(label_CurrentRoles, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 170);	
		
		setupButtonUI(button_RemoveRole, "Dialog", 16, 150, Pos.CENTER, 460, 275);			
		ViewDeleteUsers.button_RemoveRole.setOnAction((event) -> 
			{ControllerDeleteUsers.performRemoveUser(); });
		setupLabelUI(label_SelectRoleToBeRemoved, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 280);	
		setupComboBoxUI(combobox_SelectRoleToRemove, "Dialog", 16, 150, 280, 275);	
	
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerDeleteUsers.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((event) -> {ControllerDeleteUsers.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((event) -> {ControllerDeleteUsers.performQuit(); });
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

package guiRequestSystem; 

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.Request;
import entityClasses.User;
import javafx.collections.ObservableList;
import database.Database;

/**
 * <p> Title: ViewRequestSystem Class </p>
 * <p> Description: Unified Staff/Admin interface for Request Management and simple Home Page.
 * Handles UI display and forwards all user input/actions directly to the ControllerRequestSystem. </p>
 * <p> Copyright: CSE 360 Team © 2025 </p>
 *
 * @author Refactored to match provided snippet functionality
 * @version 1.05    2025-11-28 Corrected class name to ViewRequestSystem and updated controller references.
 */
public class ViewRequestSystem { 
	
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    //Page attributes
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
    protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);

    //attribute for button to go to home gui
    protected static Button button_BackToHome = new Button("Back to Home"); 
    //request attributes 
    protected static Label label_Area2Title = new Label("Active Requests"); 
    protected static ListView<Request> listView_Requests = new ListView<>();      
    protected static TextArea textArea_RequestDescription = new TextArea();
    protected static TextArea textArea_AdminAction = new TextArea();              
    protected static Button button_SubmitNewRequest = new Button("Submit New Request"); 
    protected static Button button_CloseRequest = new Button("Close Request"); 
    protected static Button button_ToggleClosedView = new Button("View Closed Requests");
    protected static Button button_ReopenRequest = new Button("Reopen Request"); // For Staff on Closed View
    protected static TextArea textArea_OriginalAdminAction = new TextArea(); // For history details
    
    protected static VBox requestVBox; 
    protected static HBox buttonHBox; 
    
	// Internal/Shared Attributes
	private static ViewRequestSystem theView;		
	private static Database theDatabase = applicationMain.FoundationsMain.database; // Accessing the static database field
	protected static Stage theStage;			
	protected static Pane theRootPane;			
	protected static User theUser;				
	private static Scene theRequestSystemScene; // Renamed for clarity
	protected static final int theRole = 3;		//staff

	/**
	 * <p> Method: displayRequestSystem(Stage ps, User user) </p>
	 *
	 * <p>Description: The single method to initialize and display the 
	 * Staff/Admin Request Management System GUI. </p>
	 *
	 * <p> In short these will do the following
	 * 1. Initializes the View if needed.
	 * 2. Calls the Controller to load data and set up handlers.
	 * 3. Updates the UI with the current User's details and role.
	 * 4. Shows the initial simple Home Page view.
	 * 5. Sets the Scene onto the Stage and displays the window.
	 * </p>
	 *
	 * @param ps The JavaFX Stage 
	 * @param user The currently logged-in User object.
	 */
	public static void displayRequestSystem(Stage ps, User user) { 
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewRequestSystem();		
		
        // Controller initialization handles data loading
        ControllerRequestSystem.initializeController(theStage, theUser); 
		
		theDatabase.getUserAccountDetails(user.getUserName()); 
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
        String roleDisplay = theUser.getAdminRole() ? "Admin" : 
                             (theUser.getNewRole2() ? "Staff" : "User");
		label_UserDetails.setText("User: " + theUser.getUserName() + " (Role: " + roleDisplay + ")");

        // Initial view is always the simple home page
		ControllerRequestSystem.toggleRequestView(true);

		theStage.setTitle("CSE 360 Foundation: Request Management System");
		theStage.setScene(theRequestSystemScene);						
		theStage.show();											
	}
	/**
	 * 
	 * <p> Constructor: ViewRequestSystem() </p>
	 *
	 * <p> Description: Private constructor used once to initialize the singleton 
	 * instance. It sets up the main Scene, configures all fixed GUI components 
	 * (labels, buttons, layouts), and assigns all user actions to delegate directly 
	 * to the ControllerRequestSystem. </p>
	 * * @see displayRequestSystem(Stage, User)
	 */
	private ViewRequestSystem() { 
		theRootPane = new Pane();
		theRequestSystemScene = new Scene(theRootPane, width, height);	
		
        // Setup
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        label_PageTitle.setText("Staff Request System"); // Initial title for the home view
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		// Buttons and events (Delegating to ControllerRequestSystem)
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, width - 190, 45); 
		button_UpdateThisUser.setOnAction((event) -> {ControllerRequestSystem.performUpdate(); });
		
		setupButtonUI(button_Logout, "Dialog", 18, 300, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerRequestSystem.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 300, Pos.CENTER, 350, 540);
        button_Quit.setOnAction((event) -> {ControllerRequestSystem.performQuit(); });
        
        // request management setup gui
        setupRequestManagementUI();
        
        setupButtonUI(button_ToggleClosedView, "Dialog", 16, 170, Pos.CENTER, width / 2 + 190, 100); 
        button_ToggleClosedView.setOnAction((event) -> {
            ControllerRequestSystem.toggleViewRequests(); 
        });
        
        // Back to Home Button Setup 
        setupButtonUI(button_BackToHome, "Dialog", 18, 100, Pos.CENTER, 657, 540);
        button_BackToHome.setOnAction((event) -> { ControllerRequestSystem.performBack(); });
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
            button_BackToHome,        // Back Button
            requestVBox,              // Request Management Container
            button_ToggleClosedView,
	        line_Separator4, button_Logout, button_Quit);
	}

    /**
     * Sets up the ListView, the detail VBox, and all associated controls.
     */
    private void setupRequestManagementUI() {
        //List view and right panels setup
        listView_Requests.setItems(ControllerRequestSystem.getRequestList()); 
        listView_Requests.setPrefSize(width / 2 - 40, 380); 
        listView_Requests.setLayoutX(20);
        listView_Requests.setLayoutY(140);
        
        //Listener for input in the action text description
        listView_Requests.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            showRequestDetails(newSelection);
        });
        
        // Column setup rightside
        textArea_RequestDescription.setWrapText(true);
        textArea_RequestDescription.setPromptText("Request Description");
        textArea_RequestDescription.setEditable(false);
        textArea_RequestDescription.setPrefHeight(75);

        // History dispaly setup
        textArea_OriginalAdminAction.setWrapText(true);
        textArea_OriginalAdminAction.setPromptText("Original Closed Request admin action will appear here if the selected request was reopened.");
        textArea_OriginalAdminAction.setEditable(false);
        textArea_OriginalAdminAction.setPrefHeight(75); 
        

        // textArea_AdminAction is used for both input and viewing closed action notes
        textArea_AdminAction.setWrapText(true);
        textArea_AdminAction.setPrefHeight(75);
        
        // Setup Buttons and HBox
        setupButtonUI(button_SubmitNewRequest, "Dialog", 14, 150, Pos.CENTER, 0, 0);
        setupButtonUI(button_CloseRequest, "Dialog", 14, 150, Pos.CENTER, 0, 0);
        setupButtonUI(button_ReopenRequest, "Dialog", 14, 150, Pos.CENTER, 0, 0);
        button_ReopenRequest.setTranslateX(-100);
        buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().addAll(button_SubmitNewRequest, button_CloseRequest, button_ReopenRequest); 
        
        // Actions (Delegating to ControllerRequestSystem)
        button_SubmitNewRequest.setOnAction(e -> {
            ControllerRequestSystem.submitNewRequest(textArea_AdminAction.getText());
            textArea_AdminAction.clear();
        });
        button_CloseRequest.setOnAction(e -> {
            Request selectedRequest = listView_Requests.getSelectionModel().getSelectedItem();
            ControllerRequestSystem.closeSelectedRequest(selectedRequest, textArea_AdminAction.getText());
            textArea_AdminAction.clear();
        });
        button_ReopenRequest.setOnAction(e -> {
            Request selectedRequest = listView_Requests.getSelectionModel().getSelectedItem();
            String newDescription = textArea_AdminAction.getText();
            ControllerRequestSystem.reopenSelectedRequest(selectedRequest, newDescription);
            textArea_AdminAction.clear();
        });
        
        // VBox to hold all rightside elements
        requestVBox = new VBox(5);
        requestVBox.setLayoutX(width / 2 + 10);
        requestVBox.setLayoutY(140);
        requestVBox.setPrefWidth(width / 2 - 30);
        requestVBox.getChildren().addAll(
            label_Area2Title, 
            new Label("Full Description:"), 
            textArea_RequestDescription, 
            
            new Label("Original Closed Request Action:"), // Title for the history
            textArea_OriginalAdminAction,              // The TextArea itself 
            
            new Label("Insert Input(Admin: What has been done?, Staff: What needs fixing?):"), 
            textArea_AdminAction, 
            buttonHBox
        );
        theRootPane.getChildren().addAll(listView_Requests); 
    }

 

    /**
     * This method formats the data directly from the Request object.
     * @param newSelection The selected Request object, or null to clear the details.
     */
    protected static void showRequestDetails(Request newSelection) {
        if (newSelection != null) {
            // getters from request class
            textArea_RequestDescription.setText(newSelection.getDescription());
            
            // Set Admin Action/Input Area based on Status
            if ("CLOSED".equalsIgnoreCase(newSelection.getStatus())) {
                textArea_AdminAction.setText(newSelection.getAdminAction() != null ? newSelection.getAdminAction() : "No admin action documented.");
            } else {
                textArea_AdminAction.clear();
            }
            
            // history of requests
            Integer originalId = newSelection.getOriginalClosedRequestId();
            if (originalId != null && originalId > 0) {
                // Delegate fetching original request details to the Controller
                Request originalRequest = ControllerRequestSystem.getOriginalClosedRequest(originalId);
                
                if (originalRequest != null) {
                    textArea_OriginalAdminAction.setText(
                        "Original ID: " + originalId + "\n" +
                        "Closed By: " + originalRequest.getClosedByAdmin() + "\n" +
                        "Admin Action: " + originalRequest.getAdminAction()
                    );
                } else {
                    textArea_OriginalAdminAction.setText("Error fetching original request details.");
                }
            } else {
                textArea_OriginalAdminAction.clear(); 
                textArea_OriginalAdminAction.setPromptText("No original closed request link found.");
            }
            
        } else {
            // Clear all fields on 
            textArea_RequestDescription.clear();
            textArea_AdminAction.clear();
            textArea_OriginalAdminAction.clear(); 
        }
        
        // once done set updated visibility
        ControllerRequestSystem.updateButtonVisibilityOnSelect();
        
        
    }
    
    /**
     * Toggles the visibility of the Staffpage elements or the Request Management elements.
     * Called by ControllerRequestSystem.toggleRequestView().
     * @param showRequestView True to show request management, false to show simple home page.
     * @param isAdmin Shows specific buttons to and Admin
     */
    public static void updateViewVisibility(boolean showRequestView, boolean isAdmin) {
       
        // Request Management elements
        button_BackToHome.setVisible(showRequestView);
        requestVBox.setVisible(showRequestView);
        listView_Requests.setVisible(showRequestView);
        button_ToggleClosedView.setVisible(showRequestView);
        
        // Role-specific visibility (only if showRequestView is true)
        if (showRequestView) {
            updateRoleSpecificWidgets(isAdmin);
        }
    }
    
    /**
     * Updates the text on the toggle button and the view title when switching between 
     * Active and Closed lists.
     * @param isViewingClosed True if the ListView is currently showing closed requests.
     */
    public static void updateToggleUI(boolean isViewingClosed) {
        textArea_AdminAction.setEditable(true); 
        
        if (isViewingClosed) {
            button_ToggleClosedView.setText("View Active Requests");
            label_Area2Title.setText("Closed Admin Requests");
            
            if (!theUser.getAdminRole()) {
                // Staff viewing CLOSED list
                button_SubmitNewRequest.setVisible(false);
                button_CloseRequest.setVisible(false);
                button_ReopenRequest.setVisible(true); 
                textArea_AdminAction.setPromptText("Staff: Enter updated description to REOPEN selected request.");
            } else {
                // Admin viewing CLOSED list
                button_ReopenRequest.setVisible(false); 
                button_CloseRequest.setVisible(false);
                button_SubmitNewRequest.setVisible(false);
                textArea_AdminAction.setPromptText("View Mode: Request is CLOSED.");
                textArea_AdminAction.setEditable(false); 
            }
            
        } else {
            button_ToggleClosedView.setText("View Closed Requests");
            // Maintain the role-specific title and buttons for Active View
            updateRoleSpecificWidgets(theUser.getAdminRole()); 
            button_ReopenRequest.setVisible(false); 
        }
    }
    
    /**
     * Updates visibility and text of role-specific widgets based on the logged-in user.
     * @param isAdmin True if the current user is an Admin (Role 1).
     */
    public static void updateRoleSpecificWidgets(boolean isAdmin) {
        if (isAdmin) {
            button_SubmitNewRequest.setVisible(false);
            button_CloseRequest.setVisible(true);
            button_ReopenRequest.setVisible(false); 
            textArea_AdminAction.setPromptText("Admin: Enter notes to close selected request.");
            label_Area2Title.setText("Active Admin Requests (Admin View)");
            textArea_AdminAction.setEditable(true); 
        } else {
            button_SubmitNewRequest.setVisible(true);
            button_CloseRequest.setVisible(false);
            button_ReopenRequest.setVisible(false); 
            textArea_AdminAction.setPromptText("Staff: Enter description for a NEW request.");
            label_Area2Title.setText("Active Admin Requests (Staff View)");
            textArea_AdminAction.setEditable(true); 
        }
    }
    
    /**
     * Called by the Controller to refresh the ListView with new data.
     * @param data The ObservableList of Requests to display.
     */
    public static void refreshRequestList(ObservableList<Request> data) 
    {
        listView_Requests.setItems(data);
    }
	//Helper methods that were taken from original code
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}

package guiRequestSystem;

import database.Database;
import entityClasses.Request;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;


/**
 * <p> Title: ControllerRequestSystem Class </p>
 * <p> Description: Controller component for the Request Management System. 
 * Manages the flow of data between the {@code ModelRequestSystem} and 
 * {@code ViewRequestSystem}, handling user actions like submitting, closing,
 * reopening, and filtering requests. </p>
 * <p> Copyright: CSE 360 Team © 2025 </p>
 *
 * @author Generated for Request System Feature
 * @version 1.01    2025-11-28 Navigation and History logic completed
 */
public class ControllerRequestSystem {
	
	 private static ModelRequestSystem theModel;
	    private static ObservableList<Request> requestList;
	    private static User currentUser; // Store the user to pass to the model and check role
	    private static boolean isViewingClosed = false;
	    
	    /**
	     * Prevents instantiation of this static utility class.
	     */
	    private ControllerRequestSystem() {
	        // Utility class only uses static methods.
	    }
	    /**
	     * Toggles the state between viewing Active and Closed requests.
	     */
	    public static void toggleViewRequests() {
	        isViewingClosed = !isViewingClosed; // Flip the state
	        
	        requestList.clear();
	        
	        if (isViewingClosed) {
	            // Load closed requests
	            requestList.addAll(theModel.getClosedRequests()); 
	        } else {
	            // Load active requests 
	            loadRequestData();
	        }
	        
	        // Update the View's title and button text
	        ViewRequestSystem.updateToggleUI(isViewingClosed);
	        ViewRequestSystem.refreshRequestList(requestList);
	    }
	    /**
	     * Initializes the Controller, Model, and loads initial data when the page is displayed.
	     * @param stage The primary JavaFX stage for scene switching.
	     * @param user The currently logged-in User object.
	     */
	    public static void initializeController(Stage stage, User user) {
	        // Set current user
	        currentUser = user; 
	        
	        //Get database reference
	        Database theDatabase = applicationMain.FoundationsMain.database; 
	        
	        // Initialize the Model
	        theModel = new ModelRequestSystem(theDatabase, currentUser);
	        
	        // Initialize the ObservableList
	        requestList = FXCollections.observableArrayList();
	        
	        // Load initial data
	        loadRequestData();
	    }
	    
	    /**
	     * Toggles visibility between the simple Home Page and the Request Management View.
	     * This is called by the Open/Back buttons in the View.
	     * @param showRequestView True to show request management, false to show simple home page.
	     */
	    public static void toggleRequestView(boolean showRequestView) {
	        // 1. Update the view visibility based on the flag and the user's role
	        ViewRequestSystem.updateViewVisibility(showRequestView, currentUser.getAdminRole()); 
	        
	        // 2. If we are entering the request view, make sure we reload the data
	        if (showRequestView) {
	            loadRequestData();
	        }
	    }
	    
	    /**
	     * Fetches active requests from the Model and updates the ObservableList and the View.
	     */
	    private static void loadRequestData() {
	        requestList.clear();
	        requestList.addAll(theModel.getActiveRequests());
	        // Tell the View to refresh its ListView
	        ViewRequestSystem.refreshRequestList(requestList);
	    }

	    /**
	     * Returns the ObservableList of Requests to the View for binding to the ListView.
	     * @return The ObservableList of Request objects.
	     */
	    public static ObservableList<Request> getRequestList() {
	        return requestList;
	    }

	    // --- Role-Specific Request Actions ---

	    /**
	     * Handles the staff action to submit a new request.
	     * @param description The textual description of the new request.
	     */
	    public static void submitNewRequest(String description) {
	        if (description != null && !description.trim().isEmpty()) {
	            // Model calls Database.createRequest() using the current user's name
	            int newId = theModel.createNewRequest(description.trim());
	            if (newId != -1) {
	                loadRequestData(); // Reload list to show the new request
	            } 
	        }
	    }
	    
	    /**
	     * Handles the admin action to close a selected request.
	     * @param selectedRequest The Request object to be closed.
	     * @param adminAction The notes provided by the admin detailing the closure.
	     */
	    public static void closeSelectedRequest(Request selectedRequest, String adminAction) {
	        if (selectedRequest != null) {
	            // Model calls Database.closeRequest() 
	            boolean success = theModel.closeRequest(selectedRequest, adminAction);
	            if (success) {
	                loadRequestData(); // Reload list to remove the closed request
	            } 
	        }
	    }
	 // Inside ControllerRole2Home.java

	    /**
	     * Loads and displays the list of closed requests from the database into the ListView.
	     * This method is superseded by toggleViewRequests().
	     */
	 public static void toggleViewToClosedRequests() {
	     requestList.clear();
	     
	     // Call the new database method via the Model
	     requestList.addAll(theModel.getClosedRequests()); 
	     
	     
	 }

	 /**
	  * Loads and displays the list of active (pending/reopened) requests.
	  * This method is superseded by calling loadRequestData() directly or 
	  * using toggleViewRequests().
	  */
	 public static void toggleViewToActiveRequests() {
	     loadRequestData(); // This loads PENDING/REOPENED requests
	     
	 }

	 /**
		* Handles the staff action to reopen a closed request and update its description.
		* @param selectedRequest The original closed Request object to be reopened.
		* @param newDescription The updated description for the newly created active request.
		*/
	public static void reopenSelectedRequest(Request selectedRequest, String newDescription) {
	  //  must have a selected request and non-empty new description
	  if (selectedRequest == null) {
	      // No request selected 
	      System.err.println("Error: No request selected for reopening.");
	      return; 
	  }
	  if (newDescription == null || newDescription.trim().isEmpty()) {
	      // Description is required for reopening
	      System.err.println("Error: A new description is required to reopen the request.");
	      return;
	  }

	  // Model call to perform the reopening logic
	  int newId = theModel.reopenRequest(selectedRequest.getRequestId(), newDescription.trim());
	  
	  // Update the view
	  if (newId != -1) {
	      // Reopened requests become 'active' again, so we switch the view to 'active'
		  toggleViewRequests(); // Loads active list and resets UI state
	  } else {
	      // Database operation failed 
	      System.err.println("Error: Failed to reopen request in the database.");
	  }
	}

	/**
	* Retrieves the original closed request details linked to the currently selected request.
	* The View will call this when a request is selected to display the history link.
	* @param originalId The originalClosedRequestId from the selected Request object.
	* @return The original closed Request object, or null if no link exists.
	*/
	public static Request getOriginalClosedRequest(int originalId) {
	 // Delegates the call to the Model
	 return theModel.getOriginalClosedRequest(originalId);
	}
	protected static void performBack() {
	    
	    // Get the current User object from the static View attribute
	    User user = ViewRequestSystem.theUser; 
	    
	    // The ViewRequestSystem's static stage
	    Stage stage = ViewRequestSystem.theStage;
	    
	    //fetch some of the user data
	    if (applicationMain.FoundationsMain.database.getUserAccountDetails(user.getUserName())) {
	        // Rebuild user object 
	        User updatedUser = new User(
	        		user.getUserName(), 
		            applicationMain.FoundationsMain.database.getCurrentPassword(), 
		            applicationMain.FoundationsMain.database.getCurrentFirstName(), 
		            applicationMain.FoundationsMain.database.getCurrentMiddleName(), 
		            applicationMain.FoundationsMain.database.getCurrentLastName(), 
		            applicationMain.FoundationsMain.database.getCurrentPreferredFirstName(), 
		            applicationMain.FoundationsMain.database.getCurrentEmailAddress(), 
		            applicationMain.FoundationsMain.database.getCurrentAdminRole(), 
		            applicationMain.FoundationsMain.database.getCurrentNewRole1(), 
		            applicationMain.FoundationsMain.database.getCurrentNewRole2()
	        );
	        
	        // This performs the scene switch back to the original Home Page
	        guiRole2.ViewRole2Home.displayRole2Home(stage, updatedUser);
	        
	    } else {
	         // Fallback if database fetch fails, still try to switch scenes with the user object
	         guiRole2.ViewRole2Home.displayRole2Home(stage, user);
	    }
	}
	/**
	 * Updates the visibility of action buttons in the View based on the current 
	 * list state (Active or Closed) after a request has been selected.
	 */
	public static void updateButtonVisibilityOnSelect() {
	    ViewRequestSystem.updateToggleUI(isViewingClosed);
	}
		
	    //function to Update,logout,and quit, Basic functionality taken from original View code
		
		protected static void performUpdate () {
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRequestSystem.theStage, currentUser);
		}	

		
		protected static void performLogout() {
			guiUserLogin.ViewUserLogin.displayUserLogin(ViewRequestSystem.theStage);
		}
		
		protected static void performQuit() {
			System.exit(0);
		}

}

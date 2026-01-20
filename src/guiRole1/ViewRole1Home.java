package guiRole1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import entityClasses.Post;
import entityClasses.PostList; //added

import java.util.List;



/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Role1 Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

public class ViewRole1Home {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH+250;	//+250 is an arbitrary value to see the rest of buttons on the right side of the screen
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT+75; //+75 is an arbitrary value to see the bottom buttons
	


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	protected static TextField tfSearch = new TextField();
	protected static Button btnSearch = new Button("Search");
	protected static Button btnClearSearch = new Button("Clear");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 105, width-20, 105);

	// GUI ARea 2: This is a stub, so there are no widgets here.  For an actual role page, this are
	// would contain the widgets needed for the user to play the assigned role.
	protected static VBox leftPanel;
	protected static VBox centerPanel;
	protected static ToggleGroup threadToggleGroup;
	protected static RadioButton rbAll, rbGeneral, rbQuestions, rbMyPosts, rbUnread;
	protected static ListView<HBox> lvPosts;
	protected static Label lblFullGrade;
	
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 600, width-20,600);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for creating a new post, 
	// quitting the application, and for logging out.
	private static TextField tfTitle;
	private static TextArea taContent;
	private static ComboBox<String> cbThread;
	private static Runnable onPostCreated;
	protected static Button btnCreatePost = new Button("Create New Post");
	protected static Button btnRefresh = new Button("Refresh");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole1Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	

	private static Scene theViewRole1HomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Role1: 2; Role2: 3
	
	//static variable for the current thread
	private static String currentThread = "All";
	//static variable for the current Search text
	private static String currentSearchText = "";
	private static Button button_PrivateInbox = new Button("Private Inbox");
	private static Label label_InboxNotification = new Label();
	private static Button button_ViewGrades = new Button("View Grades");

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayRole1Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Role1 Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 * @param db grabs the database to grab user and forum info 
	 * 
	 */
	public static void displayRole1Home(Stage ps, User user, Database db) {
		theView = null;
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		theDatabase = db; // Set the database reference
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole1Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("Welcome: " + theUser.getUserName());
				
		loadPosts();
		// Set the title for the window, display the page, and wait for the Admin to do something
		updateInboxNotification();
		theStage.setTitle("CSE 360 Foundations: Student Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewRole1Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewRole1Home() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewRole1HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Student Discussion Forum");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		//label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 14, width, Pos.BASELINE_LEFT, 20, 35);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 860, 30);
		button_UpdateThisUser.setOnAction((event) ->
			{ViewUserUpdate.displayUserUpdate(theStage, theUser); 
		});
		
		// Search Bar (HBox for layout)
		HBox searchBox = createSearchBar();
		searchBox.setLayoutX(20);
		searchBox.setLayoutY(65);
		
		// GUI Area 2
		
		// This is a stub, so this area is empty
		BorderPane contentArea = new BorderPane();
		contentArea.setLayoutX(20);
		contentArea.setLayoutY(115); // Below separator
		contentArea.setPrefSize(width - 40, 480);

		// Left Panel: Thread Filters
		leftPanel = createThreadFilterPanel();
		contentArea.setLeft(leftPanel);

		// Center Panel: Posts List
		centerPanel = createPostsPanel();
		contentArea.setCenter(centerPanel);
		
		
		// GUI Area 3
		//added
		setupButtonUI(btnCreatePost, "Dialog", 18, 170, Pos.CENTER, 20, 620);
		btnCreatePost.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		btnCreatePost.setOnAction(e -> ControllerRole1Home.handleCreatePost());

		setupButtonUI(btnRefresh, "Dialog", 18, 100, Pos.CENTER, 200, 620);
		btnRefresh.setOnAction((event) -> {
			theStage.close();
		    ControllerRole1Home.handleRefreshPage();
		});
				
        setupButtonUI(button_PrivateInbox, "Dialog", 18, 170, Pos.CENTER, 320, 620);
        button_PrivateInbox.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        button_PrivateInbox.setOnAction(e -> {
            guiInbox.ViewPrivateInbox.displayPrivateInbox(theStage, theUser.getUserName());
        });
        setupLabelUI(label_InboxNotification, "Arial", 12, 100, Pos.CENTER_LEFT, 320, 600);
        label_InboxNotification.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        label_InboxNotification.setVisible(false);
        
        setupButtonUI(button_ViewGrades, "Dialog", 18, 170, Pos.CENTER, 500, 620);
        
        setupButtonUI(button_Logout, "Dialog", 18, 170, Pos.CENTER, 680, 620);
        button_Logout.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        button_Logout.setOnAction((event) -> {
        	theStage.close();
        	ControllerRole1Home.performLogout(); 
        });
        
        setupButtonUI(button_Quit, "Dialog", 18, 170, Pos.CENTER, 860, 620);
        button_Quit.setOnAction((event) -> {ControllerRole1Home.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().clear();
        theRootPane.getChildren().addAll(
        	    label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
        	    searchBox, contentArea, line_Separator4,
        	    btnCreatePost, btnRefresh, button_PrivateInbox, label_InboxNotification,  
        	    button_Logout, button_Quit);
        
        
}
	
	
	/**********
	 * <p> Method: createSearchBar() </p>
	 * 
	 * <p> Description: Create the format and display the search Bar including the buttons.</p>
	 * 
	 * @return searchBox object of the text,action,input, and layout of the search bar
	 * 
	 */
	private HBox createSearchBar() 
	{
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Label lblSearch = new Label("Search:");
        tfSearch.setPromptText("Enter keywords (space-separated)...");
        tfSearch.setPrefWidth(400);

        btnSearch.setOnAction(e -> performSearch());
        btnClearSearch.setOnAction(e -> clearSearch());

        searchBox.getChildren().addAll(lblSearch, tfSearch, btnSearch, btnClearSearch);
        return searchBox;
    }
	/**********
	 * <p> Method: createThreadFilterPanel() </p>
	 * 
	 * <p> Description: Create the format and display the thread filter including the 
	 * individual buttons for each thread (All Posts, All, General, Questions, 
	 * MyPosts, Unread).</p>
	 * 
	 * @return panel object of the text,action,input, and layout of the thread filter
	 * 
	 */
	private VBox createThreadFilterPanel() 
	{
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10, 15, 10, 0));
        panel.setPrefWidth(150);
        panel.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 1 0 0;"); 

        Label lblThreads = new Label("Filter by Thread:");
        lblThreads.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        threadToggleGroup = new ToggleGroup();

        rbAll = new RadioButton("All Posts");
        rbAll.setToggleGroup(threadToggleGroup);
        rbAll.setSelected(true);
        rbAll.setOnAction(e -> filterByThread("All"));

        rbGeneral = new RadioButton("General");
        rbGeneral.setToggleGroup(threadToggleGroup);
        rbGeneral.setOnAction(e -> filterByThread("General"));

        rbQuestions = new RadioButton("Questions");
        rbQuestions.setToggleGroup(threadToggleGroup);
        rbQuestions.setOnAction(e -> filterByThread("Questions"));

        rbMyPosts = new RadioButton("My Posts");
        rbMyPosts.setToggleGroup(threadToggleGroup);
        rbMyPosts.setOnAction(e -> filterByThread("MyPosts"));

        rbUnread = new RadioButton("Unread");
        rbUnread.setToggleGroup(threadToggleGroup);
        rbUnread.setOnAction(e -> filterByThread("Unread"));

        Separator separator = new Separator();
        
        Label lblGrade = new Label("Current Grade:");
        lblGrade.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        refreshGrade();

        panel.getChildren().addAll(
            lblThreads, rbAll, rbGeneral, rbQuestions, rbMyPosts, rbUnread, separator, lblGrade, lblFullGrade
        );
        return panel;
    }
	
	private static void refreshGrade() {
        int gradeNum = theDatabase.getOverallGrade(theUser.getUserName());
        String letterGrade = updateOverallGrade(gradeNum);
        lblFullGrade = new Label(letterGrade);
        lblFullGrade.setFont(Font.font("Impact", FontWeight.BOLD, 36));
        System.out.println(lblFullGrade.getText());
	}
	
	/**********
	 * <p> Method: createPostsPanel() </p>
	 * 
	 * <p> Description: Create the format and display the posts panel</p>
	 * 
	 * 
	 * @return panel object of the text, and layout of the posts panel
	 * 
	 */
	private VBox createPostsPanel() 
	{
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        Label lblPosts = new Label("Posts:");
        lblPosts.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        lvPosts = new ListView<>();
        lvPosts.setPrefHeight(450);
        VBox.setVgrow(lvPosts, Priority.ALWAYS);

        panel.getChildren().addAll(lblPosts, lvPosts);
        return panel;
    }
    
    /**********
	 * <p> Method: loadPosts() </p>
	 * 
	 * <p> Description: load the posts of the current user under specific categories
	 * , MyPosts are posts created by the user, Unread are the posts not yet seen, 
	 * All being every post in the forum.</p>
	 * 
	 * 
	 * 
	 */
	protected static void loadPosts() 
	{
        if (lvPosts == null || theUser == null) 
        	return;

        lvPosts.getItems().clear();

        //Call the Controller to retrieve the filtered and sorted posts
        PostList posts = ControllerRole1Home.handleLoadPosts(currentThread, currentSearchText);
        
        if (posts != null) 
        {
            List<Post> postList = posts.getAllPosts();

            if (postList.isEmpty()) 
            {
                Label lblEmpty = new Label("No posts found for the current filter/search.");
                lblEmpty.setFont(Font.font("Arial", 14));
                HBox emptyBox = new HBox(lblEmpty);
                emptyBox.setPadding(new Insets(20));
                lvPosts.getItems().add(emptyBox);
            } else 
            {
                for (Post post : postList) 
                {
                    HBox postItem = theView.createPostItem(post);
                    lvPosts.getItems().add(postItem);
                }
            }
        }
        
        refreshGrade();
    }
	/**********
	 * <p> Method: createPostItem() </p>
	 * 
	 * <p> Description: Builds a custom list for one post to be displayed in the main forum.
	 * It takes data from the Post object and arranges it in a particular way. Uses information
	 * such as the author, time, and thread.</p>
	 * 
	 * @param post the object needed to be displayed
	 * @return item which contains the post ready to be put into the list
	 * 
	 */
	private HBox createPostItem(Post post) 
	{
	    HBox item = new HBox(15);
	    item.setPadding(new Insets(10));
	    
	    // Different styling for deleted vs active posts
	    if (post.isDeleted()) {
	        item.setStyle("-fx-border-color: #ff9999; -fx-border-width: 2; -fx-background-color: #fff0f0;");
	    } else {
	        item.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
	    }

	    VBox postInfo = new VBox(5);
	    postInfo.setPrefWidth(600);

	    // Title - show [DELETED] for deleted posts
	    String titleText = post.isDeleted() ? 
	                      "[DELETED] " + post.getTitle() : 
	                      post.getTitle();
	    Label lblTitle = new Label(titleText);
	    lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	    lblTitle.setWrapText(true);
	    
	    if (post.isDeleted()) {
	        lblTitle.setStyle("-fx-text-fill: #d32f2f; -fx-font-style: italic;");
	    }

	    // Metadata - anonymize author if deleted
	    String metaText = String.format("by %s | %s | Thread: %s", 
	        post.isDeleted() ? "[deleted]" : post.getAuthor(), 
	        post.getFormattedTimestamp(),
	        post.getThread());
	    Label lblMeta = new Label(metaText);
	    lblMeta.setFont(Font.font("Arial", 12));
	    lblMeta.setStyle("-fx-text-fill: #666;");

	    // Preview - show removal message if deleted
	    String preview;
	    if (post.isDeleted()) {
	        preview = "[Content has been removed. Click View to see replies.]";
	    } else {
	        preview = post.getContent().length() > 100 ? 
	                 post.getContent().substring(0, 100) + "..." : 
	                 post.getContent();
	    }
	    Label lblPreview = new Label(preview);
	    lblPreview.setWrapText(true);
	    
	    if (post.isDeleted()) {
	        lblPreview.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
	    }

	    postInfo.getChildren().addAll(lblTitle, lblMeta, lblPreview);

	    // Reply count - ALWAYS show, even for deleted posts
	    int replyCount = ControllerRole1Home.handleGetReplyCount(post.getPostId());
	    VBox replyInfo = new VBox(5);
	    replyInfo.setAlignment(Pos.CENTER);
	    replyInfo.setPrefWidth(80);

	    Label lblReplyCount = new Label(String.valueOf(replyCount));
	    lblReplyCount.setFont(Font.font("Arial", FontWeight.BOLD, 18));
	    Label lblReplies = new Label("replies");
	    lblReplies.setFont(Font.font("Arial", 10));

	    replyInfo.getChildren().addAll(lblReplyCount, lblReplies);

	    VBox actions = new VBox(5);
	    actions.setAlignment(Pos.CENTER_RIGHT);

	    Button btnView = new Button("View");
	    btnView.setOnAction(e -> ControllerRole1Home.handleViewPost(post));

	    actions.getChildren().add(btnView);

	    // Only show delete button for own non-deleted posts
	    if (post.getAuthor().equals(theUser.getUserName()) && !post.isDeleted()) {
	        Button btnDelete = new Button("Delete");
	        btnDelete.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
	        btnDelete.setOnAction(e -> ControllerRole1Home.handleDeletePost(post));
	        actions.getChildren().add(btnDelete);
	    }

	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.ALWAYS);

	    item.getChildren().addAll(postInfo, spacer, replyInfo, actions);
	    return item;
	}
	
	/*
	 * 
	 */
    public static void setupPostCreation() {    	
        theStage = new Stage();
        theStage.initModality(Modality.APPLICATION_MODAL);
        theStage.setTitle("Create New Post");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(600);
        
        // Header
        Label lblHeader = new Label("Create New Post");
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Title field
        Label lblTitle = new Label("Title:*");
        tfTitle = new TextField();
        tfTitle.setPromptText("Enter post title...");
        
        // Thread selection
        Label lblThread = new Label("Thread:*");
        cbThread = new ComboBox<>();
        cbThread.getItems().addAll("General", "Questions");
        cbThread.setValue("General");
        cbThread.setPrefWidth(200);
        
        // Content field
        Label lblContent = new Label("Content:*");
        taContent = new TextArea();
        taContent.setPromptText("Enter your post content here...");
        taContent.setPrefHeight(250);
        taContent.setWrapText(true);
        
        Label lblCharCount = new Label("Characters: 0");
        taContent.textProperty().addListener((obs, oldText, newText) -> {
            lblCharCount.setText("Characters: " + newText.length());
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancel, btnSubmit;
        btnCancel = new Button("Cancel");
        btnCancel.setOnAction(e -> theStage.close());
        
        btnSubmit = new Button("Submit Post");
        btnSubmit.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSubmit.setOnAction(e -> submitPost());
        
        buttonBox.getChildren().addAll(btnCancel, btnSubmit);
        
        root.getChildren().addAll(
            lblHeader,
            new Separator(),
            lblTitle,
            tfTitle,
            lblThread,
            cbThread,
            lblContent,
            taContent,
            lblCharCount,
            buttonBox
        );
        
        Scene scene = new Scene(root);
        theStage.setScene(scene);
        theStage.showAndWait();
    }
    
    /*
     * 
     */
    public static void submitPost() {
        // Validation
        String title = tfTitle.getText().trim();
        String content = taContent.getText().trim();
        String thread = cbThread.getValue();
        
        if (title.isEmpty()) {
            showPostError("Title is required", "Please enter a title for your post.");
            tfTitle.requestFocus();
            return;
        }
        
        if (content.isEmpty()) {
            showPostError("Content is required", "Please enter content for your post.");
            taContent.requestFocus();
            return;
        }
        Post newPost = new Post(theUser.getUserName(), title, content, thread);
        
        int postId = theDatabase.createPost(newPost);
        
        if (postId > 0) {
            showSuccess("Post created successfully!");
            
            loadPosts();
            theStage.close();
        } else {
            showPostError("Failed to create post", "Please try again.");
        }
    }

    protected static String updateOverallGrade(int grade) {
        if (grade >= 90) {
        	return "A (" + String.valueOf(grade) + "%)";
        } else if (grade >= 80) {
        	return "B (" + String.valueOf(grade) + "%)";
        } else if (grade >= 70) {
        	return "C (" + String.valueOf(grade) + "%)";
        } else if (grade >= 60) {
        	return "D (" + String.valueOf(grade) + "%)";
        } else if (grade == -1) {
        	return "E (" + String.valueOf(grade) + "%)";
        } else {
        	return "F (" + String.valueOf(grade) + "%)";
        }
    }


	
	/**********
	 * <p> Method: filterByThread() </p>
	 * 
	 * <p> Description: Changes the forum to filter by a specified thread by grabbing the thread and inserting
	 * it back to loadPosts thus changing the view.</p>
	 * 
	 * 
	 * 
	 */
    protected static void filterByThread(String thread) {
        currentThread = thread;
        loadPosts();
    }
    /**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: grab the input from the textbox in the search bar
	 * and insert it into loadPosts to see posts under the search keywords</p>
	 * 
	 * 
	 * 
	 */
    protected static void performSearch() {
        currentSearchText = tfSearch.getText().trim();
        loadPosts();
    }
    /**********
	 * <p> Method: clearSearch() </p>
	 * 
	 * <p> Description: delete and empty the search bar and load posts</p>
	 * 
	 * 
	 * 
	 */
    protected static void clearSearch() {
        tfSearch.clear();
        currentSearchText = "";
        loadPosts();
    }
    /**********
	 * <p> Method: showInfo(String message) </p>
	 * 
	 * <p> Description:a notification of the status of the action</p>
	 * 
	 * @param message content of the info message is put into the body
	 * 
	 */
    protected static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**********
	 * <p> Method: showInfo(String message) </p>
	 * 
	 * <p> Description:a notification of the status of the action</p>
	 * 
	 * @param message content of the info message is put into the body
	 * 
	 */
    protected static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	/**********
	 * <p> Method: showError(String message) </p>
	 * 
	 * <p> Description: show the error of the action</p>
	 * 
	 * @param message 	the error message of an action
	 * 
	 */
    protected static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	/**********
	 * <p> Method: showPostError(String header, String message) </p>
	 * 
	 * <p> Description: show the error of the action</p>
	 * 
	 * @param header	The header message of an action
	 * @param message 	The error message of an action
	 * 
	 */
    protected static void showPostError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private static void updateInboxNotification() {
        int unreadCount = theDatabase.getUnreadMessageCount(theUser.getUserName());
        
        if (unreadCount > 0) {
            label_InboxNotification.setText("(" + unreadCount + " new)");
            label_InboxNotification.setVisible(true);
        } else {
            label_InboxNotification.setVisible(false);
        }
    }
    // PLACEHOLDER for THE NEXT TWO CLASS CODES
    protected static void showNotImplemented(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feature Not Implemented");
        alert.setHeaderText(null);
        alert.setContentText(feature + " functionality is not available.");
        alert.showAndWait();
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
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}

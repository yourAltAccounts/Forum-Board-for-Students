package guiRole2;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Modality;
import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import entityClasses.Post;
import entityClasses.PostList;
import java.util.List;

/*******
 * <p> Title: ViewRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role2 Home Page for Staff Discussion Forum.  This page provides
 * a complete forum interface with CRUD functionality, allowing staff members to create, read, update,
 * and delete posts. Includes filtering, searching, and post management features.</p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author CSE 360 Team
 * 
 * @version 2.00 2025-01-XX Staff Forum CRUD Implementation
 */
public class ViewRole2Home {

    /*-*******************************************************************************************
     * Attributes
     */

    /** Application window width, including extra space for buttons */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH + 250;

    /** Application window height, including extra space for buttons */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT + 75;

<<<<<<< HEAD
    /** Label displaying the page title */
    protected static Label label_PageTitle = new Label();

    /** Label displaying the current user's details */
    protected static Label label_UserDetails = new Label();
=======
	// GUI Area 2: Staff Discussion Forum
	protected static VBox leftPanel;
	protected static VBox centerPanel;
	protected static ToggleGroup threadToggleGroup;
	protected static RadioButton rbAll, rbGeneral, rbQuestions, rbMyPosts;
	protected static ListView<HBox> lvPosts;
	protected static Button button_OpenRequestSystem = new Button("RequestCenter");
	protected static Button button_ParameterMod = new Button("Grading Parameters");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 600, width-20,600);
	protected static Button button_ModerationDashboard = new Button("Moderation Dashboard");
	
	// GUI Area 3: This is last of the GUI areas.  It is used for creating a new post, 
	// quitting the application, and for logging out.
	private static TextField tfTitle;
	private static TextArea taContent;
	private static ComboBox<String> cbThread;
	private static Stage createPostStage;
	private static Stage editPostStage;
	private static Post currentEditPost;
	protected static Button btnCreatePost = new Button("Create New Post");
	protected static Button btnRefresh = new Button("Refresh");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
>>>>>>> branch 'main' of git@github.com:AaronShih260/TP2.git

    /** Button for updating the user's account */
    protected static Button button_UpdateThisUser = new Button("Account Update");

    /** TextField for search input */
    protected static TextField tfSearch = new TextField();

<<<<<<< HEAD
    /** Button to perform search */
    protected static Button btnSearch = new Button("Search");
=======
	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/**
	 * The currently logged-in user for this session.
	 */
	public static User theUser;	
	
	private static Scene theRole2HomeScene;		// The shared Scene each invocation populates
	protected static final int theRole = 3;		// Admin: 1; Role1: 2; Role2: 3
	
	// Static variables for filtering
	private static String currentThread = "All";
	private static String currentSearchText = "";
>>>>>>> branch 'main' of git@github.com:AaronShih260/TP2.git

    /** Button to clear search input */
    protected static Button btnClearSearch = new Button("Clear");

    /** Line separator in the GUI */
    protected static Line line_Separator1 = new Line(20, 105, width-20, 105);

    /** Left panel for thread filters */
    protected static VBox leftPanel;

    /** Center panel displaying posts */
    protected static VBox centerPanel;

    /** ToggleGroup for thread radio buttons */
    protected static ToggleGroup threadToggleGroup;

    /** Radio buttons for filtering threads */
    protected static RadioButton rbAll, rbGeneral, rbQuestions, rbMyPosts;

    /** ListView containing posts */
    protected static ListView<HBox> lvPosts;

<<<<<<< HEAD
    /** Button to open request system */
    protected static Button button_OpenRequestSystem = new Button("RequestCenter");
=======
	    setupButtonUI(btnRefresh, "Dialog", 18, 100, Pos.CENTER, 200, 620);
	    btnRefresh.setOnAction(e -> loadPosts());
	    
	    setupButtonUI(button_ModerationDashboard, "Dialog", 18, 250, Pos.CENTER, 320, 620);
	    button_ModerationDashboard.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
	    button_ModerationDashboard.setOnAction(e -> {
	        guiModeration.ViewModerationDashboard.displayModerationDashboard(
	            theStage, 
	            theUser.getUserName(),
	            false
	        );
	    });
	    
	    // GUI Area 2: Grading Parameter and Request Management System Button Setup
	    setupButtonUI(button_ParameterMod, "Dialog", 15, 25, Pos.CENTER, 10, 520);
	    button_ParameterMod.setOnAction((event) -> {
	    	// Call the static display method of the new Moderation Config View
	    	guiModerationConfig.ViewModerationConfig.display(theStage, currentSearchText, false);
	    });
	    
	    setupButtonUI(button_OpenRequestSystem, "Dialog", 15, 25, Pos.CENTER, 25, 550);
	    button_OpenRequestSystem.setOnAction((event) -> {
	        // Call the static display method of the new Request System View
	        guiRequestSystem.ViewRequestSystem.displayRequestSystem(theStage, theUser); 
	    });
	    
	    setupButtonUI(button_ParameterMod, "Dialog", 15, 25, Pos.CENTER, 10, 500);
	    button_ParameterMod.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
	    button_ParameterMod.setOnAction(e -> {
	        guiModerationConfig.ViewModerationConfig.display(
	            theStage,
	            theUser.getUserName(),
	            false    
	        );
	    });
>>>>>>> branch 'main' of git@github.com:AaronShih260/TP2.git

    /** Button for moderation configuration */
    protected static Button button_ModerationConfig = new Button("Parameter Mod");

<<<<<<< HEAD
    /** Line separator for bottom buttons */
    protected static Line line_Separator4 = new Line(20, 600, width-20, 600);

    /** Button to open moderation dashboard */
    protected static Button button_ModerationDashboard = new Button("Moderation Dashboard");

    /** TextField for post title input */
    private static TextField tfTitle;

    /** TextArea for post content input */
    private static TextArea taContent;

    /** ComboBox for thread selection */
    private static ComboBox<String> cbThread;

    /** Stage used for creating posts */
    private static Stage createPostStage;

    /** Stage used for editing posts */
    private static Stage editPostStage;

    /** The post currently being edited */
    private static Post currentEditPost;

    /** Button to create new posts */
    protected static Button btnCreatePost = new Button("Create New Post");

    /** Button to refresh the posts list */
    protected static Button btnRefresh = new Button("Refresh");

    /** Button to logout */
    protected static Button button_Logout = new Button("Logout");

    /** Button to quit the application */
    protected static Button button_Quit = new Button("Quit");

    /** Singleton instance of this class */
    private static ViewRole2Home theView;

    /** Reference to the in-memory database */
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /** The JavaFX stage for this GUI */
    protected static Stage theStage;

    /** The root pane containing all GUI widgets */
    protected static Pane theRootPane;

    /** The currently logged-in user */
    protected static User theUser;

    /** Shared Scene for the Role2 Home GUI */
    private static Scene theRole2HomeScene;

    /** Role identifier for Role2 users */
    protected static final int theRole = 3;

    /** Current thread filter */
    private static String currentThread = "All";

    /** Current search text */
    private static String currentSearchText = "";

    /*-*******************************************************************************************
     * Constructors
     */

    /**
     * Displays the Role2 Home page on the specified stage for the specified user.
     * 
     * @param ps The JavaFX Stage to display the GUI
     * @param user The User whose information and posts are displayed
     */
    public static void displayRole2Home(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewRole2Home();

        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;

        label_UserDetails.setText("Welcome: " + theUser.getUserName());
        loadPosts();

        theStage.setTitle("CSE 360 Foundations: Staff Home Page");
        theStage.setScene(theRole2HomeScene);
        theStage.show();
    }

    /**
     * Private constructor for singleton pattern. Initializes all GUI elements and layout.
     */
    private ViewRole2Home() {
        theRootPane = new Pane();
        theRole2HomeScene = new Scene(theRootPane, width, height);

        // Initialize GUI area 1
        label_PageTitle.setText("Staff Discussion Forum");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        setupLabelUI(label_UserDetails, "Arial", 14, width, Pos.BASELINE_LEFT, 20, 35);
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 860, 30);
        button_UpdateThisUser.setOnAction((event) -> {ViewUserUpdate.displayUserUpdate(theStage, theUser); });

        HBox searchBox = createSearchBar();
        searchBox.setLayoutX(20);
        searchBox.setLayoutY(65);

        // GUI Area 2
        BorderPane contentArea = new BorderPane();
        contentArea.setLayoutX(20);
        contentArea.setLayoutY(115);
        contentArea.setPrefSize(width - 40, 480);

        leftPanel = createThreadFilterPanel();
        contentArea.setLeft(leftPanel);

        centerPanel = createPostsPanel();
        contentArea.setCenter(centerPanel);

        // GUI Area 3 buttons
        setupButtonUI(btnCreatePost, "Dialog", 18, 170, Pos.CENTER, 20, 620);
        btnCreatePost.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnCreatePost.setOnAction(e -> ControllerRole2Home.handleCreatePost());

        setupButtonUI(btnRefresh, "Dialog", 18, 100, Pos.CENTER, 200, 620);
        btnRefresh.setOnAction(e -> loadPosts());

        setupButtonUI(button_ModerationDashboard, "Dialog", 18, 250, Pos.CENTER, 320, 620);
        button_ModerationDashboard.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        button_ModerationDashboard.setOnAction(e -> {
            guiModeration.ViewModerationDashboard.displayModerationDashboard(
                theStage,
                theUser.getUserName(),
                false
            );
        });

        setupButtonUI(button_OpenRequestSystem, "Dialog", 15, 25, Pos.CENTER, 10, 550);
        button_OpenRequestSystem.setOnAction((event) -> guiRequestSystem.ViewRequestSystem.displayRequestSystem(theStage, theUser));

        setupButtonUI(button_ModerationConfig, "Dialog", 15, 25, Pos.CENTER, 10, 500);
        button_ModerationConfig.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        button_ModerationConfig.setOnAction(e -> guiModerationConfig.ViewModerationConfig.display(theStage, theUser.getUserName(), false));

        setupButtonUI(button_Logout, "Dialog", 18, 170, Pos.CENTER, 670, 620);
        button_Logout.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        button_Logout.setOnAction((event) -> {theStage.close(); ControllerRole2Home.performLogout();});

        setupButtonUI(button_Quit, "Dialog", 18, 170, Pos.CENTER, 860, 620);
        button_Quit.setOnAction((event) -> {ControllerRole2Home.performQuit(); });

        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
            searchBox, contentArea, button_OpenRequestSystem, line_Separator4, button_ModerationConfig,
            btnCreatePost, btnRefresh, button_ModerationDashboard, button_Logout, button_Quit
        );
    }

    /**
     * Creates the search bar HBox including label, text field, and buttons.
     * @return HBox containing the search bar GUI
     */
    private HBox createSearchBar() {
=======
	    theRootPane.getChildren().addAll(
	        label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	        searchBox, contentArea, button_OpenRequestSystem, line_Separator4, button_ParameterMod,
	        btnCreatePost, btnRefresh, button_ModerationDashboard, button_Logout, button_Quit);
	}
	
	
	/**********
	 * <p> Method: createSearchBar() </p>
	 * <p> Description: Create the format and display the search Bar including the buttons.</p>
	 * @return searchBox object of the text,action,input, and layout of the search bar
	 */
	private HBox createSearchBar() {
>>>>>>> branch 'main' of git@github.com:AaronShih260/TP2.git
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

    /**
     * Creates the VBox containing thread filter radio buttons.
     * @return VBox containing thread filter UI
     */
    private VBox createThreadFilterPanel() {
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

        Separator separator = new Separator();

        panel.getChildren().addAll(lblThreads, rbAll, rbGeneral, rbQuestions, rbMyPosts, separator);
        return panel;
    }

    /**
     * Creates the VBox displaying all forum posts.
     * @return VBox containing posts list
     */
    private VBox createPostsPanel() {
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

    /**
     * Loads posts from the database based on the current user, thread, and search filters.
     */
    protected static void loadPosts() {
        if (lvPosts == null || theUser == null) return;

        lvPosts.getItems().clear();

        PostList posts = ControllerRole2Home.handleLoadPosts(currentThread, currentSearchText);

        if (posts != null) {
            List<Post> postList = posts.getAllPosts();
            if (postList.isEmpty()) {
                Label lblEmpty = new Label("No posts found for the current filter/search.");
                lblEmpty.setFont(Font.font("Arial", 14));
                HBox emptyBox = new HBox(lblEmpty);
                emptyBox.setPadding(new Insets(20));
                lvPosts.getItems().add(emptyBox);
            } else {
                for (Post post : postList) {
                    HBox postItem = theView.createPostItem(post);
                    lvPosts.getItems().add(postItem);
                }
            }
        }
    }

    /**
     * Creates an HBox representing a single post in the forum list.
     * @param post The post object to display
     * @return HBox containing formatted post data
     */
    private HBox createPostItem(Post post) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(10));

        if (post.isDeleted()) {
            item.setStyle("-fx-border-color: #ff9999; -fx-border-width: 2; -fx-background-color: #fff0f0;");
        } else {
            item.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
        }

        VBox postInfo = new VBox(5);
        postInfo.setPrefWidth(600);

        String titleText = post.isDeleted() ? "[DELETED] " + post.getTitle() : post.getTitle();
        Label lblTitle = new Label(titleText);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblTitle.setWrapText(true);

        if (post.isDeleted()) lblTitle.setStyle("-fx-text-fill: #d32f2f; -fx-font-style: italic;");

        String metaText = String.format("by %s | %s | Thread: %s", post.isDeleted() ? "[deleted]" : post.getAuthor(), post.getFormattedTimestamp(), post.getThread());
        Label lblMeta = new Label(metaText);
        lblMeta.setFont(Font.font("Arial", 12));
        lblMeta.setStyle("-fx-text-fill: #666;");

        String preview = post.isDeleted() ? "[Content has been removed. Click View to see replies.]" : post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..." : post.getContent();
        Label lblPreview = new Label(preview);
        lblPreview.setWrapText(true);

        if (post.isDeleted()) lblPreview.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");

        postInfo.getChildren().addAll(lblTitle, lblMeta, lblPreview);

        int replyCount = ControllerRole2Home.handleGetReplyCount(post.getPostId());
        VBox replyInfo = new VBox(5);
        replyInfo.setAlignment(Pos.CENTER);
        replyInfo.setPrefWidth(80);

        Label lblReplyCount = new Label(String.valueOf(replyCount));
        lblReplyCount.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label lblReplies = new Label("replies");
        lblReplies.setFont(Font.font(10));

<<<<<<< HEAD
        replyInfo.getChildren().addAll(lblReplyCount, lblReplies);
=======
	    // Show Edit and Delete buttons for own non-deleted posts
	    if (post.getAuthor().equals(theUser.getUserName()) && !post.isDeleted()) {	        
	    	Button btnEdit = new Button("Edit");
	        btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
	        btnEdit.setOnAction(e -> ControllerRole2Home.handleEditPost(post));
	        actions.getChildren().add(btnEdit);
	        
	        Button btnDelete = new Button("Delete");
	        btnDelete.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
	        btnDelete.setOnAction(e -> ControllerRole2Home.handleDeletePost(post));
	        actions.getChildren().add(btnDelete);
	    }
>>>>>>> branch 'main' of git@github.com:AaronShih260/TP2.git

        VBox actions = new VBox(5);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button btnView = new Button("View");
        btnView.setOnAction(e -> ControllerRole2Home.handleViewPost(post));
        actions.getChildren().add(btnView);

        if (post.getAuthor().equals(theUser.getUserName()) && !post.isDeleted()) {
            Button btnEdit = new Button("Edit");
            btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            btnEdit.setOnAction(e -> ControllerRole2Home.handleEditPost(post));
            actions.getChildren().add(btnEdit);

            Button btnDelete = new Button("Delete");
            btnDelete.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
            btnDelete.setOnAction(e -> ControllerRole2Home.handleDeletePost(post));
            actions.getChildren().add(btnDelete);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        item.getChildren().addAll(postInfo, spacer, replyInfo, actions);
        return item;
    }

    /**
     * Opens a dialog to create a new post.
     */
    public static void setupPostCreation() {
        // ... implementation unchanged
    }

    /**
     * Opens a dialog to edit an existing post.
     * @param post The post to edit
     */
    public static void setupPostEdit(Post post) {
        // ... implementation unchanged
    }

    /**
     * Submits a new post to the database.
     */
    public static void submitPost() {
        // ... implementation unchanged
    }

    /**
     * Updates an existing post in the database.
     */
    public static void updatePost() {
        // ... implementation unchanged
    }

    /**
     * Filters posts by a specific thread.
     * @param thread The thread to filter by
     */
    protected static void filterByThread(String thread) {
        currentThread = thread;
        loadPosts();
    }

    /**
     * Performs search for posts based on current search text.
     */
    protected static void performSearch() {
        currentSearchText = tfSearch.getText().trim();
        loadPosts();
    }

    /**
     * Clears the search filter and reloads all posts.
     */
    protected static void clearSearch() {
        tfSearch.clear();
        currentSearchText = "";
        loadPosts();
    }

    /**
     * Displays a success alert message.
     * @param message The success message text
     */
    protected static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error alert message.
     * @param message The error message text
     */
    protected static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Displays an error alert with a custom header and message.
     *
     * @param header  The header text for the alert dialog
     * @param message The detailed error message
     */
    protected static void showPostError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*-********************************************************************************************
     * Helper methods to reduce code length
     */

    /**
     * Private helper method to initialize standard properties for a Label.
     *
     * @param l  The Label object to be initialized
     * @param ff The font family to be used
     * @param f  The font size
     * @param w  The width of the Label
     * @param p  The alignment of the text inside the Label
     * @param x  The X-coordinate position of the Label on the pane
     * @param y  The Y-coordinate position of the Label on the pane
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**
     * Private helper method to initialize standard properties for a Button.
     *
     * @param b  The Button object to be initialized
     * @param ff The font family to be used
     * @param f  The font size
     * @param w  The width of the Button
     * @param p  The alignment of the text inside the Button
     * @param x  The X-coordinate position of the Button on the pane
     * @param y  The Y-coordinate position of the Button on the pane
     */
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}


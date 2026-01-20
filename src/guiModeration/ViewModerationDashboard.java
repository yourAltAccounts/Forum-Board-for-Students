package guiModeration;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.ModerationFlag;
import java.util.List;

/**
 * <p> Title: ViewModerationDashboard Class </p>
 * <p> Description: Staff moderation interface (View component) for reviewing posts,
 * flagging inappropriate content, and sending private feedback. This class manages the 
 * layout, controls, and display logic for the moderation dashboard, delegating 
 * functional tasks to the {@code ControllerModeration}.</p>
 * <p> User Stories: </p>
 * <ul>
 * <li> Staff flags content and sends private feedback. </li>
 * <li> Admin oversight of moderation activity. </li>
 * </ul>
 * <p> Copyright: CSE 360 Team © 2025 </p>
 *
 * @author Generated for Moderation Feature
 * @version 1.00    2025-11-12 Moderation Implementation
 */
public class ViewModerationDashboard {
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
    
    // UI Components
    private static Label label_Title = new Label("Moderation Dashboard");
    private static Label label_Instructions = new Label("Review posts and flag inappropriate content");
    
    // Filter options
    private static ComboBox<String> combo_Filter = new ComboBox<>();
    private static Button button_Refresh = new Button("Refresh");
    private static TextField text_Search = new TextField();
    
    // Posts table
    /** TableView component displaying all posts based on current filters. */
    protected static TableView<Post> table_Posts = new TableView<>();
    /** The Post object currently selected in the table, used for context-based actions. */
    protected static Post selectedPost = null;
    
    // Action buttons
    private static Button button_ViewPost = new Button("View Details");
    private static Button button_FlagPost = new Button("Flag Post");
    private static Button button_SendMessage = new Button("Send Private Message");
    private static Button button_ViewFlags = new Button("View All Flags");
    private static Button button_Back = new Button("Back");
    
    // Statistics labels
    private static Label label_TotalPosts = new Label("Total Posts: 0");
    private static Label label_FlaggedPosts = new Label("Flagged Posts: 0");
    private static Label label_PendingFlags = new Label("Pending Flags: 0");
    
    /** Stores the username of the staff member currently logged in and using the dashboard. */
    protected static String currentStaffUsername = "";
    /** Flag indicating if the current user has the Administrator role. */
    protected static boolean isAdmin = false;
    
    private static Stage theStage;
    private static Pane theRootPane;
    /** The JavaFX Scene containing the moderation dashboard UI. */
    public static Scene theModerationScene = null;
    private static ViewModerationDashboard theView = null;
    
    /**
     * Displays the moderation dashboard by setting up the scene and showing the stage.
     * This method is the entry point for the moderation view.
     * * @param ps The primary JavaFX Stage (window) to display the dashboard in.
     * @param staffUsername The username of the current staff member.
     * @param adminRole A boolean indicating whether the user possesses the admin role.
     */
    public static void displayModerationDashboard(Stage ps, String staffUsername, boolean adminRole) {
        theStage = ps;
        currentStaffUsername = staffUsername;
        isAdmin = adminRole;
        
        if (theView == null) {
            theView = new ViewModerationDashboard();
        }
        
        refreshPostsList();
        updateStatistics();
        
        theStage.setTitle("CSE 360 Foundation: Moderation Dashboard");
        theStage.setScene(theModerationScene);
        theStage.show();
    }
    
    /**
     * Private constructor for the ViewModerationDashboard class.
     * Initializes the root pane, the scene, and calls {@link #setupUI()}.
     * This follows the Singleton pattern principle for the view component.
     */
    private ViewModerationDashboard() {
        theRootPane = new BorderPane();
        theModerationScene = new Scene(theRootPane, width, height);
        
        setupUI();
    }
    
    /**
     * Sets up the entire JavaFX UI layout, including the header, filter controls, 
     * statistics box, posts table, and action buttons.
     */
    private void setupUI() {
        BorderPane root = (BorderPane) theRootPane;
        
        // Top: Title and instructions
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));
        topBox.setAlignment(Pos.CENTER);
        
        label_Title.setFont(Font.font("Arial", 28));
        label_Instructions.setFont(Font.font("Arial", 14));
        
        topBox.getChildren().addAll(label_Title, label_Instructions);
        root.setTop(topBox);
        
        // Center: Posts table and controls
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));
        
        // Filter controls
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label label_Filter = new Label("Filter:");
        combo_Filter.getItems().addAll("All Posts", "Flagged Only", "Unflagged Only", "My Flags");
        combo_Filter.setValue("All Posts");
        combo_Filter.setOnAction(e -> refreshPostsList());
        
        text_Search.setPromptText("Search by keyword...");
        text_Search.setPrefWidth(200);
        text_Search.setOnAction(e -> refreshPostsList());
        
        button_Refresh.setOnAction(e -> refreshPostsList());
        
        filterBox.getChildren().addAll(label_Filter, combo_Filter, text_Search, button_Refresh);
        
        // Statistics
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
        
        label_TotalPosts.setFont(Font.font("Arial", 12));
        label_FlaggedPosts.setFont(Font.font("Arial", 12));
        label_PendingFlags.setFont(Font.font("Arial", 12));
        
        statsBox.getChildren().addAll(label_TotalPosts, label_FlaggedPosts, label_PendingFlags);
        
        // Posts table
        setupPostsTable();
        
        centerBox.getChildren().addAll(filterBox, statsBox, table_Posts);
        root.setCenter(centerBox);
        
        // Bottom: Action buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);
        
        button_ViewPost.setOnAction(e -> ControllerModeration.performViewPost(theStage));
        button_FlagPost.setOnAction(e -> ControllerModeration.performFlagPost(theStage));
        button_SendMessage.setOnAction(e -> ControllerModeration.performSendMessage(theStage));
        button_ViewFlags.setOnAction(e -> ControllerModeration.performViewFlags(theStage));
        button_Back.setOnAction(e -> ControllerModeration.performBack(theStage));
        
        buttonBox.getChildren().addAll(
            button_ViewPost, button_FlagPost, button_SendMessage, 
            button_ViewFlags, button_Back
        );
        
        root.setBottom(buttonBox);
    }
    
    /**
     * Initializes and configures the columns for the {@link #table_Posts} TableView.
     * This includes setting up cell value factories for Post properties.
     */
    private void setupPostsTable() {
        table_Posts.setPrefHeight(400);
        table_Posts.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedPost = newSelection;
                updateButtonStates();
            }
        );
        
        // Columns
        TableColumn<Post, Integer> colPostId = new TableColumn<>("ID");
        colPostId.setCellValueFactory(new PropertyValueFactory<>("postId"));
        colPostId.setPrefWidth(50);
        
        TableColumn<Post, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(250);
        
        TableColumn<Post, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAuthor.setPrefWidth(120);
        
        TableColumn<Post, String> colThread = new TableColumn<>("Thread");
        colThread.setCellValueFactory(new PropertyValueFactory<>("thread"));
        colThread.setPrefWidth(100);
        
        TableColumn<Post, String> colTimestamp = new TableColumn<>("Posted");
        colTimestamp.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFormattedTimestamp()
            )
        );
        colTimestamp.setPrefWidth(150);
        
        TableColumn<Post, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> {
            Post post = cellData.getValue();
            String status = post.isDeleted() ? "DELETED" : "ACTIVE";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        colStatus.setPrefWidth(80);
        
        table_Posts.getColumns().addAll(
            colPostId, colTitle, colAuthor, colThread, colTimestamp, colStatus
        );
    }
    
    /**
     * Refreshes the data displayed in the {@link #table_Posts} based on the currently selected 
     * filter option and search text. Also updates statistics and button states.
     */
    protected static void refreshPostsList() {
        String filter = combo_Filter.getValue();
        String searchText = text_Search.getText().trim();
        
        List<Post> posts = ControllerModeration.getFilteredPosts(filter, searchText);
        
        table_Posts.getItems().clear();
        table_Posts.getItems().addAll(posts);
        
        updateStatistics();
        updateButtonStates();
    }
    
    /**
     * Fetches and updates the moderation statistics (Total Posts, Flagged Posts, Pending Flags)
     * using the {@code ControllerModeration} and sets the text of the corresponding labels.
     */
    protected static void updateStatistics() {
        int[] stats = ControllerModeration.getModerationStatistics();
        
        label_TotalPosts.setText("Total Posts: " + stats[0]);
        label_FlaggedPosts.setText("Flagged Posts: " + stats[1]);
        label_PendingFlags.setText("Pending Flags: " + stats[2]);
    }
    
    /**
     * Updates the enabled/disabled state of action buttons based on whether a post 
     * is currently selected in the table.
     */
    private static void updateButtonStates() {
        boolean hasSelection = selectedPost != null;
        
        button_ViewPost.setDisable(!hasSelection);
        button_FlagPost.setDisable(!hasSelection);
        button_SendMessage.setDisable(!hasSelection);
    }
    
    /**
     * Displays a success alert box (INFORMATION type) to the user.
     * * @param message The success message to display to the user.
     */
    protected static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Displays an error alert box (ERROR type) to the user.
     * * @param title The header or title for the error alert.
     * @param message The detailed error message content.
     */
    protected static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
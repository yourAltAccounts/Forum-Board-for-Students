package guiInbox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.PrivateMessage;
import java.util.List;

/*******
 * <p> Title: ViewPrivateInbox Class </p>
 * 
 * <p> Description: Student inbox for viewing private messages from staff
 * and sending replies.</p>
 * 
 * <p> User Stories:
 * - Student views inbox and responds to feedback
 * - Read/unread message tracking
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Generated for Moderation Feature
 * @version 1.00    2025-11-12 Moderation Implementation
 */
public class ViewPrivateInbox {
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
    
    // UI Components
    private static Label label_Title = new Label("Private Inbox");
    private static Label label_UnreadCount = new Label("Unread Messages: 0");
    
    // Filter options
    private static ComboBox<String> combo_Filter = new ComboBox<>();
    private static Button button_Refresh = new Button("Refresh");
    
    // Messages table
    protected static TableView<PrivateMessage> table_Messages = new TableView<>();
    protected static PrivateMessage selectedMessage = null;
    
    // Action buttons
    private static Button button_ViewMessage = new Button("View Message");
    private static Button button_Reply = new Button("Reply");
    private static Button button_MarkRead = new Button("Mark as Read");
    private static Button button_MarkUnread = new Button("Mark as Unread");
    private static Button button_Back = new Button("Back");
    
    protected static String currentUsername = "";
    
    private static Stage theStage;
    private static Pane theRootPane;
    public static Scene theInboxScene = null;
    private static ViewPrivateInbox theView = null;
    
    /*******
     * Displays the private inbox.
     */
    public static void displayPrivateInbox(Stage ps, String username) {
        theStage = ps;
        currentUsername = username;
        
        if (theView == null) {
            theView = new ViewPrivateInbox();
        }
        
        refreshMessagesList();
        updateUnreadCount();
        
        theStage.setTitle("CSE 360 Foundation: Private Inbox");
        theStage.setScene(theInboxScene);
        theStage.show();
    }
    
    /*******
     * Constructor - initializes the UI.
     */
    private ViewPrivateInbox() {
        theRootPane = new BorderPane();
        theInboxScene = new Scene(theRootPane, width, height);
        
        setupUI();
    }
    
    /*******
     * Sets up the UI components.
     */
    private void setupUI() {
        BorderPane root = (BorderPane) theRootPane;
        
        // Top: Title and unread count
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));
        topBox.setAlignment(Pos.CENTER);
        
        label_Title.setFont(Font.font("Arial", 28));
        label_UnreadCount.setFont(Font.font("Arial", 14));
        label_UnreadCount.setStyle("-fx-text-fill: #d32f2f;");
        
        topBox.getChildren().addAll(label_Title, label_UnreadCount);
        root.setTop(topBox);
        
        // Center: Messages table and controls
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));
        
        // Filter controls
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label label_Filter = new Label("Filter:");
        combo_Filter.getItems().addAll("All Messages", "Unread Only", "Read Only");
        combo_Filter.setValue("All Messages");
        combo_Filter.setOnAction(e -> refreshMessagesList());
        
        button_Refresh.setOnAction(e -> refreshMessagesList());
        
        filterBox.getChildren().addAll(label_Filter, combo_Filter, button_Refresh);
        
        // Messages table
        setupMessagesTable();
        
        centerBox.getChildren().addAll(filterBox, table_Messages);
        root.setCenter(centerBox);
        
        // Bottom: Action buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);
        
        button_ViewMessage.setOnAction(e -> ControllerInbox.performViewMessage(theStage));
        button_Reply.setOnAction(e -> ControllerInbox.performReply(theStage));
        button_MarkRead.setOnAction(e -> ControllerInbox.performMarkRead());
        button_MarkUnread.setOnAction(e -> ControllerInbox.performMarkUnread());
        button_Back.setOnAction(e -> ControllerInbox.performBack(theStage));
        
        buttonBox.getChildren().addAll(
            button_ViewMessage, button_Reply, button_MarkRead, 
            button_MarkUnread, button_Back
        );
        
        root.setBottom(buttonBox);
    }
    
    /*******
     * Sets up the messages table columns.
     */
    private void setupMessagesTable() {
        table_Messages.setPrefHeight(400);
        table_Messages.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedMessage = newSelection;
                updateButtonStates();
            }
        );
        
        // Status indicator column (unread = red dot)
        TableColumn<PrivateMessage, String> colStatus = new TableColumn<>("●");
        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().isRead() ? "" : "●";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        colStatus.setPrefWidth(30);
        colStatus.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        
        TableColumn<PrivateMessage, String> colFrom = new TableColumn<>("From");
        colFrom.setCellValueFactory(new PropertyValueFactory<>("senderId"));
        colFrom.setPrefWidth(120);
        
        TableColumn<PrivateMessage, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(cellData -> {
            PrivateMessage msg = cellData.getValue();
            String subject = "Feedback on Post #" + 
                           (msg.getPostId() != null ? msg.getPostId() : "General");
            if (msg.isReply()) {
                subject = "Re: " + subject;
            }
            return new javafx.beans.property.SimpleStringProperty(subject);
        });
        colSubject.setPrefWidth(300);
        
        TableColumn<PrivateMessage, String> colPreview = new TableColumn<>("Preview");
        colPreview.setCellValueFactory(cellData -> {
            String content = cellData.getValue().getContent();
            String preview = content.length() > 50 ? 
                           content.substring(0, 50) + "..." : content;
            return new javafx.beans.property.SimpleStringProperty(preview);
        });
        colPreview.setPrefWidth(250);
        
        TableColumn<PrivateMessage, String> colTimestamp = new TableColumn<>("Received");
        colTimestamp.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFormattedTimestamp()
            )
        );
        colTimestamp.setPrefWidth(150);
        
        table_Messages.getColumns().addAll(
            colStatus, colFrom, colSubject, colPreview, colTimestamp
        );
    }
    
    /*******
     * Refreshes the messages list based on current filter.
     */
    protected static void refreshMessagesList() {
        String filter = combo_Filter.getValue();
        
        List<PrivateMessage> messages = ControllerInbox.getFilteredMessages(filter);
        
        table_Messages.getItems().clear();
        table_Messages.getItems().addAll(messages);
        
        updateUnreadCount();
        updateButtonStates();
    }
    
    /*******
     * Updates unread count label.
     */
    protected static void updateUnreadCount() {
        int unreadCount = ControllerInbox.getUnreadCount();
        label_UnreadCount.setText("Unread Messages: " + unreadCount);
    }
    
    /*******
     * Updates button enabled states based on selection.
     */
    private static void updateButtonStates() {
        boolean hasSelection = selectedMessage != null;
        
        button_ViewMessage.setDisable(!hasSelection);
        button_Reply.setDisable(!hasSelection);
        button_MarkRead.setDisable(!hasSelection || 
                                   (hasSelection && selectedMessage.isRead()));
        button_MarkUnread.setDisable(!hasSelection || 
                                     (hasSelection && !selectedMessage.isRead()));
    }
    
    /*******
     * Shows success alert.
     */
    protected static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /*******
     * Shows error alert.
     */
    protected static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
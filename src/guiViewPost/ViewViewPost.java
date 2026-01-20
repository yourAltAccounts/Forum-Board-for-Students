package guiViewPost;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.Reply;
import java.util.List;

/*******
 * <p> Title: ViewViewPost Class </p>
 * 
 * <p> Description: View component for viewing posts. Defines all GUI widgets
 * and layout for displaying a post and its replies.</p>
 * 
 * <p> User Stories:
 * - #2: View post details and replies
 * - #9: Reply to posts (provides reply input UI)
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Luke Dempsey
 * @version 1.00    2025-10-24 TP2 Implementation
 */
public class ViewViewPost {
    
    // GUI widgets
    protected static Stage theDialog;
    private static BorderPane theRootPane;
    private static Scene theViewPostScene;
    
    // Post display widgets
    private static Label label_PostTitle;
    private static TextArea text_PostContent;
    private static Label label_Author;
    private static Label label_Time;
    private static Label label_Thread;
    
    // Replies section
    protected static VBox vbox_RepliesContainer;
    private static ScrollPane scroll_Replies;
    
    // Reply input section
    protected static TextArea text_NewReply;
    private static Button button_SubmitReply;
    private static Button button_Close;
    private static Label label_CharCount;
    
    // Context variables
    protected static Post thePost;
    protected static String theCurrentUsername;
    protected static Runnable theOnUpdate;
    
    // Singleton
    private static ViewViewPost theView;
    
    /*******
     * Displays the view post dialog.
     * User Story #2: View post details
     * 
     * @param post Post to display
     * @param username Current user
     * @param onUpdate Callback to refresh parent window
     */
    public static void displayViewPost(Post post, String username, Runnable onUpdate) {
        thePost = post;
        theCurrentUsername = username;
        theOnUpdate = onUpdate;
        
        // Initialize View singleton if needed
        if (theView == null) {
            theView = new ViewViewPost();
        }
        
        // Update display with current post
        updatePostDisplay();
        
        // Mark as read
        ControllerViewPost.markCurrentPostAsRead();
        
        // Load replies
        ControllerViewPost.refreshReplies();
        
        // Show dialog
        theDialog.showAndWait();
    }
    
    /*******
     * Constructor - creates all GUI widgets (singleton).
     */
    private ViewViewPost() {
        theDialog = new Stage();
        theDialog.initModality(Modality.APPLICATION_MODAL);
        theDialog.setTitle("View Post");
        
        theRootPane = new BorderPane();
        theRootPane.setPrefWidth(800);
        theRootPane.setPrefHeight(600);
        
        // Build sections
        VBox topSection = createPostSection();
        VBox centerSection = createRepliesSection();
        VBox bottomSection = createAddReplySection();
        
        theRootPane.setTop(topSection);
        theRootPane.setCenter(centerSection);
        theRootPane.setBottom(bottomSection);
        
        theViewPostScene = new Scene(theRootPane);
        theDialog.setScene(theViewPostScene);
    }
    
    /*******
     * Creates post content display section.
     */
    private VBox createPostSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 0 0 2 0;");
        
        label_PostTitle = new Label();
        label_PostTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label_PostTitle.setWrapText(true);
        
        HBox metaBox = new HBox(20);
        label_Author = new Label();
        label_Time = new Label();
        label_Thread = new Label();
        label_Author.setStyle("-fx-text-fill: #666;");
        label_Time.setStyle("-fx-text-fill: #666;");
        label_Thread.setStyle("-fx-text-fill: #666;");
        metaBox.getChildren().addAll(label_Author, label_Time, label_Thread);
        
        Label lblContentHeader = new Label("Content:");
        lblContentHeader.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        text_PostContent = new TextArea();
        text_PostContent.setWrapText(true);
        text_PostContent.setEditable(false);
        text_PostContent.setPrefHeight(150);
        text_PostContent.setStyle("-fx-control-inner-background: white;");
        
        section.getChildren().addAll(label_PostTitle, metaBox, new Separator(), lblContentHeader, text_PostContent);
        return section;
    }
    
    /*******
     * Creates replies display section.
     */
    private VBox createRepliesSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        
        Label lblRepliesHeader = new Label("Replies:");
        lblRepliesHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        vbox_RepliesContainer = new VBox(10);
        scroll_Replies = new ScrollPane(vbox_RepliesContainer);
        scroll_Replies.setFitToWidth(true);
        scroll_Replies.setPrefHeight(250);
        VBox.setVgrow(scroll_Replies, Priority.ALWAYS);
        
        section.getChildren().addAll(lblRepliesHeader, scroll_Replies);
        return section;
    }
    
    /*******
     * Creates reply input section.
     */
    private VBox createAddReplySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-width: 2 0 0 0;");
        
        Label lblAddReply = new Label("Add Your Reply:");
        lblAddReply.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        text_NewReply = new TextArea();
        text_NewReply.setPromptText("Type your reply here...");
        text_NewReply.setPrefHeight(100);
        text_NewReply.setWrapText(true);
        
        label_CharCount = new Label("Characters: 0");
        text_NewReply.textProperty().addListener((obs, oldText, newText) -> {
            label_CharCount.setText("Characters: " + newText.length());
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        button_Close = new Button("Close");
        button_Close.setOnAction(e -> ControllerViewPost.performClose());
        
        button_SubmitReply = new Button("Submit Reply");
        button_SubmitReply.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        button_SubmitReply.setOnAction(e -> ControllerViewPost.performSubmitReply());
        
        buttonBox.getChildren().addAll(button_Close, button_SubmitReply);
        section.getChildren().addAll(lblAddReply, text_NewReply, label_CharCount, buttonBox);
        return section;
    }
    
    /*******
     * Updates post display with current post data.
     */
    private static void updatePostDisplay() {
        if (thePost.isDeleted()) {
            label_PostTitle.setText("[Post Deleted by Author]");
            label_PostTitle.setStyle("-fx-text-fill: #d32f2f; -fx-font-style: italic;");
            text_PostContent.setText(
                "This post has been deleted by the author.\n\n" +
                "The replies below are still visible."
            );
            text_PostContent.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
            label_Author.setText("Posted by: [deleted]");
            label_Time.setText("Time: " + thePost.getFormattedTimestamp());
            label_Thread.setText("Thread: " + thePost.getThread());
            text_NewReply.setDisable(true);
            button_SubmitReply.setDisable(true);
        } else {
            label_PostTitle.setText(thePost.getTitle());
            label_PostTitle.setStyle("");
            text_PostContent.setText(thePost.getContent());
            text_PostContent.setStyle("");
            label_Author.setText("Posted by: " + thePost.getAuthor());
            label_Time.setText("Time: " + thePost.getFormattedTimestamp());
            label_Thread.setText("Thread: " + thePost.getThread());
            text_NewReply.setDisable(false);
            button_SubmitReply.setDisable(false);
        }
        text_NewReply.clear();
    }
    
    /*******
     * Updates replies display.
     * User Story #2: View replies
     * 
     * @param replies List of replies to display
     */
    public static void displayReplies(List<Reply> replies) {
        vbox_RepliesContainer.getChildren().clear();
        
        if (replies.isEmpty()) {
            String emptyMessage = thePost.isDeleted() ? 
                "No replies to this post." : 
                "No replies yet. Be the first to reply!";
                
            Label lblNoReplies = new Label(emptyMessage);
            lblNoReplies.setStyle("-fx-text-fill: #999;");
            lblNoReplies.setFont(Font.font("Arial", 14));
            vbox_RepliesContainer.getChildren().add(lblNoReplies);
        } else {
            if (thePost.isDeleted()) {
                Label lblInfo = new Label("ℹ Original post deleted - Replies preserved below");
                lblInfo.setStyle(
                    "-fx-background-color: #e3f2fd; " +
                    "-fx-text-fill: #1976D2; " +
                    "-fx-padding: 10; " +
                    "-fx-font-weight: bold;"
                );
                lblInfo.setFont(Font.font("Arial", 12));
                vbox_RepliesContainer.getChildren().add(lblInfo);
            }
            for (Reply reply : replies) {
                VBox replyBox = createReplyItem(reply);
                vbox_RepliesContainer.getChildren().add(replyBox);
            }
        }
    }
    
    /*******
     * Creates GUI for a single reply.
     */
    private static VBox createReplyItem(Reply reply) {
        VBox item = new VBox(5);
        item.setPadding(new Insets(10));
        item.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white;");
        
        HBox header = new HBox(15);
        Label lblAuthor = new Label(reply.getAuthor());
        lblAuthor.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label lblTime = new Label(reply.getFormattedTimestamp());
        lblTime.setStyle("-fx-text-fill: #666;");
        lblTime.setFont(Font.font("Arial", 10));
        header.getChildren().addAll(lblAuthor, lblTime);
        
        Label lblContent = new Label(reply.getContent());
        lblContent.setWrapText(true);
        lblContent.setFont(Font.font("Arial", 12));
        
        item.getChildren().addAll(header, lblContent);
        return item;
    }
}

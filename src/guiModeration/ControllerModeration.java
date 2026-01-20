package guiModeration;

import javafx.scene.control.*;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.ModerationFlag;
import entityClasses.PrivateMessage;
import entityClasses.User;

import java.util.List;
import java.util.Optional;
import entityClasses.PostList;

/**
 * <p> Title: ControllerModeration Class </p>
 * 
 * <p> Description: Controller for moderation dashboard. Handles staff
 * actions for reviewing posts, flagging content, and sending feedback.</p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Generated for Moderation Feature
 * @version 1.00    2025-11-12 Moderation Implementation
 */
public class ControllerModeration {
    
    /**
     * Gets filtered posts based on selection.
     * 
     * @param filter The filter type to apply ("All Posts", "Flagged Only", "Unflagged Only", "My Flags")
     * @param searchText The search text to filter by keywords
     * @return List of Post objects matching the filter criteria
     */
    protected static List<Post> getFilteredPosts(String filter, String searchText) {
        PostList postList;
        
        switch (filter) {
            case "Flagged Only":
                postList = new PostList(ModelModeration.getFlaggedPosts());
                break;
            case "Unflagged Only":
                postList = new PostList(ModelModeration.getUnflaggedPosts());
                break;
            case "My Flags":
                postList = new PostList(ModelModeration.getPostsFlaggedByStaff(
                    ViewModerationDashboard.currentStaffUsername
                ));
                break;
            default:
                postList = ModelModeration.getAllPosts();
                break;
        }
        
        if (!searchText.isEmpty()) {
            postList = postList.searchByKeywords(searchText); 
        }
        
        return postList.getAllPosts();  
    }
    
    /**
     * Gets moderation statistics.
     * 
     * @return Array containing [totalPosts, flaggedPosts, pendingFlags]
     */
    protected static int[] getModerationStatistics() {
        return ModelModeration.getModerationStatistics();
    }
    
    /**
     * Handles viewing a post's details.
     * 
     * @param stage The JavaFX stage
     */
    protected static void performViewPost(Stage stage) {
        Post selected = ViewModerationDashboard.selectedPost;
        
        if (selected == null) {
            ViewModerationDashboard.showError("No Selection", 
                "Please select a post to view.");
            return;
        }
        
        showPostDetailsDialog(selected);
    }
    
    /**
     * Handles flagging a post for moderation.
     * 
     * @param stage The JavaFX stage
     */
    protected static void performFlagPost(Stage stage) {
        Post selected = ViewModerationDashboard.selectedPost;
        
        if (selected == null) {
            ViewModerationDashboard.showError("No Selection", 
                "Please select a post to flag.");
            return;
        }
        
        if (ModelModeration.isPostFlagged(selected.getPostId())) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Already Flagged");
            confirm.setHeaderText("This post is already flagged.");
            confirm.setContentText("Do you want to add another flag?");
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Flag Post for Moderation");
        dialog.setHeaderText("Flag Post ID: " + selected.getPostId());
        dialog.setContentText("Reason for flagging (min 10 characters):");
        dialog.getEditor().setPrefColumnCount(40);
        
        Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            String reason = result.get().trim();
            
            if (reason.length() < 10) {
                ViewModerationDashboard.showError("Invalid Input", 
                    "Flag reason must be at least 10 characters long.");
                return;
            }
            
            if (reason.length() > 500) {
                ViewModerationDashboard.showError("Invalid Input", 
                    "Flag reason cannot exceed 500 characters.");
                return;
            }
            
            boolean success = ModelModeration.createFlag(
                selected.getPostId(),
                ViewModerationDashboard.currentStaffUsername,
                reason
            );
            
            if (success) {
                ViewModerationDashboard.showSuccess("Post flagged successfully!");
                ViewModerationDashboard.refreshPostsList();
                
                Alert askMessage = new Alert(Alert.AlertType.CONFIRMATION);
                askMessage.setTitle("Send Private Feedback?");
                askMessage.setHeaderText("Post has been flagged.");
                askMessage.setContentText(
                    "Would you like to send private feedback to the post author?"
                );
                
                Optional<ButtonType> messageResult = askMessage.showAndWait();
                if (messageResult.isPresent() && messageResult.get() == ButtonType.OK) {
                    performSendMessage(stage);
                }
            } else {
                ViewModerationDashboard.showError("Error", 
                    "Failed to create moderation flag.");
            }
        }
    }
    
    /**
     * Handles sending a private message to a post author.
     * 
     * @param stage The JavaFX stage
     */
    protected static void performSendMessage(Stage stage) {
        Post selected = ViewModerationDashboard.selectedPost;
        
        if (selected == null) {
            ViewModerationDashboard.showError("No Selection", 
                "Please select a post first.");
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Send Private Feedback");
        dialog.setHeaderText("Send message to: " + selected.getAuthor() + 
                           "\nRegarding Post ID: " + selected.getPostId());
        
        ButtonType sendButton = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButton, ButtonType.CANCEL);
        
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter your message (max 2000 characters)...");
        textArea.setPrefRowCount(10);
        textArea.setPrefColumnCount(50);
        textArea.setWrapText(true);
        
        Label charCount = new Label("0 / 2000");
        textArea.textProperty().addListener((obs, old, newVal) -> {
            charCount.setText(newVal.length() + " / 2000");
        });
        
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(textArea, charCount);
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButton) {
                return textArea.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            String message = result.get().trim();
            
            if (message.isEmpty()) {
                ViewModerationDashboard.showError("Invalid Input", 
                    "Message cannot be empty.");
                return;
            }
            
            if (message.length() > 2000) {
                ViewModerationDashboard.showError("Invalid Input", 
                    "Message cannot exceed 2000 characters.");
                return;
            }
            
            boolean success = ModelModeration.sendPrivateMessage(
                ViewModerationDashboard.currentStaffUsername,
                selected.getAuthor(),
                selected.getPostId(),
                message
            );
            
            if (success) {
                ViewModerationDashboard.showSuccess(
                    "Private message sent to " + selected.getAuthor()
                );
            } else {
                ViewModerationDashboard.showError("Error", 
                    "Failed to send private message.");
            }
        }
    }
    
    /**
     * Handles viewing all moderation flags.
     * 
     * @param stage The JavaFX stage
     */
    protected static void performViewFlags(Stage stage) {
        List<ModerationFlag> flags;
        
        if (ViewModerationDashboard.isAdmin) {
            flags = ModelModeration.getAllFlags();
        } else {
            flags = ModelModeration.getFlagsByStaff(
                ViewModerationDashboard.currentStaffUsername
            );
        }
        
        showFlagsDialog(flags);
    }
    
    /**
     * Handles going back to previous screen.
     * 
     * @param stage The JavaFX stage
     */
    protected static void performBack(Stage stage) {
        String username = ViewModerationDashboard.currentStaffUsername;
        
        if (applicationMain.FoundationsMain.database.getUserAccountDetails(username)) {
            User user = new User(
                username, 
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
            
            guiRole2.ViewRole2Home.displayRole2Home(stage, user);
        }
    }
    
    /**
     * Shows detailed post information dialog.
     * 
     * @param post The post to display details for
     */
    private static void showPostDetailsDialog(Post post) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Post Details");
        dialog.setHeaderText("Post ID: " + post.getPostId());
        
        String details = String.format(
            "Author: %s\n" +
            "Title: %s\n" +
            "Thread: %s\n" +
            "Posted: %s\n" +
            "Status: %s\n" +
            "Flagged: %s\n\n" +
            "Content:\n%s",
            post.getAuthor(),
            post.getTitle(),
            post.getThread(),
            post.getFormattedTimestamp(),
            post.isDeleted() ? "DELETED" : "ACTIVE",
            ModelModeration.isPostFlagged(post.getPostId()) ? "YES" : "NO",
            post.getContent()
        );
        
        dialog.setContentText(details);
        dialog.showAndWait();
    }
    
    /**
     * Shows moderation flags list dialog.
     * 
     * @param flags The list of flags to display
     */
    private static void showFlagsDialog(List<ModerationFlag> flags) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Moderation Flags");
        dialog.setHeaderText("Total Flags: " + flags.size());
        
        if (flags.isEmpty()) {
            dialog.setContentText("No moderation flags found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (ModerationFlag flag : flags) {
                sb.append(String.format(
                    "Flag ID: %d | Post ID: %d | Staff: %s\n" +
                    "Status: %s | Created: %s\n" +
                    "Reason: %s\n\n",
                    flag.getFlagId(),
                    flag.getPostId(),
                    flag.getStaffId(),
                    flag.getStatus(),
                    flag.getFormattedTimestamp(),
                    flag.getFlagReason()
                ));
            }
            dialog.setContentText(sb.toString());
        }
        
        dialog.showAndWait();
    }
}
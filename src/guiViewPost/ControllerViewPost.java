package guiViewPost;

import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import entityClasses.Reply;

/*******
 * <p> Title: ControllerViewPost Class </p>
 * 
 * <p> Description: Controller component for viewing posts. Handles user actions
 * and coordinates between View and Model.</p>
 * 
 * <p> User Stories:
 * - #2: View post details
 * - #9: Reply to posts
 * - #10: Track read posts
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Luke Dempsey
 * @version 1.00    2025-10-24 TP2 Implementation
 */
public class ControllerViewPost {
    
    /*******
     * Handles submit reply button click.
     * User Story #9: Reply to posts
     * 
     * Validates input, creates reply, refreshes display.
     */
    protected static void performSubmitReply() {
        String content = ViewViewPost.text_NewReply.getText().trim();
        
        // Validate content
        String error = ModelViewPost.validateReplyContent(content);
        if (!error.isEmpty()) {
            showError("Invalid Input", error);
            ViewViewPost.text_NewReply.requestFocus();
            return;
        }
        
        // Create reply
        boolean success = ModelViewPost.createReply(
            ViewViewPost.thePost.getPostId(),
            ViewViewPost.theCurrentUsername,
            content
        );
        
        if (success) {
            showSuccess("Reply posted!");
            ViewViewPost.text_NewReply.clear();
            refreshReplies();
            
            // Notify parent to refresh
            if (ViewViewPost.theOnUpdate != null) {
                ViewViewPost.theOnUpdate.run();
            }
        } else {
            showError("Error", "Failed to post reply.");
        }
    }
    
    /*******
     * Reloads replies from database.
     * User Story #2: View post replies
     */
    protected static void refreshReplies() {
        List<Reply> replies = ModelViewPost.getRepliesForPost(ViewViewPost.thePost.getPostId());
        ViewViewPost.displayReplies(replies);
    }
    
    /*******
     * Closes the view post dialog.
     */
    protected static void performClose() {
        ViewViewPost.theDialog.close();
    }
    
    /*******
     * Marks current post as read.
     * User Story #10: Track read posts
     */
    protected static void markCurrentPostAsRead() {
        ModelViewPost.markPostAsRead(
            ViewViewPost.thePost.getPostId(),
            ViewViewPost.theCurrentUsername
        );
    }
    
    // Helper methods for alerts
    private static void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

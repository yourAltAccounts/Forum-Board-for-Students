package guiInbox;

import javafx.scene.control.*;
import javafx.stage.Stage;
import entityClasses.PrivateMessage;
import entityClasses.User;
import entityClasses.Post;
import java.util.List;
import java.util.Optional;

/*******
 * <p> Title: ControllerInbox Class </p>
 * 
 * <p> Description: Controller for private inbox. Handles student
 * actions for viewing messages and sending replies.</p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Generated for Moderation Feature
 * @version 1.00    2025-11-12 Moderation Implementation
 */
public class ControllerInbox {
    
    /*******
     * Gets filtered messages based on selection.
     */
    protected static List<PrivateMessage> getFilteredMessages(String filter) {
        List<PrivateMessage> messages = ModelInbox.getMessagesForUser(
            ViewPrivateInbox.currentUsername
        );
        
        switch (filter) {
            case "Unread Only":
                messages.removeIf(msg -> msg.isRead());
                break;
            case "Read Only":
                messages.removeIf(msg -> !msg.isRead());
                break;
            // "All Messages" shows everything
        }
        
        return messages;
    }
    
    /*******
     * Gets unread message count.
     */
    protected static int getUnreadCount() {
        return ModelInbox.getUnreadMessageCount(ViewPrivateInbox.currentUsername);
    }
    
    /*******
     * Handles viewing a message's details.
     */
    protected static void performViewMessage(Stage stage) {
        PrivateMessage selected = ViewPrivateInbox.selectedMessage;
        
        if (selected == null) {
            ViewPrivateInbox.showError("No Selection", 
                "Please select a message to view.");
            return;
        }
        
        // Mark as read if unread
        if (!selected.isRead()) {
            ModelInbox.markMessageAsRead(selected.getMessageId());
            selected.setRead(true);
        }
        
        // Show message details dialog
        showMessageDetailsDialog(selected);
        
        // Refresh to update read status
        ViewPrivateInbox.refreshMessagesList();
    }
    
    /*******
     * Handles replying to a message.
     */
    protected static void performReply(Stage stage) {
        PrivateMessage selected = ViewPrivateInbox.selectedMessage;
        
        if (selected == null) {
            ViewPrivateInbox.showError("No Selection", 
                "Please select a message to reply to.");
            return;
        }
        
        // Show reply composition dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Reply to Message");
        dialog.setHeaderText("Reply to: " + selected.getSenderId());
        
        ButtonType sendButton = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButton, ButtonType.CANCEL);
        
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter your reply (max 2000 characters)...");
        textArea.setPrefRowCount(10);
        textArea.setPrefColumnCount(50);
        textArea.setWrapText(true);
        
        Label charCount = new Label("0 / 2000");
        textArea.textProperty().addListener((obs, old, newVal) -> {
            charCount.setText(newVal.length() + " / 2000");
        });
        
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        
        // Show original message for context
        Label originalLabel = new Label("Original message:");
        TextArea originalText = new TextArea(selected.getContent());
        originalText.setEditable(false);
        originalText.setPrefRowCount(4);
        originalText.setWrapText(true);
        originalText.setStyle("-fx-control-inner-background: #f5f5f5;");
        
        content.getChildren().addAll(
            originalLabel, originalText, 
            new Label("Your reply:"), textArea, charCount
        );
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButton) {
                return textArea.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            String reply = result.get().trim();
            
            // Validate reply
            if (reply.isEmpty()) {
                ViewPrivateInbox.showError("Invalid Input", 
                    "Reply cannot be empty.");
                return;
            }
            
            if (reply.length() > 2000) {
                ViewPrivateInbox.showError("Invalid Input", 
                    "Reply cannot exceed 2000 characters.");
                return;
            }
            
            // Send reply
            // Get parent message ID (if current is a reply, use its parent, else use current)
            int parentId = selected.isReply() ? 
                          selected.getParentMessageId() : selected.getMessageId();
            
            boolean success = ModelInbox.sendReply(
                ViewPrivateInbox.currentUsername,
                selected.getSenderId(),
                selected.getPostId(),
                reply,
                parentId
            );
            
            if (success) {
                ViewPrivateInbox.showSuccess("Reply sent successfully!");
                ViewPrivateInbox.refreshMessagesList();
            } else {
                ViewPrivateInbox.showError("Error", "Failed to send reply.");
            }
        }
    }
    
    /*******
     * Marks selected message as read.
     */
    protected static void performMarkRead() {
        PrivateMessage selected = ViewPrivateInbox.selectedMessage;
        
        if (selected == null) {
            return;
        }
        
        if (selected.isRead()) {
            ViewPrivateInbox.showError("Already Read", 
                "This message is already marked as read.");
            return;
        }
        
        boolean success = ModelInbox.markMessageAsRead(selected.getMessageId());
        
        if (success) {
            selected.setRead(true);
            ViewPrivateInbox.refreshMessagesList();
        } else {
            ViewPrivateInbox.showError("Error", 
                "Failed to update message status.");
        }
    }
    
    /*******
     * Marks selected message as unread.
     */
    protected static void performMarkUnread() {
        PrivateMessage selected = ViewPrivateInbox.selectedMessage;
        
        if (selected == null) {
            return;
        }
        
        if (!selected.isRead()) {
            ViewPrivateInbox.showError("Already Unread", 
                "This message is already marked as unread.");
            return;
        }
        
        boolean success = ModelInbox.markMessageAsUnread(selected.getMessageId());
        
        if (success) {
            selected.setRead(false);
            ViewPrivateInbox.refreshMessagesList();
        } else {
            ViewPrivateInbox.showError("Error", 
                "Failed to update message status.");
        }
    }
    
    /*******
     * Handles going back to previous screen.
     */
    protected static void performBack(Stage stage) {
        // Students go back to Role1 home
        String username = ViewPrivateInbox.currentUsername;
        
        // Fetch user from database
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
            
            guiRole1.ViewRole1Home.displayRole1Home(stage, user, applicationMain.FoundationsMain.database);
        }
    }
    
    /*******
     * Shows detailed message information dialog.
     */
    private static void showMessageDetailsDialog(PrivateMessage message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Message Details");
        dialog.setHeaderText("From: " + message.getSenderId());
        
        StringBuilder details = new StringBuilder();
        details.append("Received: ").append(message.getFormattedTimestamp()).append("\n");
        details.append("Status: ").append(message.isRead() ? "Read" : "Unread").append("\n");
        
        if (message.getPostId() != null) {
            details.append("Related Post ID: ").append(message.getPostId()).append("\n");
            
            // Try to get post details
            Post post = ModelInbox.getPostById(message.getPostId());
            if (post != null) {
                details.append("Post Title: ").append(post.getTitle()).append("\n");
            }
        }
        
        if (message.isReply()) {
            details.append("Type: Reply\n");
        }
        
        details.append("\nMessage:\n");
        details.append(message.getContent());
        
        // Show replies if any
        List<PrivateMessage> replies = ModelInbox.getReplies(message.getMessageId());
        if (!replies.isEmpty()) {
            details.append("\n\n--- Replies ---\n");
            for (PrivateMessage reply : replies) {
                details.append("\nFrom: ").append(reply.getSenderId());
                details.append(" (").append(reply.getFormattedTimestamp()).append(")\n");
                details.append(reply.getContent()).append("\n");
            }
        }
        
        dialog.setContentText(details.toString());
        
        // Make dialog larger for readability
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(400);
        
        dialog.showAndWait();
    }
}
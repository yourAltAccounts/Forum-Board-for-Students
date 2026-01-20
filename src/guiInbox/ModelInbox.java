package guiInbox;

import entityClasses.PrivateMessage;
import entityClasses.Post;
import java.util.List;

/**
 * <p> Title: ModelInbox Class </p>
 *
 * <p> Description: Model component for private inbox. Handles all
 * database operations for viewing and managing private messages.</p>
 *
 * <p> Copyright: CSE 360 Team © 2025 </p>
 *
 * @author Generated for Moderation Feature
 * @version 1.00    2025-11-12 Moderation Implementation
 */
public class ModelInbox {
	
	/**
     * Default constructor for the ModelInbox class.
     */
    protected ModelInbox() {
        // This is a utility class and shouldn't typically be instantiated.
        // It's added here to satisfy Javadoc documentation requirements.
    }

    /**
     * Gets all private messages for a user.
     * 
     * @param username The username of the user whose messages to retrieve
     * @return List of PrivateMessage objects for the specified user
     */
    protected static List<PrivateMessage> getMessagesForUser(String username) {
        return applicationMain.FoundationsMain.database.getPrivateMessagesForUser(username);
    }

    /**
     * Gets unread message count for a user.
     * 
     * @param username The username of the user
     * @return The number of unread messages for the user
     */
    protected static int getUnreadMessageCount(String username) {
        return applicationMain.FoundationsMain.database.getUnreadMessageCount(username);
    }

    /**
     * Marks a message as read.
     * 
     * @param messageId The ID of the message to mark as read
     * @return true if the operation was successful, false otherwise
     */
    protected static boolean markMessageAsRead(int messageId) {
        return applicationMain.FoundationsMain.database.markPrivateMessageAsRead(messageId);
    }

    /**
     * Marks a message as unread.
     * 
     * @param messageId The ID of the message to mark as unread
     * @return true if the operation was successful, false otherwise
     */
    protected static boolean markMessageAsUnread(int messageId) {
        return applicationMain.FoundationsMain.database.markPrivateMessageAsUnread(messageId);
    }

    /**
     * Sends a reply to a message.
     * 
     * @param senderId The username of the sender
     * @param recipientId The username of the recipient
     * @param postId The ID of the post being referenced (nullable)
     * @param content The content of the reply message
     * @param parentMessageId The ID of the parent message being replied to
     * @return true if the reply was sent successfully, false otherwise
     */
    protected static boolean sendReply(String senderId, String recipientId,
                                      Integer postId, String content, int parentMessageId) {
        PrivateMessage reply = new PrivateMessage(senderId, recipientId, postId,
                                                  content, parentMessageId);

        String error = reply.validate();
        if (!error.isEmpty()) {
            System.err.println("Reply validation error: " + error);
            return false;
        }

        return applicationMain.FoundationsMain.database.createPrivateMessage(reply);
    }

    /**
     * Gets replies to a specific message.
     * 
     * @param parentMessageId The ID of the parent message
     * @return List of PrivateMessage objects that are replies to the specified message
     */
    protected static List<PrivateMessage> getReplies(int parentMessageId) {
        return applicationMain.FoundationsMain.database.getPrivateMessageReplies(parentMessageId);
    }

    /**
     * Gets a specific post by ID.
     * 
     * @param postId The ID of the post to retrieve
     * @return Post object if found, null otherwise
     */
    protected static Post getPostById(int postId) {
        return applicationMain.FoundationsMain.database.getPostById(postId);
    }
}
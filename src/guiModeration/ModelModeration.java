package guiModeration;

import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.ModerationFlag;
import entityClasses.PrivateMessage;
import java.util.List;

/**
 * <p><b>ModelModeration</b></p>
 *
 * <p>This class serves as the <b>Model</b> component for the Moderation
 * feature within the system. It provides a unified interface for 
 * interacting with the database regarding:</p>
 *
 * <ul>
 *     <li>Moderation flags</li>
 *     <li>Posts and post status (flagged/unflagged)</li>
 *     <li>Private messages and replies</li>
 *     <li>Moderation statistics</li>
 * </ul>
 *
 * <p>The methods here simply forward all requests to the database layer,
 * ensuring a clean separation of concerns in the MVC architecture.</p>
 *
 * <p>© CSE 360 Team, 2025</p>
 */
public class ModelModeration {

    /**
     * Retrieves all posts stored in the system.
     *
     * @return a {@link PostList} containing all posts
     */
    protected static PostList getAllPosts() {
        return applicationMain.FoundationsMain.database.getAllPosts();
    }

    /**
     * Retrieves all posts currently marked as flagged.
     *
     * @return a list of flagged {@link Post} objects
     */
    protected static List<Post> getFlaggedPosts() {
        return applicationMain.FoundationsMain.database.getFlaggedPosts();
    }

    /**
     * Retrieves all posts that are not flagged.
     *
     * @return a list of unflagged {@link Post} objects
     */
    protected static List<Post> getUnflaggedPosts() {
        return applicationMain.FoundationsMain.database.getUnflaggedPosts();
    }

    /**
     * Retrieves all posts flagged by the given staff member.
     *
     * @param staffUsername the username of the staff member
     * @return a list of posts flagged by that staff member
     */
    protected static List<Post> getPostsFlaggedByStaff(String staffUsername) {
        return applicationMain.FoundationsMain.database.getPostsFlaggedByStaff(staffUsername);
    }

    /**
     * Determines whether a specific post has been flagged.
     *
     * @param postId the ID of the post to check
     * @return {@code true} if flagged, {@code false} otherwise
     */
    protected static boolean isPostFlagged(int postId) {
        return applicationMain.FoundationsMain.database.isPostFlagged(postId);
    }

    /**
     * Creates a new moderation flag for a post.
     *
     * @param postId   the ID of the post being flagged
     * @param staffId  the username of the staff member flagging it
     * @param reason   the reason for the flag
     * @return {@code true} if creation succeeded, otherwise {@code false}
     */
    protected static boolean createFlag(int postId, String staffId, String reason) {
        ModerationFlag flag = new ModerationFlag(postId, staffId, reason);

        String error = flag.validate();
        if (!error.isEmpty()) {
            System.err.println("Flag validation error: " + error);
            return false;
        }

        boolean success = applicationMain.FoundationsMain.database.createModerationFlag(flag);

        if (success) {
            applicationMain.FoundationsMain.database.markPostAsFlagged(postId, true);
        }

        return success;
    }

    /**
     * Retrieves all moderation flags in the system.
     *
     * @return a list of all {@link ModerationFlag} objects
     */
    protected static List<ModerationFlag> getAllFlags() {
        return applicationMain.FoundationsMain.database.getAllModerationFlags();
    }

    /**
     * Retrieves all moderation flags created by a specific staff user.
     *
     * @param staffUsername the username of the staff member
     * @return a list of that staff member's flags
     */
    protected static List<ModerationFlag> getFlagsByStaff(String staffUsername) {
        return applicationMain.FoundationsMain.database.getModerationFlagsByStaff(staffUsername);
    }

    /**
     * Retrieves all moderation flags associated with a given post.
     *
     * @param postId the ID of the post
     * @return a list of flags linked to the post
     */
    protected static List<ModerationFlag> getFlagsForPost(int postId) {
        return applicationMain.FoundationsMain.database.getModerationFlagsForPost(postId);
    }

    /**
     * Marks a moderation flag as resolved.
     *
     * @param flagId the ID of the flag to resolve
     * @return {@code true} if resolved successfully
     */
    protected static boolean resolveFlag(int flagId) {
        return applicationMain.FoundationsMain.database.updateFlagStatus(flagId, "RESOLVED");
    }

    /**
     * Marks a moderation flag as dismissed.
     *
     * @param flagId the ID of the flag to dismiss
     * @return {@code true} if dismissed successfully
     */
    protected static boolean dismissFlag(int flagId) {
        return applicationMain.FoundationsMain.database.updateFlagStatus(flagId, "DISMISSED");
    }

    /**
     * Sends a new private message.
     *
     * @param senderId     the sender's username
     * @param recipientId  the recipient's username
     * @param postId       the related post ID (may be {@code null})
     * @param content      the message content
     * @return {@code true} if the message was stored successfully
     */
    protected static boolean sendPrivateMessage(String senderId, String recipientId,
                                               Integer postId, String content) {
        PrivateMessage message = new PrivateMessage(senderId, recipientId, postId, content, null);

        String error = message.validate();
        if (!error.isEmpty()) {
            System.err.println("Message validation error: " + error);
            return false;
        }

        return applicationMain.FoundationsMain.database.createPrivateMessage(message);
    }

    /**
     * Sends a reply to an existing private message.
     *
     * @param senderId        the sender's username
     * @param recipientId     the recipient's username
     * @param postId          the related post ID (may be null)
     * @param content         the reply content
     * @param parentMessageId the ID of the original message
     * @return {@code true} if the reply was saved successfully
     */
    protected static boolean sendReply(String senderId, String recipientId,
                                      Integer postId, String content, int parentMessageId) {
        PrivateMessage reply = new PrivateMessage(
                senderId, recipientId, postId, content, parentMessageId);

        String error = reply.validate();
        if (!error.isEmpty()) {
            System.err.println("Reply validation error: " + error);
            return false;
        }

        return applicationMain.FoundationsMain.database.createPrivateMessage(reply);
    }

    /**
     * Loads all private messages for a given user.
     *
     * @param username the username of the message recipient
     * @return a list of {@link PrivateMessage} objects
     */
    protected static List<PrivateMessage> getMessagesForUser(String username) {
        return applicationMain.FoundationsMain.database.getPrivateMessagesForUser(username);
    }

    /**
     * Retrieves the number of unread messages for a user.
     *
     * @param username the username of the user
     * @return the count of unread messages
     */
    protected static int getUnreadMessageCount(String username) {
        return applicationMain.FoundationsMain.database.getUnreadMessageCount(username);
    }

    /**
     * Marks a specific message as read.
     *
     * @param messageId the ID of the message
     * @return {@code true} if updated successfully
     */
    protected static boolean markMessageAsRead(int messageId) {
        return applicationMain.FoundationsMain.database.markPrivateMessageAsRead(messageId);
    }

    /**
     * Retrieves all replies linked to a parent message.
     *
     * @param parentMessageId the ID of the original message
     * @return a list of replies
     */
    protected static List<PrivateMessage> getReplies(int parentMessageId) {
        return applicationMain.FoundationsMain.database.getPrivateMessageReplies(parentMessageId);
    }

    /**
     * Retrieves basic moderation statistics for the dashboard.
     *
     * @return an array containing:
     *         <ul>
     *             <li>[0] total number of posts</li>
     *             <li>[1] number of flagged posts</li>
     *             <li>[2] number of pending flags</li>
     *         </ul>
     */
    protected static int[] getModerationStatistics() {
        int totalPosts = applicationMain.FoundationsMain.database.getTotalPostCount();
        int flaggedPosts = applicationMain.FoundationsMain.database.getFlaggedPostCount();
        int pendingFlags = applicationMain.FoundationsMain.database.getPendingFlagCount();

        return new int[]{totalPosts, flaggedPosts, pendingFlags};
    }

    /**
     * Retrieves a specific post using its ID.
     *
     * @param postId the ID of the post
     * @return the corresponding {@link Post}, or {@code null} if not found
     */
    protected static Post getPostById(int postId) {
        return applicationMain.FoundationsMain.database.getPostById(postId);
    }
}

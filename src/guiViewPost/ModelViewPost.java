package guiViewPost;

import java.util.List;
import database.Database;
import entityClasses.Reply;
import entityClasses.ReplyList;

/*******
 * <p> Title: ModelViewPost Class </p>
 * 
 * <p> Description: Model component for viewing posts. Handles database operations
 * and validation for the View Post functionality.</p>
 * 
 * <p> User Stories:
 * - #2: View post details and replies
 * - #9: Reply to posts
 * - #10: Track read/unread posts
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Luke Dempsey
 * @version 1.00    2025-10-24 TP2 Implementation
 */
public class ModelViewPost {
    
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    
    /*******
     * Gets all replies for a post, sorted chronologically.
     * User Story #2: View post details
     * 
     * @param postId Post ID
     * @return List of replies (oldest first)
     */
    public static List<Reply> getRepliesForPost(int postId) {
        ReplyList replyList = theDatabase.getRepliesForPost(postId);
        return replyList.sortByOldest().getAllReplies();
    }
    
    /*******
     * Validates reply content.
     * User Story #9: Reply to posts
     * 
     * @param content Reply text
     * @return Empty string if valid, error message if invalid
     */
    public static String validateReplyContent(String content) {
        if (content.isEmpty()) {
            return "Reply content cannot be empty.";
        }
        if (content.length() < 5) {
            return "Reply must be at least 5 characters.";
        }
        return "";
    }
    
    /*******
     * Creates a new reply in database.
     * User Story #9: Reply to posts
     * 
     * @param postId Post being replied to
     * @param author Reply author username
     * @param content Reply text
     * @return true if created successfully
     */
    public static boolean createReply(int postId, String author, String content) {
        Reply newReply = new Reply(postId, author, content);
        int replyId = theDatabase.createReply(newReply);
        return replyId > 0;
    }
    
    /*******
     * Marks post as read by user.
     * User Story #10: Track read posts
     * 
     * @param postId Post ID
     * @param username Username
     */
    public static void markPostAsRead(int postId, String username) {
        theDatabase.markPostAsRead(postId, username);
    }
}

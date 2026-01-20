package guiRole1;

import entityClasses.PostList;
import database.Database;
import entityClasses.User;

public class ModelRole1Home {

/*******
 * <p> Title: ModelRole1Home Class. </p>
 * 
 * <p> Description: The Role1Home Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting role information and saving it in the
 * database.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */
	// Reference to the in-memory database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Method: getFilteredPosts() </p>
     * * <p> Description: Grabs a list of posts from the database based on the 
     * given thread filter, search text, and current user. </p>
     * @param user The current User object.
     * @param currentThread The thread to filter by ("All", "General", "Questions", "MyPosts", "Unread").
     * @param searchText The keywords to search for.
     * @return A PostList object containing the filtered, searched, and sorted posts.
     * */
    public static PostList getFilteredPosts(User user, String currentThread, String searchText) {
        if (theDatabase == null || user == null) {
            return new PostList(); // Return empty list on error
        }

        PostList posts = null; 
        String username = user.getUserName();

        // Grab posts based on the main thread filter
        if (currentThread.equals("MyPosts")) {
            posts = theDatabase.getPostsByAuthor(username);
        } else if (currentThread.equals("Unread")) {
            posts = theDatabase.getUnreadPosts(username);
        } else if (currentThread.equals("All")) {
            posts = theDatabase.getAllPosts();
        } else {
            posts = theDatabase.getPostsByThread(currentThread);
        }

        if (posts == null) {
            return new PostList();
        }

        //  search filter
        if (!searchText.isEmpty()) {
            posts = posts.searchByKeywords(searchText);
        }
            
        // activate Filter and sort
        posts = posts.sortByNewest(); 

        return posts;
    }

    /**********
     * <p> Method: deletePost() </p>
     * * <p> Description: Marks a post as deleted in the database. </p>
     * * @param postId The ID of the post to delete.
     * @return true if the post was successfully deleted, false otherwise.
     * */
    public static boolean deletePost(int postId) {
        if (theDatabase == null) return false;
        return theDatabase.deletePost(postId);
    }

    /**********
     * <p> Method: getPostReplyCount() </p>
     * * <p> Description: Retrieves the number of replies for a specific post. </p>
     * * @param postId The ID of the post.
     * @return The count of replies.
     * */
    public static int getPostReplyCount(int postId) {
        if (theDatabase == null) return 0;
        return theDatabase.getReplyCountForPost(postId);
    }
    
    /**********
     * <p> Method: markPostAsRead() </p>
     * * <p> Description: Marks a post as read by a specific user. </p>
     * * @param postId The ID of the post.
     * @param username The user who read the post.
     * */
    public static void markPostAsRead(int postId, String username) {
        if (theDatabase == null) return;
        theDatabase.markPostAsRead(postId, username);
    }
}

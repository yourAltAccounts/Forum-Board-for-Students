package guiRole2;

import entityClasses.Post;
import entityClasses.PostList;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ModelRole2Home Class. </p>
 * 
 * <p> Description: The Role2Home Page Model.  This class handles business logic for the
 * Staff Discussion Forum, including CRUD operations on posts, filtering, searching, and
 * permission checks.</p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author CSE 360 Team
 * 
 * @version 1.00		2025-01-XX Staff Forum Implementation
 */
public class ModelRole2Home {
    
    // Reference to the in-memory database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Method: getFilteredPosts() </p>
     * * <p> Description: Grabs a list of posts from the database based on the 
     * given thread filter, search text, and current user. </p>
     * @param user The current User object.
     * @param currentThread The thread to filter by ("All", "General", "Questions", "MyPosts").
     * @param searchText The keywords to search for.
     * @return A PostList object containing the filtered, searched, and sorted posts.
     * @see tester.StaffCrudTest#testGetFilteredPostsByThread()
     * @see tester.StaffCrudTest#testGetFilteredPostsAll()
     * @see tester.StaffCrudTest#testGetFilteredPostsMyPosts()
     * @see tester.StaffCrudTest#testGetFilteredPostsWithSearch()
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
        } else if (currentThread.equals("All")) {
            posts = theDatabase.getAllPosts();
        } else {
            posts = theDatabase.getPostsByThread(currentThread);
        }

        if (posts == null) {
            return new PostList();
        }

        // Apply search filter
        if (!searchText.isEmpty()) {
            posts = posts.searchByKeywords(searchText);
        }
            
        // Filter out deleted posts and sort
        posts = posts.filterActive().sortByNewest(); 

        return posts;
    }

    /**********
     * <p> Method: createPost() </p>
     * * <p> Description: Creates a new post in the database. </p>
     * * @param post The Post object to create.
     * @return The postId if successful, -1 otherwise.
     * @see tester.StaffCrudTest#testCreatePostSuccess()
     * @see tester.StaffCrudTest#testCreatePostNull()
     * */
    public static int createPost(Post post) {
        if (theDatabase == null || post == null) {
            return -1;
        }
        return theDatabase.createPost(post);
    }

    /**********
     * <p> Method: updatePost() </p>
     * * <p> Description: Updates an existing post in the database. </p>
     * * @param post The Post object with updated information.
     * @return true if the post was successfully updated, false otherwise.
     * @see tester.StaffCrudTest#testUpdatePostSuccess()
     * @see tester.StaffCrudTest#testUpdatePostNull()
     * */
    public static boolean updatePost(Post post) {
        if (theDatabase == null || post == null) {
            return false;
        }
        return theDatabase.updatePost(post);
    }

    /**********
     * <p> Method: deletePost() </p>
     * * <p> Description: Marks a post as deleted in the database (soft delete). </p>
     * * @param postId The ID of the post to delete.
     * @return true if the post was successfully deleted, false otherwise.
     * @see tester.StaffCrudTest#testDeletePostSuccess()
     * @see tester.StaffCrudTest#testDeletePostInvalidId()
     * */
    public static boolean deletePost(int postId) {
        if (theDatabase == null) {
            return false;
        }
        return theDatabase.deletePost(postId);
    }

    /**********
     * <p> Method: getPostById() </p>
     * * <p> Description: Retrieves a specific post by its ID. </p>
     * * @param postId The ID of the post.
     * @return The Post object, or null if not found.
     * @see tester.StaffCrudTest#testGetPostById()
     * @see tester.StaffCrudTest#testGetPostByIdInvalid()
     * */
    public static Post getPostById(int postId) {
        if (theDatabase == null) {
            return null;
        }
        return theDatabase.getPostById(postId);
    }

    /**********
     * <p> Method: getPostReplyCount() </p>
     * * <p> Description: Retrieves the number of replies for a specific post. </p>
     * * @param postId The ID of the post.
     * @return The count of replies.
     * @see tester.StaffCrudTest#testGetPostReplyCount()
     * */
    public static int getPostReplyCount(int postId) {
        if (theDatabase == null) {
            return 0;
        }
        return theDatabase.getReplyCountForPost(postId);
    }
    
    /**********
     * <p> Method: markPostAsRead() </p>
     * * <p> Description: Marks a post as read by a specific user. </p>
     * * @param postId The ID of the post.
     * * @param username The user who read the post.
     * */
    public static void markPostAsRead(int postId, String username) {
        if (theDatabase == null) {
            return;
        }
        theDatabase.markPostAsRead(postId, username);
    }

    /**********
     * <p> Method: canEditPost() </p>
     * * <p> Description: Checks if a user can edit a specific post (only author can edit). </p>
     * * @param post The post to check.
     * * @param username The username to check.
     * @return true if the user can edit the post, false otherwise.
     * @see tester.StaffCrudTest#testCanEditPost()
     * @see tester.StaffCrudTest#testCanEditPostNotAuthor()
     * @see tester.StaffCrudTest#testCanEditPostDeleted()
     * */
    public static boolean canEditPost(Post post, String username) {
        if (post == null || username == null) {
            return false;
        }
        return post.getAuthor().equals(username) && !post.isDeleted();
    }
}

package guiRole2;

import entityClasses.Post;
import entityClasses.PostList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: ControllerRole2Home Class. </p>
 * 
 * <p> Description: The Role2Home Page Controller.  This class provides controller actions
 * based on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This controller is not a class that gets instantiated.  Rather, it is a collection of protected
 * static methods that can be called by the View (which is a singleton instantiated object) and 
 * the Model is often just a stub, or will be a singleton instantiated object.
 * 
 * The class handles user interface actions for the Staff Discussion Forum, including post creation,
 * reading, updating, deletion, filtering, searching, and permission checks.</p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author CSE 360 Team
 * 
 * @version 1.00		2025-01-XX Staff Forum CRUD Implementation
 */
public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	
	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method opens the user update dialog to allow the current user
	 * to update their account settings.</p>
	 * 
	 * @see guiUserUpdate.ViewUserUpdate#displayUserUpdate(javafx.stage.Stage, entityClasses.User)
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 * @see guiUserLogin.ViewUserLogin#displayUserLogin(javafx.stage.Stage)
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
	
    /**********
	 * <p> Method: handleLoadPosts(String currentThread, String currentSearchText) </p>
	 * 
	 * <p> Description: Calls the Model to grab the filtered and searched posts 
     * and passes them back to the View for display when called. </p>
     * 
     * @param currentThread The current thread filter.
     * @param currentSearchText The current text filter.
     * @return A PostList of filtered and sorted posts.
	 * 
	 * @see guiRole2.ModelRole2Home#getFilteredPosts(entityClasses.User, String, String)
	 * @see tester.StaffCrudTest#testGetFilteredPostsByThread()
	 * @see tester.StaffCrudTest#testGetFilteredPostsWithSearch()
	 */
    protected static PostList handleLoadPosts(String currentThread, String currentSearchText) {
        return ModelRole2Home.getFilteredPosts(ViewRole2Home.theUser, currentThread, currentSearchText);
    }
    
    /**********
	 * <p> Method: handleCreatePost() </p>
	 * 
	 * <p> Description: Opens the create post dialog for the user to create a new post. </p>
	 * 
	 * @see guiRole2.ViewRole2Home#setupPostCreation()
	 * @see tester.StaffCrudTest#testCreatePostSuccess()
	 */
    protected static void handleCreatePost() {
        ViewRole2Home.setupPostCreation();
    }
    
    /**********
	 * <p> Method: handleCreatePost(Post post) </p>
	 * 
	 * <p> Description: Creates a new post in the database using the Model. </p>
	 * 
	 * @param post The Post object to create.
	 * @return The postId if successful, -1 otherwise.
	 * 
	 * @see guiRole2.ModelRole2Home#createPost(entityClasses.Post)
	 * @see tester.StaffCrudTest#testCreatePostSuccess()
	 * @see tester.StaffCrudTest#testCreatePostNull()
	 */
    protected static int handleCreatePost(Post post) {
        return ModelRole2Home.createPost(post);
    }
    
    /**********
	 * <p> Method: handleEditPost(Post post) </p>
	 * 
	 * <p> Description: Opens the edit post dialog for the specified post, with permission checks. </p>
	 * 
	 * @param post The post to edit.
	 * 
	 * @see guiRole2.ModelRole2Home#canEditPost(entityClasses.Post, String)
	 * @see guiRole2.ViewRole2Home#setupPostEdit(entityClasses.Post)
	 * @see tester.StaffCrudTest#testCanEditPost()
	 * @see tester.StaffCrudTest#testCanEditPostNotAuthor()
	 */
    protected static void handleEditPost(Post post) {
        if (post == null) {
            return;
        }
        
        // Check if user can edit this post
        if (!ModelRole2Home.canEditPost(post, ViewRole2Home.theUser.getUserName())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Edit Post");
            alert.setHeaderText(null);
            alert.setContentText("You can only edit your own posts.");
            alert.showAndWait();
            return;
        }
        
        ViewRole2Home.setupPostEdit(post);
    }
    
    /**********
	 * <p> Method: handleUpdatePost(Post post) </p>
	 * 
	 * <p> Description: Updates an existing post in the database using the Model. 
	 * Verifies that the user still has permission to edit the post before updating. </p>
	 * 
	 * @param post The Post object with updated information.
	 * @return true if successful, false otherwise.
	 * 
	 * @see guiRole2.ModelRole2Home#canEditPost(entityClasses.Post, String)
	 * @see guiRole2.ModelRole2Home#updatePost(entityClasses.Post)
	 * @see tester.StaffCrudTest#testUpdatePostSuccess()
	 * @see tester.StaffCrudTest#testUpdatePostNull()
	 */
    protected static boolean handleUpdatePost(Post post) {
        if (post == null) {
            return false;
        }
        
        // Verify user can still edit this post
        if (!ModelRole2Home.canEditPost(post, ViewRole2Home.theUser.getUserName())) {
            ViewRole2Home.showError("You can only edit your own posts.");
            return false;
        }
        
        return ModelRole2Home.updatePost(post);
    }
    
    /**********
	 * <p> Method: handleDeletePost(Post post) </p>
	 * 
	 * <p> Description: Handles the confirmation dialog and deletes the post using the Model.
	 * Verifies that the user has permission to delete the post (must be the author) before
	 * proceeding with deletion. </p>
	 * 
	 * @param post The post object to be deleted.
	 * 
	 * @see guiRole2.ModelRole2Home#deletePost(int)
	 * @see tester.StaffCrudTest#testDeletePostSuccess()
	 * @see tester.StaffCrudTest#testDeletePostInvalidId()
	 */
    protected static void handleDeletePost(Post post) {
        if (post == null) {
            return;
        }
        
        // Verify user can delete this post
        if (!post.getAuthor().equals(ViewRole2Home.theUser.getUserName())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot Delete Post");
            alert.setHeaderText(null);
            alert.setContentText("You can only delete your own posts.");
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to delete this post?\n\n\"" + post.getTitle() + "\"");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = ModelRole2Home.deletePost(post.getPostId());
            if (success) {
                ViewRole2Home.showSuccess("Post deleted successfully!");
                ViewRole2Home.loadPosts(); // Refresh the view
            } else {
                ViewRole2Home.showError("Failed to delete post.");
            }
        }
    }
    
    /**********
	 * <p> Method: handleViewPost(Post post) </p>
	 * 
	 * <p> Description: Marks the post as read and opens the view post dialog to display
	 * the full post content and replies. </p>
	 * 
	 * @param post The post being viewed.
	 * 
	 * @see guiViewPost.ViewViewPost#displayViewPost(entityClasses.Post, String, Runnable)
	 * @see guiRole2.ModelRole2Home#markPostAsRead(int, String)
	 */
    protected static void handleViewPost(Post post) {
        if (post == null) {
            return;
        }
        
        // Use the guiViewPost MVC package to display post details
        guiViewPost.ViewViewPost.displayViewPost(post, ViewRole2Home.theUser.getUserName(), 
            () -> ViewRole2Home.loadPosts());
    }
    
    /**********
	 * <p> Method: handleGetReplyCount(int postId) </p>
	 * 
	 * <p> Description: Grabs the reply count for a post from the Model. </p>
	 * 
	 * @param postId The ID of the post.
	 * @return The number of replies.
	 * 
	 * @see guiRole2.ModelRole2Home#getPostReplyCount(int)
	 * @see tester.StaffCrudTest#testGetPostReplyCount()
	 */
    protected static int handleGetReplyCount(int postId) {
        return ModelRole2Home.getPostReplyCount(postId);
    }

}

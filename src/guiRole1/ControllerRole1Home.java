package guiRole1;

import entityClasses.Post;
import entityClasses.PostList;
import java.util.Optional;

import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public class ControllerRole1Home {
	

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	
	// Reference to the in-memory database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

	
 	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
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
	 * * <p> Description: Calls the Model to grab the filtered and searched posts 
     * and passes them back to the View for display when called. </p>
     * * @param currentThread The current thread filter.
     * @param currentSearchText The current text filter.
     * @return A PostList of filtered and sorted posts.
	 * */
    protected static PostList handleLoadPosts(String currentThread, String currentSearchText) {
        return ModelRole1Home.getFilteredPosts(ViewRole1Home.theUser, currentThread, currentSearchText);
    }

    /**********
     * <p> Method: handleFilterByThread(String thread) </p>
     * * <p> Description: Tells the View to change its thread state and refresh posts. </p>
     * * @param thread The new thread filter.
     * */
    protected static void handleFilterByThread(String thread) {
        ViewRole1Home.filterByThread(thread);
    }

    /**********
     * <p> Method: handlePerformSearch() </p>
     * * <p> Description: Tells the View to update its search state and refresh posts. </p>
     * */
    protected static void handlePerformSearch() {
        ViewRole1Home.performSearch();
    }
    
    /**********
     * <p> Method: handleClearSearch() </p>
     * * <p> Description: Tells the View to clear the search state and refresh posts. </p>
     * */
    protected static void handleClearSearch() {
        ViewRole1Home.clearSearch();
    }

    /**********
     * <p> Method: handleDeletePost(Post post) </p>
     * * <p> Description: Handles the confirmation and deletes the post, using the Model. </p>
     * * @param post The post object to be deleted.
     * */
    protected static void handleDeletePost(Post post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to delete this post?\n\n\"" + post.getTitle() + "\"");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = ModelRole1Home.deletePost(post.getPostId());
            if (success) {
                ViewRole1Home.showInfo("Post deleted successfully!");
                ViewRole1Home.loadPosts(); // Tell the view to refresh
            } else {
                ViewRole1Home.showError("Failed to delete post.");
            }
        }
    }

    /**********
     * <p> Method: handleGetReplyCount(int postId) </p>
     * * <p> Description: Grabs the reply count for a post from the Model. </p>
     * * @param postId The ID of the post.
     * @return The number of replies.
     * */
    protected static int handleGetReplyCount(int postId) {
        return ModelRole1Home.getPostReplyCount(postId);
    }
    /**********
     * <p> Method: handleGetReplyCount() </p>
     * * <p> Description: controls the creating of the post NOT IMPLEMENTED. </p>
     * * @param postId The ID of the post.
     * @return The number of replies.
     * */
    protected static void handleCreatePost() {
        ViewRole1Home.setupPostCreation();
    }
    /**********
     * <p> Method: handleViewPost(Post post) </p>
     * * <p> Description: Marks the post as read and opens the view post dialog. </p>
     * * @param post The post being viewed.
     * */
    protected static void handleViewPost(Post post) {
        // Use the guiViewPost MVC package to display post details
        guiViewPost.ViewViewPost.displayViewPost(post, ViewRole1Home.theUser.getUserName(), 
            () -> ViewRole1Home.loadPosts());
    }
    /**********
     * <p> Method: handleRefreshPage() </p>
     * * <p> Description: reloads the student discussion page. </p>
     * */
    protected static void handleRefreshPage() {
        ViewRole1Home.displayRole1Home(ViewRole1Home.theStage, ViewRole1Home.theUser, theDatabase);
    }

}

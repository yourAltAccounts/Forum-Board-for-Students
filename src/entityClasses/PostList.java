package entityClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: PostList Class </p>
 * 
 * <p> Description: Collection manager for Post objects with filtering, sorting, 
 * and search capabilities. Returns new PostList instances for immutability.</p>
 * 
 * <p> User Stories Supported:
 * - #3: Filter posts by thread
 * - #4: Search posts by keywords
 * - #5: Sort posts by date (newest/oldest)
 * - #6: Filter out deleted posts
 * - #8: View posts by specific author
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Luke Dempsey
 * @version 1.00    2025-10-24 TP2 Implementation
 */
public class PostList {
    
    private List<Post> posts;  // Internal post collection
    
    /*******
     * Creates empty PostList.
     */
    public PostList() {
        this.posts = new ArrayList<>();
    }
  
    /*******
     * Creates PostList from existing list (defensive copy).
     * @param posts List of posts to copy
     */
    public PostList(List<Post> posts) {
        this.posts = new ArrayList<>(posts);
    }
    
    /*******
     * Adds a post to this collection.
     * @param post Post to add (null ignored)
     */
    public void addPost(Post post) {
        if (post != null) {
            posts.add(post);
        }
    }
    
    /*******
     * Removes a post from collection.
     * @param post Post to remove
     * @return true if removed
     */
    public boolean removePost(Post post) {
        return posts.remove(post);
    }
    
    /*******
     * Removes post by ID. User Story #7: Delete posts
     * @param postId ID of post to remove
     * @return true if removed
     */
    public boolean removePostById(int postId) {
        return posts.removeIf(p -> p.getPostId() == postId);
    }
    
    /*******
     * Returns all posts (defensive copy).
     * @return List of all posts
     */
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }
    
    /*******
     * Gets specific post by ID. User Story #2: View post details
     * @param postId Post ID to find
     * @return Post with matching ID, or null if not found
     */
    public Post getPostById(int postId) {
        for (Post post : posts) {
            if (post.getPostId() == postId) {
                return post;
            }
        }
        return null;
    }
    
    /*******
     * Returns number of posts in collection.
     * @return Post count
     */
    public int size() {
        return posts.size();
    }
    
    /*******
     * Checks if collection is empty.
     * @return true if no posts
     */
    public boolean isEmpty() {
        return posts.isEmpty();
    }
    
    /*******
     * Removes all posts from collection.
     */
    public void clear() {
        posts.clear();
    }
    
    /*******
     * Filters posts by thread category.
     * User Story #3: Filter posts by thread
     * 
     * @param thread Thread name to filter by (case-insensitive)
     * @return New PostList with only matching posts
     */
    public PostList filterByThread(String thread) {
        List<Post> filtered = posts.stream()
                .filter(p -> p.getThread().equalsIgnoreCase(thread))
                .collect(Collectors.toList());
        return new PostList(filtered);
    }
    
    /*******
     * Filters posts by author.
     * User Story #8: View own posts
     * 
     * @param author Username to filter by (case-insensitive)
     * @return New PostList with only posts by this author
     */
     PostList filterByAuthor(String author) {
        List<Post> filtered = posts.stream()
                .filter(p -> p.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
        return new PostList(filtered);
    }
     
    /*******
     * Filters out deleted posts (soft delete pattern).
     * User Story #6: See only active posts
     * 
     * @return New PostList with only non-deleted posts
     */
    public PostList filterActive() {
        List<Post> filtered = posts.stream()
                .filter(p -> !p.isDeleted())
                .collect(Collectors.toList());
        return new PostList(filtered);
    }
    
    /*******
     * Searches posts by keywords (all keywords must match).
     * User Story #4: Search posts by keywords
     * 
     * @param searchText Space-separated keywords (empty returns all)
     * @return New PostList with matching posts
     */
    public PostList searchByKeywords(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new PostList(posts);
        }
        
        List<Post> filtered = posts.stream()
                .filter(p -> p.matchesAllKeywords(searchText))
                .collect(Collectors.toList());
        return new PostList(filtered);
    }
    
    /*******
     * Combined filter: thread + keyword search.
     * User Stories #3 and #4 combined
     * 
     * @param thread Thread to filter by ("All" or null for all threads)
     * @param searchText Keywords to search (empty for no search)
     * @return New PostList with both filters applied
     */
    public PostList filterByThreadAndSearch(String thread, String searchText) {
        PostList result = this;
        
        // Filter by thread if specified
        if (thread != null && !thread.trim().isEmpty() && !thread.equalsIgnoreCase("All")) {
            result = result.filterByThread(thread);
        }
        
        // Then search within results
        if (searchText != null && !searchText.trim().isEmpty()) {
            result = result.searchByKeywords(searchText);
        }
        
        return result;
    }
    
    /*******
     * Sorts posts by date, newest first.
     * User Story #5: Sort posts by date
     * 
     * @return New PostList sorted by timestamp descending
     */
    public PostList sortByNewest() {
        List<Post> sorted = new ArrayList<>(posts);
        sorted.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        return new PostList(sorted);
    }
    
    /*******
     * Sorts posts by date, oldest first.
     * User Story #5: Sort posts by date
     * 
     * @return New PostList sorted by timestamp ascending
     */
    public PostList sortByOldest() {
        List<Post> sorted = new ArrayList<>(posts);
        sorted.sort((p1, p2) -> p1.getTimestamp().compareTo(p2.getTimestamp()));
        return new PostList(sorted);
    }
    
    @Override
    public String toString() {
        return "PostList{count=" + posts.size() + " posts}";
    }
}

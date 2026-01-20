package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Post Class </p>
 * 
 * <p> Description: Represents a discussion post in the student forum system.
 * Includes all post data (title, content, author, etc.) and methods for searching
 * and managing posts. Supports the Student User Stories for creating, viewing,
 * searching, and deleting posts.</p>
 * 
 * <p> User Stories Supported:
 * - #1: Create posts with title and content
 * - #2: View post details 
 * - #3: Categorize posts by thread
 * - #4: Search posts by keywords
 * - #7: Delete posts (soft delete - marks as deleted but keeps in database)
 * </p>
 * 
 * <p> Attributes and Rationale:
 * Each attribute was chosen to support specific User Stories and database requirements:
 * 
 * postId: Unique identifier assigned by database. Source: Database primary key requirement.
 * Rationale: Enables efficient post lookups and maintains referential integrity with replies.
 * 
 * author: Username of post creator. Source: User Story #2 (view who posted).
 * Rationale: Links post to creator, enables filtering by author, controls edit permissions.
 * 
 * title: Post subject line. Source: User Story #1 (create post with title).
 * Rationale: Provides quick topic overview in post listings, included in keyword searches.
 * 
 * content: Main post body. Source: User Story #1 (create post with content).
 * Rationale: Contains the actual discussion message, searchable for keywords.
 * 
 * thread: Category/topic area. Source: User Story #3 (filter by thread).
 * Rationale: Organizes posts into categories like "General" or "Questions" for easier browsing.
 * 
 * timestamp: Creation time. Source: User Story #5 (sort by date).
 * Rationale: Enables chronological sorting, shows post age to users.
 * 
 * isDeleted: Soft delete flag. Source: User Story #7 (delete posts) and #6 (hide deleted).
 * Rationale: Marks posts as deleted without removing from database, preserving audit trail
 * and maintaining reply links.
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Luke Dempsey
 * @version 1.00    2025-10-24 TP2 Implementation
 */
public class Post {
    
    /*******
     * Unique post identifier assigned by database.
     * Source: Database primary key requirement
     * Rationale: Enables efficient lookups and maintains referential integrity
     */
    private int postId;
    
    /*******
     * Username of post creator.
     * Source: User Story #2 (view post author)
     * Rationale: Links post to creator, enables author filtering
     */
    private String author;
    
    /*******
     * Post title/subject line.
     * Source: User Story #1 (create post with title)
     * Rationale: Quick topic overview, included in keyword searches
     */
    private String title;
    
    /*******
     * Main post body text.
     * Source: User Story #1 (create post with content)
     * Rationale: Contains discussion message, searchable
     */
    private String content;
    
    /*******
     * Thread/category (e.g., "General", "Questions").
     * Source: User Story #3 (filter by thread)
     * Rationale: Organizes posts for easier browsing
     */
    private String thread;
    
    /*******
     * Post creation timestamp.
     * Source: User Story #5 (sort by date)
     * Rationale: Enables chronological sorting
     */
    private LocalDateTime timestamp;
    
    /*******
     * Soft delete flag (true = deleted).
     * Source: User Story #7 (delete posts), #6 (hide deleted)
     * Rationale: Marks deleted without removing from DB, preserves audit trail
     */
    private boolean isDeleted;
    
    /*******
     * Default constructor - creates empty post with defaults.
     */
    public Post() {
        this.postId = -1;
        this.author = "";
        this.title = "";
        this.content = "";
        this.thread = "General";
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
    }
    
    /*******
     * Constructor for creating a new post.
     * User Story #1: Create posts
     * 
     * @param author Username of creator
     * @param title Post title
     * @param content Post body
     * @param thread Category/thread name
     */
    public Post(String author, String title, String content, String thread) {
        this.postId = -1;  // Database assigns this
        this.author = author;
        this.title = title;
        this.content = content;
        this.thread = thread;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
    }
    
    /*******
     * Full constructor - used when loading from database.
     * 
     * @param postId Database ID
     * @param author Username
     * @param title Post title
     * @param content Post body
     * @param thread Category
     * @param timestamp Creation time
     * @param isDeleted Delete status
     */
    public Post(int postId, String author, String title, String content, 
                String thread, LocalDateTime timestamp, boolean isDeleted) {
        this.postId = postId;
        this.author = author;
        this.title = title;
        this.content = content;
        this.thread = thread;
        this.timestamp = timestamp;
        this.isDeleted = isDeleted;
    }
    
    // ==================== CRUD Operations ====================
    
    // READ operations - getters for all attributes
    /**
     * getter function for grabbing the post id.
     * @return postId the posts identification
     */
    public int getPostId() 
    { 
    	return postId; 
    }
    /**
     * getter function to grab the authors name.
     * @return author the name of creator
     */
    public String getAuthor() 
    { 
    	return author; 
    }
    /**
     * getter function to grab the title of the post.
     * @return title the header of the post
     */
    public String getTitle() 
    { 
    	return title; 
    }
    /**
     * getter function to grab the content of the post.
     * @return content the body 
     */
    public String getContent() 
    { 
    	return content; 
    }
    /**getter function to grab the thread of the post.
     * 
     * @return thread	the category
     */
    public String getThread() 
    { 
    	return thread; 
    }
    /**
     * getter function to grab the current timestamp post was made.
     * @return timestamp the time
     */
    public LocalDateTime getTimestamp() 
    { 
    	return timestamp; 
    	
    }
    /**
     * getter function to flag a post deletion.
     * @return isDeleted a flag boolean
     */
    public boolean isDeleted() 
    { 
    	return isDeleted; 
    }
    
    // UPDATE operations - setters for modifying post data
    /**
     * set the post with an id
     * @param postId the code for the post
     * 
     */
    public void setPostId(int postId) 
    { 
    	this.postId = postId;
    }
    /**
     * set the post with an author
     * @param author the name of creator
     */
    public void setAuthor(String author) 
    { 
    	this.author = author; 
    }
    /**
     * set the post with a title
     * @param title the header of the post
     */
    public void setTitle(String title) 
    { 
    	this.title = title; 
    }
    /**
     * set the post with a body
     * @param content the body
     */
    public void setContent(String content) 
    { 
    	this.content = content; 
    	
    }
    /**
     * set the thread of a post
     * @param thread the category
     */
    public void setThread(String thread) 
    { 
    	this.thread = thread; 
    }
    /**
     * set the timestamp of the post
     * @param timestamp the time
     */
    public void setTimestamp(LocalDateTime timestamp) 
    { 
    	this.timestamp = timestamp; 
    }
    
    /*******
     * DELETE operation - marks post as deleted (soft delete).
     * User Story #7: Delete posts
     * @param deleted true to mark as deleted, false to restore
     */
    public void setDeleted(boolean deleted) { 
        isDeleted = deleted;  // Soft delete - keeps post in DB but hides from users
    }
    
    /*******
     * Returns formatted timestamp for display.
     * User Story #2: View post details
     * @return Timestamp in format "yyyy-MM-dd HH:mm:ss"
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /*******
     * Checks if post matches a keyword (case-insensitive).
     * User Story #4: Search posts
     * 
     * @param keyword Word to search for
     * @return true if keyword found in title or content
     */
    public boolean matchesKeyword(String keyword) {
        // Empty keyword matches everything (used for "clear search")
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        
        // Convert to lowercase for case-insensitive comparison
        String lowerKeyword = keyword.toLowerCase().trim();
        String lowerTitle = title.toLowerCase();
        String lowerContent = content.toLowerCase();
        
        // Search both title and content fields
        return lowerTitle.contains(lowerKeyword) || lowerContent.contains(lowerKeyword);
    }
    
    /*******
     * Checks if post matches ALL keywords (AND logic, not OR).
     * User Story #4: Search posts by keywords
     * 
     * @param searchText Space-separated keywords
     * @return true if all keywords found in post
     */
    public boolean matchesAllKeywords(String searchText) {
        // Empty search matches everything
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }
        
        // Split into individual keywords by whitespace
        String[] keywords = searchText.trim().split("\\s+");
        
        // All keywords must match (AND logic)
        for (String keyword : keywords) {
            if (!matchesKeyword(keyword)) {
                return false;  // Early exit if any keyword doesn't match
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", thread='" + thread + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                ", isDeleted=" + isDeleted +
                '}';
    }
}

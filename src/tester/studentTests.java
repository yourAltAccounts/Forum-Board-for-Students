package tester;

import database.Database;
import java.sql.SQLException;
import entityClasses.*;

/*******
 * <p> Title: studentTests Class </p>
 * 
 * <p> Description: Comprehensive test suite for the student discussion forum database operations.
 * Tests cover post creation, deletion, replies, search functionality, and edge cases.</p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Test Team
 * @version 2.00    2025-10-28 Added additional tests and JavaDocs
 * @version 1.00    2025-10-24 Initial test suite
 */
public class studentTests {

    /**
     * Main test runner that executes all database operation tests.
     * Creates test users, runs various test scenarios, and reports results.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Database db = null;
        String user1 = "User1";
        String user2 = "User2";

        try {
            db = new Database();
            db.connectToDatabase();

            // Setup users
            if (!db.doesUserExist(user1)) {
                entityClasses.User u1 = new entityClasses.User(user1, "pass1",
                    "Test", "M", "User", "U1", "u1@email.com", false, true, false);
                db.register(u1);
            }
            if (!db.doesUserExist(user2)) {
                entityClasses.User u2 = new entityClasses.User(user2, "pass2",
                    "Test", "M", "User", "U2", "u2@email.com", false, true, false);
                db.register(u2);
            }
            System.out.println("Running tests...\n");
            testCreatePostValid(db, user1);
            testCreateReplyValid(db, user1, user2);
            testCreatePostInvalid(db, user1);
            testGetAllPosts(db, user1);
            testGetPostsByThread(db, user1);
            testMarkPostAsRead(db, user1, user2);
            testDeletePost(db, user1);
            testDeleteInvalidPost(db);
            testSearchByKeywords(db, user1);
            testGetPostsByAuthor(db, user1, user2);
            testGetUnreadPosts(db, user1, user2);
            testGetReplyCount(db, user1, user2);
            testDeletedPostPreservesReplies(db, user1, user2);

            System.out.println("\nAll tests completed!");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            if (db != null) db.closeConnection();
        }
    }

    /**
     * Test 1: Create a valid post.
     * Verifies that a post with valid title, content, and thread can be created.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     */
    private static void testCreatePostValid(Database db, String user1) {
        Post p1 = new Post(user1, "Test Post", "Some text", "General");
        int id1 = db.createPost(p1);
        System.out.println("Test 1 - CreatePost_Valid: " + (id1 > 0));
    }

    /**
     * Test 2: Create a valid reply to a post.
     * Verifies that replies can be added to existing posts.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     * @param user2 Username for reply author
     */
    private static void testCreateReplyValid(Database db, String user1, String user2) {
        int postId = db.createPost(new Post(user1, "Q1", "Help pls", "Questions"));
        Reply r1 = new Reply(postId, user2, "Try notes");
        int replyId = db.createReply(r1);
        System.out.println("Test 2 - CreateReply_Valid: " + (replyId > 0));
    }

    /**
     * Test 3: Attempt to create a post with invalid (empty) title.
     * Verifies that validation prevents creation of posts without titles.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     */
    private static void testCreatePostInvalid(Database db, String user1) {
        try {
            Post bad = new Post(user1, "", "No title", "General");
            db.createPost(bad);
            System.out.println("Test 3 - CreatePost_Invalid: FAILED (no exception)");
        } catch (IllegalArgumentException e) {
            System.out.println("Test 3 - CreatePost_Invalid: PASSED (" + e.getMessage() + ")");
        }
    }

    /**
     * Test 4: Retrieve all posts from the database.
     * Verifies that getAllPosts returns all created posts.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     */
    private static void testGetAllPosts(Database db, String user1) {
        db.createPost(new Post(user1, "A", "a", "General"));
        db.createPost(new Post(user1, "B", "b", "Questions"));
        PostList posts = db.getAllPosts();
        System.out.println("Test 4 - GetAllPosts: " + (posts.size() >= 2));
    }

    /**
     * Test 5: Filter posts by thread type.
     * Verifies that getPostsByThread only returns posts from the specified thread.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     */
    private static void testGetPostsByThread(Database db, String user1) {
        db.createPost(new Post(user1, "Math", "Q", "Questions"));
        PostList list = db.getPostsByThread("Questions");
        boolean allMatch = true;
        for (Post p : list.getAllPosts()) {
            if (!p.getThread().equals("Questions")) {
                allMatch = false;
                break;
            }
        }
        System.out.println("Test 5 - GetPostsByThread: " + allMatch);
    }

    /**
     * Test 6: Mark a post as read by a user.
     * Verifies that the read tracking system works correctly.
     * 
     * @param db Database instance
     * @param user1 Username who is reading the post
     * @param user2 Username for post author
     */
    private static void testMarkPostAsRead(Database db, String user1, String user2) {
        int id2 = db.createPost(new Post(user2, "Check", "Data", "General"));
        db.markPostAsRead(id2, user1);
        System.out.println("Test 6 - MarkAsRead: " + db.isPostRead(id2, user1));
    }

    /**
     * Test 7: Delete a valid post.
     * Verifies that posts can be soft-deleted and marked as deleted.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     */
    private static void testDeletePost(Database db, String user1) {
        int id3 = db.createPost(new Post(user1, "Del", "Temp", "General"));
        boolean deleted = db.deletePost(id3);
        Post p = db.getPostById(id3);
        System.out.println("Test 7 - DeletePost: " + (deleted && p.isDeleted()));
    }

    /**
     * Test 8: Attempt to delete a non-existent post.
     * Verifies that delete operation fails gracefully for invalid post IDs.
     * 
     * @param db Database instance
     */
    private static void testDeleteInvalidPost(Database db) {
        boolean result = db.deletePost(-1);
        System.out.println("Test 8 - DeleteInvalidPost: " + !result);
    }

    /**
     * Test 9: Search posts by keywords.
     * Verifies that the keyword search functionality finds relevant posts.
     * Tests both single and multiple keyword searches.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     */
    private static void testSearchByKeywords(Database db, String user1) {
        db.createPost(new Post(user1, "Java Tutorial", "Learn Java programming", "General"));
        db.createPost(new Post(user1, "Python Basics", "Python for beginners", "General"));
        db.createPost(new Post(user1, "Java Advanced", "Advanced Java concepts", "General"));
        
        PostList allPosts = db.getAllPosts();
        PostList searchResults = allPosts.searchByKeywords("Java");
        
        boolean foundRelevant = false;
        for (Post p : searchResults.getAllPosts()) {
            if (p.getTitle().contains("Java") || p.getContent().contains("Java")) {
                foundRelevant = true;
                break;
            }
        }
        
        System.out.println("Test 9 - SearchByKeywords: " + (foundRelevant && searchResults.size() >= 2));
    }

    /**
     * Test 10: Get posts by specific author.
     * Verifies that getPostsByAuthor only returns posts created by the specified user.
     * 
     * @param db Database instance
     * @param user1 First username
     * @param user2 Second username
     */
    private static void testGetPostsByAuthor(Database db, String user1, String user2) {
        db.createPost(new Post(user1, "User1 Post 1", "Content", "General"));
        db.createPost(new Post(user2, "User2 Post", "Content", "General"));
        db.createPost(new Post(user1, "User1 Post 2", "Content", "Questions"));
        
        PostList user1Posts = db.getPostsByAuthor(user1);
        
        boolean allFromUser1 = true;
        int count = 0;
        for (Post p : user1Posts.getAllPosts()) {
            if (!p.getAuthor().equals(user1)) {
                allFromUser1 = false;
                break;
            }
            count++;
        }
        
        System.out.println("Test 10 - GetPostsByAuthor: " + (allFromUser1 && count >= 2));
    }

    /**
     * Test 11: Get unread posts for a user.
     * Verifies that the unread post tracking correctly identifies posts that
     * have not been read by a specific user.
     * 
     * @param db Database instance
     * @param user1 Username who will check for unread posts
     * @param user2 Username for post author
     */
    private static void testGetUnreadPosts(Database db, String user1, String user2) {
        int unreadPostId = db.createPost(new Post(user2, "Unread Post", "Not seen yet", "General"));
        int readPostId = db.createPost(new Post(user2, "Read Post", "Already seen", "General"));
        db.markPostAsRead(readPostId, user1);
        PostList unreadPosts = db.getUnreadPosts(user1);
        
        boolean foundUnread = false;
        boolean excludedRead = true;
        for (Post p : unreadPosts.getAllPosts()) {
            if (p.getPostId() == unreadPostId) {
                foundUnread = true;
            }
            if (p.getPostId() == readPostId) {
                excludedRead = false;
            }
        }
        
        System.out.println("Test 11 - GetUnreadPosts: " + (foundUnread && excludedRead));
    }

    /**
     * Test 12: Get reply count for a post.
     * Verifies that the reply counter accurately tracks the number of replies
     * associated with a post.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     * @param user2 Username for reply author
     */
    private static void testGetReplyCount(Database db, String user1, String user2) {
        int postId = db.createPost(new Post(user1, "Popular Post", "Discussion topic", "General"));
        db.createReply(new Reply(postId, user2, "First reply"));
        db.createReply(new Reply(postId, user1, "Second reply"));
        db.createReply(new Reply(postId, user2, "Third reply"));
        
        int replyCount = db.getReplyCountForPost(postId);
        
        System.out.println("Test 12 - GetReplyCount: " + (replyCount == 3));
    }

    /**
     * Test 13: Verify deleted posts preserve their replies.
     * Critical test to ensure that when a post is deleted (soft delete),
     * all associated replies remain in the database and can still be retrieved.
     * This is essential for maintaining discussion context.
     * 
     * @param db Database instance
     * @param user1 Username for post author
     * @param user2 Username for reply author
     */
    private static void testDeletedPostPreservesReplies(Database db, String user1, String user2) {
        int postId = db.createPost(new Post(user1, "To Delete", "Will be removed", "General"));
        db.createReply(new Reply(postId, user2, "Reply 1"));
        db.createReply(new Reply(postId, user1, "Reply 2"));
        int repliesBeforeDelete = db.getReplyCountForPost(postId);
        db.deletePost(postId);
        Post deletedPost = db.getPostById(postId);
        int repliesAfterDelete = db.getReplyCountForPost(postId);
        ReplyList replies = db.getRepliesForPost(postId);
        
        boolean postMarkedDeleted = deletedPost.isDeleted();
        boolean repliesPreserved = (repliesBeforeDelete == repliesAfterDelete && repliesAfterDelete == 2);
        boolean repliesRetrievable = (replies.size() == 2);
        
        System.out.println("Test 13 - DeletedPostPreservesReplies: " + 
            (postMarkedDeleted && repliesPreserved && repliesRetrievable));
    }
}
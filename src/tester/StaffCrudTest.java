package tester;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import guiRole2.ModelRole2Home;
import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.User;
import database.Database;
import java.sql.SQLException;
import java.lang.reflect.Field;

/**
 * <p>Title: StaffCrudTest Class</p>
 * 
 * <p>Description: JUnit test class for validating CRUD (Create, Read, Update, Delete) 
 * operations in the Staff Discussion Forum. Tests cover post creation, retrieval, 
 * updating, deletion, filtering, searching, and permission checks.</p>
 * 
 * @author CSE 360 Team
 * @version 1.00 2025-11-29 Initial implementation
 */
public class StaffCrudTest {
    
    private Database db;
    private User testUser;
    
    /**
     * Sets up the test environment before each test method.
     * Establishes a database connection, initializes the static database reference
     * used by ModelRole2Home, and creates a test user for staff operations.
     */
    @Before
    public void setUp() {
        try {
            db = new Database();
            db.connectToDatabase();
            
            // Set the static database reference that ModelRole2Home uses
            // Use reflection to avoid loading JavaFX classes
            setFoundationsMainDatabase(db);
            
            try {
                Field modelDbField = ModelRole2Home.class.getDeclaredField("theDatabase"); // matches variable name in Model
                modelDbField.setAccessible(true);
                modelDbField.set(null, db); // Set the static field to the NEW db instance
            } catch (Exception e) {
                fail("Could not reset ModelRole2Home database reference: " + e.getMessage());
            }
            
            // Create test user - database is fresh each time, so user won't exist
            testUser = new User("test_staff", "password123", "Test", "", "Staff", 
                              "TestStaff", "test@example.com", false, false, true);
            
            // Register the test user
            try {
                db.register(testUser);
            } catch (SQLException e) {
                // If user already exists (shouldn't happen with fresh DB), that's okay
                if (!db.doesUserExist("test_staff")) {
                    e.printStackTrace();
                    fail("Failed to register test user: " + e.getMessage());
                }
            }
            
            // Verify user was created
            if (!db.doesUserExist("test_staff")) {
                fail("Test user 'test_staff' was not created successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database setup failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected error in setUp: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to set FoundationsMain.database using reflection.
     * This avoids loading the JavaFX Application class which isn't available in test runtime.
     */
    private void setFoundationsMainDatabase(Database database) {
        try {
            Class<?> foundationsMainClass = Class.forName("applicationMain.FoundationsMain");
            Field databaseField = foundationsMainClass.getField("database");
            databaseField.set(null, database);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to set FoundationsMain.database: " + e.getMessage());
        }
    }
    
    /**
     * Tears down the test environment after each test method.
     * Closes the database connection.
     */
    @After
    public void tearDown() {
        if (db != null) {
            db.closeConnection();
            // Clear the static reference to prevent stale connections
            try {
                setFoundationsMainDatabase(null);
            } catch (Exception e) {
                // Ignore errors when clearing
            }
        }
    }
    
    /**
     * Test: Create a valid post successfully.
     * Verifies that a post with valid title, content, and thread can be created
     * and returns a valid post ID.
     * 
     * @see ModelRole2Home#createPost(Post)
     */
    @Test
    public void testCreatePostSuccess() {
        try {
            // Verify database is set up
            assertNotNull("Database should not be null", db);
            assertTrue("Test user should exist", db.doesUserExist("test_staff"));
            
            Post post = new Post("test_staff", "Test Post Title", "Test post content", "General");
            int postId = ModelRole2Home.createPost(post);
            
            assertTrue("Post ID should be greater than 0, got: " + postId, postId > 0);
            
            Post retrieved = ModelRole2Home.getPostById(postId);
            assertNotNull("Post should be retrievable", retrieved);
            assertEquals("Title should match", "Test Post Title", retrieved.getTitle());
            assertEquals("Content should match", "Test post content", retrieved.getContent());
            assertEquals("Author should match", "test_staff", retrieved.getAuthor());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
    
    /**
     * Test: Attempt to create a post with null parameter.
     * Verifies that the method handles null input gracefully and returns -1.
     * 
     * @see ModelRole2Home#createPost(Post)
     */
    @Test
    public void testCreatePostNull() {
        int postId = ModelRole2Home.createPost(null);
        assertEquals("Should return -1 for null post", -1, postId);
    }
    
    /**
     * Test: Retrieve a post by its ID.
     * Verifies that an existing post can be retrieved using its post ID.
     * 
     * @see ModelRole2Home#getPostById(int)
     */
    @Test
    public void testGetPostById() {
        Post post = new Post("test_staff", "Retrieval Test", "Content for retrieval", "Questions");
        int postId = ModelRole2Home.createPost(post);
        
        Post retrieved = ModelRole2Home.getPostById(postId);
        assertNotNull("Post should be retrieved", retrieved);
        assertEquals("Post ID should match", postId, retrieved.getPostId());
        assertEquals("Title should match", "Retrieval Test", retrieved.getTitle());
    }
    
    /**
     * Test: Retrieve a post with invalid ID.
     * Verifies that retrieving a non-existent post returns null.
     * 
     * @see ModelRole2Home#getPostById(int)
     */
    @Test
    public void testGetPostByIdInvalid() {
        Post retrieved = ModelRole2Home.getPostById(99999);
        assertNull("Should return null for non-existent post", retrieved);
    }
    
    /**
     * Test: Update an existing post successfully.
     * Verifies that a post's title and content can be updated.
     * 
     * @see ModelRole2Home#updatePost(Post)
     */
    @Test
    public void testUpdatePostSuccess() {
        Post post = new Post("test_staff", "Original Title", "Original content", "General");
        int postId = ModelRole2Home.createPost(post);
        
        Post updatedPost = ModelRole2Home.getPostById(postId);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated content");
        
        boolean success = ModelRole2Home.updatePost(updatedPost);
        assertTrue("Update should succeed", success);
        
        Post retrieved = ModelRole2Home.getPostById(postId);
        assertEquals("Title should be updated", "Updated Title", retrieved.getTitle());
        assertEquals("Content should be updated", "Updated content", retrieved.getContent());
    }
    
    /**
     * Test: Attempt to update a post with null parameter.
     * Verifies that the method handles null input gracefully and returns false.
     * 
     * @see ModelRole2Home#updatePost(Post)
     */
    @Test
    public void testUpdatePostNull() {
        boolean success = ModelRole2Home.updatePost(null);
        assertFalse("Should return false for null post", success);
    }
    
    /**
     * Test: Delete a post successfully (soft delete).
     * Verifies that a post can be marked as deleted and is filtered out from results.
     * 
     * @see ModelRole2Home#deletePost(int)
     */
    @Test
    public void testDeletePostSuccess() {
        Post post = new Post("test_staff", "Post to Delete", "Content to delete", "General");
        int postId = ModelRole2Home.createPost(post);
        
        boolean success = ModelRole2Home.deletePost(postId);
        assertTrue("Delete should succeed", success);
        
        Post deleted = ModelRole2Home.getPostById(postId);
        assertNotNull("Post should still exist in database", deleted);
        assertTrue("Post should be marked as deleted", deleted.isDeleted());
        
        // Verify deleted post is filtered out
        PostList allPosts = ModelRole2Home.getFilteredPosts(testUser, "All", "");
        for (Post p : allPosts.getAllPosts()) {
            assertNotEquals("Deleted post should not appear in filtered results", 
                          postId, p.getPostId());
        }
    }
    
    /**
     * Test: Attempt to delete a post with invalid ID.
     * Verifies that deleting a non-existent post returns false.
     * 
     * @see ModelRole2Home#deletePost(int)
     */
    @Test
    public void testDeletePostInvalidId() {
        boolean success = ModelRole2Home.deletePost(99999);
        assertFalse("Should return false for non-existent post", success);
    }
    
    /**
     * Test: Filter posts by thread.
     * Verifies that posts can be filtered by thread category (General, Questions, etc.).
     * 
     * @see ModelRole2Home#getFilteredPosts(User, String, String)
     */
    @Test
    public void testGetFilteredPostsByThread() {
        Post post1 = new Post("test_staff", "General Post", "Content", "General");
        Post post2 = new Post("test_staff", "Question Post", "Content", "Questions");
        ModelRole2Home.createPost(post1);
        ModelRole2Home.createPost(post2);
        
        PostList generalPosts = ModelRole2Home.getFilteredPosts(testUser, "General", "");
        PostList questionPosts = ModelRole2Home.getFilteredPosts(testUser, "Questions", "");
        
        assertTrue("Should have at least one General post", generalPosts.size() >= 1);
        assertTrue("Should have at least one Questions post", questionPosts.size() >= 1);
        
        // Verify all posts in General filter are actually General
        for (Post p : generalPosts.getAllPosts()) {
            assertEquals("All posts should be General thread", 
                        "General", p.getThread());
        }
    }
    
    /**
     * Test: Filter posts by "All" thread.
     * Verifies that selecting "All" returns posts from all threads.
     * 
     * @see ModelRole2Home#getFilteredPosts(User, String, String)
     */
    @Test
    public void testGetFilteredPostsAll() {
        Post post1 = new Post("test_staff", "Post 1", "Content", "General");
        Post post2 = new Post("test_staff", "Post 2", "Content", "Questions");
        ModelRole2Home.createPost(post1);
        ModelRole2Home.createPost(post2);
        
        PostList allPosts = ModelRole2Home.getFilteredPosts(testUser, "All", "");
        assertTrue("Should have at least 2 posts", allPosts.size() >= 2);
    }
    
    /**
     * Test: Filter posts by "MyPosts" thread.
     * Verifies that selecting "MyPosts" returns only posts by the current user.
     * 
     * @see ModelRole2Home#getFilteredPosts(User, String, String)
     */
    @Test
    public void testGetFilteredPostsMyPosts() {
        Post myPost = new Post("test_staff", "My Post", "Content", "General");
        ModelRole2Home.createPost(myPost);
        
        PostList myPosts = ModelRole2Home.getFilteredPosts(testUser, "MyPosts", "");
        assertTrue("Should have at least one post", myPosts.size() >= 1);
        
        for (Post p : myPosts.getAllPosts()) {
            assertEquals("All posts should be by test_staff", 
                        "test_staff", p.getAuthor());
        }
    }
    
    /**
     * Test: Search posts by keywords.
     * Verifies that posts can be filtered by search keywords in title or content.
     * 
     * @see ModelRole2Home#getFilteredPosts(User, String, String)
     */
    @Test
    public void testGetFilteredPostsWithSearch() {
        Post post1 = new Post("test_staff", "Java Programming", "Learn Java basics", "General");
        Post post2 = new Post("test_staff", "Python Tutorial", "Python programming guide", "General");
        ModelRole2Home.createPost(post1);
        ModelRole2Home.createPost(post2);
        
        PostList javaPosts = ModelRole2Home.getFilteredPosts(testUser, "All", "Java");
        assertTrue("Should find Java post", javaPosts.size() >= 1);
        
        boolean foundJava = false;
        for (Post p : javaPosts.getAllPosts()) {
            if (p.getTitle().contains("Java")) {
                foundJava = true;
                break;
            }
        }
        assertTrue("Should contain Java post", foundJava);
    }
    
    /**
     * Test: Check if user can edit their own post.
     * Verifies that the permission check allows authors to edit their own posts.
     * 
     * @see ModelRole2Home#canEditPost(Post, String)
     */
    @Test
    public void testCanEditPost() {
        Post post = new Post("test_staff", "Editable Post", "Content", "General");
        int postId = ModelRole2Home.createPost(post);
        Post retrieved = ModelRole2Home.getPostById(postId);
        
        boolean canEdit = ModelRole2Home.canEditPost(retrieved, "test_staff");
        assertTrue("Author should be able to edit their own post", canEdit);
    }
    
    /**
     * Test: Check if user cannot edit another user's post.
     * Verifies that the permission check prevents non-authors from editing posts.
     * 
     * @see ModelRole2Home#canEditPost(Post, String)
     */
    @Test
    public void testCanEditPostNotAuthor() {
        Post post = new Post("test_staff", "Other User Post", "Content", "General");
        int postId = ModelRole2Home.createPost(post);
        Post retrieved = ModelRole2Home.getPostById(postId);
        
        boolean canEdit = ModelRole2Home.canEditPost(retrieved, "other_user");
        assertFalse("Non-author should not be able to edit post", canEdit);
    }
    
    /**
     * Test: Check if deleted post cannot be edited.
     * Verifies that deleted posts cannot be edited even by the author.
     * 
     * @see ModelRole2Home#canEditPost(Post, String)
     */
    @Test
    public void testCanEditPostDeleted() {
        Post post = new Post("test_staff", "Deleted Post", "Content", "General");
        int postId = ModelRole2Home.createPost(post);
        ModelRole2Home.deletePost(postId);
        Post deleted = ModelRole2Home.getPostById(postId);
        
        boolean canEdit = ModelRole2Home.canEditPost(deleted, "test_staff");
        assertFalse("Deleted post should not be editable", canEdit);
    }
    
    /**
     * Test: Get reply count for a post.
     * Verifies that the reply count is correctly retrieved for a post.
     * 
     * @see ModelRole2Home#getPostReplyCount(int)
     */
    @Test
    public void testGetPostReplyCount() {
        Post post = new Post("test_staff", "Post with Replies", "Content", "General");
        int postId = ModelRole2Home.createPost(post);
        
        int count = ModelRole2Home.getPostReplyCount(postId);
        assertTrue("Reply count should be non-negative", count >= 0);
    }
}


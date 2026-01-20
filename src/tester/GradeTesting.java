package tester;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import entityClasses.ModerationConfig;
import entityClasses.ModerationFlag;
import entityClasses.PrivateMessage;
import entityClasses.Reply;
import entityClasses.Post;
import entityClasses.User;
import database.Database;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>Title: GradeTesting Class</p>
 * <p>Description: JUnit test class for validating the grading logic in the forum-style application. 
 * These tests verify that user grades are calculated correctly based on the number of posts and replies
 * written by each student, and the parameters set for grading (via {@link ModerationConfig}) work as expected. </p>
 * * @author CSE 360 Team
 * @version 1.00 2025-11-29 Initial implementation
 */
public class GradeTesting {
	
    private Database db;

    /**
     * Sets up the database and testing environment before each test method.
     * <ul>
     *   <li>Establishes a live database connection</li>
     *   <li>Initializes moderation tables</li>
     *   <li>Creates a staff user and a student user</li>
     * </ul>
     * 
     * If the setup fails at any point, an assertion error is thrown.
     */
    @Before
    public void setUp() {
        try {
            db = new Database();
            db.connectToDatabase();
            db.initializeModerationTables();
            
            createTestUser("staff_jones", "Staff123!", false, true);
            createTestUser("student_smith", "Student123!", true, false);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database setup failed: " + e.getMessage());
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
        }
    }
    
    /**
     * Helper method to create and register a test user in the database.
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @param role1 If true, grants the user Role 1 (e.g., Student).
     * @param role2 If true, grants the user Role 2 (e.g., Staff).
     * 
     * <p>This method register the user in the database and throws an exception if the registration
     * fails for any reason.</p>
     */
    private void createTestUser(String username, String password, boolean role1, boolean role2) {
        try {
            User user = new User(username, password, "Test", "", "User", username, 
                               username + "@test.com", false, role1, role2);
            db.register(user);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to create test user: " + username);
        }
    }
    
    /**
     * Tests the grade calculation with default parameters when the student has not made any posts or replies.
     * <p>The student should receive a 0 as their overall grade.</p>
     */
    @Test
    public void testNoPostsGrade() {    	
    	assertEquals(
    		db.getOverallGrade("student_smith"), 
    		0, 
    		"Grade should be 0 because no posts or replies are made."
    	);
    }
    
    /**
     * Tests the grade calculation with default parameters when the student has completed one post but zero replies.
     * <p>Completion of all the posts required by the grading parameters counts for 60% of the overall grade.</p>
     */
    @Test
    public void test1PostNoRepliesGrade() {
        Post testPost = new Post("student_smith", "Test Post", "Test content", "General");
        db.createPost(testPost);

        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	60, 
        	"Grade should be 60 because completing all required posts is 60% of the total grade."
        );
    }
   
    /**
     * Tests the grade calculation with default parameters when the student has zero posts but completes one reply.
     * <p>Completion of all the peer replies required by grading parameters counts for 40% of the overall grade.</p>
     */
    @Test
    public void testNoPost1ReplyGrade() {
        Post testPost = new Post("staff_jones", "Test Post", "Test content", "General");
        int postId = db.createPost(testPost);
        
        Reply testReply = new Reply(postId, "student_smith", "Test content");
        db.createReply(testReply);

        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	40, 
        	"Grade should be 40 because completing all required peer responses is 40% of the total grade."
        );
    }

    /**
     * Tests the grade calculation with default parameters when the student completes both one post and one reply.
     * <p>This results in the student receiving a 100% for completing all the required work.</p>
     */
    @Test
    public void test1Post1ReplyGrade() {
        Post testPost = new Post("student_smith", "Test Post", "Test content", "General");
        int postId = db.createPost(testPost);
        
        Reply testReply = new Reply(postId, "student_smith", "Test content");
        db.createReply(testReply);

        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	100, 
        	"Grade should be 100 for completing the required posts and replies."
        );
    }

    /**
     * Tests the grade calculation with default parameters when a student submits 2 posts and 2 replies, more than the 
     * required posts and replies in this case. 
     * <p>Students will still receive a 100%, set as the maximum grade they can achieve.</p>
     */
    @Test
    public void test2Post2ReplyGrade() {
        Post testPost = new Post("student_smith", "Test Post", "Test content", "General");
        db.createPost(testPost);
        testPost = new Post("student_smith", "Test Post", "Test content", "General");
        int postId = db.createPost(testPost);
        
        Reply testReply = new Reply(postId, "student_smith", "Test content");
        db.createReply(testReply);
        testReply = new Reply(postId, "student_smith", "Test content");
        db.createReply(testReply);

        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	100, 
        	"Grade should still be 100 for completing the required posts and replies since grade cannot exceed 100."
        );
    }
    
    /**
     * Tests the grade calculation with default parameters when the student completes both one post and one reply, 
     * but then proceeds to delete their post. 
     * <p>This results in the student receiving a 100% for completing all the required work, 
     * but that grade then dropping to a 40% after the deleted post leaves the student with
     * zero posts and one reply.</p>
     */
    @Test
    public void testDeletePostGrade() {
        Post testPost = new Post("student_smith", "Test Post", "Test content", "General");
        int postId = db.createPost(testPost);
        
        Reply testReply = new Reply(postId, "student_smith", "Test content");
        db.createReply(testReply);

        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	100, 
        	"Grade should be 100 for completing the required posts and replies."
        );
        
        db.deletePost(postId);
        
        assertEquals(
            db.getOverallGrade("student_smith"), 
            40, 
            "Grade should be 40 after losing the points for the post after deleting it."
        );
    }


    /**
     * This test configures the parameters for grading:
     * <ul>
     *   <li>2 required posts</li>
     *   <li>2 required replies</li>
     *   <li>70% of grade from posts</li>
     *   <li>85% completion threshold</li>
     * </ul>
     *
     * The test verifies grading with these parameters at each progression stage:
     * <ul>
     *   <li>One post completed</li>
     *   <li>One post + one reply</li>
     *   <li>One post + two replies</li>
     *   <li>Two posts + two replies (full completion)</li>
     * </ul>
     */
    @Test
    public void testNewParametersGrade() {
    	ModerationConfig newCFG = new ModerationConfig(2, 2, 70.0, 85.0, 1, true);
    	assertTrue(
    		"New grading parameters should be set, specifically 2 posts and 2 replies", 
    		db.saveModerationConfig(newCFG)
    	);
    	
        Post testPost = new Post("student_smith", "Test Post", "Test content", "General");
        int postId = db.createPost(testPost);
        
        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	30, 
        	"Grade should be 30 for completing the half required posts and no replies."
        );
        
        Reply testReply = new Reply(postId, "student_smith", "Test content");
        db.createReply(testReply);

        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	50, 
        	"Grade should be 50 for completing the half required posts and replies."
        );
        
        testReply = new Reply(postId, "student_smith", "More test content");
        db.createReply(testReply);
        
        assertEquals(
        	db.getOverallGrade("student_smith"), 
        	70, 
        	"Grade should be 70 for completing the half required posts and all required replies."
        );
        
        testPost = new Post("student_smith", "Test Post", "Test content", "General");
        db.createPost(testPost);
        
        assertEquals(
        	db.getOverallGrade("student_smith"),
        	100, 
        	"Grade should be 100 for completing the required posts and replies."
        );
    }

}
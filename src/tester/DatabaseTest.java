package tester;

import database.Database;
import entityClasses.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    
    private Database db;
    
    @BeforeEach
    public void setUp() throws SQLException {
        db = new Database();
        db.connectToDatabase();
    }
    
    @AfterEach
    public void tearDown() {
        db.closeConnection();
    }
    
    @Test
    public void testConnectionAndTableCreation() throws SQLException {
        assertNotNull(db, "Database connection should not be null");
        assertTrue(db.isDatabaseEmpty(), "Database should be empty initially");
    }
    
    @Test
    public void testUserRegistrationWithBoundaries() throws SQLException {
        String longUsername = "a".repeat(255);
        User user = new User(longUsername, "pass123", "John", "", "Doe", 
                           "Johnny", "john@test.com", true, false, false);
        
        db.register(user);
        assertEquals(1, db.getNumberOfUsers(), "User count should be 1");
        assertTrue(db.doesUserExist(longUsername), "User should exist");
    }
    
    @Test
    public void testLoginAllRoles() throws SQLException {
        User admin = new User("admin1", "adminpass", "Admin", "", "User", 
                            "", "admin@test.com", true, false, false);
        User student = new User("student1", "studentpass", "Student", "", "User", 
                              "", "student@test.com", false, true, false);
        User reviewer = new User("reviewer1", "reviewpass", "Reviewer", "", "User", 
                               "", "reviewer@test.com", false, false, true);
        
        db.register(admin);
        db.register(student);
        db.register(reviewer);
        
        assertTrue(db.loginAdmin(admin), "Admin login should succeed");
        assertTrue(db.loginRole1(student), "Student login should succeed");
        assertTrue(db.loginRole2(reviewer), "Reviewer login should succeed");
        
        User emptyPass = new User("admin1", "", "Admin", "", "User", 
                                "", "admin@test.com", true, false, false);
        assertFalse(db.loginAdmin(emptyPass), "Login with empty password should fail");
    }
    
    @Test
    public void testUserDataRetrievalBoundaries() throws SQLException {
        assertEquals(0, db.getNumberOfUsers(), "Empty database should have 0 users");
        
        User user1 = new User("user1", "pass1", "First", "", "User", 
                            "", "user1@test.com", false, true, false);
        db.register(user1);
        assertEquals(1, db.getNumberOfUsers(), "Should have 1 user");
        
        for (int i = 2; i <= 5; i++) {
            User user = new User("user" + i, "pass" + i, "User", "", String.valueOf(i), 
                               "", "user" + i + "@test.com", false, true, false);
            db.register(user);
        }
        assertEquals(5, db.getNumberOfUsers(), "Should have 5 users");
        
        assertEquals("<Select a User>", db.getUserList().get(0), "User list should start with '<Select a User>'");
    }
    
    @Test
    public void testUserAttributeUpdateBoundaries() throws SQLException {
        User user = new User("testuser", "pass", "John", "", "Doe", 
                           "", "test@test.com", false, true, false);
        db.register(user);
        
        String longName = "B".repeat(255);
        db.updateFirstName("testuser", longName);
        assertEquals(longName, db.getFirstName("testuser"), "First name should be updated to 255 chars");
        
        db.updateFirstName("testuser", "");
        assertEquals("", db.getFirstName("testuser"), "First name should be empty");
    }
    
    @Test
    public void testRoleManagementBoundaries() throws SQLException {
        User noRoles = new User("norole", "pass", "No", "", "Role", 
                              "", "norole@test.com", false, false, false);
        db.register(noRoles);
        assertEquals(0, db.getNumberOfRoles(noRoles), "User should have 0 roles");
        
        User allRoles = new User("allroles", "pass", "All", "", "Roles", 
                               "", "allroles@test.com", true, true, true);
        db.register(allRoles);
        assertEquals(3, db.getNumberOfRoles(allRoles), "User should have 3 roles");
        
        assertTrue(db.updateUserRole("norole", "Admin", "true"), "Should update Admin role");
        assertFalse(db.updateUserRole("norole", "InvalidRole", "true"), "Should fail with invalid role");
    }
    
    @Test
    public void testInvitationCodeBoundaries() {
        String email = "invite@test.com";
        String code = db.generateInvitationCode(email, "Admin");
        
        assertEquals(email, db.getEmailAddressUsingCode(code), "Should retrieve correct email");
        
        long futureDeadline = System.currentTimeMillis() + 10000;
        db.setDeadlineInDatabase(code, futureDeadline);
        assertEquals(futureDeadline, db.getDeadlineFromDatabase(code), "Should retrieve correct deadline");
        
        db.removeInvitationAfterUse(code);
        assertEquals("", db.getEmailAddressUsingCode(code), "Email should be empty after removal");
    }
    
    @Test
    public void testForumPostBoundaries() throws SQLException {
        User user = new User("postuser", "pass", "Post", "", "User", 
                           "", "post@test.com", false, true, false);
        db.register(user);
        
        assertEquals(0, db.getAllPosts().size(), "Should have 0 posts initially");
        
        String longTitle = "T".repeat(1000);
        Post post1 = new Post(0, "postuser", longTitle, "Content", "Thread1", 
                            java.time.LocalDateTime.now(), false);
        int postId = db.createPost(post1);
        assertTrue(postId > 0, "Post ID should be positive");
        
        assertEquals(1, db.getAllPosts().size(), "Should have 1 post");
        
        assertTrue(db.deletePost(postId), "Should delete post successfully");
        
        assertFalse(db.deletePost(-1), "Should fail with negative ID");
    }
    
    @Test
    public void testForumReplyBoundaries() throws SQLException {
        User user = new User("replyuser", "pass", "Reply", "", "User", 
                           "", "reply@test.com", false, true, false);
        db.register(user);
        
        Post post = new Post(0, "replyuser", "Test Post", "Content", "Thread", 
                           java.time.LocalDateTime.now(), false);
        int postId = db.createPost(post);
        
        assertEquals(0, db.getReplyCountForPost(postId), "Should have 0 replies initially");
        
        for (int i = 1; i <= 3; i++) {
            Reply reply = new Reply(0, postId, "replyuser", "Reply " + i, 
                                  java.time.LocalDateTime.now());
            db.createReply(reply);
        }
        
        assertEquals(3, db.getReplyCountForPost(postId), "Should have 3 replies");
        assertEquals(3, db.getRepliesForPost(postId).size(), "getRepliesForPost should return 3");
    }
    
    @Test
    public void testPostReadStatusBoundaries() throws SQLException {
        User user = new User("readuser", "pass", "Read", "", "User", 
                           "", "read@test.com", false, true, false);
        db.register(user);
        
        Post post = new Post(0, "readuser", "Test Post", "Content", "Thread", 
                           java.time.LocalDateTime.now(), false);
        int postId = db.createPost(post);
        
        assertFalse(db.isPostRead(postId, "readuser"), "Post should be unread initially");
        db.markPostAsRead(postId, "readuser");
        assertTrue(db.isPostRead(postId, "readuser"), "Post should be read after marking");
        
        db.markPostAsRead(postId, "readuser");
        assertTrue(db.isPostRead(postId, "readuser"), "Post should still be read (idempotent)");
    }
}
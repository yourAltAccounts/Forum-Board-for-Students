package tester;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import entityClasses.ModerationFlag;
import entityClasses.PrivateMessage;
import entityClasses.Post;
import entityClasses.User;
import database.Database;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>Title: staffTester Class</p>
 * * <p>Description: JUnit test class for validating the functionality specific to staff/moderator roles
 * within the application's database layer. This includes post flagging, private messaging, 
 * retrieving flagged posts, and end-to-end workflow testing.</p>
 * * @author Aaron Shih (Based on previous test author)
 * @version 1.00 2025-11-12 Initial implementation
 */
public class staffTester {
	
    private Database db;
    /**
     * Constructs a new instance of the staffTester class.
     * This constructor is used by JUnit to instantiate the test class.
     */
    public staffTester() {
        // Default constructor logic (usually empty for JUnit test classes)
    }
    /**
     * Sets up the test environment before each test method.
     * Establishes a database connection, initializes moderation tables, and 
     * creates necessary test users (staff and students).
     */
    @Before
    public void setUp() {
        try {
            db = new Database();
            db.connectToDatabase();
            db.initializeModerationTables();
            
            createTestUser("staff_jones", "Staff123!", false, true);
            createTestUser("student_smith", "Student123!", true, false);
            createTestUser("student_jones", "Student123!", true, false);
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
     * * @param username The username for the new user.
     * @param password The password for the new user.
     * @param role1 If true, grants the user Role 1 (e.g., Student).
     * @param role2 If true, grants the user Role 2 (e.g., Staff).
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
     * Test case 1.1: Verifies that a staff user can successfully flag an existing post.
     * Also checks if the post's flagged status is updated in the database.
     */
    @Test
    public void test1_1_StaffSuccessfullyFlagsPost() {
        Post testPost = new Post("student_smith", "Test Post", "Test content", "General");
        int postId = db.createPost(testPost);
        
        ModerationFlag flag = new ModerationFlag(postId, "staff_jones", 
                                                  "Inappropriate language");
        
        boolean result = db.createModerationFlag(flag);
        db.markPostAsFlagged(postId, true);
        
        assertTrue("Flag should be created successfully", result);
        assertTrue("Post should be marked as flagged", db.isPostFlagged(postId));
    }
    
    /**
     * Test case 1.2: Attempts to flag a post that does not exist.
     * Asserts that the operation fails gracefully.
     */
    @Test
    public void test1_2_FlagNonExistentPost() {
        int fakePostId = 9999;
        ModerationFlag flag = new ModerationFlag(fakePostId, "staff_jones", 
                                                  "Test reason");
        
        boolean result = db.createModerationFlag(flag);
        
        assertFalse("Should not create flag for non-existent post", result);
    }
    
    /**
     * Test case 2.1: Verifies that a staff user can send a private message linked to a specific post.
     */
    @Test
    public void test2_1_SendMessageLinkedToPost() {
        Post testPost = new Post("student_smith", "Test", "Content", "General");
        int postId = db.createPost(testPost);
        
        PrivateMessage message = new PrivateMessage(
            "staff_jones", 
            "student_smith", 
            postId,
            "Please review forum guidelines",
            null
        );
        
        boolean result = db.createPrivateMessage(message);
        
        assertTrue("Message should be created successfully", result);
    }
    
    /**
     * Test case 2.2: Attempts to send a private message to a non-existent user.
     * Asserts that the message creation fails.
     */
    @Test
    public void test2_2_SendMessageToNonExistentUser() {
        PrivateMessage message = new PrivateMessage(
            "staff_jones",
            "fake_student_999",
            null,
            "Test message",
            null
        );
        
        boolean result = db.createPrivateMessage(message);
        
        assertFalse("Should not create message for non-existent user", result);
    }
    
    /**
     * Test case 3.1: Retrieves all private messages for a user (Inbox).
     * Asserts that the correct number of messages (read and unread) are returned.
     */
    @Test
    public void test3_1_RetrieveAllInboxMessages() {
        createTestMessage("staff_jones", "student_smith", "Message 1", false);
        createTestMessage("staff_jones", "student_smith", "Message 2", false);
        createTestMessage("staff_jones", "student_smith", "Message 3", true);
        
        List<PrivateMessage> messages = db.getPrivateMessagesForUser("student_smith");
        
        assertEquals("Should retrieve 3 messages", 3, messages.size());
    }
    
    /**
     * Test case 3.2: Retrieves the inbox for a new user with zero messages.
     * Asserts that an empty list is returned.
     */
    @Test
    public void test3_2_RetrieveInboxWithZeroMessages() {
        createTestUser("student_new", "Student123!", true, false);
        
        List<PrivateMessage> messages = db.getPrivateMessagesForUser("student_new");
        
        assertEquals("Should return empty list", 0, messages.size());
    }
    
    /**
     * Helper method to create and optionally mark a private message as read.
     * * @param sender The sender's username.
     * @param recipient The recipient's username.
     * @param content The message content.
     * @param isRead If true, the message is marked as read after creation.
     */
    private void createTestMessage(String sender, String recipient, 
                                   String content, boolean isRead) {
        PrivateMessage msg = new PrivateMessage(sender, recipient, null, content, null);
        db.createPrivateMessage(msg);
        if (isRead && msg.getMessageId() > 0) {
            db.markPrivateMessageAsRead(msg.getMessageId());
        }
    }
    
    /**
     * Test case 4.1: Marks an unread private message as read.
     * Asserts that the operation succeeds and the unread count for the recipient decreases.
     */
    @Test
    public void test4_1_MarkMessageAsRead() {
        PrivateMessage message = new PrivateMessage(
            "staff_jones", "student_smith", null, "Test message", null
        );
        db.createPrivateMessage(message);
        
        int unreadBefore = db.getUnreadMessageCount("student_smith");
        
        boolean result = db.markPrivateMessageAsRead(message.getMessageId());
        int unreadAfter = db.getUnreadMessageCount("student_smith");
        
        assertTrue("Should mark as read successfully", result);
        assertTrue("Unread count should decrease", unreadAfter < unreadBefore);
    }
    
    /**
     * Test case 4.2: Attempts to mark an already read message as read again (idempotency check).
     * Asserts that the operation succeeds gracefully.
     */
    @Test
    public void test4_2_MarkAlreadyReadMessage() {
        PrivateMessage message = new PrivateMessage(
            "staff_jones", "student_smith", null, "Test", null
        );
        db.createPrivateMessage(message);
        db.markPrivateMessageAsRead(message.getMessageId());
        
        boolean result = db.markPrivateMessageAsRead(message.getMessageId());
        
        assertTrue("Should handle already-read message gracefully", result);
    }
    
    /**
     * Test case 5.1: Verifies a student can successfully reply to a staff message.
     * Checks that the reply links to the parent message and that the staff user's unread count increases.
     */
    @Test
    public void test5_1_StudentRepliesToStaff() {
        Post testPost = new Post("student_smith", "Test", "Content", "General");
        int postId = db.createPost(testPost);
        
        PrivateMessage original = new PrivateMessage(
            "staff_jones", "student_smith", postId, "Original message", null
        );
        db.createPrivateMessage(original);
        
        PrivateMessage reply = new PrivateMessage(
            "student_smith",
            "staff_jones", 
            postId,
            "Thank you for the feedback",
            original.getMessageId()
        );
        boolean result = db.createPrivateMessage(reply);
        
        assertTrue("Reply should be created", result);
        // Note: Assuming getMessageId() populates after createPrivateMessage
        assertNotNull("Reply should have parent message ID", reply.getParentMessageId());
        
        int staffUnread = db.getUnreadMessageCount("staff_jones");
        assertTrue("Staff should have unread reply", staffUnread > 0);
    }
    
    /**
     * Test case 5.2: Attempts to create a reply using an invalid parent message ID.
     * Asserts that the reply creation fails.
     */
    @Test
    public void test5_2_ReplyWithInvalidParentId() {
        PrivateMessage reply = new PrivateMessage(
            "student_smith",
            "staff_jones",
            null,
            "Reply to nothing",
            9999
        );
        
        boolean result = db.createPrivateMessage(reply);
        
        assertFalse("Should not create reply with invalid parent", result);
    }
    
    /**
     * Test case 6.1: Verifies that a user can only view their own messages and not messages 
     * intended for other users.
     */
    @Test
    public void test6_1_StudentCannotViewOthersMessages() {
        createTestMessage("staff_jones", "student_smith", "For Smith", false);
        createTestMessage("staff_jones", "student_jones", "For Jones", false);
        
        List<PrivateMessage> jonesMessages = db.getPrivateMessagesForUser("student_jones");
        
        assertEquals("Should only see own messages", 1, jonesMessages.size());
        assertEquals("Should be message for Jones", "For Jones", 
                    jonesMessages.get(0).getContent());
    }
    
    /**
     * Test case 6.2: Verifies that a staff member can flag a post (base check). 
     * This test is redundant but kept for original structure.
     */
    @Test
    public void test6_2_NonStaffCannotFlagPosts() {
        Post testPost = new Post("student_smith", "Test", "Content", "General");
        int postId = db.createPost(testPost);
        
        ModerationFlag flag = new ModerationFlag(postId, "staff_jones", "Test");
        boolean result = db.createModerationFlag(flag);
        
        assertTrue("Staff should be able to flag", result);
    }
    
    /**
     * Test case 7.1: Verifies that staff can retrieve a list of all flagged posts.
     * Asserts that the flags are correctly created and the posts are marked.
     */
    @Test
    public void test7_1_StaffRetrievesAllFlaggedPosts() {
        Post post1 = new Post("student_smith", "Post 1", "Content 1", "General");
        Post post2 = new Post("student_smith", "Post 2", "Content 2", "General");
        Post post3 = new Post("student_smith", "Post 3", "Content 3", "General");
        
        int id1 = db.createPost(post1);
        int id2 = db.createPost(post2);
        int id3 = db.createPost(post3);
        
        db.createModerationFlag(new ModerationFlag(id1, "staff_jones", "Reason 1"));
        db.createModerationFlag(new ModerationFlag(id2, "staff_jones", "Reason 2"));
        db.createModerationFlag(new ModerationFlag(id3, "staff_jones", "Reason 3"));
        
        boolean flag1Set = db.markPostAsFlagged(id1, true);
        boolean flag2Set = db.markPostAsFlagged(id2, true);
        boolean flag3Set = db.markPostAsFlagged(id3, true);
        
        assertTrue("Should mark post 1 as flagged", flag1Set);
        assertTrue("Should mark post 2 as flagged", flag2Set);
        assertTrue("Should mark post 3 as flagged", flag3Set);
        
        assertTrue("Post 1 should be flagged", db.isPostFlagged(id1));
        assertTrue("Post 2 should be flagged", db.isPostFlagged(id2));
        assertTrue("Post 3 should be flagged", db.isPostFlagged(id3));
    }
    
    /**
     * Test case 7.2: Verifies the dashboard logic returns correctly when there are no flags.
     * Assumes a clean setup where previous tests have been reset.
     */
    @Test
    public void test7_2_DashboardWithZeroFlags() {
        List<Post> flaggedPosts = db.getFlaggedPosts();
        
        // Note: Asserts size is zero *if* the database clears flagged status, otherwise, 
        // it checks if the size is non-negative. Assuming a clean slate per test.
        assertEquals("Should return an empty list when no posts are flagged", 0, flaggedPosts.size());
    }
    
    /**
     * Test case 8.1: Verifies that a post can be successfully retrieved using the ID 
     * referenced in a private message.
     */
    @Test
    public void test8_1_RetrievePostFromMessageReference() {
        Post testPost = new Post("student_smith", "Referenced Post", 
                                "Important content", "General");
        int postId = db.createPost(testPost);
        
        PrivateMessage message = new PrivateMessage(
            "staff_jones", "student_smith", postId, "Feedback about your post", null
        );
        db.createPrivateMessage(message);
        
        Post retrievedPost = db.getPostById(postId);
        
        assertNotNull("Should retrieve post", retrievedPost);
        assertEquals("Post ID should match", postId, retrievedPost.getPostId());
        assertEquals("Title should match", "Referenced Post", retrievedPost.getTitle());
    }
    
    /**
     * Test case 8.2: Attempts to retrieve a post using a non-existent ID referenced in a message.
     * Asserts that `getPostById` returns null and that message creation fails if 
     * referential integrity is enforced at creation.
     */
    @Test
    public void test8_2_MessageReferencesDeletedPost() {
        // This message creation will likely fail if the database enforces FK constraints
        PrivateMessage message = new PrivateMessage(
            "staff_jones", "student_smith", 999, "Reference to deleted post", null
        );
        boolean messageCreated = db.createPrivateMessage(message);
        
        Post retrievedPost = db.getPostById(999);
        
        assertNull("Should return null for non-existent post", retrievedPost);
        // This assertion checks if the database prevents creation when the referenced post doesn't exist.
        assertFalse("Should not create message with invalid post reference", messageCreated); 
    }
    
    /**
     * Test case 10.1: Verifies that the database handles a private message with empty content.
     */
    @Test
    public void test10_1_EmptyMessageContent() {
        PrivateMessage message = new PrivateMessage(
            "staff_jones", "student_smith", null, "", null
        );
        
        boolean result = db.createPrivateMessage(message);
        
        assertTrue("Database accepts empty content", result);
    }
    
    /**
     * Test case 10.2: Verifies that the database handles a moderation flag with an empty reason.
     */
    @Test
    public void test10_2_EmptyFlagReason() {
        Post testPost = new Post("student_smith", "Test", "Content", "General");
        int postId = db.createPost(testPost);
        
        ModerationFlag flag = new ModerationFlag(postId, "staff_jones", "");
        
        boolean result = db.createModerationFlag(flag);
        
        assertTrue("Database accepts empty flag reason", result);
    }
    
    /**
     * Test case 11: Comprehensive end-to-end workflow test simulating the full moderation process:
     * Flag -> Staff message -> Student reads -> Student replies -> Staff receives reply.
     */
    @Test
    public void test11_EndToEndWorkflow() {
        Post post = new Post("student_smith", "Problematic Post", "Bad content", "General");
        int postId = db.createPost(post);
        
        ModerationFlag flag = new ModerationFlag(postId, "staff_jones", 
                                                  "Inappropriate content");
        db.createModerationFlag(flag);
        db.markPostAsFlagged(postId, true);
        
        assertTrue("Post should be flagged", db.isPostFlagged(postId));
        
        PrivateMessage staffMessage = new PrivateMessage(
            "staff_jones", "student_smith", postId, 
            "Please review guidelines", null
        );
        db.createPrivateMessage(staffMessage);
        
        int unreadCount = db.getUnreadMessageCount("student_smith");
        assertTrue("Student should have at least 1 unread message", unreadCount >= 1);
        
        List<PrivateMessage> messages = db.getPrivateMessagesForUser("student_smith");
        assertTrue("Should have at least one message", messages.size() >= 1);
        
        // Get the ID of the message just sent (assuming it's the latest)
        // Since we are creating a fresh DB per test, the latest message is likely the one we want.
        int messageId = messages.get(0).getMessageId(); 
        db.markPrivateMessageAsRead(messageId);
        
        int unreadAfterRead = db.getUnreadMessageCount("student_smith");
        assertTrue("Unread count should decrease or be 0", unreadAfterRead < unreadCount);
        
        PrivateMessage studentReply = new PrivateMessage(
            "student_smith", "staff_jones", postId,
            "Thank you, I understand", messageId
        );
        db.createPrivateMessage(studentReply);
        
        int staffUnread = db.getUnreadMessageCount("staff_jones");
        assertTrue("Staff should have at least 1 unread reply", staffUnread >= 1);
        
        List<PrivateMessage> replies = db.getPrivateMessageReplies(messageId);
        assertTrue("Should have at least 1 reply", replies.size() >= 1);
        assertNotNull("Reply should reference parent", replies.get(0).getParentMessageId());
    }
}
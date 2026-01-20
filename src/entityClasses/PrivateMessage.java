package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: PrivateMessage Class </p>
 * 
 * <p> Description: Represents a private message between staff and students
 * for moderation feedback. Supports threading with parent messages.</p>
 * 
 * <p> User Stories Supported:
 * - Staff flags content and sends private feedback
 * - Student views inbox and responds to feedback
 * </p>
 * 
 * <p> Copyright: CSE 360 Team © 2025 </p>
 * 
 * @author Generated for Moderation Feature
 * @version 1.00    2025-11-12 Moderation Implementation
 */
public class PrivateMessage {
    
    /*******
     * Unique message identifier assigned by database.
     * Source: Database primary key requirement
     */
    private int messageId;
    
    /*******
     * Username of message sender (staff or student).
     * Source: User Story - Staff sends feedback, students reply
     */
    private String senderId;
    
    /*******
     * Username of message recipient.
     * Source: User Story - Private messaging between staff and students
     */
    private String recipientId;
    
    /*******
     * Reference to the post being discussed (nullable for general messages).
     * Source: Requirement - Messages must reference specific post ID
     */
    private Integer postId;
    
    /*******
     * Message content/body.
     * Source: User Story - Staff provides feedback, students respond
     */
    private String content;
    
    /*******
     * Read/unread status flag.
     * Source: User Story - Read/unread marker to filter content
     */
    private boolean isRead;
    
    /*******
     * Reference to parent message for threading (null if original message).
     * Source: Operational Constraint - Message threading limited to one level
     */
    private Integer parentMessageId;
    
    /*******
     * Message creation timestamp.
     * Source: Standard requirement for message ordering
     */
    private LocalDateTime timestamp;
    
    /*******
     * Default constructor.
     */
    public PrivateMessage() {
        this.messageId = -1;
        this.senderId = "";
        this.recipientId = "";
        this.postId = null;
        this.content = "";
        this.isRead = false;
        this.parentMessageId = null;
        this.timestamp = LocalDateTime.now();
    }
    
    /*******
     * Constructor for creating a new message.
     * 
     * @param senderId Username of sender
     * @param recipientId Username of recipient
     * @param postId Related post ID (can be null)
     * @param content Message body
     * @param parentMessageId Parent message ID for replies (can be null)
     */
    public PrivateMessage(String senderId, String recipientId, Integer postId, 
                         String content, Integer parentMessageId) {
        this.messageId = -1;  // Database assigns this
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.postId = postId;
        this.content = content;
        this.isRead = false;
        this.parentMessageId = parentMessageId;
        this.timestamp = LocalDateTime.now();
    }
    
    /*******
     * Full constructor - used when loading from database.
     */
    public PrivateMessage(int messageId, String senderId, String recipientId, 
                         Integer postId, String content, boolean isRead, 
                         Integer parentMessageId, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.postId = postId;
        this.content = content;
        this.isRead = isRead;
        this.parentMessageId = parentMessageId;
        this.timestamp = timestamp;
    }
    
    // ==================== Getters ====================
    
    public int getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getRecipientId() { return recipientId; }
    public Integer getPostId() { return postId; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead; }
    public Integer getParentMessageId() { return parentMessageId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    // ==================== Setters ====================
    
    public void setMessageId(int messageId) { this.messageId = messageId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public void setContent(String content) { this.content = content; }
    public void setRead(boolean read) { isRead = read; }
    public void setParentMessageId(Integer parentMessageId) { this.parentMessageId = parentMessageId; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    // ==================== Utility Methods ====================
    
    /*******
     * Returns formatted timestamp for display.
     * @return Timestamp in format "yyyy-MM-dd HH:mm:ss"
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /*******
     * Checks if this is a reply to another message.
     * @return true if this message has a parent
     */
    public boolean isReply() {
        return parentMessageId != null && parentMessageId > 0;
    }
    
    /*******
     * Checks if message references a specific post.
     * @return true if linked to a post
     */
    public boolean hasPostReference() {
        return postId != null && postId > 0;
    }
    
    /*******
     * Validates message content.
     * @return Error message if invalid, empty string if valid
     */
    public String validate() {
        if (senderId == null || senderId.trim().isEmpty()) {
            return "Sender ID cannot be empty";
        }
        if (recipientId == null || recipientId.trim().isEmpty()) {
            return "Recipient ID cannot be empty";
        }
        if (content == null || content.trim().isEmpty()) {
            return "Message content cannot be empty";
        }
        if (content.length() > 2000) {
            return "Message content cannot exceed 2000 characters";
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "PrivateMessage{" +
                "messageId=" + messageId +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", postId=" + postId +
                ", isRead=" + isRead +
                ", parentMessageId=" + parentMessageId +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
}
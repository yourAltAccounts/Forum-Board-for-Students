package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a moderation flag placed on a post by staff when inappropriate
 * or concerning content is detected. A moderation flag contains the post ID,
 * staff ID, reason for flagging, timestamp, and status.
 */
public class ModerationFlag {

    /**
     * Unique identifier for this moderation flag.
     */
    private int flagId;

    /**
     * Identifier of the post that was flagged.
     */
    private int postId;

    /**
     * Username of the staff member who created the flag.
     */
    private String staffId;

    /**
     * Text description explaining why the post was flagged.
     */
    private String flagReason;

    /**
     * Timestamp representing when the flag was created.
     */
    private LocalDateTime timestamp;

    /**
     * Current status of the moderation flag.
     * Expected values: "PENDING", "RESOLVED", "DISMISSED".
     */
    private String status;

    /**
     * Default constructor that initializes all fields with default values and
     * sets the status to "PENDING" with the current timestamp.
     */
    public ModerationFlag() {
        this.flagId = -1;
        this.postId = -1;
        this.staffId = "";
        this.flagReason = "";
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    /**
     * Constructs a new moderation flag with the given post ID, staff ID, and
     * flag reason. The flag ID is set to -1 until saved by the database.
     *
     * @param postId     the ID of the post being flagged
     * @param staffId    the username of the staff member who flagged the post
     * @param flagReason the reason for creating the flag
     */
    public ModerationFlag(int postId, String staffId, String flagReason) {
        this.flagId = -1;
        this.postId = postId;
        this.staffId = staffId;
        this.flagReason = flagReason;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    /**
     * Full constructor used when loading a moderation flag from the database.
     *
     * @param flagId      unique identifier of the flag
     * @param postId      ID of the post being flagged
     * @param staffId     staff member who created the flag
     * @param flagReason  explanation for flagging the post
     * @param timestamp   the date and time the flag was created
     * @param status      the current status of the flag
     */
    public ModerationFlag(int flagId, int postId, String staffId,
                          String flagReason, LocalDateTime timestamp, String status) {
        this.flagId = flagId;
        this.postId = postId;
        this.staffId = staffId;
        this.flagReason = flagReason;
        this.timestamp = timestamp;
        this.status = status;
    }

    // ==================== Getters ====================

    /**
     * Returns the unique flag ID.
     *
     * @return the flag ID
     */
    public int getFlagId() { return flagId; }

    /**
     * Returns the ID of the flagged post.
     *
     * @return the post ID
     */
    public int getPostId() { return postId; }

    /**
     * Returns the staff ID of the staff member who created the flag.
     *
     * @return the staff ID
     */
    public String getStaffId() { return staffId; }

    /**
     * Returns the reason the post was flagged.
     *
     * @return the flag reason
     */
    public String getFlagReason() { return flagReason; }

    /**
     * Returns the timestamp when the flag was created.
     *
     * @return the timestamp as a LocalDateTime
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Returns the current status of the flag.
     *
     * @return the flag status
     */
    public String getStatus() { return status; }

    // ==================== Setters ====================

    /**
     * Sets the unique flag ID.
     *
     * @param flagId the new flag ID
     */
    public void setFlagId(int flagId) { this.flagId = flagId; }

    /**
     * Sets the ID of the post being flagged.
     *
     * @param postId the post ID
     */
    public void setPostId(int postId) { this.postId = postId; }

    /**
     * Sets the ID of the staff member who flagged the post.
     *
     * @param staffId the staff ID
     */
    public void setStaffId(String staffId) { this.staffId = staffId; }

    /**
     * Sets the reason the post was flagged.
     *
     * @param flagReason explanation for the flag
     */
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }

    /**
     * Sets the timestamp when the flag was created.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /**
     * Sets the current status of the flag.
     *
     * @param status the new status ("PENDING", "RESOLVED", "DISMISSED")
     */
    public void setStatus(String status) { this.status = status; }

    // ==================== Utility Methods ====================

    /**
     * Returns the timestamp formatted as a human-readable string.
     *
     * @return formatted timestamp (yyyy-MM-dd HH:mm:ss)
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    /**
     * Checks whether this moderation flag is still pending.
     *
     * @return true if the flag status is "PENDING"; false otherwise
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * Marks this moderation flag as resolved.
     */
    public void resolve() {
        this.status = "RESOLVED";
    }

    /**
     * Marks this moderation flag as dismissed.
     */
    public void dismiss() {
        this.status = "DISMISSED";
    }

    /**
     * Validates the fields of this moderation flag.
     *
     * @return an error message string if validation fails, or an empty string if valid
     */
    public String validate() {
        if (postId <= 0) {
            return "Invalid post ID";
        }
        if (staffId == null || staffId.trim().isEmpty()) {
            return "Staff ID cannot be empty";
        }
        if (flagReason == null || flagReason.trim().isEmpty()) {
            return "Flag reason cannot be empty";
        }
        if (flagReason.length() < 10) {
            return "Flag reason must be at least 10 characters";
        }
        if (flagReason.length() > 500) {
            return "Flag reason cannot exceed 500 characters";
        }
        return "";
    }

    /**
     * Returns a string representation of this moderation flag.
     *
     * @return formatted string containing all flag information
     */
    @Override
    public String toString() {
        return "ModerationFlag{" +
                "flagId=" + flagId +
                ", postId=" + postId +
                ", staffId='" + staffId + '\'' +
                ", flagReason='" + flagReason + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
}

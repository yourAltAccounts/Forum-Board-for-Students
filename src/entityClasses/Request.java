package entityClasses;

import java.time.LocalDateTime;


/**
 * <p> Title: Request Class </p>
 * <p> Description: Entity class representing a single request submitted by a Staff member
 * for administrative action. This class acts as the data model, directing directly to 
 * records in the requests database table. </p>
 * <p> Copyright: CSE 360 Team © 2025 </p>
 *
 * @author Jason Morales
 * @version 1.0
 */
public class Request {
    private int requestId;			//the unique ID for the request
    private String staffUsername; //Username of the staff member
    private String description;	//description of the request
    private String adminAction; // Documented actions taken by the admin
    private String status;      //"PENDING", "CLOSED", "REOPENED"
    private LocalDateTime timestamp;	
    private String closedByAdmin;	//description of a reqeuest that is closed
    private Integer originalClosedRequestId; 

    /**
     * <p> Constructor: Request(String staffUsername, String description) </p>
     *
     * <p> Description: Constructor used specifically for creating a new request 
     * before inserting into the database. It initializes all fields 
     * to default values (ex: status to "PENDING"). </p>
     *
     * @param staffUsername The username of the staff member submitting the request.
     * @param description The description of the request.
     */
    public Request(String staffUsername, String description) {
        this.staffUsername = staffUsername;
        this.description = description;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
        this.adminAction = null;
        this.closedByAdmin = null;
        this.originalClosedRequestId = null;
    }

    /**
     * <p> Constructor: Request(int requestId, String staffUsername, String description, String adminAction, 
                   String status, LocalDateTime timestamp, String closedByAdmin, 
                   Integer originalClosedRequestId) </p>
     *
     * <p> Description: Full constructor used for retrieving an existing request 
     * record from the database. It sets all fields, including the ID and administrative details. </p>
     *
     * @param requestId The ID primary key of the request.
     * @param staffUsername The username of the staff member who submitted the request.
     * @param description The description of the request.
     * @param adminAction Documented notes/actions taken by the administrator upon closing the request.
     * @param status The current state of the request ("PENDING", "CLOSED", "REOPENED").
     * @param timestamp The date and time the request was initially submitted.
     * @param closedByAdmin The username of the admin who closed the request (null if active).
     * @param originalClosedRequestId The ID of the original request this record links to, used for reopened requests (null if not reopened).
     */
    public Request(int requestId, String staffUsername, String description, String adminAction, 
                   String status, LocalDateTime timestamp, String closedByAdmin, 
                   Integer originalClosedRequestId) {
        this.requestId = requestId;
        this.staffUsername = staffUsername;
        this.description = description;
        this.adminAction = adminAction;
        this.status = status;
        this.timestamp = timestamp;
        this.closedByAdmin = closedByAdmin;
        this.originalClosedRequestId = originalClosedRequestId;
    }

   
    /**
     * Retrieves the unique identifier for this request.
     * @return The requestId.
     */
    public int getRequestId() 
    { 
    	return requestId; 
    }
    /**
     * Sets the unique identifier for this request.
     * @param requestId The new requestId.
     */
    public void setRequestId(int requestId) 
    { 
    	this.requestId = requestId; 
    	
    }
    
    /**
     * Retrieves the username of the staff member who submitted the request.
     * @return The staffUsername.
     */
    public String getStaffUsername() 
    { 
    	return staffUsername; 
    	
    }
    /**
     * Retrieves the detailed description of the request.
     * @return The request description.
     */
    public String getDescription() 
    { 
    	return description; 
    	
    }
    
    /**
     * Updates the detailed description of the request (e.g., when reopening).
     * @param description The new description.
     */
    public void setDescription(String description) 
    { 
    	
    	this.description = description; 
    	
    }
    /**
     * Retrieves the documented actions or notes taken by the administrator.
     * @return The adminAction notes.
     */
    public String getAdminAction() 
    { 
    	return adminAction; 
    
    }
    
    /**
     * Sets the administrative action/notes when closing a request.
     * @param adminAction The notes to document the closure.
     */
    public void setAdminAction(String adminAction) 
    { 
    	this.adminAction = adminAction; 
    }
    /**
     * Retrieves the current status of the request ("PENDING", "CLOSED", or "REOPENED").
     * @return The current status string.
     */
    public String getStatus() 
    { 
    	return status; 
    }
    
    /**
     * Sets the current status of the request.
     * @param status The new status string.
     */
    public void setStatus(String status) 
    { 
    	this.status = status; 
    }
    /**
     * Retrieves the date and time the request was submitted.
     * @return The timestamp.
     */
    public LocalDateTime getTimestamp() 
    { 
    	return timestamp; 
    }
    
    /**
     * Retrieves the username of the administrator who closed the request.
     * @return The closedByAdmin username, or null if the request is active.
     */
    public String getClosedByAdmin() 
    { 
    	return closedByAdmin; 
    }
    /**
     * Sets the username of the administrator who closed the request.
     * @param closedByAdmin The closing admin's username.
     */
    public void setClosedByAdmin(String closedByAdmin) 
    { 
    	this.closedByAdmin = closedByAdmin; 
    }

    /**
     * Retrieves the ID of the original request this reopened request is linked to.
     * @return The originalClosedRequestId, or null if the request is not a reopened issue.
     */
    public Integer getOriginalClosedRequestId() 
    { 
    	return originalClosedRequestId; 
    	
    }
    
    /**
     * Sets the ID of the original closed request this record is linked to.
     * @param originalClosedRequestId The ID of the original closed request.
     */
    public void setOriginalClosedRequestId(Integer originalClosedRequestId) { 
        this.originalClosedRequestId = originalClosedRequestId; 
    }
}

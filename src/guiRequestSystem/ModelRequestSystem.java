package guiRequestSystem;

import database.Database;
import entityClasses.Request;
import entityClasses.User;
import java.util.List;

/**
 * <p> Title: ModelRequestSystem Class </p>
 * <p> Description: Model component for the Request System feature. Handles database
 * operations related to staff requests, including creation, viewing, closing, and reopening. </p>
 * <p> Copyright: CSE 360 Team © 2025 </p>
 *
 * @author Generated for Request System Feature
 * @version 1.00    2025-11-28 Request System Implementation
 */
public class ModelRequestSystem 
{

    private Database theDatabase;
    private User currentUser;

    /**
     * <p> Method: ModelRequestSystem </p>
     * <p> Description: Constructor to set up database access and store the current user. </p>
     * @param db The active Database instance.
     * @param user The current logged-in User object.
     */
    public ModelRequestSystem(Database db, User user) 
    {
        this.theDatabase = db;
        this.currentUser = user;
    }

    /**
     * Retrieves all active requests from the database.
     * @return A List of active Request objects.
     */
    public List<Request> getActiveRequests() 
    {
        // Calls the database method to fetch requests
        return theDatabase.getActiveRequests();
    }

    /**
     * Creates a new request in the database.
     * @param description The staff's request description.
     * @return The ID of the newly created request, or -1 on failure.
     */
    public int createNewRequest(String description) 
    {
        // Calls the database method to create a request
        return theDatabase.createRequest(currentUser.getUserName(), description);
    }

    /**
     * Closes an existing request.
     * @param request The request to close.
     * @param adminAction The notes/action taken by the admin.
     * @return true if the close was successful, false otherwise.
     */
    public boolean closeRequest(Request request, String adminAction) 
    {
        return theDatabase.closeRequest(request.getRequestId(), adminAction, currentUser.getUserName());
        
    }

    /**
     * Reopens a closed request by updating its status and description.
     * @param closedRequestId The ID of the request to reopen. 
     * @param newDescription The updated description provided by the staff.
     * @return The ID of the newly created request, or -1 on failure.
     */
    public int reopenRequest(int closedRequestId, String newDescription) 
    {
        // Call the new database method to create a linked record
        return theDatabase.createReopenedRequest(closedRequestId, currentUser.getUserName(), newDescription);
    }

    /**
     * Retrieves all closed requests from the database.
     * @return A List of closed Request objects.
     */
    public List<Request> getClosedRequests() 
    {
        // Calls the new database method to fetch closed requests
        return theDatabase.getClosedRequests(); 
    }

 /**
  * Retrieves the full details of the original closed request that a reopened request links to.
  * This is used for displaying history.
  * @param originalId The ID of the original closed request (from the reopened request's record).
  * @return The original closed Request object, or null if no link exists or if the ID is invalid.
  */
 public Request getOriginalClosedRequest(int originalId) {
     if (originalId > 0) 
     {
         return theDatabase.getRequestById(originalId);
     }
     return null;
 }
    

}

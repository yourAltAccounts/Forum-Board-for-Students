package tester;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import entityClasses.Request;
import entityClasses.User;
import guiRequestSystem.ModelRequestSystem;

/**
 * @author [Jason Morales]
 * @version 1.0
 * * <p>JUnit test class for the core functionalities of the Request Management System.
 * It focuses on request functionalities: submission, closure, and reopening,
 * makes sure correct tests are done using fake users instance.</p>
 */
class RequestSystemTest {

    private Database testDB;
    private ModelRequestSystem staffModel;
    private ModelRequestSystem adminModel;
    private User staff1;
    private User admin1;
    
    /**
     * <p>Sets up the necessary environment before each test method runs.</p>
     * <li>Instantiating and connecting to the test database.</li>
     * <li>Creating and registering two test users: one staff and one admin.</li>
     * <li>Instantiating two Modelrequest objects, one for each user role,
     * which are used to simulate submitting and processing requests.</li>
     * * @throws SQLException if a database connection or registration error occurs.
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Instantiate the Database. 
        testDB = new Database(); 
        testDB.connectToDatabase(); 
        
        // make the test staff users 
        staff1 = new User("staff1", "pass", "S", "M", "L", "S", "staff@test.com", false, false, true); 
        admin1 = new User("admin1", "pass", "A", "M", "L", "A", "admin@test.com", true, false, false); 
        
        // Register users so foreign key checks pass 
        testDB.register(staff1);
        testDB.register(admin1);
        
        staffModel = new ModelRequestSystem(testDB, staff1);
        adminModel = new ModelRequestSystem(testDB, admin1);
    }
    
    
    /**
     * <p>submission by staff and visibility
     * in the admin's active list.</p>
     * <li>If ModelRequestSystem createNewRequest(String) returns a valid ID.</li>
     * <li>If the request appears in ModelRequestSystem#getActiveRequests().</li>
     * <li>If the initial status is correctly set to "PENDING".</li>
     * <li>If the submitter's username is correctly recorded.</li>
     */
    @Test
    void testStaffSubmitsAndAdminSeesActiveRequest() {
        String originalDescription = "Staff needs access to deployment log.";
        
        // Staff submits the request
        int newRequestId = staffModel.createNewRequest(originalDescription);
        assertTrue(newRequestId > 0, "Test 1 Failed: Request creation should return a positive ID.");
        
        // Admin checks active list
        List<Request> activeRequests = adminModel.getActiveRequests();
        Request submittedRequest = activeRequests.stream()
                .filter(r -> r.getRequestId() == newRequestId)
                .findFirst().orElse(null);

        assertNotNull(submittedRequest, "Test 1 Failed: Admin should see the new request in the active list.");
        assertEquals("PENDING", submittedRequest.getStatus(), "Test 1 Failed: New request status must be PENDING.");
        assertEquals(staff1.getUserName(), submittedRequest.getStaffUsername(), "Test 1 Failed: Submitter username is incorrect.");
    }
    
    /**
     * <p>Tests the closure of an active request by an admin and ensures it is removed
     * from the active list and shown in the closed list for staff.</p>
     * <li>If ModelRequestSystem#closeRequest(Request, String) is successful.</li>
     * <li>If the request is no longer visible in the active requests list.</li>
     * <li>If the request appears in the staff's ModelRequestSystem#getClosedRequests().</li>
     * <li>If the final status is "CLOSED" and admin notes/username are recorded.</li>
     */
    @Test
    void testAdminClosesRequestAndStaffSeesClosedList() {
        // Setup and create an active request first
        int requestId = testDB.createRequest(staff1.getUserName(), "Test request for closure.");
        String adminActionNotes = "Access granted.";
        
        // Admin closes the request
        boolean success = adminModel.closeRequest(testDB.getRequestById(requestId), adminActionNotes);
        assertTrue(success, "Test 2 Failed: Admin closure should return true.");
        
        // Request should be removed from the active list
        List<Request> activeRequests = adminModel.getActiveRequests();
        assertFalse(activeRequests.stream().anyMatch(r -> r.getRequestId() == requestId), 
                "Test 2 Failed: Closed request should not appear in the active list.");
        
        // Staff checks the closed list
        List<Request> closedRequests = staffModel.getClosedRequests(); 
        Request closedRequest = closedRequests.stream()
                .filter(r -> r.getRequestId() == requestId)
                .findFirst().orElse(null);
        
        assertNotNull(closedRequest, "Test 2 Failed: Closed request must appear in the closed list.");
        assertEquals("CLOSED", closedRequest.getStatus(), "Test 2 Failed: Status must be CLOSED.");
        assertEquals(adminActionNotes, closedRequest.getAdminAction(), "Test 2 Failed: Admin action notes must be documented.");
        assertEquals(admin1.getUserName(), closedRequest.getClosedByAdmin(), "Test 2 Failed: Closed by admin field is incorrect.");
    }

   
    /**
     * <p>Tests the ability of a staff member to reopen a previously closed request
     * and verifies that history is correctly intact.</p>
     * <li>If ModelRequestSystem reopenRequest(int, String) creates a new request with a positive ID.</li>
     * <li>If the new request's status is "REOPENED" and its description is updated.</li>
     * <li>If the new request correctly links to the original closed request ID via
     * Request getOriginalClosedRequestId().</li>
     * <li>If the original closed request history can be retrieved and its data is intact.</li>
     */
    @Test
    void testStaffReopensClosedRequestAndChecksHistory() {
        // Create original request
        int originalId = testDB.createRequest(staff1.getUserName(), "Original issue description.");
        // Close original request
        String originalAction = "Resolution action: JerryRigged the service.";
        testDB.closeRequest(originalId, originalAction, admin1.getUserName());
        
        String newDescription = "What I did didnt work. Needs permanent solution.";
        
        //Staff reopens the request
        int reopenedId = staffModel.reopenRequest(originalId, newDescription);
        assertTrue(reopenedId > 0, "Test 3 Failed: Reopened request should return a positive new ID.");
        
        //Check the new request in the Active List
        List<Request> activeRequests = adminModel.getActiveRequests();
        Request reopenedRequest = activeRequests.stream()
                .filter(r -> r.getRequestId() == reopenedId)
                .findFirst().orElse(null);
        
        assertNotNull(reopenedRequest, "Test 3 Failed: Reopened request must be active.");
        assertEquals("REOPENED", reopenedRequest.getStatus(), "Test 3 Failed: New status must be REOPENED.");
        assertEquals(newDescription, reopenedRequest.getDescription(), "Test 3 Failed: Description must be updated.");

        //Check History Link
        assertEquals(originalId, reopenedRequest.getOriginalClosedRequestId(), 
                "Test 3 Failed: Reopened request must link to the original closed request ID.");
        
        //Admin/Staff retrieves the history
        Request originalClosed = adminModel.getOriginalClosedRequest(reopenedRequest.getOriginalClosedRequestId());
        
        assertNotNull(originalClosed, "Test 3 Failed: Should be able to retrieve the original closed request by ID.");
        assertEquals("CLOSED", originalClosed.getStatus(), "Test 3 Failed: Original record status should remain CLOSED.");
        assertEquals(originalAction, originalClosed.getAdminAction(), "Test 3 Failed: Original admin action should be preserved.");
    }
}

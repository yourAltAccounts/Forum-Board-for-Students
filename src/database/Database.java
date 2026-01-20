package database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import entityClasses.*;
import guiModerationConfig.ControllerModerationConfig;
/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	private void createForumTables() throws SQLException {
	    // Create the posts table
	    String postsTable = "CREATE TABLE IF NOT EXISTS posts ("
	            + "postId INT AUTO_INCREMENT PRIMARY KEY, "
	            + "author VARCHAR(255), "
	            + "title VARCHAR(1000), "
	            + "content CLOB, "
	            + "thread VARCHAR(255), "
	            + "timestamp TIMESTAMP, "
	            + "isDeleted BOOL DEFAULT FALSE, "
	            + "FOREIGN KEY (author) REFERENCES userDB(userName))";
	    statement.execute(postsTable);
	    
	    // Create the replies table
	    String repliesTable = "CREATE TABLE IF NOT EXISTS replies ("
	            + "replyId INT AUTO_INCREMENT PRIMARY KEY, "
	            + "postId INT, "
	            + "author VARCHAR(255), "
	            + "content CLOB, "
	            + "timestamp TIMESTAMP, "
	            + "FOREIGN KEY (postId) REFERENCES posts(postId), "
	            + "FOREIGN KEY (author) REFERENCES userDB(userName))";
	    statement.execute(repliesTable);
	    
	    // Create table to track read status
	    String readStatusTable = "CREATE TABLE IF NOT EXISTS postReadStatus ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "postId INT, "
	            + "userName VARCHAR(255), "
	            + "isRead BOOL DEFAULT FALSE, "
	            + "FOREIGN KEY (postId) REFERENCES posts(postId), "
	            + "FOREIGN KEY (userName) REFERENCES userDB(userName), "
	            + "UNIQUE(postId, userName))";
	    statement.execute(readStatusTable);
	    
	    System.out.println("*** Forum tables created successfully ***");
	}

	/**
	 * Creates the admin requests table in the database if it does not already exist.
	 * This table stores all requests, their status, descriptions, and any administrative actions.
	 *
	 *NOTE: CLOB is used for the text box which may conatin more characters than the storage of a string. 
	 * <p>The table includes foreign key constraints linking staffUsername and 
	 * closedByAdmin to the userDB table.</p>
	 *
	 * <p>Fields created: requestId, staffUsername, description, adminAction, status (PENDING/CLOSED/REOPENED), timestamp, 
	 * closedByAdmin, and originalClosedRequestId .</p>
	 *
	 * @throws SQLException If a database access error occurs or the SQL statement is malformed.
	 */
	private void createRequestTables() throws SQLException {
	    // Status: PENDING, CLOSED, REOPENED
	    String requestsTable = "CREATE TABLE IF NOT EXISTS admin_requests ("
	            + "requestId INT AUTO_INCREMENT PRIMARY KEY, "
	            + "staffUsername VARCHAR(255) NOT NULL, "
	            + "description CLOB, " // The Staff's initial request description
	            + "adminAction CLOB, " // The Admin's documented actions/notes
	            + "status VARCHAR(50) DEFAULT 'PENDING', "
	            + "timestamp TIMESTAMP, "
	            + "closedByAdmin VARCHAR(255), "
	            + "originalClosedRequestId INT, " // Link for reopened requests
	            + "FOREIGN KEY (staffUsername) REFERENCES userDB(userName), "
	            + "FOREIGN KEY (closedByAdmin) REFERENCES userDB(userName))";
	    statement.execute(requestsTable);
	    
	    System.out.println("*** Admin Request tables created successfully ***");
	}

	/**
	 * Inserts a new request into the Admin Request table.
	 *
	 * @param staffUsername The username of the Staff member submitting the request.
	 * @param description The detailed text description of the request or issue.
	 * @return The generated primary key (requestId) of the newly created request, or -1 if the insertion failed or an Exception.
	 */
	public int createRequest(String staffUsername, String description) {
	    String insertRequest = "INSERT INTO admin_requests (staffUsername, description, timestamp) "
	            + "VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, staffUsername);
	        pstmt.setString(2, description);
	        pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
	        
	        int affectedRows = pstmt.executeUpdate();
	        
	        if (affectedRows > 0) {
	            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    return generatedKeys.getInt(1); // Return the new ID
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}
	
	/**
	 * Updates an existing request's status to reopened and modifies its description.
	 * The update is only performed if the current status of the request is 'CLOSED'(Admin cannot do this).
	 *
	 * @param requestId The ID of the request to be reopened.
	 * @param newDescription The updated description for the reopened request.
	 * @return true if the request was successfully updated went from closed to reopen, false otherwise (request not found, not CLOSED, or SQLException).
	 */
	public boolean reopenRequest(int requestId, String newDescription) {
	    // Sets status to REOPENED and updates the description, 
	    // when the current status is CLOSED exclusively
	    String query = "UPDATE admin_requests SET status = 'REOPENED', description = ? WHERE requestId = ? AND status = 'CLOSED'";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newDescription);
	        pstmt.setInt(2, requestId);
	        
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Retrieves a list of requests that are currently 'active' for administrative review.
	 * Active requests include those with a status of PENDING or 'REOPENED'.
	 * The results are ordered by the request creation timestamp in ascending order (oldest first).
	 *
	 * @return A Request List containing all active request records, or an empty list if no active requests are found or an Exception occurs.
	 * @see entityClasses.Request
	 */
	public List<Request> getActiveRequests() {
	    List<Request> requestList = new ArrayList<>();
	    String query = "SELECT * FROM admin_requests WHERE status = 'PENDING' OR status = 'REOPENED' ORDER BY timestamp ASC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            // Mapping the ResultSet to the Request entity object
	            Request request = new Request(
	                rs.getInt("requestId"),
	                rs.getString("staffUsername"),
	                rs.getString("description"),
	                rs.getString("adminAction"),
	                rs.getString("status"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getString("closedByAdmin"),
	                (Integer) rs.getObject("originalClosedRequestId") 
	            );
	            requestList.add(request);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	  

	    return requestList;
	}

	public int createPost(Post post) {
	    String insertPost = "INSERT INTO posts (author, title, content, thread, timestamp, isDeleted) "
	            + "VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertPost, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, post.getAuthor());
	        pstmt.setString(2, post.getTitle());
	        pstmt.setString(3, post.getContent());
	        pstmt.setString(4, post.getThread());
	        pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(post.getTimestamp()));
	        pstmt.setBoolean(6, post.isDeleted());
	        
	        int affectedRows = pstmt.executeUpdate();
	        
	        if (affectedRows > 0) {
	            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    return generatedKeys.getInt(1);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}

	/*******
	 * Retrieves all posts from the database
	 * 
	 * @return PostList containing all posts
	 */
	public PostList getAllPosts() {
	    PostList postList = new PostList();
	    String query = "SELECT * FROM posts ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getBoolean("isDeleted")
	            );
	            postList.addPost(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return postList;
	}


	public Post getPostById(int postId) {
	    String query = "SELECT * FROM posts WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getBoolean("isDeleted")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	
	public boolean updatePost(Post post) {
	    String query = "UPDATE posts SET title = ?, content = ?, thread = ? WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, post.getTitle());
	        pstmt.setString(2, post.getContent());
	        pstmt.setString(3, post.getThread());
	        pstmt.setInt(4, post.getPostId());
	        
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public boolean deletePost(int postId) {
	    String query = "UPDATE posts SET isDeleted = TRUE WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	public PostList getPostsByAuthor(String author) {
	    PostList postList = new PostList();
	    String query = "SELECT * FROM posts WHERE author = ? ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, author);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getBoolean("isDeleted")
	            );
	            postList.addPost(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return postList;
	}

	
	public PostList getPostsByThread(String thread) {
		if (thread.equals("All")) {
	    	return getAllPosts();
	    }
		
	    PostList postList = new PostList();
	    String query = "SELECT * FROM posts WHERE thread = ? ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, thread);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getBoolean("isDeleted")
	            );
	            postList.addPost(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return postList;
	}

	
	public int createReply(Reply reply) {
	    String insertReply = "INSERT INTO replies (postId, author, content, timestamp) "
	            + "VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertReply, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, reply.getPostId());
	        pstmt.setString(2, reply.getAuthor());
	        pstmt.setString(3, reply.getContent());
	        pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(reply.getTimestamp()));
	        
	        int affectedRows = pstmt.executeUpdate();
	        
	        if (affectedRows > 0) {
	            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    return generatedKeys.getInt(1);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}

	
	public ReplyList getRepliesForPost(int postId) {
	    ReplyList replyList = new ReplyList();
	    String query = "SELECT * FROM replies WHERE postId = ? ORDER BY timestamp ASC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Reply reply = new Reply(
	                rs.getInt("replyId"),
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("content"),
	                rs.getTimestamp("timestamp").toLocalDateTime()
	            );
	            replyList.addReply(reply);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return replyList;
	}

	
	public ReplyList getAllReplies() {
	    ReplyList replyList = new ReplyList();
	    String query = "SELECT * FROM replies ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Reply reply = new Reply(
	                rs.getInt("replyId"),
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("content"),
	                rs.getTimestamp("timestamp").toLocalDateTime()
	            );
	            replyList.addReply(reply);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return replyList;
	}

	
	public int getReplyCountForPost(int postId) {
	    String query = "SELECT COUNT(*) AS count FROM replies WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("count");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	
	public boolean deleteReply(int replyId) {
	    String query = "DELETE FROM replies WHERE replyId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, replyId);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	public void markPostAsRead(int postId, String userName) {
	    String query = "MERGE INTO postReadStatus (postId, userName, isRead) "
	            + "KEY(postId, userName) VALUES (?, ?, TRUE)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	public boolean isPostRead(int postId, String userName) {
	    String query = "SELECT isRead FROM postReadStatus WHERE postId = ? AND userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        pstmt.setString(2, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getBoolean("isRead");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; 
	}
	public PostList getUnreadPosts(String userName) {
	    PostList postList = new PostList();
	    String query = "SELECT p.* FROM posts p " +
	                   "LEFT JOIN postReadStatus prs ON p.postId = prs.postId AND prs.userName = ? " +
	                   "WHERE (prs.isRead IS NULL OR prs.isRead = FALSE) AND p.author != ? " +
	                   "ORDER BY p.timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.setString(2, userName); 
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getBoolean("isDeleted")
	            );
	            postList.addPost(post);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return postList;
	}
	public void setTemporaryPassword(String username, String tempPassword) {
	    String query = "UPDATE userDB SET password = ?, isTemporaryPassword = TRUE WHERE userName = ?";  // Changed username to userName
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, tempPassword);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public boolean isPasswordTemporary(String username) {
	    String query = "SELECT isTemporaryPassword FROM userDB WHERE userName = ?";  // Already correct
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("isTemporaryPassword");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public void clearTemporaryPasswordFlag(String username) {
	    String query = "UPDATE userDB SET isTemporaryPassword = FALSE WHERE userName = ?";  // Changed username to userName
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	

	private void createTables() throws SQLException {
		
	    String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            + "firstName VARCHAR(255), "
	            + "middleName VARCHAR(255), "
	            + "lastName VARCHAR (255), "
	            + "preferredFirstName VARCHAR(255), "
	            + "emailAddress VARCHAR(255), "
	            + "adminRole BOOL DEFAULT FALSE, "
	            + "newRole1 BOOL DEFAULT FALSE, "
	            + "newRole2 BOOL DEFAULT FALSE)";
	    statement.execute(userTable);
	    
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);

	    String invitationCodesDeadline = "CREATE TABLE IF NOT EXISTS invitationCodesDeadline ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "deadline BIGINT, "
	            + "FOREIGN KEY (code) REFERENCES InvitationCodes(code) ON DELETE CASCADE"
	            + ")";
	    statement.execute(invitationCodesDeadline);
	    
	    ensureDeadlineColumnExists();
	    ensureTemporaryPasswordColumnExists();  
		createRequestTables();
	    createForumTables();
	}
	private void ensureTemporaryPasswordColumnExists() throws SQLException {
	    if (!columnExists("userDB", "isTemporaryPassword")) {
	        System.out.println("Adding missing 'isTemporaryPassword' column to userDB...");
	        String alterSQL = "ALTER TABLE userDB ADD COLUMN isTemporaryPassword BOOL DEFAULT FALSE";
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(alterSQL);
	        }
	    }
	}
	/*******
	 * <p> Method: getAllUsers() </p>
	 * ADDED
	 * <p> Description: grab all the information of the user from a query table in SQL and extract
	 * all the elements. This code borrows for the getUserList().</p>
	 * 
	 */
	public ArrayList<User> getAllUsers()
	{	// Create a array list called userList to store all the needed information
		ArrayList<User> userList = new ArrayList<>();
		//create a string of a query to be used to connect to the SQL table to access the database
		String query = "SELECT userName, firstname, lastName, preferredFirstName, emailAddress, "
				+ "adminRole, newRole1, newRole2 FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query))
		{
			//Once accessed save that connection of the table
			ResultSet rs = pstmt.executeQuery();
			//now grab each value using the rs guide
			while (rs.next()) {
				//Using the query get all the information for a user
				String username = rs.getString("userName");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String preferredFirstName = rs.getString("preferredFirstName");
				String emailAddress = rs.getString("emailAddress");
				boolean isAdmin = rs.getBoolean("adminRole");
				boolean isRole1 = rs.getBoolean("newRole1");
				boolean isRole2 = rs.getBoolean("newRole2");
				//create another object to store the information 
				User user = new User(username, "", firstName, "", lastName, preferredFirstName, 
						emailAddress, isAdmin, isRole1, isRole2);
				//Use the user object and store it into the ArrayList userList
				userList.add(user);
				
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}
	

/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "&lt;Select User&gt;" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	/*******
	 * <p> Method: void updateUsername(String oldUsername, String newUsername) </p>
	 * 
	 * <p> Description: Update the username of a user given that user's old username and the new
	 *		username.</p>
	 * 
	 * @param oldUsername is the old username of the user
	 * 
	 * @param newUsername is the new username for the user
	 *  
	 */
	// update the first name
	public void updateUsername(String oldUsername, String newUsername) {
	    String query = "UPDATE userDB SET userName = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newUsername);
	        pstmt.setString(2, oldUsername);
	        pstmt.executeUpdate();
	        currentUsername = newUsername;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	
	public String getPassword(String password) {
		String query = "SELECT password FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, password);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("password"); // Return the password if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updatePassword(String username, String password) </p>
	 * 
	 * <p> Description: Update the password of a user given that user's username and the new
	 *		password.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param password is the new password for the user
	 *  
	 */
	// update the first name
	public void updatePassword(String username, String password) {
	    String query = "UPDATE userDB SET password = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = password;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	/*******
	 * <p> Method: String checkEmailAddress(String email address) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's email address.</p>
	 * 
	 * @param emailAddress is the email address of the user
	 * 
	 * @return check if an email address is already in the database given an email address 
	 *  
	 */
	// check the email address
	public boolean checkEmailAddress(String emailAddress) {
		String query = "SELECT emailAddress FROM userDB WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return true; // Return true if email is in database
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	

	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	public boolean DeleteUser(String userName) {
	    String query = "DELETE FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        int deleteRows = pstmt.executeUpdate();
	        return deleteRows>0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; 
	}
	
	public List<String> getInvitationList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select an Email>");
		String query = "SELECT emailAddress FROM InvitationCodes";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("emailAddress"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		
		return userList;
	}
	
	public String getCodebyEmail (String email ) {
	    String query = "SELECT code FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("code");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	public void setDeadlineInDatabase(String code, long deadline) {
	    try {
	        String sql = "MERGE INTO invitationCodesDeadline (code, deadline) KEY(code) VALUES (?, ?)";
	        PreparedStatement ps = connection.prepareStatement(sql);
	        ps.setString(1, code);
	        ps.setLong(2, deadline);
	        ps.executeUpdate();
	        ps.close();
	    } catch (SQLException e) {
	        System.err.println("Error setting deadline in database: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	private boolean columnExists(String tableName, String columnName) throws SQLException {
	    DatabaseMetaData meta = connection.getMetaData();
	    try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
	        return rs.next();
	    }
	}
	
	public void ensureDeadlineColumnExists() throws SQLException {
	    if (!columnExists("invitationCodesDeadline", "deadline")) {
	        System.out.println("Adding missing 'deadline' column to invitationCodesDeadline...");
	        String alterSQL = "ALTER TABLE invitationCodesDeadline ADD COLUMN deadline BIGINT";
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(alterSQL);
	        }
	    }
	}
	
	public long getDeadlineFromDatabase(String code) {
	    long deadline = 0L;
	    String sql = "SELECT deadline FROM invitationCodesDeadline WHERE code = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, code);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                deadline = rs.getLong("deadline");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error fetching deadline from database: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return deadline;
	}
	
	public void removeExpiredInvitations() {
	    long now = System.currentTimeMillis();
	    String sql = "DELETE FROM InvitationCodes WHERE code IN (SELECT code FROM invitationCodesDeadline WHERE deadline < ?)";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setLong(1, now);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        System.err.println("Error cleaning expired invitations: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Creates a new private message in the database.
	 * @param message PrivateMessage object to create
	 * @return true if successful
	 */
	public boolean createPrivateMessage(PrivateMessage message) {
	    String sql = "INSERT INTO privateMessages (senderId, recipientId, postId, content, isRead, parentMessageId, timestamp) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, message.getSenderId());
	        pstmt.setString(2, message.getRecipientId());
	        
	        if (message.getPostId() != null) {
	            pstmt.setInt(3, message.getPostId());
	        } else {
	            pstmt.setNull(3, java.sql.Types.INTEGER);
	        }
	        
	        pstmt.setString(4, message.getContent());
	        pstmt.setBoolean(5, message.isRead());
	        
	        if (message.getParentMessageId() != null) {
	            pstmt.setInt(6, message.getParentMessageId());
	        } else {
	            pstmt.setNull(6, java.sql.Types.INTEGER);
	        }
	        
	        pstmt.setString(7, message.getTimestamp().toString());
	        
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error creating private message: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Gets all private messages for a specific user (as recipient).
	 * @param username User's username
	 * @return List of PrivateMessage objects ordered by newest first
	 */
	public List<PrivateMessage> getPrivateMessagesForUser(String username) {
	    List<PrivateMessage> messages = new ArrayList<>();
	    String sql = "SELECT * FROM privateMessages WHERE recipientId = ? ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Integer postId = rs.getObject("postId", Integer.class);
	            Integer parentId = rs.getObject("parentMessageId", Integer.class);
	            
	            PrivateMessage message = new PrivateMessage(
	                rs.getInt("messageId"),
	                rs.getString("senderId"),
	                rs.getString("recipientId"),
	                postId,
	                rs.getString("content"),
	                rs.getBoolean("isRead"),
	                parentId,
	                LocalDateTime.parse(rs.getString("timestamp"))
	            );
	            messages.add(message);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting private messages: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return messages;
	}

	/**
	 * Gets count of unread messages for a user.
	 * @param username User's username
	 * @return Count of unread messages
	 */
	public int getUnreadMessageCount(String username) {
	    String sql = "SELECT COUNT(*) FROM privateMessages WHERE recipientId = ? AND isRead = FALSE";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting unread message count: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return 0;
	}

	/**
	 * Marks a message as read.
	 * @param messageId Message ID
	 * @return true if successful
	 */
	public boolean markPrivateMessageAsRead(int messageId) {
	    String sql = "UPDATE privateMessages SET isRead = TRUE WHERE messageId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, messageId);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error marking message as read: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Marks a message as unread.
	 * @param messageId Message ID
	 * @return true if successful
	 */
	public boolean markPrivateMessageAsUnread(int messageId) {
	    String sql = "UPDATE privateMessages SET isRead = FALSE WHERE messageId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, messageId);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error marking message as unread: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Gets replies to a specific message.
	 * @param parentMessageId Parent message ID
	 * @return List of reply messages ordered by oldest first
	 */
	public List<PrivateMessage> getPrivateMessageReplies(int parentMessageId) {
	    List<PrivateMessage> replies = new ArrayList<>();
	    String sql = "SELECT * FROM privateMessages WHERE parentMessageId = ? ORDER BY timestamp ASC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, parentMessageId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Integer postId = rs.getObject("postId", Integer.class);
	            Integer parentId = rs.getObject("parentMessageId", Integer.class);
	            
	            PrivateMessage message = new PrivateMessage(
	                rs.getInt("messageId"),
	                rs.getString("senderId"),
	                rs.getString("recipientId"),
	                postId,
	                rs.getString("content"),
	                rs.getBoolean("isRead"),
	                parentId,
	                LocalDateTime.parse(rs.getString("timestamp"))
	            );
	            replies.add(message);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting message replies: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return replies;
	}

	/**
	 * Creates a new moderation flag in the database.
	 * @param flag ModerationFlag object to create
	 * @return true if successful
	 */
	public boolean createModerationFlag(ModerationFlag flag) {
	    String sql = "INSERT INTO moderationFlags (postId, staffId, flagReason, timestamp, status) " +
	                 "VALUES (?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, flag.getPostId());
	        pstmt.setString(2, flag.getStaffId());
	        pstmt.setString(3, flag.getFlagReason());
	        pstmt.setString(4, flag.getTimestamp().toString());
	        pstmt.setString(5, flag.getStatus());
	        
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error creating moderation flag: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Marks a post as flagged or unflagged.
	 * @param postId Post ID
	 * @param flagged True to mark as flagged, false to unflag
	 * @return true if successful
	 */
	public boolean markPostAsFlagged(int postId, boolean flagged) {
	    String sql = "UPDATE posts SET isFlagged = ? WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setBoolean(1, flagged);
	        pstmt.setInt(2, postId);
	        
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error marking post as flagged: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Checks if a post is currently flagged.
	 * @param postId Post ID
	 * @return true if post is flagged
	 */
	public boolean isPostFlagged(int postId) {
	    String sql = "SELECT isFlagged FROM posts WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getBoolean("isFlagged");
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error checking if post is flagged: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return false;
	}

	/**
	 * Gets all moderation flags in the system.
	 * @return List of all ModerationFlag objects ordered by newest first
	 */
	public List<ModerationFlag> getAllModerationFlags() {
	    List<ModerationFlag> flags = new ArrayList<>();
	    String sql = "SELECT * FROM moderationFlags ORDER BY timestamp DESC";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        while (rs.next()) {
	            ModerationFlag flag = new ModerationFlag(
	                rs.getInt("flagId"),
	                rs.getInt("postId"),
	                rs.getString("staffId"),
	                rs.getString("flagReason"),
	                LocalDateTime.parse(rs.getString("timestamp")),
	                rs.getString("status")
	            );
	            flags.add(flag);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting all moderation flags: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return flags;
	}

	/**
	 * Gets flags created by a specific staff member.
	 * @param staffUsername Staff member's username
	 * @return List of ModerationFlag objects created by this staff member
	 */
	public List<ModerationFlag> getModerationFlagsByStaff(String staffUsername) {
	    List<ModerationFlag> flags = new ArrayList<>();
	    String sql = "SELECT * FROM moderationFlags WHERE staffId = ? ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, staffUsername);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            ModerationFlag flag = new ModerationFlag(
	                rs.getInt("flagId"),
	                rs.getInt("postId"),
	                rs.getString("staffId"),
	                rs.getString("flagReason"),
	                LocalDateTime.parse(rs.getString("timestamp")),
	                rs.getString("status")
	            );
	            flags.add(flag);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting flags by staff: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return flags;
	}

	/**
	 * Gets all flags for a specific post.
	 * @param postId Post ID
	 * @return List of ModerationFlag objects for this post
	 */
	public List<ModerationFlag> getModerationFlagsForPost(int postId) {
	    List<ModerationFlag> flags = new ArrayList<>();
	    String sql = "SELECT * FROM moderationFlags WHERE postId = ? ORDER BY timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            ModerationFlag flag = new ModerationFlag(
	                rs.getInt("flagId"),
	                rs.getInt("postId"),
	                rs.getString("staffId"),
	                rs.getString("flagReason"),
	                LocalDateTime.parse(rs.getString("timestamp")),
	                rs.getString("status")
	            );
	            flags.add(flag);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting flags for post: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return flags;
	}

	/**
	 * Updates the status of a moderation flag.
	 * @param flagId Flag ID
	 * @param status New status (PENDING, RESOLVED, DISMISSED)
	 * @return true if successful
	 */
	public boolean updateFlagStatus(int flagId, String status) {
	    String sql = "UPDATE moderationFlags SET status = ? WHERE flagId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, status);
	        pstmt.setInt(2, flagId);
	        
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("Error updating flag status: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}



	/**
	 * Gets only flagged posts.
	 * @return List of flagged Post objects ordered by newest first
	 */
	public List<Post> getFlaggedPosts() {
	    List<Post> posts = new ArrayList<>();
	    String sql = "SELECT * FROM posts WHERE isFlagged = TRUE ORDER BY timestamp DESC";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                LocalDateTime.parse(rs.getString("timestamp")),
	                rs.getBoolean("isDeleted")
	            );
	            posts.add(post);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting flagged posts: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return posts;
	}

	/**
	 * Gets only unflagged posts.
	 * @return List of unflagged Post objects ordered by newest first
	 */
	public List<Post> getUnflaggedPosts() {
	    List<Post> posts = new ArrayList<>();
	    String sql = "SELECT * FROM posts WHERE isFlagged = FALSE ORDER BY timestamp DESC";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                LocalDateTime.parse(rs.getString("timestamp")),
	                rs.getBoolean("isDeleted")
	            );
	            posts.add(post);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting unflagged posts: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return posts;
	}

	/**
	 * Gets posts that have been flagged by a specific staff member.
	 * @param staffUsername Staff member's username
	 * @return List of Post objects flagged by this staff member
	 */
	public List<Post> getPostsFlaggedByStaff(String staffUsername) {
	    List<Post> posts = new ArrayList<>();
	    String sql = "SELECT DISTINCT p.* FROM posts p " +
	                 "INNER JOIN moderationFlags mf ON p.postId = mf.postId " +
	                 "WHERE mf.staffId = ? ORDER BY p.timestamp DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, staffUsername);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("postId"),
	                rs.getString("author"),
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("thread"),
	                LocalDateTime.parse(rs.getString("timestamp")),
	                rs.getBoolean("isDeleted")
	            );
	            posts.add(post);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting posts flagged by staff: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return posts;
	}


	/**
	 * Gets total count of all posts in the system.
	 * @return Total number of posts
	 */
	public int getTotalPostCount() {
	    String sql = "SELECT COUNT(*) FROM posts";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting total post count: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return 0;
	}

	/**
	 * Gets count of flagged posts.
	 * @return Number of posts with isFlagged = TRUE
	 */
	public int getFlaggedPostCount() {
	    String sql = "SELECT COUNT(*) FROM posts WHERE isFlagged = TRUE";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting flagged post count: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return 0;
	}

	/**
	 * Gets count of pending moderation flags.
	 * @return Number of flags with status = 'PENDING'
	 */
	public int getPendingFlagCount() {
	    String sql = "SELECT COUNT(*) FROM moderationFlags WHERE status = 'PENDING'";
	    
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error getting pending flag count: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return 0;
	}


	/**
	 * Initializes the moderation tables in the database.
	 * Call this method during database setup/initialization.
	 */
	public void initializeModerationTables() {
	    try (Statement stmt = connection.createStatement()) {
	        
	        // Create privateMessages table
	        System.out.println("Creating privateMessages table...");
	        stmt.execute(
	            "CREATE TABLE IF NOT EXISTS privateMessages (" +
	            "messageId INTEGER PRIMARY KEY AUTO_INCREMENT, " +
	            "senderId VARCHAR(255) NOT NULL, " +
	            "recipientId VARCHAR(255) NOT NULL, " +
	            "postId INTEGER, " +
	            "content TEXT NOT NULL, " +
	            "isRead BOOLEAN DEFAULT FALSE, " +
	            "parentMessageId INTEGER, " +
	            "timestamp VARCHAR(50) NOT NULL)"
	        );
	        
	        // Create moderationFlags table
	        System.out.println("Creating moderationFlags table...");
	        stmt.execute(
	            "CREATE TABLE IF NOT EXISTS moderationFlags (" +
	            "flagId INTEGER PRIMARY KEY AUTO_INCREMENT, " +
	            "postId INTEGER NOT NULL, " +
	            "staffId VARCHAR(255) NOT NULL, " +
	            "flagReason TEXT NOT NULL, " +
	            "timestamp VARCHAR(50) NOT NULL, " +
	            "status VARCHAR(20) DEFAULT 'PENDING')"
	        );
	        
	        // Add isFlagged column to posts table
	        System.out.println("Adding isFlagged column to posts table...");
	        try {
	            stmt.execute("ALTER TABLE posts ADD COLUMN isFlagged BOOLEAN DEFAULT FALSE");
	            System.out.println("isFlagged column added successfully");
	        } catch (SQLException e) {
	            // Column might already exist
	            if (e.getMessage().contains("duplicate column") || 
	                e.getMessage().contains("already exists")) {
	                System.out.println("isFlagged column already exists");
	            } else {
	                throw e;
	            }
	        }
	        
	        // Create indexes for better performance
	        System.out.println("Creating indexes...");
	        try {
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_pm_recipient ON privateMessages(recipientId)");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_pm_unread ON privateMessages(recipientId, isRead)");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_mf_post ON moderationFlags(postId)");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_mf_staff ON moderationFlags(staffId)");
	            stmt.execute("CREATE INDEX IF NOT EXISTS idx_posts_flagged ON posts(isFlagged)");
	            System.out.println("Indexes created successfully");
	        } catch (SQLException e) {
	            // Indexes might already exist, that's okay
	            System.out.println("Some indexes already exist: " + e.getMessage());
	        }
	        
	        System.out.println("Moderation tables initialized successfully!");
	        
	    } catch (SQLException e) {
	        System.err.println("Error initializing moderation tables: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	/**
	 * Updates the status of an existing request to 'CLOSED' and records the details of the admin action.
	 *
	 * @param requestId The ID of the request to be closed.
	 * @param adminAction A detailed note or summary provided by the Admin explaining why the request was closed.
	 * @param adminUsername The username of the Admin who performed the closing action.
	 * @return True if the request was successfully updated, false otherwise (request not found or Exception).
	 */
	public boolean closeRequest(int requestId, String adminAction, String adminUsername) {
	    String query = "UPDATE admin_requests SET status = 'CLOSED', adminAction = ?, closedByAdmin = ? WHERE requestId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, adminAction);
	        pstmt.setString(2, adminUsername);
	        pstmt.setInt(3, requestId);
	        
	        return pstmt.executeUpdate() > 0; // Returns true if one or more rows were updated
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Retrieves a list of all requests that currently have a status of 'CLOSED' from the database.
	 * The results are ordered by the timestamp in descending order (most recently closed first).
	 *
	 * @return A Request List containing all closed request records, or an empty list if no closed requests are found or an SQLException occurs.
	 * @see entityClasses.Request
	 */
	public List<Request> getClosedRequests() {
	    List<Request> closedRequestList = new ArrayList<>();
	    // Query selects only requests that have been closed
	    String query = "SELECT * FROM admin_requests WHERE status = 'CLOSED' ORDER BY timestamp DESC"; 
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            // Mapping the ResultSet to the Request entity object
	            Request request = new Request(
	                rs.getInt("requestId"),
	                rs.getString("staffUsername"),
	                rs.getString("description"),
	                rs.getString("adminAction"),
	                rs.getString("status"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getString("closedByAdmin"),
	                (Integer) rs.getObject("originalClosedRequestId") 
	            );
	            closedRequestList.add(request);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return closedRequestList;
	}
	
	/**
	 * Creates a new request record in the database with a status of 'REOPENED', linking it to the specified closed request ID.
	 * For when a Staff Memeber reopens a closed request
	 *
	 * @param closedRequestId The ID of the original request that is being reopened. This value is stored in the originalClosedRequestId column.
	 * @param staffUsername The username of the Staff member that reopened request.
	 * @param newDescription The new or updated description provided by the Staff for the reopened request.
	 * @return The generated ID of the newly created reopened request, or -1 if the insertion failed.
	 */
	public int createReopenedRequest(int closedRequestId, String staffUsername, String newDescription) {
	    String insertRequest = "INSERT INTO admin_requests (staffUsername, description, status, originalClosedRequestId, timestamp) "
	            + "VALUES (?, ?, 'REOPENED', ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, staffUsername);
	        pstmt.setString(2, newDescription);
	        pstmt.setInt(3, closedRequestId); // Link to the closed request ID
	        pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
	        
	        // ... (rest of execution and key retrieval) ...
	        int affectedRows = pstmt.executeUpdate();
	        if (affectedRows > 0) {
	            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    return generatedKeys.getInt(1); // Return the new ID
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}
	
	/**
	 * Retrieves a single request record from the database using its unique request identifier.
	 *
	 * @param requestId The ID of the request to retrieve.
	 * @return The fully mapped Request object if found, or NULL if no request matches the given ID or an Exception occurs.
	 * @see entityClasses.Request
	 */
	public Request getRequestById(int requestId) {
	    String query = "SELECT * FROM admin_requests WHERE requestId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, requestId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // Reusing your mapping logic from getActiveRequests
	            return new Request(
	                rs.getInt("requestId"),
	                rs.getString("staffUsername"),
	                rs.getString("description"),
	                rs.getString("adminAction"),
	                rs.getString("status"),
	                rs.getTimestamp("timestamp").toLocalDateTime(),
	                rs.getString("closedByAdmin"),
	                (Integer) rs.getObject("originalClosedRequestId") 
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	private void createModerationConfigTable() throws SQLException {
	    String sql = """
	        CREATE TABLE IF NOT EXISTS MODERATION_CONFIG (
	            id INT AUTO_INCREMENT PRIMARY KEY,
	            min_posts INT,
	            min_peers INT,
	            passing REAL,
	            excellence REAL,
	            sensitivity INT,
	            auto_highlight BOOLEAN
	        )
	    """;

	    statement.execute(sql);
	}

	/** Load config */
	public ModerationConfig loadModerationConfig() {
	    try {
	        createModerationConfigTable();

	        var rs = connection.prepareStatement(
	            "SELECT * FROM MODERATION_CONFIG ORDER BY id DESC LIMIT 1"
	        ).executeQuery();

	        if (rs.next()) {
	            return new ModerationConfig(
	                rs.getInt("min_posts"),
	                rs.getInt("min_peers"),
	                rs.getDouble("passing"),
	                rs.getDouble("excellence"),
	                rs.getInt("sensitivity"),
	                rs.getBoolean("auto_highlight")
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return ModerationConfig.defaultConfig();
	}

	/** Save config */
	public boolean saveModerationConfig(ModerationConfig cfg) {
	    try {
	        createModerationConfigTable();

	        var ps = connection.prepareStatement(
	            "INSERT INTO MODERATION_CONFIG " +
	            "(min_posts, min_peers, passing, excellence, sensitivity, auto_highlight) " +
	            "VALUES (?, ?, ?, ?, ?, ?)"
	        );

	        ps.setInt(1, cfg.getMinPosts());
	        ps.setInt(2, cfg.getMinPeerResponses());
	        ps.setDouble(3, cfg.getPassingThreshold());
	        ps.setDouble(4, cfg.getExcellenceThreshold());
	        ps.setInt(5, cfg.getFlagSensitivity());
	        ps.setBoolean(6, cfg.isAutoHighlightLowEngagement());

	        ps.executeUpdate();
	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Retrieves the current min posts threshold for grading from the MODERATION_CONFIG table.
	 *
	 * @return The value of min_posts from MODERATION_CONFIG, or -1 if no parameters set.
	 */
	public int getMinPostsThreshold() {
		String query = "SELECT min_posts FROM MODERATION_CONFIG";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("min_posts");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return -1;
	}

	/**
	 * Retrieves the current min peer replies threshold for grading from the MODERATION_CONFIG table.
	 *
	 * @return The value of min_peers from MODERATION_CONFIG, or -1 if no parameters set.
	 */
	public int getMinPeersThreshold() {
		String query = "SELECT min_peers FROM MODERATION_CONFIG";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("min_peers");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return -1;
	}

	/**
	 * Calculates and returns the overall grade for the active student.
	 *
	 * @param username the current student
	 * @return The value of min_posts from MODERATION_CONFIG, or -1 if no parameters set.
	 */
	public int getOverallGrade(String username) {
		ModerationConfig cfg = loadModerationConfig();	
		int postsBar = cfg.getMinPosts();
		int peersBar = cfg.getMinPeerResponses();
		int totalPosts = 0;
		int totalReplies = 0;
		
		List<Post> posts = getAllPosts().getAllPosts();
		for (Post post : posts) {
			if (post.getAuthor().equals(username) && !post.isDeleted()) {
				totalPosts += 1;
			}
			List<Reply> replies = getRepliesForPost(post.getPostId()).getAllReplies();
			for (Reply reply : replies) {
				if (reply.getAuthor().equals(username)) {
					totalReplies += 1;
				}
			}
		}
				
		double postPercent = (double) totalPosts / postsBar;
		if (postPercent > 1) {postPercent = 1; }
		double replyPercent = (double) totalReplies / peersBar;
		if (replyPercent > 1) {replyPercent = 1; }
		double overall = (postPercent * 60) + (replyPercent * 40);
		
		return (int)Math.round(overall);
	}
	
}


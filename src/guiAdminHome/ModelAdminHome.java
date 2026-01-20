package guiAdminHome;
import entityClasses.User; //added
import java.util.ArrayList; //added
import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import guiNewAccount.ModelNewAccount;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import guiNewAccount.ModelNewAccount; 
/*******
 * <p> Title: ModelAdminHome Class. </p>
 * 
 * <p> Description: The AdminHome Page Model.  This class is used as a access point for the Database 
 * where the view class and the controller class can grab data, move it and print it. No business logic
 * is needed because data is not being manipulated.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */


public class ModelAdminHome {
		private Database theDatabase;
	
	/*******
	 * <p> Title: ModelAdminHome Constructor. </p>
	 * 
	 * <p> Description: A reference to the Database object to allow access of data</p>
	 */
	public ModelAdminHome(Database database)
	{
		this.theDatabase = database;
	}
	/*******
	 * <p> Title: getAllUsers(). </p>
	 * 
	 * <p> Description: This class returns an array list of the data of users by calling on the 
	 * getAllUsers() method in the Database.</p>
	 */
	
	public ArrayList<User> getAllUsers()
	{
		return theDatabase.getAllUsers();
	}
  
	public static String checkForValidEmailAddress(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			return "\n*** ERROR *** The input is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		int state = 0;							// This is the FSM state number
		String inputLine = input;					// Save the reference to the input line as a global
		int currentCharNdx = 0;					// The index of the current character
		char currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		boolean running = true;						// Start the loop
		int nextState = -1;						// There is no next state
		boolean foundMiddleString = false;		// Reset the boolean flag
		boolean foundFullEmail = false;			// Reset the boolean flag
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
		
		// This is the place where semantic actions for a transition to the initial state occur
		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
				
				// The current character is checked against A-Z. If any are matched the FSM goes to state 1
				
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||	// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z')) {	// Check for a-z
					
					nextState = 1;
					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;
				
				// The execution of this state is finished
				break;
			
			case 1: 
				// State 1 has two valid transitions, 
				//	1: a A-Z or a-z that transitions back to state 1
				//  2: a @ symbol that transitions to state 2 

				
				// A-Z or a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
					(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
				}
				// ., -, _ -> State 2
				else if (currentChar == '@') {	// Check for @
					nextState = 2;
				}				
				// If it is none of those characters, the FSM halts
				else
					running = false;
				
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				break;			
				
			case 2: 
				// State 2 has two valid transitions.
				// 1: a A-Z- or a-z that transitions back to state 2
				// 2: a period that transitions to state 3
				
				// A-Z or a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
					(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					foundMiddleString = true;
					nextState = 2;					
				}
				// . -> State 3
				else if (currentChar == '.') {	// Check for .
					nextState = 3;
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;		
				
			case 3:
				// State 3 deals with a character after a period in the name.
				
				// a-z -> State 3
				if((currentChar >= 'a') && (currentChar <= 'z')) {
					foundFullEmail = true;
					nextState = 3;
				}
				// If is is none of those characters, the FSM halts
				else
					running = false;
				
			}
			
			if (running) {
				// displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				// moveToNextCharacter();
				currentCharNdx++;
				if (currentCharNdx < inputLine.length())
					currentChar = inputLine.charAt(currentCharNdx);
				else {
					currentChar = ' ';
					running = false;
				}

				// Move to the next state
				state = nextState;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		// displayDebuggingInfo();
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		String newEmailRecognizerErrorMessage = "\n*** ERROR *** ";
		
		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message depending
			// on whether or not a character was entered.
			if(currentCharNdx == 0) {newEmailRecognizerErrorMessage += "You must enter an email.\n";} 
			else {newEmailRecognizerErrorMessage += "Your email must start with A-Z or a-z(e.g: johndoe@example.com).\n";}
			
			return newEmailRecognizerErrorMessage;

		case 1:
			// State 1 is not a final state, so we can return a very specific error message.

			newEmailRecognizerErrorMessage += "Your email must contain an @ symbol(e.g: johndoe@example.com).\n";
			return newEmailRecognizerErrorMessage;
			

		case 2:
			// State 2 is not a final state, so we can return a very specific error message.
			newEmailRecognizerErrorMessage +=
				"Your email must contain an A-Z or a-z character after a @ symbol(e.g: johndoe@example.com).\n";
			return newEmailRecognizerErrorMessage;
			
		case 3:
			// State 3 is the final state. Check to see if the full email has been found.
			if(!foundMiddleString){
				newEmailRecognizerErrorMessage += 
						"Your email requires an A-Z or a-z character between a @ "
						+ "symbol and a period(e.g: johndoe@example.com).\n";
				return newEmailRecognizerErrorMessage;
			} else if(!foundFullEmail) {
				newEmailRecognizerErrorMessage += "Your email cannot end in a period(e.g: johndoe@example.com).\n";
				return newEmailRecognizerErrorMessage;
			} else {
				// Email is valid
				newEmailRecognizerErrorMessage = "";
				return newEmailRecognizerErrorMessage;
			}
			
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
}

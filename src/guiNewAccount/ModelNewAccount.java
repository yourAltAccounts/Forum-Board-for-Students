package guiNewAccount;

/*******
 * <p> Title: ModelNewAccount Class. </p>
 * 
 * <p> Description: The NewAccount Page Model.  This Model deals with an input from the 
 * user and checks to see if it conforms to the requirements specified by a graphical
 * representation of a finite state machine.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */
public class ModelNewAccount {
	
	public static String evaluatePassword(String input) {
		// The following are the local variable used to perform the Directed Graph simulation
		String inputLine = input;					// Save the reference to the input line as a global
		int currentCharNdx = 0;					// The index of the current character
		
		if(input.length() <= 0) {
			return "*** Error *** The password is empty!";
		}
		
		// The input is not empty, so we can access the first character
		char currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state.  This
		// local variable is a working copy of the input.
		
		// The following are the attributes associated with each of the requirements
		boolean foundUpperCase = false;				// Reset the Boolean flag
		boolean foundLowerCase = false;				// Reset the Boolean flag
		boolean foundNumericDigit = false;			// Reset the Boolean flag
		boolean foundSpecialChar = false;			// Reset the Boolean flag
		boolean foundLongEnough = false;			// Reset the Boolean flag
		
		// This flag determines whether the directed graph (FSM) loop is operating or not
		boolean running = true;						// Start the loop

		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		while (running) {
			// The cascading if statement sequentially tries the current character against all of
			// the valid transitions, each associated with one of the requirements
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				return "*** Error *** An invalid character has been found!";
			}
			if (currentCharNdx >= 7 && currentCharNdx <= 32) {
				System.out.println("At least eight characters found");
				foundLongEnough = true;
			} else if (currentCharNdx > 32) {
				System.out.println("Over thirty-two characters found");
				foundLongEnough = false;
			}
			
			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
			
			System.out.println();
		}
		
		// Construct a String with a list of the requirement elements that were found.
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "At least one uppercase letter required. ";
		
		if (!foundLowerCase)
			errMessage += "At least one lowercase letter required. ";
		
		if (!foundNumericDigit)
			errMessage += "At least one numeric digit required. ";
			
		if (!foundSpecialChar)
			errMessage += "At least one special character required. ";
			
		if (!foundLongEnough)
			errMessage += "Must be between eight and thirty-two characters. ";
		
		if (errMessage == "")
			return "";
		
		// If it gets here, there something was not found, so return an appropriate message
		return errMessage;
	}
}

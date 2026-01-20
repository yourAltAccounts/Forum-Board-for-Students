package guiUserUpdate;

public class Model {
	
	public static String checkForValidUserName(String input) {
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
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
		
		// This is the place where semantic actions for a transition to the initial state occur
		
		int userNameSize = 0;					// Initialize the UserName size

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
				
				// The current character is checked against A-Z or a-z. If any are matched
				// the FSM goes to state 1
				
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z
					nextState = 1;
					
					// Count the character 
					userNameSize++;
					
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
				//	1: a A-Z, a-z, 0-9 that transitions back to state 1
				//  2: a period, minus, or underscore that transitions to state 2 

				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
					
					// Count the character
					userNameSize++;
				}
				// ., -, _ -> State 2
				else if ((currentChar == '.') || (currentChar == '-') || (currentChar == '_')) {							// Check for /
					nextState = 2;
					
					// Count the ., -, or _
					userNameSize++;
				}				
				// If it is none of those characters, the FSM halts
				else
					running = false;
				
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 32)
					running = false;
				break;			
				
			case 2: 
				// State 2 deals with a character after a period, minus, or underscore in the name.
				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
					(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
					
					// Count the odd digit
					userNameSize++;
					
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 32)
					running = false;
				break;			
			}
			
			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
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
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		String userNameRecognizerErrorMessage = "\n*** ERROR *** ";
		
		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage += "A UserName must start with A-Z or a-z.\n";
			return userNameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
			// we must ensure the whole string has been consumed.

			if (userNameSize < 8) {
				// UserName is too small
				userNameRecognizerErrorMessage += "A UserName must have at least 8 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (userNameSize > 32) {
				// UserName is too long
				userNameRecognizerErrorMessage += 
					"A UserName must have no more than 32 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				userNameRecognizerErrorMessage += 
					"A UserName character may only contain the characters A-Z, a-z, 0-9.\n";
				return userNameRecognizerErrorMessage;
			}
			else {
					// UserName is valid
					userNameRecognizerErrorMessage = "";
					return userNameRecognizerErrorMessage;
			}

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage +=
				"A UserName character after a period, minus, or underscore must be A-Z, a-z, 0-9.\n";
			return userNameRecognizerErrorMessage;
			
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
	
	public static String checkForValidPassword(String input) {
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
			if (currentCharNdx > 7 && currentCharNdx <= 32) {
				System.out.println("At least eight characters found");
				System.out.println(currentCharNdx);
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
			errMessage += "At least one uppercase letter required.\n";
		
		if (!foundLowerCase)
			errMessage += "At least one lowercase letter required.\n";
		
		if (!foundNumericDigit)
			errMessage += "At least one numeric digit required.\n";
			
		if (!foundSpecialChar)
			errMessage += "At least one special character required.\n";
			
		if (!foundLongEnough)
			errMessage += "Must be between eight and thirty-two characters.\n";
		
		if (errMessage == "")
			return "";
		
		// If it gets here, there something was not found, so return an appropriate message
		return errMessage;
	}
	
	public static String checkForValidNewName(String input) {
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
				
				// A-Z -> State 1
				if (currentChar >= 'A' && currentChar <= 'Z' ) {	// Check for A-Z
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
				//  2: a hyphen or apostrophe that transitions to state 2 

				
				// A-Z or a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z
					nextState = 1;
				}
				// ', - -> State 2
				else if ((currentChar == '-') || (currentChar == '\'')) {	// Check for - or '
					nextState = 2;
				}				
				// If it is none of those characters, the FSM halts
				else
					running = false;
				
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				break;			
				
			case 2: 
				// State 2 deals with a character after a hyphen or apostrophe in the name.
				
				// A-Z or a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z
					nextState = 1;					
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;			
			}
			
			if (running) {
				// displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
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
		String newNameRecognizerErrorMessage = "\n*** ERROR *** ";
		
		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			newNameRecognizerErrorMessage += "Your name must start with A-Z.\n";
			return newNameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
			// we must ensure the whole string has been consumed.

			if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				newNameRecognizerErrorMessage += 
					"Your name may only contain the characters A-Z, a-z, -(hyphen) and '(apostrophe).\n";
				return newNameRecognizerErrorMessage;
			}
			else {
					// UserName is valid
					newNameRecognizerErrorMessage = "";
					return newNameRecognizerErrorMessage;
			}

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			newNameRecognizerErrorMessage +=
				"Your name must contain an A-Z or a-z character after a hyphen or apostrophe.\n";
			return newNameRecognizerErrorMessage;
			
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
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
				
				// The current character is checked against A-Z or a-z. 
				// If any are matched the FSM goes to state 1
				
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||	// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z
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

				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z' ) ||		// Check for a-z
					(currentChar >= '0' && currentChar <= '9')) {		// Check for 0-9
					nextState = 1;
				}
				// @ -> State 2
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
				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
					(currentChar >= 'a' && currentChar <= 'z' ) ||		// Check for a-z
					(currentChar >= '0' && currentChar <= '9')) {		// Check for 0-9
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
			if(currentCharNdx == 0) {newEmailRecognizerErrorMessage += "You must enter a email.\n";} 
			else {newEmailRecognizerErrorMessage += "Your email must contain only A-Z, a-z, or 0-9(e.g: johndoe@example.com).\n";}
			
			return newEmailRecognizerErrorMessage;

		case 1:
			// State 1 is not a final state, so we can return a very specific error message.

			newEmailRecognizerErrorMessage += "Your email must contain an @ symbol(e.g: johndoe@example.com).\n";
			return newEmailRecognizerErrorMessage;
			

		case 2:
			// State 2 is not a final state, so we can return a very specific error message.
			if(foundMiddleString) {
				newEmailRecognizerErrorMessage += "Your email must contain a period(e.g: johndoe@example.com).\n";
			} else {				
				newEmailRecognizerErrorMessage +=
					"Your email must contain an A-Z, a-z, or 0-9 after a @ symbol(e.g: johndoe@example.com).\n";
			}
			return newEmailRecognizerErrorMessage;
		case 3:
			// State 3 is the final state. Check to see if the full email has been found.
			if(!foundMiddleString){
				newEmailRecognizerErrorMessage += 
						"Your email requires an A-Z, a-z, or 0-9 between a @ "
						+ "symbol and a period(e.g: johndoe@example.com).\n";
				return newEmailRecognizerErrorMessage;
			} else if(!foundFullEmail) {
				newEmailRecognizerErrorMessage += 
						"Your email requires an a-z character after a period(e.g: johndoe@example.com).\n";
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

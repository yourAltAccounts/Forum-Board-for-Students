package guiNewAccount;

public class PasswordRecognizer {

	public static String passwordRecognizerErrorMessage = "";
	public static String passwordRecognizerInput = "";
	public static int passwordRecognizerIndexofError = -1;
	
	private static int passwordSize = 0;
	private static boolean hasUppercase = false;
	private static boolean hasLowercase = false;
	private static boolean hasDigit = false;
	private static boolean hasSpecial = false;

	private static boolean isSpecialChar(char c) {
		return c == '!' || c == '@' || c == '#' || c == '$' || 
		       c == '%' || c == '^' || c == '&' || c == '*';
	}

	public static String checkForValidPassword(String input) {
		if(input.length() <= 0) {
			passwordRecognizerIndexofError = 0;
			return "\n*** ERROR *** The password is empty";
		}
		
		passwordRecognizerInput = input;
		passwordSize = input.length();
		hasUppercase = false;
		hasLowercase = false;
		hasDigit = false;
		hasSpecial = false;
		
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			
			if (c >= 'A' && c <= 'Z') {
				hasUppercase = true;
			} else if (c >= 'a' && c <= 'z') {
				hasLowercase = true;
			} else if (c >= '0' && c <= '9') {
				hasDigit = true;
			} else if (isSpecialChar(c)) {
				hasSpecial = true;
			} else {
				passwordRecognizerIndexofError = i;
				passwordRecognizerErrorMessage = 
					"Password contains invalid character at position " + (i+1) + ".\n" +
					"Only A-Z, a-z, 0-9, and !@#$%^&* are allowed.";
				return passwordRecognizerErrorMessage;
			}
		}
		
		if (passwordSize < 8) {
			passwordRecognizerIndexofError = 0;
			passwordRecognizerErrorMessage = "Password must be at least 8 characters long.\n";
			return passwordRecognizerErrorMessage;
		}
		
		if (passwordSize > 32) {
			passwordRecognizerIndexofError = 32;
			passwordRecognizerErrorMessage = "Password must be no more than 32 characters long.\n";
			return passwordRecognizerErrorMessage;
		}
		
		StringBuilder missing = new StringBuilder();
		if (!hasUppercase) missing.append("- At least one uppercase letter (A-Z)\n");
		if (!hasLowercase) missing.append("- At least one lowercase letter (a-z)\n");
		if (!hasDigit) missing.append("- At least one digit (0-9)\n");
		if (!hasSpecial) missing.append("- At least one special character (!@#$%^&*)\n");
		
		if (missing.length() > 0) {
			passwordRecognizerIndexofError = 0;
			passwordRecognizerErrorMessage = "Password is missing required elements:\n" + missing.toString();
			return passwordRecognizerErrorMessage;
		}
		
		passwordRecognizerIndexofError = -1;
		passwordRecognizerErrorMessage = "";
		return passwordRecognizerErrorMessage;
	}
}
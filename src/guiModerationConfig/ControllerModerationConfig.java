package guiModerationConfig;

import entityClasses.ModerationConfig;
import entityClasses.User;
import javafx.stage.Stage;

/**
 * Controller for the Moderation Configuration feature within the Role 2 (Staff)
 * interface. Acts as the intermediary between the Moderation Config View and
 * the underlying Model, following the MVC architectural pattern.
 *
 * <p>This controller supports the following operations:</p>
 * <ul>
 *     <li>Loading existing moderation configuration settings</li>
 *     <li>Saving updated moderation parameters</li>
 *     <li>Navigating back to the Role 2 home page</li>
 * </ul>
 *
 * <p>Since moderation settings are system-wide and not tied to individual
 * users, the controller's operations are implemented as static methods.</p>
 *
 * <p>© CSE 360 Team, 2025</p>
 */
public class ControllerModerationConfig {

    /**
     * Default constructor for the controller.
     * <p>
     * Not used since this controller operates exclusively through static
     * methods, but provided for completeness and to satisfy Javadoc standards.
     * </p>
     */
    public ControllerModerationConfig() {
        // No initialization needed.
    }

    /**
     * Loads the currently stored moderation configuration from the Model layer.
     *
     * <p>This method acts as a pass-through to
     * {@link ModelModerationConfig#loadConfig()}, allowing the View layer to
     * retrieve the active moderation settings without accessing the Model
     * directly.</p>
     *
     * @return a {@link ModerationConfig} object containing the system's
     *         current moderation parameters.
     */
    public static ModerationConfig loadConfig() {
        return ModelModerationConfig.loadConfig();
    }

    /**
     * Saves updated moderation configuration settings by forwarding the
     * provided {@link ModerationConfig} object to the Model layer.
     *
     * <p>This method simply delegates the operation to
     * {@link ModelModerationConfig#saveConfig(ModerationConfig)}.</p>
     *
     * @param cfg the updated moderation configuration to be persisted.
     */
    protected static void saveConfig(ModerationConfig cfg) {
        ModelModerationConfig.saveConfig(cfg);
    }

    /**
     * Handles navigation when the user clicks the “Back” button on the
     * Moderation Config screen.
     *
     * <p>This method retrieves the current staff member’s account information
     * from the database. If successful, it reconstructs a {@link User} object
     * and forwards control to
     * {@link guiRole2.ViewRole2Home#displayRole2Home(Stage, User)} to display
     * the Role 2 (Staff) home page.</p>
     *
     * @param stage the JavaFX {@link Stage} on which the Role 2 home interface
     *              will be displayed.
     */
    protected static void performBack(Stage stage) {
<<<<<<< HEAD
        String username = ViewModerationConfig.currentStaffUsername;

=======
        String username = guiRole2.ViewRole2Home.theUser.getUserName();
        
>>>>>>> branch 'main' of git@github.com:AaronShih260/TP2.git
        if (applicationMain.FoundationsMain.database.getUserAccountDetails(username)) {
            User user = new User(
                username,
                applicationMain.FoundationsMain.database.getCurrentPassword(),
                applicationMain.FoundationsMain.database.getCurrentFirstName(),
                applicationMain.FoundationsMain.database.getCurrentMiddleName(),
                applicationMain.FoundationsMain.database.getCurrentLastName(),
                applicationMain.FoundationsMain.database.getCurrentPreferredFirstName(),
                applicationMain.FoundationsMain.database.getCurrentEmailAddress(),
                applicationMain.FoundationsMain.database.getCurrentAdminRole(),
                applicationMain.FoundationsMain.database.getCurrentNewRole1(),
                applicationMain.FoundationsMain.database.getCurrentNewRole2()
            );

            guiRole2.ViewRole2Home.displayRole2Home(stage, user);
        } else {
        	System.out.println("Fail");
        }
    }
}

package guiModerationConfig;

import entityClasses.ModerationConfig;


/**
 * <p><b>ModelModerationConfig</b></p>
 *
 * <p>This class serves as the Model layer for the Moderation Configuration 
 * system. It handles persistence operations such as loading and saving 
 * moderation settings to the application's database.</p>
 *
 * <p>This class contains only static methods because moderation settings are
 * global, and no instance of this class is ever required.</p>
 */
public class ModelModerationConfig {
	
	/**
     * Private constructor to prevent instantiation.
     * <p>
     * This utility-style model class is not meant to be instantiated, because
     * all operations are provided through static methods.
     * </p>
     */
    private ModelModerationConfig() {
        // Prevent instantiation
    }

    /**
     * Loads the current moderation configuration from the database.
     *
     * <p>If no configuration exists yet in the database, the method returns 
     * {@link ModerationConfig#defaultConfig()} supplied by the Database layer.</p>
     *
     * @return A {@link ModerationConfig} object containing all grading and 
     *         participation rule settings.
     */
    protected static ModerationConfig loadConfig() {
        return applicationMain.FoundationsMain.database.loadModerationConfig();
    }

    /**
     * Saves the provided moderation configuration into the database.
     *
     * <p>This method overwrites the existing configuration stored in the system.
     * It is typically invoked after staff apply parameter updates from the GUI.</p>
     *
     * @param cfg The updated {@link ModerationConfig} settings to save.
     * @return {@code true} if the save operation succeeded; 
     *         {@code false} otherwise.
     */
    protected static boolean saveConfig(ModerationConfig cfg) {
        return applicationMain.FoundationsMain.database.saveModerationConfig(cfg);
    }
}


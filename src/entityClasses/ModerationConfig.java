package entityClasses;

/**
 * <p><b>ModerationConfig</b></p>
 *
 * This class stores all configuration parameters used by the moderation and 
 * discussion–evaluation system. Each parameter is configurable by teaching staff 
 * through the Moderation Configuration GUI.
 * 
 * <p>The configuration includes:</p>
 * <ul>
 *     <li>Minimum number of posts required by a student</li>
 *     <li>Minimum number of peer responses required</li>
 *     <li>Passing grade threshold</li>
 *     <li>Excellence grade threshold</li>
 *     <li>Flag sensitivity for automated moderation</li>
 *     <li>Auto-highlighting rules for low engagement</li>
 * </ul>
 *
 * <p>This class acts as a simple data model (POJO) and contains only fields 
 * and getters, and a static factory method for default settings.</p>
 * 
 * <p>Copyright: CSE 360 Team © 2025</p>
 */
public class ModerationConfig {

    /** Minimum required posts a student must create. */
    private int minPosts;

    /** Minimum required peer responses a student must provide. */
    private int minPeerResponses;

    /** Grade threshold required to pass the discussion assignment. */
    private double passingThreshold;

    /** Grade threshold representing excellent/high-performance work. */
    private double excellenceThreshold;

    /** Sensitivity used by automated flag detection (1 = strict, 10 = lenient). */
    private int flagSensitivity;

    /** Indicates whether low engagement should be automatically highlighted. */
    private boolean autoHighlightLowEngagement;

    /**
     * Constructs a ModerationConfig object with all moderation-related parameters.
     *
     * @param minPosts                   Minimum posts required
     * @param minPeerResponses           Minimum peer responses required
     * @param passingThreshold           Grade required to pass
     * @param excellenceThreshold        Grade required for excellence
     * @param flagSensitivity            Auto-flag sensitivity (1 = strict, 10 = lenient)
     * @param autoHighlightLowEngagement Whether low engagement is automatically highlighted
     */
    public ModerationConfig(int minPosts, int minPeerResponses,
                            double passingThreshold, double excellenceThreshold,
                            int flagSensitivity, boolean autoHighlightLowEngagement) {
        this.minPosts = minPosts;
        this.minPeerResponses = minPeerResponses;
        this.passingThreshold = passingThreshold;
        this.excellenceThreshold = excellenceThreshold;
        this.flagSensitivity = flagSensitivity;
        this.autoHighlightLowEngagement = autoHighlightLowEngagement;
    }

    /**
     * Gets the minimum required posts.
     * @return minimum required posts
     */
    public int getMinPosts() { return minPosts; }

    /**
     * Gets the minimum peer responses required.
     * @return minimum required peer responses
     */
    public int getMinPeerResponses() { return minPeerResponses; }

    /**
     * Gets the passing grade threshold.
     * @return passing grade threshold
     */
    public double getPassingThreshold() { return passingThreshold; }

    /**
     * Gets the excellence grade threshold.
     * @return excellence grade threshold
     */
    public double getExcellenceThreshold() { return excellenceThreshold; }

    /**
     * Gets the engagement flag sensitivity (1 = strict, 10 = lenient).
     * @return flag sensitivity
     */
    public int getFlagSensitivity() { return flagSensitivity; }

    /**
     * Indicates whether auto-highlight for low engagement is enabled.
     * @return true if enabled
     */
    public boolean isAutoHighlightLowEngagement() { return autoHighlightLowEngagement; }

    /**
     * Returns a default configuration used if no data exists in the database.
     * @return default ModerationConfig
     */
    public static ModerationConfig defaultConfig() {
        return new ModerationConfig(
            1,   // min posts
            1,   // min peer responses
            60,  // passing threshold
            85,  // excellence threshold
            5,   // sensitivity
            true // auto highlight
        );
    }
}


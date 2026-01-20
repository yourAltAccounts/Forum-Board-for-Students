package guiModerationConfig;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import entityClasses.ModerationConfig;

/**
 * <p><b>ViewModerationConfig</b></p>
 *
 * <p>This class represents the GUI (View component in the MVC pattern) used by
 * teaching staff to configure moderation parameters for evaluating student
 * discussions. These settings include minimum posting requirements, peer
 * responses, grading thresholds, flag sensitivity, and auto-highlight rules.</p>
 *
 * <p>The page displays:</p>
 * <ul>
 *     <li>Current moderation settings (live values)</li>
 *     <li>Editable input fields for each parameter</li>
 *     <li>An Apply button that saves and updates the UI in real time</li>
 *     <li>A Back button that returns the user to Role 2 Home</li>
 * </ul>
 *
 * <p>This View uses a Singleton pattern to ensure only one configuration GUI
 * exists at a time.</p>
 *
 * <p>Part of the Moderation MVC subsystem.</p>
 *
 * <p>© CSE 360 Team, 2025</p>
 */
public class ViewModerationConfig {

    /** Width inherited from application main window. */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

    /** Height inherited from application main window. */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /** Page title label. */
    private static Label label_Title = new Label("Parameter Modification");

    /** Instructions shown under the title. */
    private static Label label_Instructions = new Label("Set up the parameters for grading");

    /** Button used to return to the Role 2 Home page. */
    private static Button button_Back = new Button("Back");

    /** Username of staff currently using the configuration screen. */
    protected static String currentStaffUsername = "";

    /** Indicates whether the staff user is an administrator. */
    protected static boolean isAdmin = false;

    /** The Stage used to render this configuration page. */
    private static Stage theStage;

    /** Root pane for the configuration GUI layout. */
    private static Pane theRootPane;

    /** Shared scene instance (Singleton). */
    public static Scene theModerationScene = null;

    /** Singleton instance of this view. */
    private static ViewModerationConfig theView = null;

    /**
     * Displays the Moderation Configuration GUI.
     *
     * @param ps           The JavaFX stage to render this interface into.
     * @param staffUsername Username of the staff member accessing this page.
     * @param adminRole    True if the user has admin privileges.
     */
    public static void display(Stage ps, String staffUsername, boolean adminRole) {
        theStage = ps;
        currentStaffUsername = staffUsername;
        isAdmin = adminRole;

        if (theView == null) {
            theView = new ViewModerationConfig();
        }

        theStage.setTitle("Moderation Parameter Setup");
        theStage.setScene(theModerationScene);
        theStage.show();
    }

    /**
     * Private constructor for the Singleton pattern.
     * Initializes the root pane and builds the page UI.
     */
    private ViewModerationConfig() {
        theRootPane = new BorderPane();
        theModerationScene = new Scene(theRootPane, width, height);
        setupUI();
    }

    /**
     * Builds the full moderation configuration GUI layout.
     * <p>Layout sections:</p>
     * <ul>
     *     <li>Top: Title + Instructions</li>
     *     <li>Center: Configuration fields & current values</li>
     *     <li>Bottom: Apply + Back buttons</li>
     * </ul>
     */
    private void setupUI() {

        BorderPane root = (BorderPane) theRootPane;

        /* ===========================================================
         *   TOP TITLE SECTION
         * =========================================================== */
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));
        topBox.setAlignment(Pos.CENTER);

        label_Title.setFont(Font.font("Arial", 28));
        label_Instructions.setFont(Font.font("Arial", 14));

        topBox.getChildren().addAll(label_Title, label_Instructions);
        root.setTop(topBox);

        /* ===========================================================
         *   LOAD CURRENT CONFIGURATION
         * =========================================================== */
        ModerationConfig cfg = ControllerModerationConfig.loadConfig();

        /* ===========================================================
         *   CENTER CONFIGURATION FIELDS
         * =========================================================== */
        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));

        // -------- Minimum Posts --------
        Label lblPosts = new Label("Minimum Posts Required (Current: " + cfg.getMinPosts() + ")");
        Spinner<Integer> spnMinPosts = new Spinner<>(0, 50, cfg.getMinPosts());
        VBox blockPosts = new VBox(5, lblPosts, spnMinPosts);

        // -------- Minimum Peer Responses --------
        Label lblPeers = new Label("Minimum Peer Responses (Current: " + cfg.getMinPeerResponses() + ")");
        Spinner<Integer> spnMinPeers = new Spinner<>(0, 50, cfg.getMinPeerResponses());
        VBox blockPeers = new VBox(5, lblPeers, spnMinPeers);

        // -------- Passing Threshold --------
        Label lblPassing = new Label("Passing Grade Threshold (Current: " + cfg.getPassingThreshold() + ")");
        TextField txtPassing = new TextField(String.valueOf(cfg.getPassingThreshold()));
        VBox blockPassing = new VBox(5, lblPassing, txtPassing);

        // -------- Excellence Threshold --------
        Label lblExcellence = new Label("Excellence Grade Threshold (Current: " + cfg.getExcellenceThreshold() + ")");
        TextField txtExcellence = new TextField(String.valueOf(cfg.getExcellenceThreshold()));
        VBox blockExcellence = new VBox(5, lblExcellence, txtExcellence);

        // -------- Flag Sensitivity --------
        Label lblSensitivity = new Label("Flag Sensitivity (Current: " + cfg.getFlagSensitivity() + ")");
        Slider sldFlagSensitivity = new Slider(1, 10, cfg.getFlagSensitivity());
        sldFlagSensitivity.setShowTickMarks(true);
        sldFlagSensitivity.setShowTickLabels(true);
        VBox blockSensitivity = new VBox(5, lblSensitivity, sldFlagSensitivity);

        // -------- Auto Highlight --------
        Label lblAuto = new Label("Auto-highlight low engagement (Current: " +
                (cfg.isAutoHighlightLowEngagement() ? "Enabled" : "Disabled") + ")");
        CheckBox chkAutoHighlight = new CheckBox("Enable Auto Highlight");
        chkAutoHighlight.setSelected(cfg.isAutoHighlightLowEngagement());
        VBox blockAuto = new VBox(5, lblAuto, chkAutoHighlight);

        centerBox.getChildren().addAll(
            blockPosts, blockPeers, blockPassing,
            blockExcellence, blockSensitivity, blockAuto
        );

        root.setCenter(centerBox);

        /* ===========================================================
         *   BOTTOM BUTTONS (APPLY + BACK)
         * =========================================================== */
        HBox buttonBox = new HBox(20);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(Pos.CENTER);

        Button button_Apply = new Button("Apply");

        /**
         * APPLY button event handler:
         * <ol>
         *     <li>Validates input values</li>
         *     <li>Saves new configuration through controller</li>
         *     <li>Reloads configuration from model</li>
         *     <li>Updates labels live (real-time)</li>
         *     <li>Displays success message</li>
         * </ol>
         */
        button_Apply.setOnAction(e -> {
            try {
                ModerationConfig updated = new ModerationConfig(
                    spnMinPosts.getValue(),
                    spnMinPeers.getValue(),
                    Double.parseDouble(txtPassing.getText()),
                    Double.parseDouble(txtExcellence.getText()),
                    (int) sldFlagSensitivity.getValue(),
                    chkAutoHighlight.isSelected()
                );

                // Save new config
                ControllerModerationConfig.saveConfig(updated);

                // Reload updated config to refresh UI
                ModerationConfig refreshed = ControllerModerationConfig.loadConfig();

                // Update labels to reflect new live values
                lblPosts.setText("Minimum Posts Required (Current: " + refreshed.getMinPosts() + ")");
                lblPeers.setText("Minimum Peer Responses (Current: " + refreshed.getMinPeerResponses() + ")");
                lblPassing.setText("Passing Grade Threshold (Current: " + refreshed.getPassingThreshold() + ")");
                lblExcellence.setText("Excellence Grade Threshold (Current: " + refreshed.getExcellenceThreshold() + ")");
                lblSensitivity.setText("Flag Sensitivity (Current: " + refreshed.getFlagSensitivity() + ")");
                lblAuto.setText("Auto-highlight low engagement (Current: " +
                        (refreshed.isAutoHighlightLowEngagement() ? "Enabled" : "Disabled") + ")");

                showSuccess("Settings updated!");

            } catch (NumberFormatException ex) {
                showError("Invalid Number", "Please enter valid numeric values.");
            }
        });

        /**
         * BACK button → returns to Role 2 Home page through the Controller.
         */
        button_Back.setOnAction(e -> ControllerModerationConfig.performBack(theStage));

        buttonBox.getChildren().addAll(button_Apply, button_Back);
        root.setBottom(buttonBox);
    }

    /**
     * Displays an informational alert dialog.
     *
     * @param message The message to display in the alert.
     */
    protected static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error alert dialog.
     *
     * @param title   The header/title of the error dialog.
     * @param message The detailed error message.
     */
    protected static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


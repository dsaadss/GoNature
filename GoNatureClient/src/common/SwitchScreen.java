package common;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The {@code SwitchScreen} class provides utility methods for changing screens within a JavaFX application.
 * It supports loading a new FXML screen, applying an associated CSS stylesheet, and enabling window dragging functionality.
 */
public class SwitchScreen {
	
	
	/**
     * Changes the current screen to the specified FXML file and applies the provided CSS stylesheet. This method is invoked
     * during various navigation actions within the application, facilitating smooth transitions between different user interfaces.
     *
     * @param event The {@link ActionEvent} triggered by the user action that initiates the screen change.
     * @param path The path to the FXML file that defines the layout of the new screen.
     * @param css The path to the CSS file that contains the styles to be applied to the new screen.
     */
	public static void changeScreen(ActionEvent event,String path,String css ) {
	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    try {
	    	// Load the FXML file
	        Parent root = FXMLLoader.load(SwitchScreen.class.getResource(path));	        
	        // Create a new scene with the loaded content
	        Scene scene = new Scene(root);
	        // Set the new scene to the provided stage
	        stage.setScene(scene);
	        java.net.URL cssResource = SwitchScreen.class.getResource(css);
	        if (cssResource != null) {
	            scene.getStylesheets().add(cssResource.toExternalForm());
	        } else {
	            System.out.println("CSS file not found: " + css);
	        }
	        enableDrag(root, stage);

	        // Show the updated stage
	        stage.show();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Error switching screens: " + e.getMessage());
	    }
    }
	
	
    /**
     * Enables dragging functionality for the application window. This method allows the user to click and drag the window
     * to a new position on the screen, enhancing the application's usability.
     *
     * @param root The {@link Parent} node of the current scene, used as the draggable area.
     * @param stage The {@link Stage} representing the application window, which will be moved based on user interaction.
     */
    public static void enableDrag(Parent root, Stage stage) {
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
}

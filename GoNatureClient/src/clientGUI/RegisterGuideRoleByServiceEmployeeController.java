package clientGUI;

import java.io.IOException;

import client.ClientUI;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * The {@code RegisterGuideRoleByServiceEmployeeController} class controls the interface for service employees to assign
 * the guide role to users based on their ID. It provides the functionality for the service employee to enter a user ID,
 * update the user's role to 'guide', and navigate back to the service employee menu.
 */
public class RegisterGuideRoleByServiceEmployeeController {

    @FXML
    private Button backBtn;

    @FXML
    private TextField idField;

    @FXML
    private Button updateBtn;

    
    /**
     * Navigates back to the service employee menu when the 'Back' button is clicked, allowing the service employee to
     * exit the guide role assignment process.
     *
     * @param event The event triggered by clicking the 'Back' button.
     * @throws IOException If an I/O error occurs during screen transition.
     */
    @FXML
    void ClickOnBack(ActionEvent event)  throws IOException {
    	try {
    		  SwitchScreen.changeScreen(event,"/clientGUI/ServiceEmployeeMenuController.fxml"
    				  ,"/resources/ServiceEmployeeMenuController.css");

			  } catch (Exception e) {
			      e.printStackTrace();}
    }
    
    
    /**
     * Updates the role of the user specified by the ID in the text field to 'guide' when the 'Update' button is clicked.
     * It sends a request to the server to update the user's role based on the provided ID.
     *
     * @param event The event triggered by clicking the 'Update' button.
     * @throws IOException If an I/O error occurs during the role update process.
     */
    @FXML
    void ClickOnUpdate(ActionEvent event) throws IOException {
    	
    	String id = idField.getText();
    	if(id!=null&&!id.isEmpty()) {
    		String message = "updateGuideRole " + id;
    		try {
    			ClientUI.chat.accept(message);
    			System.out.println("LoginController> userExist request Sent to server");
    		}catch (Exception e){
    			System.out.println("LoginController> User does not exist");
    			e.printStackTrace();
    		}
    	}
    	else { 
            System.out.println("Invalid guide ID"); // Handle invalid input
        }
    }

}
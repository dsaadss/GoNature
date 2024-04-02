
package clientGUI;

import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


/**
 * The {@code ServiceEmployeeMenuController} class controls the service employee's menu interface, providing functionalities such as
 * defining a guide role for users and logging out. This class handles user interactions within the service employee's section
 * of the application and communicates with the server to perform the designated actions.
 */
public class ServiceEmployeeMenuController {
    @FXML
    private Button defineGuideBtn;

    @FXML
    private Button logoutBtn;
    
    
    /**
     * Navigates to the interface for assigning a guide role to a user when the 'Define Guide' button is clicked. This allows
     * the service employee to enter a user ID and update the user's role to 'guide'.
     *
     * @param event The event triggered by clicking the 'Define Guide' button.
     */
    @FXML
    void clickOnDefineGuide(ActionEvent event) {
		  SwitchScreen.changeScreen(event,"/clientGUI/RegisterGuideRoleByServiceEmployeeController.fxml"
  				  ,"/resources/RegisterGuideRoleByServiceEmployee.css");
    }
    
    
    /**
     * Handles the logout process for the service employee when the logout button is clicked. It sends a logout request to the
     * server and navigates the user back to the login screen upon successful logout.
     *
     * @param event The event triggered by clicking the logout button.
     */
    @FXML
    void clickOnLogout(ActionEvent event) {
		try {
			ClientUI.chat.accept("logout "+StaticClass.username);
			System.out.println("UserMenuController> request Sent to server");
		}catch (Exception e){
			System.out.println("UserMenuController> Logout failed");
		}
		if(StaticClass.islogout) {
			StaticClass.islogout=false;
			StaticClass.typeacc="";
      		SwitchScreen.changeScreen(event,"/clientGUI/LoginController.fxml","/resources/LoginController.css");
		}
    }
}
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    

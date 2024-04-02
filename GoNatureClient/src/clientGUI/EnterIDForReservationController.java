package clientGUI;

import client.ChatClient;
import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The {@code EnterIDForReservationController} class manages the interface for entering a user ID as part of
 * the reservation process. This class facilitates the transition from the ID entry screen to either the user
 * menu or back to the login/new reservation screen, depending on the user's actions. It also handles the verification
 * and addition of external users in the system.
 */
public class EnterIDForReservationController {
	

    @FXML
    private TextField idField;

    @FXML
    private Button BackBtn;

    @FXML
    private Button NextBtn;
    
    /**
     * Navigates back to the previous screen, typically the login or new reservation screen, when the 'Back' button is clicked.
     *
     * @param event The event triggered by clicking the 'Back' button.
     */
    @FXML
    void ClickBackBtn(ActionEvent event) {
		try {
	          SwitchScreen.changeScreen(event,"/clientGUI/LoginOrNewReservation.fxml"
	        		  ,"/resources/LoginOrNewReservation.css");

			  } catch (Exception e) {
			      e.printStackTrace();}
    }
    
    
    /**
     * Processes the entered user ID when the 'Next' button is clicked, moving forward in the reservation process.
     * It checks if the user ID exists as an external user, adds it to the system if not, and then navigates to the
     * user menu screen to proceed with the reservation.
     *
     * @param event The event triggered by clicking the 'Next' button.
     */
    @FXML
    void ClickNextBtn(ActionEvent event) {
    	String userId=idField.getText();
		StaticClass.typeacc="guest";
    	try {
    		StaticClass.reservationtype="customer";
			StaticClass.discounttype="personal";
    		String id=idField.getText();
    		ClientUI.chat.accept("checkExternalUser "+ id);
    		if(StaticClass.externaluser.equals("0")) {
    			ClientUI.chat.accept("addExternalUser "+id);
    		}
    		if(StaticClass.addexternaluser==1||StaticClass.externaluser.equals("1")) {
    			StaticClass.userid=userId;
    			  SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
    					  ,"/resources/UserMenuController.css");
    		}
	  	  } catch (Exception e) {
		      e.printStackTrace();}	
    }
}

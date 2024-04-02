package clientGUI;

import java.io.IOException;

import client.ClientUI;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The {@code LoginOrNewReservationController} class controls the initial interface where users can choose between logging in
 * or making a new reservation. It handles navigation to the login screen for existing users or to the ID entry screen for
 * new or unregistered users looking to make a reservation.
 */
public class LoginOrNewReservationController {
	
    @FXML
    private Button LoginBtn;

    @FXML
    private Button NewReservationBtn;


    /**
     * Navigates to the login screen when the 'Login' button is clicked, allowing existing users to enter their credentials.
     *
     * @param event The event triggered by clicking the 'Login' button.
     * @throws IOException If an I/O error occurs during screen transition.
     */
    @FXML
    void LoginBtnAction(ActionEvent event) throws IOException {
        SwitchScreen.changeScreen(event,"/clientGUI/LoginController.fxml","/resources/LoginController.css");
    }
    

    /**
     * Navigates to the ID entry screen for new reservations when the 'New Reservation' button is clicked, allowing
     * new or unregistered users to start the reservation process by entering their ID.
     *
     * @param event The event triggered by clicking the 'New Reservation' button.
     * @throws IOException If an I/O error occurs during screen transition.
     */
    @FXML
    void NewReservationBtnAction(ActionEvent event) throws IOException {
        SwitchScreen.changeScreen(event,"/clientGUI/EnterIDForReservationController.fxml"
        		,"/resources/EnterIDForReservationController.css");
    }
    
    /**
     * Handles the action of closing the application when the close button is clicked. It sends a disconnect message
     * to the server before shutting down the application.
     *
     * @param event The event triggered by clicking the close button.
     */
    @FXML
    void clickOnClose(ActionEvent event) {
    	ClientUI.chat.accept("disconnect");
    	System.exit(0);
    }

}

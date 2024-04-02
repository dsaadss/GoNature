package clientGUI;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The {@code EmployeeMenuController} class manages the employee menu interface, providing functionalities such as
 * checking order details, making reservations, and logging out. This class handles user interactions within the
 * employee section of the application and communicates with the server to fetch or update data as needed.
 */
public class EmployeeMenuController {
	
        public static String username;
	    public static boolean islogout =false;
	    @FXML
	    private Button logoutBtn;
	    
	    @FXML
	    private Label amountLabel;

	    @FXML
	    private Button checkAmountBtn;

	    @FXML
	    private Button checkOrderBtn;

	    @FXML
	    private Label checkOrderLabel;

	    @FXML
	    private Button guideReservationBtn;

	    @FXML
	    private TextField numberVisitorsTxt;

	    @FXML
	    private TextField orderIDTxt;

	    @FXML
	    private Button reservationBtn;

	    /**
	     * Handles the action of checking an order when the "Check Order" button is clicked. It validates the order
	     * ID and the number of visitors, updates the order status, and displays relevant information or error messages
	     * to the employee.
	     *
	     * @param event The event triggered by clicking the "Check Order" button.
	     */
	    @FXML
	    void ClickOnCheckOrder(ActionEvent event) {
	    	String OrderId = orderIDTxt.getText();
	    	String numberofvisiter = numberVisitorsTxt.getText();
	    	ClientUI.chat.accept("orderexistYarden "+ OrderId);
	    	try {
		    	if (StaticClass.istheorderexist == 0) {
		    	    checkOrderLabel.setText("no order found");

		    	} 
		    	else {
		    	    // Since an order exists, check the amount of people
		    	    ClientUI.chat.accept("checkamountofpeople " + OrderId + " " + numberofvisiter);


		    	if (StaticClass.amoutgreaterthenorder == 0) {
		    	        checkOrderLabel.setText("amount of people is bigger then registered in the order");

		    	    } else {
		    	        // Update the table only if the amount of people is correct
		    	        ClientUI.chat.accept("UpdateTable " + OrderId + " " + numberofvisiter + " " + StaticClass.typeacc);
		    	        ClientUI.chat.accept("PriceAfterEnter "+ OrderId );
		    	        checkOrderLabel.setText("ENJOY YOUR VISIT\n the price for the visit is : "+StaticClass.PriceforOrder);
		    	    }
		    	}
	    	} catch (Exception e) {
	    	    e.printStackTrace(); // or handle the exception appropriately
	    	}


	    }


	    /**
	     * Navigates to the guide reservation interface when the "Guide Reservation" button is clicked, allowing
	     * the employee to make a new reservation for a guide.
	     *
	     * @param event The event triggered by clicking the "Guide Reservation" button.
	     */
	    @FXML
	    void ClickOnGuideReservation(ActionEvent event) {
	    	StaticClass.reservationtype="group";
	    	StaticClass.discounttype="casual_group";
    		SwitchScreen.changeScreen(event,"/clientGUI/NewReservationForGuideController.fxml"
    				,"/resources/NewReservationForGuideController.css");
	    }

	    /**
	     * Navigates to the new reservation interface for individual visitors when the "New Reservation" button is clicked,
	     * allowing the employee to make a new reservation for a park visitor.
	     *
	     * @param event The event triggered by clicking the "New Reservation" button.
	     */
	    @FXML
	    void ClickOnNewReservation(ActionEvent event) {
	    	StaticClass.reservationtype="customer";
	    	StaticClass.discounttype="casual_personal";
    		SwitchScreen.changeScreen(event,"/clientGUI/NewReservationForUserController.fxml"
    				,"/resources/NewReservationForUserController.css");
	    }

	    /**
	     * Handles the action of checking the current amount of people in the park when the "Check Amount" button is clicked.
	     * It displays the number of people currently in the park or indicates if the park is closed based on the current time.
	     *
	     * @param event The event triggered by clicking the "Check Amount" button.
	     */
	    @FXML
	    void clickOnCheckAmount(ActionEvent event) {
	    	ClientUI.chat.accept("userId "+StaticClass.username);
	    	ClientUI.chat.accept("amountInPark "+StaticClass.userid);
	    	LocalTime now = LocalTime.now();	    	// Define the closing and opening times
	    	LocalTime openingTime = LocalTime.of(8, 0); // 08:00 AM
	    	LocalTime closingTime = LocalTime.of(20, 0); // 08:00 PM

	    	// Check if current time is before opening or after closing
	    	if (now.isBefore(openingTime)|| now.isAfter(closingTime)) {
	    	    // Park is closed
	    		amountLabel.setText("The park is closed.");
	    	} else {
		    	String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
		    	String labelText = String.format("The amount of people in the park is %s according to time %s", StaticClass.amountinparkyarden, formattedTime);
		    	amountLabel.setText(labelText);
	    	}

	    }
	    
	    /**
	     * Handles the logout process for an employee when the logout button is clicked. It sends a logout request to the
	     * server and navigates the user back to the login screen upon successful logout.
	     *
	     * @param event The event triggered by clicking the logout button.
	     * @throws IOException If an I/O error occurs during screen transition.
	     */
	    @FXML
	    void clickOnLogout(ActionEvent event) throws IOException {
	    	String message="logout "+StaticClass.username;
			try {
				ClientUI.chat.accept(message);
				System.out.println("UserMenuController> request Sent to server");
			}catch (Exception e){
				System.out.println("UserMenuController> Logout failed");
			}
			if(StaticClass.islogout) {
				StaticClass.islogout=false;
	      		SwitchScreen.changeScreen(event,"/clientGUI/LoginController.fxml","/resources/LoginController.css");

			}
	    }
}


package clientGUI;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import entities.Park;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * The {@code LoginController} class is responsible for handling user login functionality within the application. It
 * provides the interface for users to enter their credentials and processes the login attempts by communicating with
 * the server to verify user details. Based on the user type, it navigates to the appropriate menu screen upon successful login.
 */
public class LoginController {
    @FXML
    private Label userNotExist;
	
    @FXML
    private PasswordField password;

    @FXML
    private TextField userID;
	
    @FXML
    private Button BackBtn;

    @FXML
    private Button LoginBtn;
    
    /**
     * Navigates back to the previous screen, typically the main menu or the login/new reservation screen, when the 'Back' button is clicked.
     *
     * @param event The event triggered by clicking the 'Back' button.
     * @throws IOException If an I/O error occurs during screen transition.
     */
    @FXML 
    void ClickOnBackBtn(ActionEvent event) throws IOException {
		try {
      		  SwitchScreen.changeScreen(event,"/clientGUI/LoginOrNewReservation.fxml"
      				  ,"/resources/LoginOrNewReservation.css");

			  } catch (Exception e) {
			      e.printStackTrace();}
    }
    
    /**
     * Processes the user's login attempt when the 'Login' button is clicked. It verifies the user credentials against
     * the server and, upon successful authentication, navigates to the corresponding menu screen based on the user type.
     * Displays appropriate messages for invalid credentials or login errors.
     *
     * @param event The event triggered by clicking the 'Login' button.
     * @throws IOException If an I/O error occurs during screen transition or server communication.
     */
    @FXML
    void ClickOnLoginBtn(ActionEvent event) throws IOException {
    	StaticClass.available=0;
    	String username=userID.getText();
    	String pass=password.getText();
    	String message="userExist "+username + " " + pass;
		try {
			ClientUI.chat.accept(message);
			System.out.println("LoginController> userExist request Sent to server");
		}catch (Exception e){
			System.out.println("LoginController> User does not exist");
		}
		System.out.println("check before if exist login controller");
		if (StaticClass.isexist&& (!StaticClass.typeacc.equals(""))) {
			StaticClass.username=username;
			System.out.println("this is loginController> username is: "+StaticClass.username);
			StaticClass.isexist=false;
			try {
				String loginMessage= "login " + username +" "+ pass ;
			ClientUI.chat.accept(loginMessage);//Send Msg TO Server
			System.out.println("LoginController> login request Sent to server");
			}catch(Exception e) {
				System.out.println("LoginController> Login Failed");
			}
		}	
		if(!StaticClass.islogged&&StaticClass.isexist) {
			if (StaticClass.isexist){
				StaticClass.isexist=false;
		        System.out.println(StaticClass.typeacc);
		        switch(StaticClass.typeacc) {
		        	case "guide":
		        		if(StaticClass.typeacc.equals("customer")) {
		        			StaticClass.reservationtype="customer";
		        			StaticClass.discounttype="personal";
		        		}
		        		
		        		else { 
		        			StaticClass.reservationtype="guide";
		        			StaticClass.discounttype="guide";	
		        		}
		        		SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
		        				,"/resources/UserMenuController.css");
		        		break;
		        	case "park manager" :
		        		System.out.println("this is login controller park manager switch!\nuser name = " + StaticClass.username);
		        		SwitchScreen.changeScreen(event,"/clientGUI/ParkManagerMenuController.fxml","/resources/ParkManagerMenuController.css");
		        		System.out.println("this is login controller park manager switch!2\nuser name = " + StaticClass.username);
		        		break;
		        	case "department manager":
		        		SwitchScreen.changeScreen(event,"/clientGUI/DmMenuController.fxml"
		        				,"/resources/DmMenuController.css");
		        		break;
		        	case "service employee":
		        		SwitchScreen.changeScreen(event,"/clientGUI/ServiceEmployeeMenuController.fxml"
		        				,"/resources/ServiceEmployeeMenuController.css");
		        		break;
		        	case "park employee":
		        		SwitchScreen.changeScreen(event,"/clientGUI/EmployeeMenuController.fxml"
		        				,"/resources/EmployeeMenuController.css");
		        		break;
		        	default:
		        		userNotExist.setText("User is not defined");
		        }
	        }
		}		
		else {
			userNotExist.setStyle("-fx-text-fill: red;");
			if(StaticClass.islogged) { 
				StaticClass.islogged=false;
				userNotExist.setText("User already logged in");}
			else 
				userNotExist.setText("User details are not valid");
		}
    }  
}


package client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.io.IOException;

import common.SwitchScreen;


public class ClientUI extends Application {
    public static ClientController chat; // only one instance
    
    @FXML
    private Button closeBtn;
    
    @FXML
    private Button okBtn;

    @FXML
    private TextField enterHostIP;

    
    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application. Not used in this application.
     * @throws Exception if an error occurs during application startup.
     */
    public static void main(String args[]) throws Exception { 
        launch(args);  
    } // end main

    /**
     * Starts the primary stage of the JavaFX application, setting up the initial scene and configurations.
     * This method is called after the {@code Application} class is initialized.
     *
     * @param primaryStage The primary stage for this JavaFX application.
     * @throws Exception If an error occurs while loading the FXML file or setting the scene.
     */
    public void start(Stage primaryStage) throws Exception {
    	Parent root = FXMLLoader.load(getClass().getResource("/client/EnterServerIp.fxml"));
    	primaryStage.initStyle(StageStyle.UNDECORATED);
    	Scene scene = new Scene(root);
    	SwitchScreen.enableDrag(root, primaryStage);
        scene.getStylesheets().add(getClass().getResource("/client/EnterServerIp.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
   	} 
   
    
    /**
     * Handles the action triggered by pressing the 'OK' button in the UI. This method attempts to establish a connection 
     * to the server using the IP address entered by the user. If the IP address field is empty, it defaults to 'localhost'. 
     * Upon successful connection, it transitions the user to the next screen, typically a login or reservation screen.
     *
     * @param event The {@link ActionEvent} triggered by clicking the 'OK' button.
     * @throws IOException If an I/O error occurs during the screen transition or while attempting to connect to the server.
     */
    @FXML
    private void OkAction(ActionEvent event) throws IOException {
        String serverIp = enterHostIP.getText();
		//FXMLLoader loader = new FXMLLoader();
		if(serverIp.isEmpty())
		{
			System.out.println("You must enter an id number");
			chat= new ClientController("localhost", 5555);
		}    
		chat= new ClientController(serverIp, 5555);
		try {
			chat.accept("connect");
		}catch (Exception e){
			System.out.println("ClientUI> Failed to load client into server monitor");
		}
		SwitchScreen.changeScreen(event, "/clientGUI/LoginOrNewReservation.fxml","/resources/LoginOrNewReservation.css");
    }
    
    @FXML
    void clickOnClose(ActionEvent event) {
    	System.exit(0);
    }
}

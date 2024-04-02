package Server;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.Order;
import serverGUI.ServerPortFrameController;
import Server.EchoServer;
import java.util.ArrayList;
import java.util.Vector;

import Server.EchoServer;


public class ServerUI extends Application {
    final public static int DEFAULT_PORT = 5555;
    /**
     * Entry point of the application. Launches the JavaFX application.
     *
     * @param args Command-line arguments (not used)
     * @throws Exception If an error occurs during application launch
     */
    public static void main(String args[]) throws Exception {   
        launch(args);
    } // end main
    
    /**
     * JavaFX start method. Creates an instance of ServerPortFrameController and starts the JavaFX application.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set
     * @throws Exception If an error occurs during application startup
     */
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
		ServerPortFrameController aFrame = new ServerPortFrameController(); // create StudentFrame
		 
		aFrame.start(primaryStage);
	}
    /**
     * Method to run the server on the specified port.
     *
     * @param p String representing the port number
     */
 

    public static void runServer(String p) {
        int port; // Port to listen on
        port = Integer.parseInt(p);


        EchoServer sv = new EchoServer(port);

        try {
            sv.listen(); // Start listening for connections
            //System.out.println("Server is listening on port " + port);
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients: " + ex.getMessage());
        }
    }
}

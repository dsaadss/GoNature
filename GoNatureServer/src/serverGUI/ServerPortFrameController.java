package serverGUI;


import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logic.ClientConnectionStatus;
import Server.DbController;
import Server.EchoServer;
import Server.ServerUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServerPortFrameController implements Initializable{
    @FXML
    private Circle Circle;

    @FXML
    private TextField dbNameField;

    @FXML
    private PasswordField dbPasswordField;

    @FXML
    private TextField dbUserNameField;

    @FXML
    private TextField ipAddress;

    @FXML
    private TextField portField;
    
    @FXML
    private Button startServer;

    @FXML
    private Button stopServer;
    
    @FXML
    private Label statusServer;
    
    @FXML
    private TableView<ClientConnectionStatus> tableField;
    
    @FXML
    private TableColumn<ClientConnectionStatus, String> hostColumn;

    @FXML
    private TableColumn<ClientConnectionStatus, String> ipColumn;

    @FXML
    private TableColumn<ClientConnectionStatus, String> startTimeColumn;

    @FXML
    private TableColumn<ClientConnectionStatus, String> statusColumn;
    
    @FXML
    private Button importDataBtn;
    /**
     * Retrieves the port number from the port field.
     *
     * @return The port number as a string
     */
	private String getport() {
		return portField.getText();			
	}
	
    @FXML
    void ClickOnImportData(ActionEvent event) {
    	EchoServer.importData();
    }
    
    /**
     * Handles the action when the "Start Server" button is clicked.
     * Starts the server with the specified port number.
     *
     * @param event The action event triggered by clicking the button
     */
    @FXML
	public void handleStartServerAction(ActionEvent event) {
		String p;
		tableField.setItems(EchoServer.clientsList);
        this.ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        this.hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		p=getport();
		if(p.trim().isEmpty()) {
			System.out.println("You must enter a port number");			
		}
		else
		{
			((Node)event.getSource()).getScene().getWindow(); 
			@SuppressWarnings("unused")
			Stage primaryStage = new Stage();
			@SuppressWarnings("unused")
			FXMLLoader loader = new FXMLLoader();
			
			if(Integer.parseInt(p)>=0 && Integer.parseInt(p)<=65536) {
				try{
					ServerUI.runServer(p);
					statusServer.setText("Server Started Successfully");
					Circle.setFill(javafx.scene.paint.Color.GREEN);
				}catch (Exception e){
					System.out.println("ServerPortFrame> Server didn't start");
				}
			}
			else {
				statusServer.setText("Port number is not valid");
			}
		}
	}

    public void start(Stage primaryStage) throws IOException {
        // Load the ServerPortFrameController's FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/serverGUI/ServerPortFrame.fxml")); // Ensure the path is correct
        Parent root = loader.load();
    	primaryStage.initStyle(StageStyle.UNDECORATED);
    	Scene scene = new Scene(root);
    	enableDrag(root, primaryStage);
        scene.getStylesheets().add(getClass().getResource("/serverGUI/ServerPort.css").toExternalForm());
        primaryStage.setScene(scene);
        @SuppressWarnings("unused")
		ServerPortFrameController controller = loader.getController();
        primaryStage.setTitle("Server Control Panel");
        primaryStage.show();
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
    
    /**
     * Handles the action when the "Stop Server" button is clicked.
     * Exits the application.
     *
     * @param event The action event triggered by clicking the button
     * @throws Exception If an error occurs during the exit process
     */
    
    @FXML
    void stopServerAction(ActionEvent event) throws Exception {
    	System.exit(0);
    }
    
    /**
     * Handles the action when the "Import Data" button is clicked.
     * Synchronizes users' data from the database.
     *
     * @param event The action event triggered by clicking the button
     */
    
	@FXML
	void ImportData(ActionEvent event) {
	    String myDbUrl = dbNameField.getText(); 
	    String myDbUsername = dbUserNameField.getText(); 
	    String myDbPassword = dbPasswordField.getText();    
	    DbController.synchronizeUsers(myDbUrl, myDbPassword,myDbUsername,"jdbc:mysql://localhost:3306/usermanagement?serverTimezone=IST", "Aa123456","root");
	    
	    //statusServer.setText("Users synchronized successfully");
	}
	
	/**
	 * Initializes the controller with default values for the fields.
	 *
	 * @param location The location used to resolve relative paths for the root object, or null if the location is not known
	 * @param resources The resources used to localize the root object, or null if the root object was not localized
	 */
   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		portField.setText("5555");
    	dbNameField.setText("jdbc:mysql://localhost/gonaturedb?serverTimezone=IST");
    	dbUserNameField.setText("root");
    	dbPasswordField.setText("Aa123456");
    	ipAddress.setText(EchoServer.getHostIp());
	}  
}

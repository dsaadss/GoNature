package clientGUI;
import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import logic.Message;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * The {@code ParkManagerMenuController} class manages the park manager's menu interface, providing functionalities such as
 * submitting requests to change dwell time and maximum capacity for parks, logging out, and generating reports. This class
 * handles user interactions within the park manager's section of the application and communicates with the server to
 * submit change requests and fetch report data.
 */
public class ParkManagerMenuController { 
	
    @FXML
    private Button dwellOkBtn;

    @FXML
    private TextField dwellTimeText;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button maxCapacityOkBtn;

    @FXML
    private TextField maxCapacityText;

    @FXML
    private ComboBox<String> parkNameCombo;

    @FXML
    private Button totalReportBtn;

    @FXML
    private Button usageReportBtn;
	public static String[] parks;
	
    /**
     * Initializes the park manager menu by fetching and displaying the parks managed by the logged-in park manager.
     * This method is called automatically after the FXML fields have been populated.
     */
    @FXML
    public void initialize() {
    	System.out.println("park manager initialize");
    	String[] parks=getParksMangedByParkManger(StaticClass.username);
    	if(parks==null)
    		System.out.println("No parks found!");
    	else {
    		parkNameCombo.getItems().addAll((String[])parks);
    	}
    }
    
    /**
     * Fetches the list of parks managed by the park manager from the server.
     *
     * @param username The username of the logged-in park manager.
     * @return An array of park names managed by the park manager.
     */
    private String[] getParksMangedByParkManger(String username) {
		String msg="getParksMangedByParkManger " + username;
		ClientUI.chat.accept(msg);	
    	return ParkManagerMenuController.parks;
	}

    
    /**
     * Handles the logout process for the park manager when the logout button is clicked. It sends a logout request to the
     * server and navigates the user back to the login screen upon successful logout.
     *
     * @param event The event triggered by clicking the logout button.
     */
    @FXML
    void clickOnLogout(ActionEvent event) {
    	String message="logout "+StaticClass.username;
		try {
			ClientUI.chat.accept(message);
			System.out.println("ParkManagerMenuController> request Sent to server");
		}catch (Exception e){
			System.out.println("ParkManagerMenuController> Logout failed");
		}
		if(StaticClass.islogout) {
			StaticClass.islogout=false;
      		SwitchScreen.changeScreen(event,"/clientGUI/LoginController.fxml","/resources/LoginController.css");
		}
    }
    
    
    /**
     * Processes the dwell time change request when the corresponding 'OK' button is clicked. It validates the input and sends
     * a change request for the dwell time of the selected park to the server.
     *
     * @param event The event triggered by clicking the 'OK' button for dwell time change.
     */
    @FXML
    void ClickOnOkDwell(ActionEvent event) {
    	String visitTime = dwellTimeText.getText();
    	String selectedPark = parkNameCombo.getValue();
		try {
			ClientUI.chat.accept("requastToChangevisit " + selectedPark+ " " + visitTime);

		} catch (Exception e) {
	        System.out.println("Error occurred" + e.getMessage());
		}

    }

    
    /**
     * Processes the maximum capacity change request when the corresponding 'OK' button is clicked. It validates the input and
     * sends a change request for the maximum capacity of the selected park to the server.
     *
     * @param event The event triggered by clicking the 'OK' button for maximum capacity change.
     */
    @FXML
    void clickOnOkMaxCapacity(ActionEvent event) {
    	String maxCapacity = maxCapacityText.getText();
    	String selectedPark = parkNameCombo.getValue();
		try {
			ClientUI.chat.accept("requastToChangeMaxCapcitiy " + selectedPark+ " " + maxCapacity);

		} catch (Exception e) {
	        System.out.println("Error occurred" + e.getMessage());
		}

    }
   

    /**
     * Navigates to the total visitors report preparation screen when the corresponding button is clicked, allowing the park
     * manager to generate and view total visitors reports.
     *
     * @param event The event triggered by clicking the button for total visitors reports.
     */
    @FXML
    void clickOnTotalBtn(ActionEvent event) {
    	System.out.println("TotalVistorsBtn");
    	SwitchScreen.changeScreen(event, "/clientGUI/TotalVisitorsReportsPreparation.fxml","/resources/TotalVisitorsReportsPreparation.css");
    }

    
    /**
     * Navigates to the usage report preparation screen when the corresponding button is clicked, allowing the park manager
     * to generate and view usage reports.
     *
     * @param event The event triggered by clicking the button for usage reports.
     */
    @FXML
    void clickOnUsageReport(ActionEvent event) {
	System.out.println("UsageBtn");
    	SwitchScreen.changeScreen(event, "/clientGUI/UsageReportsPreparation.fxml", "/resources/UsageReportsPreparation.css");
    }
}
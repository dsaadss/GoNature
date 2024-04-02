package clientGUI;

import java.io.IOException;
import java.util.ArrayList;

import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import entities.Park;
import entities.ParkForChange;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * The {@code DmMenuController} class manages the Department Manager's menu interface in the application. It handles the
 * functionality related to logging out, viewing cancellation reports, and visitation reports. Additionally, it manages
 * approval or denial of changes to dwell time and maximum capacity for parks.
 */
public class DmMenuController {
	
	    @FXML
	    private Button logOutBtn;

	    @FXML
	    private TableView<ParkForChange> dwellTimeTable;
	    @FXML
	    private TableView<ParkForChange> capicityTable;

	    @FXML
	    private TableColumn<ParkForChange, String> maxAfterCapacityTable;

	    @FXML
	    private TableColumn<ParkForChange, String> maxAfterDwellTable;

	    @FXML
	    private TableColumn<ParkForChange, String> maxBeforeCapacityTable;

	    @FXML
	    private TableColumn<ParkForChange, String> maxBeforeDwellTable;

	    @FXML
	    private TableColumn<ParkForChange, String> noCapacityTable;

	    @FXML
	    private TableColumn<ParkForChange, String> noDwellTable;

	    @FXML
	    private TableColumn<ParkForChange, String> parkNameCapacityTable;

	    @FXML
	    private TableColumn<ParkForChange, String> parkNameDwellTable;

	    @FXML
	    private TableColumn<ParkForChange, String> yesCapacityTable;

	    @FXML
	    private TableColumn<ParkForChange, String> yesDwellTable;
	    
	    
	    public static  String[] parks;


	    /**
	     * Initializes the controller by setting up tables for park changes related to dwell time and capacity. This
	     * method is called automatically after the FXML fields are populated and is used for initial setup, including
	     * fetching park change requests from the server and populating the tables.
	     */
	    @FXML
	    public void initialize() {   
		    if(StaticClass.parks.isEmpty()) {
	    	  ClientUI.chat.accept("park");
	    	}
	    	  parks=new String[StaticClass.parks.size()];
	    	  for(int i=0;i<StaticClass.parks.size();i++) {
	    		  parks[i]=StaticClass.parks.get(i).getParkString();
	    	  }
		    StaticClass.changeparkdwelltime.clear();
		    StaticClass.changeparkmaxcap.clear();
	    	ClientUI.chat.accept("parkChangesVisit ");
	    	ClientUI.chat.accept("parkMaxCap ");
	    	//dwell time table
		    maxBeforeDwellTable.setCellValueFactory(new PropertyValueFactory<>("dwellBefore"));
		    maxAfterDwellTable.setCellValueFactory(new PropertyValueFactory<>("dwellAfter"));
		    parkNameDwellTable.setCellValueFactory(new PropertyValueFactory<>("parkName"));
		    dwellTimeTable.setItems(FXCollections.observableArrayList(StaticClass.changeparkdwelltime));
	    	//max cap table

		    maxAfterCapacityTable.setCellValueFactory(new PropertyValueFactory<>("maxCapacityAfter"));
		    maxBeforeCapacityTable.setCellValueFactory(new PropertyValueFactory<>("maxCapacityBefore"));
		    parkNameCapacityTable.setCellValueFactory(new PropertyValueFactory<>("parkName"));
	        capicityTable.setItems(FXCollections.observableArrayList(StaticClass.changeparkmaxcap));

		    //buttons
	        yesDwellTable.setCellFactory(col -> {
	            Button yesButton = new Button("Yes");
	            TableCell<ParkForChange, String> cell = new TableCell<ParkForChange, String>() {
	                @Override
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                        yesButton.setOnAction(event -> handleYesAction(getTableView().getItems().get(getIndex())));
	                        setGraphic(yesButton);
	                    }
	                }
	            };
	            return cell;
	        });

	        // Set up 'No' button column
	        noDwellTable.setCellFactory(col -> {
	            Button noButton = new Button("No");
	            TableCell<ParkForChange, String> cell = new TableCell<ParkForChange, String>() {
	                @Override
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                        noButton.setOnAction(event -> handleNoAction(getTableView().getItems().get(getIndex())));
	                        setGraphic(noButton);
	                    }
	                }
	            };
	            return cell;
	        });
	       
	     // Set up 'Yes' button column for capacity
	        yesCapacityTable.setCellFactory(col -> {
	            Button yesButton = new Button("Yes");
	            TableCell<ParkForChange, String> cell = new TableCell<ParkForChange, String>() {
	                @Override
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                        yesButton.setOnAction(event -> handleYesCapacityAction(getTableView().getItems().get(getIndex())));
	                        setGraphic(yesButton);
	                    }
	                }
	            };
	            return cell;
	        });

	        // Set up 'No' button column for capacity
	        noCapacityTable.setCellFactory(col -> {
	            Button noButton = new Button("No");
	            TableCell<ParkForChange, String> cell = new TableCell<ParkForChange, String>() {
	                @Override
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) {
	                        setGraphic(null);
	                    } else {
	                        noButton.setOnAction(event -> handleNoCapacityAction(getTableView().getItems().get(getIndex())));
	                        setGraphic(noButton);
	                    }
	                }
	            };
	            return cell;
	        });	  
	    }
	    
	    /**
	     * Navigates to the cancellation report screen when the corresponding button is clicked in the UI.
	     *
	     * @param event The event that triggered this action, typically a button click.
	     */
	    @FXML
	    void CancelledReport(ActionEvent event) {
	    	SwitchScreen.changeScreen(event, "/clientGUI/DmCancelledController.fxml","/resources/DmCancelledController.css");
	    }

	    /**
	     * Navigates to the visitation report screen when the corresponding button is clicked in the UI.
	     *
	     * @param event The event that triggered this action, typically a button click.
	     */
	    @FXML
	    void VisitReport(ActionEvent event) {
	    	SwitchScreen.changeScreen(event, "/clientGUI/DmVisitReport.fxml", "/resources/DmVisitReport.css");
	    }
	     
	    /**
	     * Handles the logout process when the logout button is clicked. This method sends a logout request to the server
	     * and navigates the user back to the login screen upon successful logout.
	     *
	     * @param event The event that triggered this action, typically a button click.
	     */
	    @FXML
	    void clickOnLogout(ActionEvent event) {
			try {
				ClientUI.chat.accept("logout "+StaticClass.username);
				System.out.println("DmMenuController> Logout request Sent to server");
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("error"+e.getMessage());
			}
			if(StaticClass.islogout) {
				StaticClass.islogout=false;
		        
	    		SwitchScreen.changeScreen(event,"/clientGUI/LoginController.fxml", "/resources/LoginController.css");
			}
	    }
	    
	    /**
	     * Handles the 'Yes' button action for approving dwell time changes. It sends an approval message to the server
	     * and updates the UI to reflect the change.
	     *
	     * @param parkForChange The {@link ParkForChange} object representing the park for which the dwell time change
	     *                      is approved.
	     */
	    private void handleYesAction(ParkForChange parkForChange) {
	        // Show popup message
	    	ClientUI.chat.accept("approveVisitTime "+parkForChange.getParkName()+" "+parkForChange.getDwellAfter());
	    	
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Confirmation");
	        alert.setHeaderText(null);
	        alert.setContentText("==========\nYou clicked 'Yes' to change max visit time for " + parkForChange.getParkName() +"to "+ parkForChange.getDwellAfter()+".\n==========");
	        StaticClass.changeparkdwelltime.remove(parkForChange);
	        refreshTableView();

	        alert.showAndWait();
	    }

	    
	    /**
	     * Handles the 'No' button action for declining dwell time changes. It sends a decline message to the server
	     * and updates the UI to remove the change request.
	     *
	     * @param parkForChange The {@link ParkForChange} object representing the park for which the dwell time change
	     *                      is declined.
	     */
	    private void handleNoAction(ParkForChange parkForChange) {
	        // Show popup message
	    	ClientUI.chat.accept("declineVisitTime "+parkForChange.getParkName()+" "+parkForChange.getDwellAfter());

	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Confirmation");
	        alert.setHeaderText(null);
	        alert.setContentText("==========\nYou clicked 'NO' to change max visit time for " + parkForChange.getParkName() +"to "+ parkForChange.getDwellAfter()+".\n==========");
	        StaticClass.changeparkdwelltime.remove(parkForChange);
	        refreshTableView();
	        alert.showAndWait();
	    }
	    
	    /**
	     * Refreshes the dwell time table view to reflect any changes made, such as after approving or declining a change.
	     */
	    private void refreshTableView() {
	        dwellTimeTable.setItems(FXCollections.observableArrayList(StaticClass.changeparkdwelltime));  // Reset the items in your table view
	        dwellTimeTable.refresh();  // Refresh the table view
	    }
	    
	    
	    
	    /**
	     * Handles the 'Yes' button action for approving capacity changes. It sends an approval message to the server
	     * and updates the UI to reflect the new capacity.
	     *
	     * @param parkForChange The {@link ParkForChange} object representing the park for which the capacity change
	     *                      is approved.
	     */
	    private void handleYesCapacityAction(ParkForChange parkForChange) {
	        // Implement the logic to handle the 'Yes' button click for capacity changes
	        // For example, sending a message to the server
	        ClientUI.chat.accept("approveMaxCapacity " + parkForChange.getParkName() + " " + parkForChange.getMaxCapacityAfter());

	        // Show confirmation alert
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Capacity Change Approved");
	        alert.setHeaderText(null);
	        alert.setContentText("You approved the new max capacity for " + parkForChange.getParkName());
	        alert.showAndWait();

	        // Update the table
	        StaticClass.changeparkmaxcap.remove(parkForChange);
	        refreshCapacityTableView();
	    }

	    /**
	     * Handles the 'No' button action for declining capacity changes. It sends a decline message to the server
	     * and updates the UI to remove the change request.
	     *
	     * @param parkForChange The {@link ParkForChange} object representing the park for which the capacity change
	     *                      is declined.
	     */
	    private void handleNoCapacityAction(ParkForChange parkForChange) {
	        // Implement the logic to handle the 'No' button click for capacity changes
	        ClientUI.chat.accept("declineMaxCapacity " + parkForChange.getParkName() + " " + parkForChange.getMaxCapacityAfter());

	        // Show confirmation alert
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Capacity Change Declined");
	        alert.setHeaderText(null);
	        alert.setContentText("You declined the new max capacity for " + parkForChange.getParkName());
	        alert.showAndWait();

	        // Update the table
	        StaticClass.changeparkmaxcap.remove(parkForChange);
	        refreshCapacityTableView();
	    }

	    /**
	     * Refreshes the capacity table view to reflect any changes made, such as after approving or declining a change.
	     */
	    private void refreshCapacityTableView() {
	        capicityTable.setItems(FXCollections.observableArrayList(StaticClass.changeparkmaxcap));  // Reset the items in your table view
	        capicityTable.refresh();  // Refresh the table view
	    }
}



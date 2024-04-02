package clientGUI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import entities.Park;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import logic.Order;

/**
 * The {@code UpdateReservationForUserController} class controls the interface for users to update their reservations. It allows users to modify details of an existing reservation, such as the park, date, time, number of visitors, and contact email. The class facilitates saving the updated reservation details and navigating back to the user menu.
 */
public class UpdateReservationForUserController {

    @FXML
    private Button backBtn;

    @FXML
    private DatePicker date;

    @FXML
    private TextField numberOfVisitors;

    @FXML
    private ComboBox<String> parkNameCombo;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField textEmail;

    @FXML
    private ComboBox<String> timeCombo;

    @FXML
    private Label guidelabel;
    
    
    /**
     * Saves the updated reservation details and performs necessary checks and actions based on the updated information when the 'Save' button is clicked. This includes validating the updated details, communicating with the server to save the changes, and providing feedback or options to the user based on availability and payment adjustments.
     *
     * @param event The event triggered by clicking the 'Save' button.
     */
    @FXML
    void ClickForSave(ActionEvent event) {
        LocalDate selectedDate = date.getValue();
        LocalDate currentDate = LocalDate.now();
        guidelabel.setStyle("-fx-text-fill: red;");
        // Assuming your timeCombo has values in "HH:mm" format
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime selectedTime = LocalTime.parse(timeCombo.getValue(), timeFormatter);
        LocalTime currentTime = LocalTime.now();

        if (selectedDate == null || selectedDate.isBefore(currentDate) || (selectedDate.isEqual(currentDate) && selectedTime.isBefore(currentTime))) {
            guidelabel.setText("Please enter valid date and time");}
        else if(StaticClass.typeacc.equals("guide")&&Integer.parseInt(numberOfVisitors.getText())>15) {
    			guidelabel.setText("Cannot enter more than 15 visitors");
    	}
        else if(Integer.parseInt(numberOfVisitors.getText())<=0)
        	guidelabel.setText("*Invalid amount of visitors");
    	else {
	    	StaticClass.updatetowaitinglist=0;
	    	StaticClass.o1.setDate(""+date.getValue());
	    	StaticClass.o1.setNumberOfVisitors(numberOfVisitors.getText());
	    	StaticClass.o1.setParkName(parkNameCombo.getValue());
	    	StaticClass.o1.setTimeOfVisit(timeCombo.getValue());
	    	ClientUI.chat.accept("updateOrder " +StaticClass.orderid + " " + StaticClass.o1.getParkName() +" "+ 
	    			StaticClass.o1.getDate() + " "+StaticClass.o1.getTimeOfVisit() + " "+StaticClass.o1.getNumberOfVisitors()+" "+StaticClass.discounttype + " "+
	    			StaticClass.typeacc+ " "+ StaticClass.reservationtype);
	    	System.out.println("update waiting list = "+StaticClass.updatetowaitinglist + "\n\n\n\nupdate visa = "+ StaticClass.visa);
	    	if(StaticClass.updatetowaitinglist==1) {
	        	Alert alertpayment = new Alert(AlertType.INFORMATION);
	        	alertpayment.setTitle("waiting list");
	        	alertpayment.setHeaderText(null);
	        	alertpayment.setContentText("The park is fully booked, do you want to enter watitng list?");
	
	        	// Create custom ButtonTypes
	        	ButtonType okButton = new ButtonType("Confirm", ButtonData.OK_DONE);
	        	ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	
	        	// Set the ButtonTypes to the alert
	        	alertpayment.getButtonTypes().setAll(okButton, cancelButton);
	
	        	// Show the alert and wait for a response
	        	Optional<ButtonType> result = alertpayment.showAndWait();
	        	if (result.isPresent()) {
	        		if (result.get() == okButton) {
	        		   	ClientUI.chat.accept("updateWaitingList " +StaticClass.orderid);
				   		SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
				    			,"/resources/UserMenuController.css");
	        		}		
	    	    }	
			}else if(StaticClass.visa==1){
					StaticClass.visa=0;
		        	Alert alertvisa = new Alert(AlertType.INFORMATION);
		        	alertvisa.setTitle("payment differences");
		        	alertvisa.setHeaderText(null);
		        	alertvisa.setContentText("You will receive a visa credit for the price differences");
		
		        	// Create custom ButtonTypes
		        	ButtonType okVisaButton = new ButtonType("Confirm", ButtonData.OK_DONE);
		        	ButtonType cancelVisaButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		        	// Set the ButtonTypes to the alert
		        	alertvisa.getButtonTypes().setAll(okVisaButton, cancelVisaButton);
		        	// Show the alert and wait for a response
		        	Optional<ButtonType> result = alertvisa.showAndWait();
		        	
		        	if (result.isPresent()) {
		        		if (result.get() == okVisaButton) {
					   		SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
					    			,"/resources/UserMenuController.css");
		        		}		
		    	    }
			}
			else {
		   		SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
		    			,"/resources/UserMenuController.css");
			}
		}		
    }

    
    /**
     * Navigates back to the user menu when the 'Back' button is clicked, allowing the user to exit the update reservation process.
     *
     * @param event The event triggered by clicking the 'Back' button.
     */
    @FXML
    void ClickOnBack(ActionEvent event) {
	    SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
	        			,"/resources/UserMenuController.css");
    }
    

    /**
     * Loads an existing order into the update form, pre-filling the form fields with the order's current details.
     *
     * @param o1 The {@link Order} object containing the reservation details to be loaded into the form.
     */
	public void loadOrder(Order o1) {
		this.parkNameCombo.setValue(StaticClass.o1.getParkName());
		this.timeCombo.setValue(StaticClass.o1.getTimeOfVisit());
        this.numberOfVisitors.setText(StaticClass.o1.getNumberOfVisitors());
        this.textEmail.setText(StaticClass.o1.getEmail());
        String dateString = StaticClass.o1.getDate();
        LocalDate localDate = null;
        // Ensure dateString is not null or empty to avoid parsing errors
        if(dateString != null && !dateString.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the pattern to match your date format
            localDate = LocalDate.parse(dateString, formatter);
        }
        this.date.setValue(localDate);
		
	}
	
	
    /**
     * Initializes the update reservation screen by populating the park names and setting up listeners for park selection to update available times based on the selected park's operating hours and visit duration limits.
     */
    @FXML  
    private void initialize() {
    	StaticClass.parks.clear();
        ClientUI.chat.accept("park");
        parkNameCombo.getItems().addAll(getParkNames());
        parkNameCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            updateAvailableTimes(newValue);
        });
        // Default time slots for when no park is selected or if a park allows all-day visits
        timeCombo.getItems().addAll("8:00", "9:00", "10:00", "11:00", "12:00", "13:00",
        		"14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
    }
    
    
    /**
     * Updates the available visiting times in the time combo box based on the selected park and its visit time limits.
     *
     * @param parkName The name of the park for which available times are to be updated.
     */
    private void updateAvailableTimes(String parkName) {
        // Find the selected park
        Park selectedPark = StaticClass.parks.stream().filter(park -> park.getParkString().equals(parkName)).findFirst()
        		.orElse(null);
        if (selectedPark != null) {
            int visitLimitHours = selectedPark.getVisitTimeLimit();
            // Clear previous time slots
            timeCombo.getItems().clear();
            // Add time slots based on visit limit
            for (int hour = 8; hour < 20; hour++) {
                if (hour + visitLimitHours <= 20) {
                    timeCombo.getItems().add(String.format("%02d:00", hour));
                }
            }
        }
    }
    
    
    /**
     * Retrieves the names of all parks from the server and adds them to a list for display in the park selection combo box.
     *
     * @return An {@link ArrayList} of {@link String} containing the names of all parks.
     */
    public static ArrayList<String> getParkNames() {
        ArrayList<String> parkName = new ArrayList<>();
        for (Park park : StaticClass.parks) {
            parkName.add(park.getParkString()); // Assuming getParkString() returns the park's name
        }
        return parkName;
    }
}

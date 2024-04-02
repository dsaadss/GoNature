package clientGUI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import client.ClientUI;
import common.StaticClass;
import common.SwitchScreen;
import entities.Park;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The {@code NewReservationForGuideController} class manages the interface for creating new reservations for guides.
 * It allows the guide to select a park, date, time, and number of visitors for the reservation and provides functionality
 * to proceed to payment after filling in the necessary details. The class interacts with the server to fetch available parks
 * and times and to check pricing information.
 */
public class NewReservationForGuideController {

    @FXML
    private Label errorlabel;
    
    @FXML
    private Button backBtn;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<String> numberVisitorsCombo;

    @FXML
    private ComboBox<String> parkNameCombo;

    @FXML
    private Button paymentBtn;

    @FXML
    private TextField textEmail;

    @FXML
    private ComboBox<String> timeCombo;
    
    
    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // For the time

    /**
     * Navigates back to the user menu when the 'Back' button is clicked, allowing the user to exit the reservation creation process.
     *
     * @param event The event triggered by clicking the 'Back' button.
     * @throws IOException If an I/O error occurs during screen transition.
     */
    @FXML 
    void ClickOnBack(ActionEvent event) throws IOException {
    	//StaticClass.flagG=0;
        SwitchScreen.changeScreen(event,"/clientGUI/UserMenuController.fxml"
        		,"/resources/UserMenuController.css");
    }

    
    /**
     * Processes the reservation details entered by the guide and navigates to the payment screen when the 'Payment' button is clicked.
     * It compiles the reservation details and stores them in {@code StaticClass} for use in the payment process.
     *
     * @param event The event triggered by clicking the 'Payment' button.
     * @throws IOException If an I/O error occurs during screen transition.
     */
    @FXML 
    void ClickForPayment(ActionEvent event) throws IOException {
        errorlabel.setStyle("-fx-text-fill: red;");
        boolean isValidEmail = textEmail.getText().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+[a-zA-Z]+\\.[a-zA-Z]{2,}");
        LocalTime receivedTime = null;
	    LocalDate selectedDate = null;
	    if(date.getValue() != null)
	    	selectedDate=date.getValue();
	    if(timeCombo.getValue()!=null)
	    	receivedTime=LocalTime.parse(timeCombo.getValue(), timeFormatter);
    	if (numberVisitorsCombo.getValue()==null || 
    		    parkNameCombo.getValue() == null || 
    		    date.getValue() == null ||
    		    textEmail.getText().isEmpty() ||
    		    timeCombo.getValue() == null) { 
    		    errorlabel.setText("*Please fill all fields");
    	} else if (!isValidEmail) {
    		    errorlabel.setText("*Invalid email address");
		} else if (selectedDate.isBefore(currentDate)) {
    		    errorlabel.setText("*Please enter a valid date");
		} else if (selectedDate.isEqual(currentDate)&&receivedTime.isBefore(currentTime)) {
		       errorlabel.setText("*Please enter a valid time");      
		}
		else {
	    	ClientUI.chat.accept("priceCheck " + parkNameCombo.getValue().toString());
	    	StaticClass.numberofvisitors=Integer.parseInt(numberVisitorsCombo.getValue());
	    	StaticClass.o1.setParkName(parkNameCombo.getValue().toString());//inserting the order data
	    	StaticClass.o1.setDate(date.getValue().toString());
	    	StaticClass.o1.setNumberOfVisitors(""+StaticClass.numberofvisitors);
	    	StaticClass.o1.setTimeOfVisit(timeCombo.getValue().toString());
	    	StaticClass.o1.setEmail(textEmail.getText());
	
	    	StaticClass.orderdetails+="Park name: "+ parkNameCombo.getValue().toString();
	    	StaticClass.orderdetails+="\nNumber of visitors: "+StaticClass.numberofvisitors;
	    	StaticClass.orderdetails+="\nDate: "+ date.getValue().toString();
	    	StaticClass.orderdetails+="\nTime: "+ timeCombo.getValue().toString();
	    	StaticClass.orderdetails+="\nEmail: " +textEmail.getText();
	        SwitchScreen.changeScreen(event,"/clientGUI/PaymentController.fxml"
	        		,"/resources/PaymentController.css");
		}
    }
    
    
    /**
     * Initializes the reservation screen by populating the park names and setting up listener for park selection
     * to update available times. It pre-fills the number of visitors combo box and sets default values for the date and time.
     */
    @FXML 
    private void initialize() {
    	StaticClass.parks.clear();
        ClientUI.chat.accept("park");
        numberVisitorsCombo.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15");
        parkNameCombo.getItems().addAll(getParkNames());
        parkNameCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            updateAvailableTimes(newValue);
        });
        // Default time slots for when no park is selected or if a park allows all-day visits
        timeCombo.getItems().addAll("8:00", "9:00", "10:00", "11:00", "12:00", "13:00",
        		"14:00", "15:00", "16:00", "17:00", "18:00", "19:00");
    }
    
    /**
     * Updates the available visiting times based on the selected park and its visit time limits. This method is called
     * when a park is selected to ensure that the time options are appropriate for the park's operating hours and visit duration limits.
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
            parkName.add(park.getParkString()); 
        }
        return parkName;
    }
	
	
}
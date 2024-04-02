package clientGUI;
import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import logic.Message;


/**
 * The {@code TotalVisitorsReportsPreparation} class controls the interface for generating and viewing total visitors reports
 * by park managers. It allows park managers to select a park, year, and month to generate a report detailing the number
 * of individual and group visitors. The class handles user interactions, fetches report data, and updates the UI with
 * the generated report.
 */
public class TotalVisitorsReportsPreparation { 

    public static int[] res;
    
    @FXML
    private ComboBox<String>parkComboBox;

	@FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private ComboBox<Integer> monthComboBox;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label  totalLabel;

	
    
    /**
     * Initializes the report preparation screen by populating the park selection combo box with parks managed by the park manager,
     * and setting up the year and month selection combo boxes.
     */
    @FXML
    public void initialize() {
    	
    	//this should be a parks that managed by the specific parkmanager
    	parkComboBox.getItems().addAll(ParkManagerMenuController.parks);
    	//set upcompobox year and month
    	yearComboBox.getItems().addAll(2024, 2023);
    	for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }
    }
    
    
    /**
     * Navigates back to the park manager menu when the 'Back' button is clicked, allowing the park manager to exit the report
     * preparation process.
     *
     * @param event The event triggered by clicking the 'Back' button.
     */
    @FXML
    void ClickOnBackBtn(ActionEvent event) {
    	SwitchScreen.changeScreen(event,"/clientGUI/ParkManagerMenuController.fxml","/resources/ParkManagerMenuController.css");
    }
    
    
    /**
     * Prepares and displays the total visitors report based on the selected park, year, and month when the 'Prepare Report' button
     * is clicked. It fetches the report data and updates the pie chart and total label with the visitor statistics.
     *
     * @param event The event triggered by clicking the 'Prepare Report' button.
     */
    @FXML
    void prepareReport(ActionEvent event) {
    	if(parkComboBox.getValue()!=null && yearComboBox.getValue()!=null && yearComboBox.getValue()!=null) {
    	String parkName =parkComboBox.getValue();
        int year = yearComboBox.getValue();
        int month = monthComboBox.getValue();

        int result[]= getTotalVisitorsReport(parkName,month,year);
       
        if(result!=null) {
        int groups = result[0];
        int individuals= result[1];
        int total=individuals+groups;
        // Update PieChart data
        pieChart.getData().clear();
        pieChart.getData().add(new PieChart.Data("Individuals "+individuals, individuals));
        pieChart.getData().add(new PieChart.Data("Groups"+groups, groups));

        // Update total label
        totalLabel.setText("Total: " + total);
        }else {
        totalLabel.setText("No data found! " );
        
        }
    	}
    	else {
    		totalLabel.setText("select Input!" );
    	}
    }


    /**
     * Fetches the total visitors report data for a specified park, month, and year from the server. This method sends a request
     * to the server and returns the report data, which includes the number of individual and group visitors.
     *
     * @param parkName The name of the park for which the report is being prepared.
     * @param month The month for which the report is being prepared.
     * @param year The year for which the report is being prepared.
     * @return An array containing the number of group visitors and individual visitors for the specified park, month, and year.
     */
	private int[] getTotalVisitorsReport(String parkName, int month, int year) {
		ArrayList<Object> dataToSend=new ArrayList<>();
		dataToSend.add(parkName);
		dataToSend.add(month);
		dataToSend.add(year);
		Message msg=new Message("getTotalVisitorsByYearAndMonth", dataToSend);
		ClientUI.chat.acceptObj(msg);		
		return TotalVisitorsReportsPreparation.res;//static parameter will be updated from handleMessagFromServer
	}
}
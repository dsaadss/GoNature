package clientGUI;
import java.util.ArrayList;

import client.ClientUI;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import logic.Message;


/**
 * The {@code UsageReportsPreparation} class controls the interface for generating and visualizing usage reports for park managers. 
 * It allows the selection of a park, year, and month to create a report that showcases the usage patterns, specifically highlighting 
 * the days and hours when the park was not fully booked. This class facilitates user interaction, fetches necessary report data, 
 * and updates the UI with a scatter chart that represents the detailed usage data.
 */
public class UsageReportsPreparation { 
	
	public static int[][] res;

    @FXML
    private ComboBox<String>parkComboBox;

	@FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private ComboBox<Integer> monthComboBox;
    
    @FXML
    private ScatterChart<String, Number> scatterChart;

    @FXML
    private Label  totalLabel;

	
    
    /**
     * Initializes the report preparation screen by populating the park selection combo box with parks managed by the park manager
     * and setting up the year and month selection combo boxes for report generation.
     */
    @FXML
    public void initialize() {
    	
    	parkComboBox.getItems().addAll(ParkManagerMenuController.parks);
    	//set upcompobox year and month
    	yearComboBox.getItems().addAll(2024, 2023);
    	for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }
    }
    
    
    /**
     * Handles the action of navigating back to the park manager menu when the 'Back' button is clicked. This allows the park manager 
     * to exit the report preparation process and return to the main menu.
     *
     * @param event The event triggered by clicking the 'Back' button.
     */
	@FXML
    void ClickOnBackBtn(ActionEvent event) {
    	SwitchScreen.changeScreen(event,"/clientGUI/ParkManagerMenuController.fxml","/resources/ParkManagerMenuController.css");
    }
    
	

    /**
     * Prepares and displays the usage report based on the selected park, year, and month when the 'Prepare Report' button is clicked.
     * Fetches the report data and updates the scatter chart to visualize the park's usage patterns, highlighting the times when the park
     * was not fully booked.
     *
     * @param event The event triggered by clicking the 'Prepare Report' button.
     */
	@FXML
	void prepareReport(ActionEvent event) {
	    System.out.println("UsageReports>PrepareReportStarted");
	  String parkName = parkComboBox.getValue();
	  if( monthComboBox.getValue()!=null &&yearComboBox.getValue()!=null &&parkName!=null) {
		    int year = yearComboBox.getValue();
		int month = monthComboBox.getValue();
	    int[][] result = getUsageReport(parkName, month, year);

	    if (result != null) {
	        // Clear previous data from scatter chart
	        scatterChart.getData().clear();

	        // Set up y-axis range
	        NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();
	        CategoryAxis xAxis = new CategoryAxis();
	        xAxis.setLabel("Day");
	        yAxis.setLabel("Hour");

	        yAxis.setAutoRanging(false);
	        yAxis.setLowerBound(7);
	        yAxis.setUpperBound(20);
	        yAxis.setTickUnit(1); // Tick unit of 1 hour
	        scatterChart.setTitle("Usage report of park " + parkName);

	        scatterChart.setPrefSize(600, 400);
	        scatterChart.setAnimated(false); // Disable animation for better performance
	        scatterChart.setLegendVisible(true); 

	        // Add data to scatter chart
	        XYChart.Series<String, Number> series = new XYChart.Series<>();
	        series.setName("Days and hours where the park was not fully booked");
	        for (int i = 0; i < 31; i++) {
	            String day = String.valueOf(i + 1);
	            boolean found = false; // Flag to track if a shape has been plotted for the day
	            for (int j = 0; j < 12; j++) {
	                if (result[i][j] == 1 ) {
	                    
	                    series.getData().add(new XYChart.Data<>(day, j + 8)); // Days start from 1, hours start from 8
	                   
	                   
	                }
	            }
	        }
	        
	        scatterChart.getData().add(series);

	        System.out.println("UsageReport>End");
	    } else {
	        System.out.println("UsageReports>data was null");
	    }
	    }else
	    {
	    	scatterChart.setTitle("No Input Selected ");
	    }
	}

    
    /**
     * Prepares and displays an older version of the usage report based on the selected park, year, and month when invoked.
     * This method was used in earlier implementations for generating usage reports and has been superseded by the updated 
     * {@code prepareReport(ActionEvent event)} method. It fetches report data and updates the scatter chart to visualize the park's 
     * usage patterns, highlighting the times when the park was not fully booked. It is retained for backward compatibility or 
     * reference purposes.
     *
     * @param event The event triggered by an action that requires generating an older version of the usage report.
     */
    @FXML
    void prepareReportold(ActionEvent event) {
    	System.out.println("UsageReports>PrepareReportStarted");
        String parkName = parkComboBox.getValue();
        int year = yearComboBox.getValue();
        int month = monthComboBox.getValue();

        int[][] result = getUsageReport(parkName, month, year);

      if(result!=null) {
		// Clear previous data from scatter chart
        scatterChart.getData().clear();

        // Set up y-axis range
        NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Hour");
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(7); 
        yAxis.setUpperBound(20); 
        yAxis.setTickUnit(1); // Tick unit of 1 hour
        scatterChart.setTitle("Usage report of park "+parkName);
        
        scatterChart.setPrefSize(600, 400);
        scatterChart.setAnimated(false); // Disable animation for better performance
        scatterChart.setLegendVisible(false); // Hide legend
        scatterChart.setHorizontalGridLinesVisible(false); // Hide horizontal grid lines

        // Add data to scatter chart
        for (int i = 0; i < 31; i++) {
        	 String day = String.valueOf(i + 1);
            for (int j = 0; j < 12; j++) {
                if (result[i][j] == 1) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.getData().add(new XYChart.Data<>(day, j + 8)); // Days start from 1, hours start from 8
                    scatterChart.getData().add(series);
                }
            }
        }

        System.out.println("UsageReport>End");
      }
      else {
    	  System.out.println("UsageReports>data was null");
      }
    }

    
    /**
     * Fetches the usage report data for the specified park, month, and year from the server. This method constructs and sends a request 
     * to the server and awaits the report data, which includes detailed information on park usage.
     *
     * @param parkName The name of the park for which the report is being generated.
     * @param month The month for which the report is being generated.
     * @param year The year for which the report is being generated.
     * @return A 2D array representing the usage data for the specified park, month, and year, with each cell indicating the park's booking status at a given time.
     */
	private int[][] getUsageReport(String parkName, int month, int year) {
		ArrayList<Object> dataToSend=new ArrayList<>();
		dataToSend.add(parkName);
		dataToSend.add(month);
		dataToSend.add(year);
		Message msg=new Message("getUsageReportByYearAndMonth", dataToSend);
		System.out.println("UsageReports>getUsageReportByYearAndMonth "+parkName+" "+month+" "+year+" msg was sent to server");
		ClientUI.chat.acceptObj(msg);		
		return UsageReportsPreparation.res;//static parameter will be updated from handleMessagFromServer
	}



}
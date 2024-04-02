package clientGUI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import client.ClientUI;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import logic.CancelReport;
import logic.Message;

/**
 * The {@code DmCancelledReport} class is a controller for the UI related to generating and viewing cancellation 
 * and non-visit reports within the Department Manager's interface. It allows for the selection of a park, 
 * report type, and date range for which the report should be generated. The class is responsible for 
 * handling user interactions, fetching the required data, and updating the UI with the generated reports.
 */

public class DmCancelledReport { 
       /**
     * A static list that holds data for the cancellation report type 1.
     */ 
	public static ArrayList<CancelReport> DataforCancelReportType1;
  

    //public static ArrayList<Object> DataforCancelReportType2;

	@FXML
    private Label headerLabel;

    @FXML
    private ComboBox<String> parkComboBox;
       

    @FXML
    private DatePicker datePickerStart;
    
    @FXML
    private DatePicker datePickerEnd;
    
    
    @FXML
    private BarChart<String, Double> barChart1;//Average ,median graph
    @FXML
    private BarChart<String, Integer> barChart2;//distribution

    /**
     * Initializes the components of the cancellation report generation interface by populating the park selection
     * dropdown and setting up default values for other UI components.
     */
    @FXML
    public void initialize() {
    	String[] allParks=new String[]{"All Area"};
    	String[] both = new String[ DmMenuController.parks.length+1];
    	System.arraycopy(allParks, 0, both, 0, allParks.length);
    	System.arraycopy(DmMenuController.parks, 0, both, allParks.length, DmMenuController.parks.length);
    	
    	parkComboBox.getItems().addAll(both); 
    
    }
    
    /**
     * Handles the event triggered by clicking the 'Back' button. This method changes the screen back to 
     * the Department Manager menu, allowing the user to navigate away from the cancellation report screen.
     *
     * @param event The action event triggered by the user's click action.
     */
    @FXML
    void ClickOnBackBtn(ActionEvent event) {
    	SwitchScreen.changeScreen(event,"/clientGUI/DmMenuController.fxml", "/resources/DmMenuController.css");
    }
    

    /**
     * Initiates the preparation of the selected report based on the user's input from the UI components. 
     * This includes fetching the required data, generating the report, and updating the UI with the report visualization.
     *
     * @param event The action event triggered by the user's request to generate a report.
     */
    @FXML
    void prepareReport(ActionEvent event) {
    	drawGraph(event);
    }
    
    /**
     * Draws the cancellation report graphs based on the fetched data. This method is responsible for 
     * visualizing the report data in a bar chart format, including the calculation of cancellation percentages
     * and distribution over the selected time range.
     *
     * @param event The action event that triggers the graph drawing. This parameter is currently unused in the method.
     */
    void drawGraph(ActionEvent event) {
        System.out.println("prepareReport");

        String parkName = parkComboBox.getValue();
        LocalDate pickedDateStart = datePickerStart.getValue();
        LocalDate pickedDateEnd = datePickerEnd.getValue();

        barChart1.getData().clear();
        barChart1.setAnimated(false);
        barChart1.setTitle("Order Cancellation Precentage by Day ");
        barChart2.setTitle("Order Cancellation  Distribution");
        barChart1.getYAxis().setLabel("Cancellation Percentage");
        barChart2.getXAxis().setLabel("Day");
        barChart2.getYAxis().setLabel("frequency (times happend)");
        barChart2.getXAxis().setLabel("Cancels");
        ArrayList<CancelReport> result = getDataforCancelReportType1(pickedDateStart, pickedDateEnd);
        if (result != null) {
            if ("All Area".equals(parkName)) {
                Map<String, Map<Integer, Integer>> distribution = CancelReport.getDistribution(result);
                drawGraph2(distribution);
                // Draw bar chart for all parks
                for (CancelReport cancelReport : result) {
					/*
					 * XYChart.Series<String, Double> cancelSeries = new XYChart.Series<>();
					 * cancelSeries.setName(cancelReport.getParkName() + " - Cancel Orders");
					 */
                    XYChart.Series<String, Double> averageSeries = new XYChart.Series<>();
                    averageSeries.setName(cancelReport.getParkName() + " - Average");
                    
                    XYChart.Series<String, Double> medianSeries = new XYChart.Series<>();
                    medianSeries.setName(cancelReport.getParkName() + " - Median");

                    for (Map.Entry<LocalDate, CancelReport.CancelOrderData> entry : cancelReport.getCancelDataMap().entrySet()) {
                        LocalDate date = entry.getKey();
                        double cancelOrders = (double)entry.getValue().getCancelOrders();
                        double avg = (double)entry.getValue().getTotalOrders();
                        System.out.println("cancelOrders = "+cancelOrders);
                        System.out.println("average = "+avg);
                        if(avg!=0)
                        avg=cancelOrders/avg;
                        else {
                        	avg=0;
                        	cancelOrders=0;
                        }
                        System.out.println("average = "+avg);
                        double median = CancelReport.calculateMedianOfAverages(cancelReport);
                        System.out.println("median = "+median);
                        //cancelSeries.getData().add(new XYChart.Data<>(date.toString(), cancelOrders));
                        averageSeries.getData().add(new XYChart.Data<>(date.toString(), avg*100));
                        medianSeries.getData().add(new XYChart.Data<>(date.toString(), median*100));
                    }

                   // barChart1.getData().addAll(cancelSeries, averageSeries);
                    barChart1.getData().addAll(averageSeries,medianSeries);
                }
            } else {
                // Draw bar chart for a specific park
            	
            	
                for (CancelReport cancelReport : result) {
                    if (cancelReport.getParkName().equals(parkName)) {
                        Map<Integer, Integer> distribution = cancelReport.getCancellationDistribution();
                        drawGraph2(parkName, distribution);
                    	
						/*
						 * XYChart.Series<String, Double> cancelSeries = new XYChart.Series<>();
						 * cancelSeries.setName(cancelReport.getParkName() + " - Cancel Orders");
						 */
                        XYChart.Series<String, Double> averageSeries = new XYChart.Series<>();
                        averageSeries.setName(cancelReport.getParkName() + " - Average");
                        
                        XYChart.Series<String, Double> medianSeries = new XYChart.Series<>();
                        medianSeries.setName(cancelReport.getParkName() + " - Median");

                        
                        for (Map.Entry<LocalDate, CancelReport.CancelOrderData> entry : cancelReport.getCancelDataMap().entrySet()) {
                        	LocalDate date = entry.getKey();
                            Double cancelOrders = (double) entry.getValue().getCancelOrders();
                            Double totalOrders = (double) entry.getValue().getTotalOrders();

                          //  cancelSeries.getData().add(new XYChart.Data<>(date.toString(), cancelOrders));
                            averageSeries.getData().add(new XYChart.Data<>(date.toString(), totalOrders == 0 ? 0 : (cancelOrders / totalOrders)*100));
                            double median = CancelReport.calculateMedianOfAverages(cancelReport);
                            medianSeries.getData().add(new XYChart.Data<>(date.toString(), median*100));
                        }
  
                        //barChart1.getData().addAll(cancelSeries, averageSeries);
                        barChart1.getData().addAll(averageSeries,medianSeries);
                        break; // Exit loop after finding the specific park
                    }
                }
            }
        } else {
            System.out.println("Result is null.");
        }
    }
    
    /**
     * Draws the distribution graph for cancellation data. This version of the method is used when displaying
     * distribution data for a specific park.
     *
     * @param parkName The name of the park for which the distribution graph is being drawn.
     * @param distribution A map representing the distribution data, where the key is an identifier for the data point
     *                     (such as a day) and the value is the frequency of cancellations for that data point.
     */
    private void drawGraph2(String parkName, Map<Integer, Integer> distribution) {
    	barChart2.getData().clear();
        barChart2.setAnimated(false);

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName(parkName + " - Cancellation Distribution");

        for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
            series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
        }

        barChart2.getData().add(series);
		
	}

    /**
     * Draws the distribution graph for cancellation data across all parks. This method iterates through the distribution
     * data for each park and adds a series to the bar chart for each one.
     *
     * @param distribution A map where the key is the park name and the value is another map representing the
     *                     distribution data for that park, with the inner map's key being the data point identifier
     *                     and the value being the frequency of cancellations.
     */
	void drawGraph2(Map<String, Map<Integer, Integer>> distribution) {
        barChart2.getData().clear();
        barChart2.setAnimated(false);

        for (Map.Entry<String, Map<Integer, Integer>> entry : distribution.entrySet()) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName(entry.getKey());

            for (Map.Entry<Integer, Integer> data : entry.getValue().entrySet()) {
                series.getData().add(new XYChart.Data<>(String.valueOf(data.getKey()), data.getValue()));
            }

            barChart2.getData().add(series);
        }
    }

    /**
     * Fetches the data required for the cancellation report type 1. This method sends a request to the server
     * to retrieve cancellation data within the specified date range and updates the static list with the response.
     *
     * @param pickedDateStart The start date of the range for which cancellation data is requested.
     * @param pickedDateEnd The end date of the range for which cancellation data is requested.
     * @return An ArrayList of {@link CancelReport} objects containing the cancellation data for the specified range,
     *         or null if the start or end date is not specified.
     */
    private ArrayList<CancelReport> getDataforCancelReportType1(LocalDate pickedDateStart, LocalDate pickedDateEnd){
    	DmCancelledReport.DataforCancelReportType1=null;
    	ArrayList<LocalDate> dateRange=new ArrayList<LocalDate>();
    	if(pickedDateStart!=null && pickedDateEnd!=null) {
    	dateRange.add(pickedDateStart);
    	dateRange.add(pickedDateEnd);
    	System.out.println("gdfkshd;lkj");
    	Message msgToSend=new Message("getDataforCancelReportType1", dateRange);
    	ClientUI.chat.acceptObj(msgToSend);
		return DmCancelledReport.DataforCancelReportType1;
    	}
    	else
    		return null;
    	
    }

}
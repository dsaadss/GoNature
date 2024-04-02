package clientGUI;
import java.time.LocalDate;

import java.util.ArrayList;

import client.ClientUI;
import common.SwitchScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import logic.Message;

/**
 * The {@code DmVisitReport} class is responsible for controlling the visit report interface within the Department Manager's
 * section of the application. It allows for the selection of a park and a date to generate a report detailing the
 * number of individual and group visitors over time. The class handles user interactions, fetches necessary data,
 * and updates the UI with the generated report.
 */
public class DmVisitReport { 

    public static int[] resGroup;
    public static int[] resInd;
    public static int parkVisitTimeLimit;
    public static int parkCapacityOfVisitors;
    

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private Label totalLabel;

    @FXML
    private ComboBox<String> parkComboBox;

    @FXML
    private DatePicker datePicker;
	
    /**
     * Initializes the UI components, specifically the park selection ComboBox, with available parks. This method
     * is automatically invoked after the FXML fields have been populated.
     */
    @FXML
    public void initialize() {
    	parkComboBox.getItems().addAll(DmMenuController.parks); 
    }
    
    /**
     * Handles the event triggered by the 'Back' button click, navigating the user back to the Department Manager's
     * main menu.
     *
     * @param event The action event triggered by the user's click action.
     */
    @FXML
    void ClickOnBackBtn(ActionEvent event) {
    	SwitchScreen.changeScreen(event, "/clientGUI/DmMenuController.fxml","/resources/DmMenuController.css");
    }
    
    
    /**
     * Prepares and displays the visit report based on the selected park and date. This method collects data,
     * calculates stay times for individual and group visitors, and updates the line chart with the visitation data.
     *
     * @param event The action event triggered by the 'Prepare Report' button click.
     */
    @SuppressWarnings("unchecked")
	@FXML
	void prepareReport(ActionEvent event) {
		System.out.println("prepareReport");
    	LocalDate chosenDate=datePicker.getValue();
    	String parkName = parkComboBox.getValue();
    	lineChart.getData().clear();
    	if(chosenDate!=null && parkName!=null) {   	
        System.out.println(parkComboBox.getValue());
        System.out.println(datePicker.getValue().toString());
        lineChart.setTitle(parkName+" Entry and stay times segmented by\r\n"
        		+ "Types of visitors");
        lineChart.getXAxis().setLabel("Entry Hour");
        lineChart.getYAxis().setLabel("Visits in Park");
    	int[] indTimeEntryVisitors=getIndTimeEntryVisitors(parkName,chosenDate);
    	int[] groupTimeEntryVisitors=getGroupTimeEntryVisitors(parkName,chosenDate);
    	
    	int stayTime=getParkVisitTimeLimit(parkName);
    	int ParkMaxCapacity=getParkMaxCapacity(parkName);
    	///Calculate the stay time of each array
    	int[] indTimeEntryVisitorsAndStayTime=calculateStayTime(indTimeEntryVisitors,stayTime);
    	int[] groupTimeEntryVisitorsAndStayTime=calculateStayTime(groupTimeEntryVisitors,stayTime);
    	
    	printIntArray(indTimeEntryVisitors, "indTimeEntryVisitors");
    	printIntArray(indTimeEntryVisitorsAndStayTime, "indTimeEntryVisitorsAndStayTime");
    	///////////////////////////////////////////////////////////////////////////////////////
    	///making the graph
    	lineChart.getData().clear();
    	System.out.println("graph making");

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(7);
        xAxis.setUpperBound(20);
        xAxis.setTickUnit(1); // Set tick unit to 1 hour
        
        NumberAxis yAxies = (NumberAxis) lineChart.getYAxis();
        //yAxies.setAutoRanging(false);
        yAxies.setLowerBound(0);
        yAxies.setUpperBound(ParkMaxCapacity);
        yAxies.setTickUnit(2); // Set tick unit to 2 visitors
         

         XYChart.Series<Number, Number> indSeries = new XYChart.Series<>();
         for (int i = 0; i < indTimeEntryVisitorsAndStayTime.length; i++) {
             indSeries.getData().add(new XYChart.Data<>(i + 8, indTimeEntryVisitorsAndStayTime[i]));
         }
         indSeries.setName("Individual Visitors");

         XYChart.Series<Number, Number> groupSeries = new XYChart.Series<>();
         for (int i = 0; i < groupTimeEntryVisitorsAndStayTime.length; i++) {
             groupSeries.getData().add(new XYChart.Data<>(i + 8, groupTimeEntryVisitorsAndStayTime[i]));
         }
         groupSeries.setName("Group Visitors");
        
         lineChart.getData().addAll(indSeries, groupSeries);
         System.out.println("graph making end");
    	}else {
    		lineChart.setTitle("No Input Selected");
    	}	
    }

    /**
     * Calculates the cumulative stay time for visitors based on their entry time and the park's visit time limit.
     *
     * @param arr The array representing the number of visitors entering at each time slot.
     * @param stayTime The maximum allowed stay time in the park.
     * @return An array representing the cumulative number of visitors in the park over time, considering their stay time.
     */
	private int[] calculateStayTime(int[] arr, int stayTime) {
		int[] res=new int[arr.length+1];
		
		for(int i=0;i<arr.length-stayTime+1;i++) {
			for(int j=0;j<stayTime;j++) {
				res[i+j]+=arr[i];
			}
		}
		for(int i=arr.length-stayTime+1;i<arr.length;i++) {
			for(int j=i;j<res.length;j++) {
				res[j]+=arr[i];
			}
				
		}
		//res[arr.length]+=arr[arr.length-1];	
		return res;
	}

    /**
     * Fetches the visit time limit for a specified park from the server.
     *
     * @param parkName The name of the park for which the visit time limit is requested.
     * @return The visit time limit for the specified park.
     */
	private int getParkVisitTimeLimit(String parkName) {
		Message msg=new Message("getParkVisitTimeLimit", parkName);
		ClientUI.chat.acceptObj(msg);
		return DmVisitReport.parkVisitTimeLimit;
		//return 3;
	}
	
    /**
     * Fetches the maximum capacity for a specified park from the server.
     *
     * @param parkName The name of the park for which the maximum capacity is requested.
     * @return The maximum capacity for the specified park.
     */
	private int getParkMaxCapacity(String parkName) {
		Message msg=new Message("getParkMaxCapacity", parkName);
		ClientUI.chat.acceptObj(msg);
		return DmVisitReport.parkCapacityOfVisitors;
		//return 50;
	}

    /**
     * Fetches the number of group visitors entering the park at different times for a specified park and date.
     *
     * @param parkName The name of the park.
     * @param chosenDate The date for which the data is requested.
     * @return An array representing the number of group visitors entering the park at each time slot.
     */
	private int[] getGroupTimeEntryVisitors(String parkName, LocalDate chosenDate) {
		ArrayList<Object> dataToSend=new ArrayList<>();
		dataToSend.add(parkName);
		dataToSend.add(chosenDate);
		Message msg=new Message("getGroupTimeEntryVisitors", dataToSend);
		ClientUI.chat.acceptObj(msg);
		//return new int[]{2, 3, 4, 5, 6, 7, 8, 4, 9, 2, 2, 1};
		return DmVisitReport.resGroup;
		
	}


    /**
     * Fetches the number of individual visitors entering the park at different times for a specified park and date.
     *
     * @param parkName The name of the park.
     * @param chosenDate The date for which the data is requested.
     * @return An array representing the number of individual visitors entering the park at each time slot.
     */
	private int[] getIndTimeEntryVisitors(String parkName, LocalDate chosenDate) {
		ArrayList<Object> dataToSend=new ArrayList<>();
		dataToSend.add(parkName);
		dataToSend.add(chosenDate);
		Message msg=new Message("getIndTimeEntryVisitors", dataToSend);
		ClientUI.chat.acceptObj(msg);
		//return new int[]{0, 0, 2, 3, 3, 1, 5, 0, 3, 2, 1, 1};
		return DmVisitReport.resInd;
	}

	
    /**
     * Utility method for printing the contents of an integer array, primarily for debugging purposes.
     *
     * @param arr The integer array to be printed.
     * @param name The name or description of the array, to be included in the printout for clarity.
     */
	public void printIntArray(int[] arr,String name) {
        System.out.print(name+": [");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
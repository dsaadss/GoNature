package logic;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class CancelReport  implements Serializable{

	private static final long serialVersionUID = 1L;
	private String parkName;
	 private Map<LocalDate, CancelOrderData> cancelDataMap;
	 	
	 
	   /**
	     * Constructs a CancelReport object with the specified park name.
	     *
	     * @param parkName The name of the park
	     */
	    public CancelReport(String parkName) {
	        this.parkName = parkName;
	        this.cancelDataMap = new HashMap<>();
	    }
	    
	    
	    /**
	     * Gets the name of the park.
	     *
	     * @return The name of the park
	     */
	    public String getParkName() {
	        return parkName;
	    }
	    
	    /**
	     * Sets the name of the park.
	     *
	     * @param parkName The name of the park to set
	     */

	    public void setParkName(String parkName) {
	        this.parkName = parkName;
	    }
	    
	    /**
	     * Gets the cancellation data for a specific date.
	     *
	     * @param date The date for which cancellation data is requested
	     * @return The cancellation data for the specified date
	     */

	    public CancelOrderData getCancelDataByDate(LocalDate date) {
	        return cancelDataMap.get(date);
	    }
	    
	    /**
	     * Adds cancellation data for a specific date.
	     *
	     * @param date          The date for which cancellation data is being added
	     * @param cancelOrders  The number of cancelled orders
	     * @param totalOrders   The total number of orders
	     */

	    public void addCancelData(LocalDate date, int cancelOrders, int totalOrders) {
	        cancelDataMap.put(date, new CancelOrderData(cancelOrders, totalOrders));
	    }
	    
	    /**
	     * Gets the cancellation data map.
	     *
	     * @return The cancellation data map
	     */
	    public Map<LocalDate, CancelOrderData> getCancelDataMap() {
	        return cancelDataMap;
	    }

	    public static class CancelOrderData implements Serializable {
	
			private static final long serialVersionUID = 1L;
			private int cancelOrders;
	        private int totalOrders;
	        
	        /**
	         * Constructs CancelOrderData with the specified cancellation and total orders.
	         *
	         * @param cancelOrders The number of cancelled orders
	         * @param totalOrders  The total number of orders
	         */

	        public CancelOrderData(int cancelOrders, int totalOrders) {
	            this.cancelOrders = cancelOrders;
	            this.totalOrders = totalOrders;
	        }
	        
	        /**
	         * Gets the number of cancelled orders.
	         *
	         * @return The number of cancelled orders
	         */
	        
	        public int getCancelOrders() {
	            return cancelOrders;
	        }
	        
	        /**
	         * Gets the total number of orders.
	         *
	         * @return The total number of orders
	         */
	        
	        public int getTotalOrders() {
	            return totalOrders;
	        }
	    }
	    
	    /**
	     * Calculates the median of averages based on cancellation data.
	     *
	     * @param cancelReport The cancel report containing cancellation data
	     * @return The median of averages
	     */
	    
	   public static Double calculateMedianOfAverages(CancelReport cancelReport) {
	        // Extract average data for each date
	        ArrayList<Double> averageList = new ArrayList<>();
	        for (CancelReport.CancelOrderData cancelOrderData : cancelReport.getCancelDataMap().values()) {
	        	double cancelOrders = cancelOrderData.getCancelOrders();
	        	double totalOrders = cancelOrderData.getTotalOrders();
	            double average = totalOrders == 0 ? 0 : (double) cancelOrders / totalOrders;
	            averageList.add(average);
	        }

	        // Sort the average data
	        Collections.sort(averageList);

	        int size = averageList.size();
	        if (size == 0) {
	            return (double) 0; // No data, return 0
	        }

	        // Find the middle element(s)
	        int middleIndex = size / 2;

	        // If the number of elements is odd, the median is the middle element
	        if (size % 2 != 0) {
	            return (double) averageList.get(middleIndex);
	        }
	        // If the number of elements is even, the median is the average of the two middle elements
	        else {
	            double middleValue1 = averageList.get(middleIndex - 1);
	            double middleValue2 = averageList.get(middleIndex);
	            return  (double) (middleValue1 + middleValue2) / 2.0;
	        }
	    }
	   
	    /**
	     * Generates a distribution map of cancellation counts for each park.
	     *
	     * @param result The list of cancel reports
	     * @return The distribution map
	     */

	   public static Map<String, Map<Integer, Integer>> getDistribution(ArrayList<CancelReport> result) {
	        Map<String, Map<Integer, Integer>> distributionMap = new HashMap<>();

	        // Iterate through the list of CancelReport objects
	        for (CancelReport cancelReport : result) {
	            Map<Integer, Integer> distribution = new HashMap<>();
	            Map<LocalDate, CancelReport.CancelOrderData> cancelDataMap = cancelReport.getCancelDataMap();

	            // Iterate through the cancellation data and count occurrences
	            for (CancelReport.CancelOrderData cancelOrderData : cancelDataMap.values()) {
	                int cancelOrders = cancelOrderData.getCancelOrders();
	                distribution.put(cancelOrders, distribution.getOrDefault(cancelOrders, 0) + 1);
	            }

	            // Store distribution data in the map with park names as keys
	            distributionMap.put(cancelReport.getParkName(), distribution);
	        }

	        return distributionMap;
	    }
	   
	    /**
	     * Gets the cancellation distribution for the current cancel report.
	     *
	     * @return The cancellation distribution map
	     */

	public  Map<Integer, Integer> getCancellationDistribution() {
		 Map<Integer, Integer> distribution = new HashMap<>();

	        // Iterate through the cancellation data
	        for (CancelOrderData cancelOrderData : cancelDataMap.values()) {
	            int cancelOrders = cancelOrderData.getCancelOrders();

	            // Count the occurrences of each cancellation count
	            distribution.put(cancelOrders, distribution.getOrDefault(cancelOrders, 0) + 1);
	        }

	        return distribution;
	    
	}
	    
	}
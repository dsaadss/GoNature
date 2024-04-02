package common;

import java.util.ArrayList;

import entities.Park;
import entities.ParkForChange;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import logic.Order;


/**
 * The {@code StaticClass} serves as a utility class to hold global static variables that are shared across various
 * components of the application. This class includes identifiers for users and orders, flags for user state, and collections
 * for storing application-wide data such as parks and orders.
 */
public class StaticClass {
    public static String userid;// Holds the user ID
    public static String username;// Holds the username
    public static String orderid;// Holds the order ID
    public static boolean islogout =false;// Flag indicating logout status
    public static Order o1 = new Order(null, null, null, null, null, null,null);// Represents an order object
    public static String typeacc="";// Indicates the account type
	public static boolean isexist; // Flag indicating if user exists
	public static boolean islogged;// Flag indicating login status
    public static ArrayList<Park> parks = new ArrayList<>();// List of parks
    public static int numberofvisitors;// Number of visitors
    public static String orderdetails="";// Details of an order
    public static String reservationtype;// Type of reservation
    public static double discount;// Discount amount
    public static int parkprice;// Price for the park
	public static int available=0; // Availability flag
	public static String maxorderid=""; // Holds the maximum order ID
	public static String dwelltime;// Dwell time in the park
    public static ArrayList<Order> ordersforapprovetable = new ArrayList<>();// Orders for approval
    public static ArrayList<Order> ordersforwaitingtable = new ArrayList<>();// Orders in the waiting list
	public static String discounttype;// Type of discount
	public static int updatetowaitinglist;// Flag indicating update to waiting list
	public static int visa;// Visa processing flag
	public static String updatelabel;// Label for update operations
	public static int orderalert=1;// Order alert flag
	public static int userregistration;// User registration flag
	public static String externaluser= "0";// External user flag
	public static int addexternaluser; // Flag for adding an external user
	public static ArrayList<ParkForChange> changeparkdwelltime = new ArrayList<>();// List of parks with dwell time changes
	public static ArrayList<ParkForChange> changeparkmaxcap = new ArrayList<>();// List of parks with max capacity changes
    public static String amountinparkyarden="";// Amount in Park
    public static int istheorderexist=0;// Flag indicating if the order exists
    public static int amoutgreaterthenorder=0;// Flag for amount greater than order
    public static String PriceforOrder=""; // Price for an order

}

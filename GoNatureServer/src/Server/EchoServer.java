package Server;

import java.io.*;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import Server.DbController;
import entities.Park;
import entities.ParkForChange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.CancelReport;
import logic.ClientConnectionStatus;
import logic.Message;
import logic.Order;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * This class overrides some of the methods in the abstract superclass to provide
 * specific functionality for an order management server.
 */

/**
 * The {@code EchoServer} class extends {@code AbstractServer} to implement server-side logic specific to an order management system.
 * It handles client connections, message reception and processing, command execution based on received messages, and response back to clients.
 * This server is capable of interfacing with a database to perform operations such as querying and updating data as per client requests.
 */
public class EchoServer extends AbstractServer {
    // Class variables
    final public static int DEFAULT_PORT = 5555;
    
    //static DbController dbcontroller = new DbController();
    public static ObservableList<ClientConnectionStatus> clientsList = FXCollections.observableArrayList();

	public static String is_logged="";
	public static String type;
    public  String dbCMessage="";
	static int importdata=0;
    // Constructor
    public EchoServer(int port) {
        super(port);
    }

    
    /**
     * This method is called each time a message is received from a client. Based on the type and content of the message,
     * it performs the corresponding actions, such as querying the database, updating data, and sending responses back to the client.
     *
     * @param msg The message received from the client, typically encapsulated as a {@code Message} object.
     * @param client The {@code ConnectionToClient} object representing the client from which the message was received.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    	String typeacc="";
        try {
        	System.out.println("EchoServer>handleMessageFromClient");
        	 Connection conn = DbController.createDbConnection();
        if (msg instanceof Message) {
    		Message msgObject=((Message)msg);
    		String command=msgObject.getCommand();
    		Object payLoad=msgObject.getPayload();
    		System.out.println("EchoServer>Command and payload recived from client");
    		System.out.println("EchoServer>"+((Message)msg).getCommand()+","+((Message)msg).getPayload());
    		//getDataforCancelReportType1
        }
        if (msg instanceof Message) {
    		Message msgObject=((Message)msg);
    		String command=msgObject.getCommand();
    		Object payLoad=msgObject.getPayload();
    		System.out.println("EchoServer>Command and payload recived from client");
    		System.out.println("EchoServer>"+((Message)msg).getCommand()+","+((Message)msg).getPayload());
    		//getUsageReportByYearAndMonth
    		switch (command) {
			case "getTotalVisitorsByYearAndMonth": {
				try {
					if(payLoad instanceof ArrayList) {
						@SuppressWarnings("unchecked")
						ArrayList<Object> monthYear=(ArrayList<Object>)payLoad;
						int year=(int)monthYear.remove(2);
		    			int month=(int)monthYear.remove(1);
		    			String parkNname=(String)monthYear.remove(0);
		    			int[] arr=getTotalVisitorsByYearAndMonth(conn,parkNname,month,year);
		    			if(arr!=null) {
		    			Message res=new Message(((Message)msg).getCommand(), arr);
						client.sendToClient(res);
		    			}
		    			else
		    			{
		    				  handleErrorMessage(client, "returned null integer[] from db ");
		    			}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			}
			////getParksMangedByParkManger
			case "getUsageReportByYearAndMonth": {
				try {
					if(payLoad instanceof ArrayList) {
						ArrayList<Object> monthYear=(ArrayList<Object>)payLoad;
						int year=(int)monthYear.remove(2);
		    			int month=(int)monthYear.remove(1);
		    			String parkNname=(String)monthYear.remove(0);
		    			int[][] arr=getUsageReportByYearAndMonth(conn,parkNname,month,year);
		    			if(arr!=null) {
		    			Message res=new Message(((Message)msg).getCommand(), arr);
						client.sendToClient(res);
		    			}
		    			else
		    			{
		    				  handleErrorMessage(client, "returned null integer[][]  from db ");
		    			}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;		
			}
			case "getParkVisitTimeLimit":{
				int parkVisitTimeLimit=DbController.getParkVisitTimeLimit(conn,(String)payLoad);//by parkName
				Message res=new Message(((Message)msg).getCommand(), parkVisitTimeLimit);
				client.sendToClient(res);
				break;
			}
			case "getParkMaxCapacity":{
				int getParkMaxCapacity=DbController.getParkMaxCapacity(conn,(String)payLoad);//by parkName
				Message res=new Message(((Message)msg).getCommand(), getParkMaxCapacity);
				client.sendToClient(res);
				break;
			}
			case "getGroupTimeEntryVisitors":{
				@SuppressWarnings("unchecked")
				ArrayList<Object> data=(ArrayList<Object>)payLoad;
				LocalDate chosenDate=(LocalDate)data.remove(1);
    			String parkNname=(String)data.remove(0);
    			int[] GroupTimeEntryVisitors=DbController.getGroupTimeEntryVisitors(conn,parkNname,chosenDate);
    			Message res=new Message(((Message)msg).getCommand(), GroupTimeEntryVisitors);
    			client.sendToClient(res);
				break;
			}
			case "getIndTimeEntryVisitors":{
				ArrayList<Object> data=(ArrayList<Object>)payLoad;
				LocalDate chosenDate=(LocalDate)data.remove(1);
    			String parkNname=(String)data.remove(0);
    			int[] IndTimeEntryVisitors=DbController.getIndTimeEntryVisitors(conn,parkNname,chosenDate);
    			Message res=new Message(((Message)msg).getCommand(), IndTimeEntryVisitors);
    			client.sendToClient(res);
				break;
			}
			
	
			case "getDataforCancelReportType1": {//is canceled
				try {
					if(payLoad instanceof ArrayList) {
						@SuppressWarnings("unchecked")
						ArrayList<LocalDate> dates =(ArrayList<LocalDate>)payLoad;
						LocalDate start=dates.get(0),end=dates.get(dates.size()-1);
						
		    			ArrayList<CancelReport> al=DbController.getDataforCancelReportType1(conn,start, end);
		    			if(al!=null) {
		    			Message res=new Message(((Message)msg).getCommand(), al);
						client.sendToClient(res);
		    			}
		    			else
		    			{
		    				  handleErrorMessage(client, "returned null ArrayList from db ");
		    			}
					}
					else {
						handleErrorMessage(client, "PayLoad is not instance of LocalDate");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

			default:
				handleErrorMessage(client, "Invalid command in instance of message");
				throw new IllegalArgumentException("Unexpected value: " + command);
			}
    		
    	} ///not massage
        else {

            System.out.println("EchoServer> Message received: " + (String)msg + " from " + client);
            String returnmsg="";
            String message = (String) msg.toString();
            System.out.println("EchoServer> " + message);
            String[] result = message.split(" ");
            System.out.println(result[0]);
            if (result.length < 1) {
                handleErrorMessage(client, "Invalid message format");
                return;
            }
            if(result[0].equals("park")) {
            	System.out.println("its park!~~~~~~~~~~~~~~~~");
            	result[0]="park";}
        switch (result[0]) {
			case "getParksMangedByParkManger": {
					String[] arr=getParksMangedByParkManger(conn,result[1]);
					System.out.println("EchoServer>getParksMangedByParkManger");
	    			if(arr!=null) {
	    				for(int i=0;i<arr.length;i++) {
	    					System.out.println("EchoSrver> "+arr[i]);
	    				}
	    			Message res=new Message("getParksMangedByParkManger", arr);
					client.sendToClient(res);
	    			}
	    			else
	    			{
	    				handleErrorMessage(client, "returned null String[]  from db ");
	    			}
				break;
			}
        	case "connect":
            	String Ip = client.toString() + " " ;
            	String[] clientIp=Ip.split(" ");
            	String hostIp= getHostIp();
            	ClientConnectionStatus thisClient=clientConnection(clientIp[0],hostIp);
            	updateClientConnect(thisClient);
            	sendToClient(client,"Connected succeed");
            	break;
        	case "disconnect":
            	String Ip1 = client.toString() + " " ;
            	String[] clientIp1=Ip1.split(" ");
            	String hostIp1= getHostIp();
            	ClientConnectionStatus thisClient1=clientDisconnection(clientIp1[0],hostIp1);
            	updateClientConnect(thisClient1);
            	sendToClient(client,"Disconnected succeed");
            	break;	
            	
            case "updateOrder":
                int update =updateOrderDetails(conn,result[1],result[2],result[3],result[4],result[5],result[6],result[7],result[8]) ;
                if(update==1) {
                	System.out.println("updateOrder case, update =" +update + "\nvisa = "+DbController.needvisaalert);
                    if(DbController.needvisaalert.equals("yes")) {
                    	sendToClient(client, "updateOrder visaCredit");
                    	DbController.needvisaalert="no";
                    }
                    sendToClient(client, "updateOrder succeed");
                } 
                else if (update==10) {
                    sendToClient(client, "updateOrder waitinglist");
                }
                else {
                    sendToClient(client, "updateOrder failed");
                }
                //need to finish those if blocks so chat client will receive the visa value, and to change on dbcontroller
                //the total calculation, (when the total is less than zero, need to plus instead minus
                break;
            case "orderExist":
                if (result.length < 2) {
                    handleErrorMessage(client, "Invalid message format");
                    return;
                }
                if (orderExist(result[1],conn) == 1) {
                    sendToClient(client, "orderExist succeed");
                } else {
                    sendToClient(client, "OrderExist failed");
                }
                break;
            case "loadOrder":
                if (result.length < 2) {
                    handleErrorMessage(client, "Invalid message format");
                    return;
                }
                ArrayList<String> order = DbController.loadOrder(conn, result[1]);  
                if(order!=null) {
                	returnmsg="loadOrder "+order.get(0)+" "+ order.get(1) + " " +
                order.get(2)+" "+ order.get(3)+" "+order.get(4)+" "+order.get(5)+" "
                +order.get(6);
                	sendToClient(client,returnmsg);
                	return;}
                else {
                	sendToClient(client, "loadOrder failed");
                }
                break;
            case "userExist":
                if (result.length < 2) {
                    handleErrorMessage(client, "Invalid message format");
                    return;
                }
                if (userExist(result[1],result[2],conn) == 1) {
                    sendToClient(client, "userExist succeed "+ is_logged + " " + type);
                } else {
                    sendToClient(client, "userExist failed");
                }
                break;     
            case "login":
                if (userLogin(result[1],result[2],conn) == 1) {
                    sendToClient(client, "login succeed "+ result[1]);
                } else {
                    sendToClient(client, "login failed");
                }
                break;
            case "logout":
            	  if (userLogout(result[1],conn) == 1) {
                      sendToClient(client, "logout succeed");
                  } else {
                      sendToClient(client, "logout failed");
                  }
                  break;
            case "park":
            	System.out.println("echoserver> its park case");
            	ArrayList<Park> parks = new ArrayList<>();
            	parks = Park(conn);
            	Message payload = new Message("park", parks);
				try {
					client.sendToClient(payload);
				} catch (IOException e) {
					e.printStackTrace();
				}
      		    break;
            case "priceCheck":
            	int price = parkPrice(conn,result[1]);
            	sendToClient(client, "priceCheck "+price);
            	break;
            	
            case "checkDiscount":
            	int parkdiscount = checkDiscount(conn,result[1]);
            	sendToClient(client, "checkDiscount "+parkdiscount);
            	break;
            case "checkAvailability":
            	int avaiable=checkAvailability(conn,result[1],result[2],result[3],result[4]);
            	sendToClient(client, "checkAvailability "+avaiable);
            	
            	break;
            case "waitingList":
            	int waitingList = enterWaitingList(conn,result[1],result[2],result[3],result[4],result[5],result[6],result[7],result[8],result[9],result[10]);
            	sendToClient(client, "waitingList "+waitingList);
            	break;
            case "maxNumberOrder":
            	int max=DbController.checkMax(conn);
            	sendToClient(client, "maxNumberOrder "+max );
            	break;	
            case "saveOrder":
            	int save;
				if(result[8].equals("park")) {
	            	 typeacc=result[8]+" "+result[9];
	             	 save = saveOrder(conn,result[1],result[2],result[3],result[4],result[5],result[6],result[7],typeacc, result[10],result[11],result[12]);
				}			
				else {typeacc=result[8];
            	save = saveOrder(conn,result[1],result[2],result[3],result[4],result[5],result[6],result[7],typeacc, result[9],result[10],result[11]);
				}
            	sendToClient(client, "saveOrder " + save );
            	break;
            case "dwellTime":
            	String dwell=DbController.checkDwell(conn, result[1]);
            	sendToClient(client, "dwellTime " + dwell);
            	break;
            case "loadOrderForApproveTable":
            	ArrayList<Order> ordersForApproveTable = new ArrayList<>();
            	ordersForApproveTable = loadOrderForApproveTable(conn,result[1]);
            	Message payload1 = new Message("loadOrderForApproveTable", ordersForApproveTable);
			try {
				client.sendToClient(payload1);
			} catch (IOException e) {
				e.printStackTrace();
			}
            	break;
            case "loadOrderForWaitingTable": 
            	ArrayList<Order> ordersForWaitingListTable = new ArrayList<>();
            	ordersForWaitingListTable = loadOrderForWaitingListTable(conn,result[1]);
            	Message payload2 = new Message("loadOrderForWaitingTable", ordersForWaitingListTable);
			try {
				client.sendToClient(payload2);
			} catch (IOException e) {
				e.printStackTrace();
			}
				break;
            case "userId":
            	String userid=DbController.checkUserId(conn,result[1]);
            	sendToClient(client, "userId "+ userid);
            	break;
            case "deleteOrder":
            	String delete = DbController.DeleteOrder(conn, result[1],result[2],result[3]);
            	sendToClient(client, "deleteOrder "+delete);
            	break;
            case "deleteOrderAuto":
            	String deleteauto = DbController.DeleteOrderAuto(conn, result[1],result[2],result[3]);
            	sendToClient(client, "deleteOrderAuto "+deleteauto);
            	break;
            case "updateWaitingList":
            	DbController.updateWaitingList(conn,result[1]);
            	sendToClient(client, "updateWaitingList succeed");
            	break;
            case "updateGuideRole":
            	try {
            		boolean isSucessfull = DbController.updateGuideRole(conn, result[1]);
            		Message payloadforupdaterole = new Message("updateRole", isSucessfull);
            		client.sendToClient(payloadforupdaterole);
				} catch (Exception e) {
					e.printStackTrace(); 
				}
            	break;
            case "checkExternalUser":
            	int externalexist=DbController.checkExternalUser(conn,result[1]);
            	if(externalexist==1) {
            		sendToClient(client,"checkExternalUser exist");
            	}
            	else {sendToClient(client,"checkExternalUser notExist");}
            	break;
            case "addExternalUser":
            	int addexternal=DbController.addExternalUser(conn,result[1]);
            	if(addexternal==1) {
            		sendToClient(client,"addExternalUser succeed");
            	}
            	else {sendToClient(client,"addExternalUser failed");}
            	break;
            case"requastToChangevisit":
        		DbController.requastToChangevisit(conn, result[1],result[2]);
        		client.sendToClient(result[1]);
        		break;
            case"requastToChangeMaxCapcitiy":
        		DbController.requastToChangeMaxCapcitiy(conn, result[1],result[2]);
        		client.sendToClient(result[1]);
        		break;
            case"parkChangesVisit":
            	ArrayList<ParkForChange> waitforchange = new ArrayList<>();
            	waitforchange = DbController.LoadparkForChangevisittime(conn);
        		Message payloadfordmchange = new Message("updatechangeparkdwelltime", waitforchange);
        		client.sendToClient(payloadfordmchange);
        		break;
            case"parkMaxCap":
            	ArrayList<ParkForChange> waitforchange1 = new ArrayList<>();
            	waitforchange1 = DbController.LoadparkForMaxcap(conn);
        		Message payloadfordmchange1 = new Message("updateparkMaxCap", waitforchange1);
        		client.sendToClient(payloadfordmchange1);
        		break;
            	
            case"approveVisitTime":
            	DbController.approveVisitTime(conn,result[1],result[2]);
        		client.sendToClient("test");
        		break;
            case"declineVisitTime":
            	DbController.decline(conn,result[1],result[2]);
        		client.sendToClient("test");
        		break;
            case"approveMaxCapacity":
            	DbController.approveMaxCap(conn,result[1],result[2]);
        		client.sendToClient("test");
        		break;
            case"declineMaxCapacity":
            	DbController.declineMaxCap(conn,result[1],result[2]);
        		client.sendToClient("test");
        		break;
            case"amountInPark":
            	String amountinpark=DbController.getamountinpark(conn,result[1]);
            	sendToClient(client,"amountInPark " +amountinpark);
            	break;
            case"orderexistYarden":
            	int Exist=DbController.searchOrder(conn,result[1]);
            	sendToClient(client,"OrderExistYarden "+ Exist);
            	break;
            case"checkamountofpeople":
            	int greatorless =DbController.checkamountofpeople(conn,result[1],result[2]);
            	sendToClient(client, "checkamountofpeople "+ greatorless);
            	break;
            case"UpdateTable":
            	DbController.updateyardentable(conn,result[1],result[2],result[3]);
            	sendToClient(client, "test "+ "test11111111111111111111111");
            	break;
            case"PriceAfterEnter":
            	String PriceAfter=DbController.PriceAfter(conn,result[1]);
            	sendToClient(client, "PriceAfter1 "+ PriceAfter);



            default:
                handleErrorMessage(client, "Invalid command");
        	}
        }
        }    catch(IOException e) {
       	 handleErrorMessage(client, "Invalid command");
       	 e.printStackTrace();
       	}
        
    
        
    }
    
    /**
     * Retrieves a list of parks managed by a specific park manager from the database.
     *
     * @param conn The database connection.
     * @param username The username of the park manager.
     * @return An array of park names managed by the park manager.
     */
	private String[] getParksMangedByParkManger(Connection conn, String username) {
		System.out.println("EchoServersending sql to db username="+username);
		try {
			return DbController.getParksMangedByParkManger(conn,username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
    /**
     * Fetches usage report data for a specific park, month, and year from the database.
     *
     * @param conn The database connection.
     * @param parkName The name of the park.
     * @param month The month for the report.
     * @param year The year for the report.
     * @return A 2D array containing the usage data for the specified park, month, and year.
     */
	private int[][] getUsageReportByYearAndMonth(Connection conn, String parkNname, int month, int year) {
		System.out.println("EchoServersending sql to db month="+month+" year="+year);
		try {
			return DbController.getUsageByYearAndMonth(conn,parkNname,month,year);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	   /**
     * Fetches total visitors report data for a specific park, month, and year from the database.
     *
     * @param conn The database connection.
     * @param parkName The name of the park.
     * @param month The month for the report.
     * @param year The year for the report.
     * @return An array containing the total visitors data for the specified park, month, and year.
     */
	private int[] getTotalVisitorsByYearAndMonth(Connection conn,String parkName,int month, int year) {
		System.out.println("EchoServersending sql to db month="+month+" year="+year);
		
		try {
			return DbController.getTotalVisitorsByYearAndMonth(conn,parkName,month,year);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    

	
    /**
     * Loads orders into the waiting list table for a specific user.
     *
     * @param conn The database connection.
     * @param userid The user ID for whom to load orders.
     * @return An ArrayList of {@code Order} objects for the specified user.
     */
	private ArrayList<Order> loadOrderForWaitingListTable(Connection conn, String userid) {
		return DbController.loadOrderForWaitingListTable(conn,userid);
	}

	
	   /**
     * Loads approved orders into the table for a specific user.
     *
     * @param conn The database connection.
     * @param username The username for whom to load orders.
     * @return An ArrayList of {@code Order} objects for the specified user.
     */
	private ArrayList<Order> loadOrderForApproveTable(Connection conn, String username) {
		return DbController.loadOrderForApproveTable(conn,username);
	}

	
    /**
     * Saves a new order into the database.
     *
     * @param conn The database connection.
     * @param parkName The name of the park for the order.
     * @param username The username creating the order.
     * @param date The date for the order.
     * @param time The time for the order.
     * @param numberOfVisitors The number of visitors for the order.
     * @param orderId The ID of the order.
     * @param totalPrice The total price for the order.
     * @param typeacc The type of account making the order.
     * @param reservationType The type of reservation.
     * @param dwellTime The dwell time for the order.
     * @param email The contact email for the order.
     * @return An integer indicating the result of the save operation.
     */
	private int saveOrder(Connection conn, String parkname, String username, String date, String time,
			String numberofvisitors,String orderId,String totalprice, String typeacc,String reservationtype,String dwelltime,String email) {
			int saveorder=DbController.saveOrder(conn,parkname,username,date,time,numberofvisitors,orderId,totalprice, typeacc,reservationtype,dwelltime,email);
			return saveorder;
	}

	
	   /**
     * Enters an order into the waiting list in the database.
     *
     * @param conn The database connection.
     * @param parkName The name of the park for the order.
     * @param username The username creating the order.
     * @param date The date for the order.
     * @param time The time for the order.
     * @param numberOfVisitors The number of visitors for the order.
     * @param orderId The ID of the order.
     * @param totalPrice The total price for the order.
     * @param email The contact email for the order.
     * @param typeacc The type of account making the order.
     * @param reservationType The type of reservation.
     * @return An integer indicating the result of the waiting list entry operation.
     */
	private int enterWaitingList(Connection conn, String parkname, String username, String date, String time,
			String numberofvisitors,String orderId,String totalprice,String email,String typeacc,String reservationtype) {
		int waitinglist=DbController.waitingList(conn,parkname,username,date,time,numberofvisitors,orderId,totalprice,email, typeacc, reservationtype);
		return waitinglist;
	}

	
	/**
     * Checks the availability for a specific park, date, and time based on the number of visitors.
     *
     * @param conn The database connection.
     * @param parkName The name of the park.
     * @param numberOfVisitors The number of visitors.
     * @param date The date for the visit.
     * @param time The time for the visit.
     * @return An integer indicating availability.
     */
	private int checkAvailability(Connection conn, String parkname, String numberofvisitors,String date,String time) {
		int check=DbController.checkAvailable(conn,parkname,numberofvisitors,date,time);
		return check;
	}

	
	 /**
     * Checks if a specific discount is applicable based on the discount type.
     *
     * @param conn The database connection.
     * @param discountType The type of discount to check.
     * @return The percentage of the discount if applicable; otherwise, 0.
     */
	private int checkDiscount(Connection conn ,String discounttype) {
		int discount=DbController.discountCheck(conn,discounttype);
		return discount;
	}

	
	/**
     * Retrieves the price for a specific park from the database.
     *
     * @param conn The database connection.
     * @param parkName The name of the park.
     * @return The price associated with the specified park.
     */
	private int parkPrice(Connection conn ,String parkname) {
		int parkprice=DbController.checkPrice(conn,parkname);
		return parkprice;	
	}


	 /**
     * Updates the details of an existing order in the database.
     *
     * @param conn The database connection.
     * @param orderId The ID of the order to update.
     * @param parkName The new park name for the order.
     * @param date The new date for the order.
     * @param time The new time for the order.
     * @param numberOfVisitors The new number of visitors for the order.
     * @param discountType The type of discount applied to the order.
     * @param typeacc The account type of the user making the order.
     * @param reservationType The type of reservation.
     * @return An integer indicating the result of the update operation.
     */
	private int updateOrderDetails( Connection conn,String orderid,String parkname, String date, String time, String numberofvisitors, String discounttype
			,String typeacc,String reservationtype) {
		int update = DbController.updateOrder(conn, orderid,parkname,date,time,numberofvisitors,discounttype,typeacc,reservationtype);
		return update;
	}

	
	 /**
     * Logs out a user from the system, updating their status in the database.
     *
     * @param username The username of the user to log out.
     * @param conn The database connection.
     * @return An integer indicating the result of the logout operation.
     */
	private int userLogout(String username, Connection conn) {
		int logout=DbController.userLogout(conn,username);
		return logout;
	}

	
	/**
     * Logs in a user to the system, verifying their credentials against the database.
     *
     * @param username The username of the user attempting to log in.
     * @param password The password of the user.
     * @param conn The database connection.
     * @return An integer indicating the result of the login operation.
     */
	private int userLogin(String username,String password, Connection conn) {
		int login=DbController.userLogin(conn,username);
		return login;
	}

	
	/**
     * Checks if a user exists in the system based on the provided username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param conn The database connection.
     * @return An integer indicating whether the user exists (1) or not (0).
     */
	private int userExist(String username, String password, Connection conn) {
		  //System.out.println("echo server userExist method");
		  int exist=DbController.searchUser(conn,username,password);
		  if (exist==1) {return 1;} 
		  else {return 0;}
	}

	
	/**
     * Updates the connection status of a client in the list of connected clients. If the client is new, it adds them to the list;
     * if they already exist, it updates their status.
     *
     * @param thisClient The {@code ClientConnectionStatus} object representing the client's current connection status.
     */
	public void updateClientConnect(ClientConnectionStatus thisClient) {
		ClientConnectionStatus client = new ClientConnectionStatus(thisClient.ip, thisClient.host, thisClient.status);
		if(clientsList.indexOf(client) == -1 ) {
			clientsList.add(client);
		}	
		else {
			clientsList.remove(clientsList.indexOf(client));
			clientsList.add(client);
		}
		System.out.println(client.ip +" Connected succsessfully!");
	}
	
	
	/**
     * Creates a {@code ClientConnectionStatus} object representing the disconnection status of a client.
     *
     * @param clientIp The IP address of the client.
     * @param hostIp The IP address of the host server.
     * @return A {@code ClientConnectionStatus} object with the status set to "Disconnected".
     */
	private ClientConnectionStatus clientDisconnection(String clientIp, String hostIp) {
		ClientConnectionStatus clientStatus = new ClientConnectionStatus(clientIp,hostIp,"Disconnected");
		return clientStatus;	
	}

	
	/**
     * Creates a {@code ClientConnectionStatus} object representing the connection status of a client.
     *
     * @param clientIp The IP address of the client.
     * @param hostIp The IP address of the host server.
     * @return A {@code ClientConnectionStatus} object with the status set to "Connected".
     */
	private ClientConnectionStatus clientConnection(String clientIp, String hostIp) {
		ClientConnectionStatus clientStatus = new ClientConnectionStatus(clientIp,hostIp,"Connected");
		return clientStatus;	
	}

	
	 /**
     * Sends a message to a connected client.
     *
     * @param client The {@code ConnectionToClient} object representing the client to whom the message will be sent.
     * @param message The message to be sent to the client.
     */
	private void sendToClient(ConnectionToClient client, String message) {
        try {
            client.sendToClient(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	
	 /**
     * Handles the sending of an error message to a client, encapsulating common functionality for error reporting.
     *
     * @param client The {@code ConnectionToClient} object representing the client to whom the error message will be sent.
     * @param errorMessage The error message to be sent to the client.
     */
    private void handleErrorMessage(ConnectionToClient client, String errorMessage) {
        sendToClient(client, errorMessage);
    }

    /**
     * Checks if an order exists in the system based on the provided order ID.
     *
     * @param order The order ID to check.
     * @param conn The database connection.
     * @return An integer indicating whether the order exists (1) or not (0).
     */
	public static int orderExist(String order, Connection conn) {
		  int exist=DbController.searchOrder(conn,order);
	
		  if (exist==1) {return 1;} 
		  else {return 0;}
	}
	
	
	/**
     * Retrieves a list of parks from the database.
     *
     * @param conn The database connection.
     * @return An ArrayList of {@code Park} objects representing all parks in the system.
     */
	private ArrayList<Park> Park(Connection conn) {
		return DbController.park(conn);
	}
	
	/**
     * This method is called when the server starts listening for connections. It logs the event indicating that the server is now
     * ready to accept incoming client connections on the specified port.
     */
    protected void serverStarted() {
        System.out.println("EchoServer> Server listening for connections on port " + getPort());
    }

    /**
     * This method is called when the server stops listening for connections. It logs the event to indicate that the server has
     * ceased to accept incoming client connections and is effectively offline.
     */
    protected void serverStopped() {
        System.out.println("EchoServer> Server has stopped listening for connections.");
    }
    
    /**
     * Retrieves the IP address of the host machine where the server is running. This method can be used to display or log the
     * server's IP address for informational purposes or for clients to know where to connect.
     *
     * @return The IP address of the host server as a {@code String}. Returns {@code null} if the host IP address cannot be determined.
     */
	public static String getHostIp() {
		try {
			InetAddress localHost= InetAddress.getLocalHost();
			return localHost.getHostAddress();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
     * Gets the IP address of the client connected to the server.
     *
     * @param client The {@code ConnectionToClient} object representing the connected client.
     * @return A {@code String} representing the IP address of the client.
     */
	public static String getClientIp(ConnectionToClient client) {
	    return client.getInetAddress().getHostAddress();
	}

	
	/**
     * Imports initial data into the server's database if it has not been imported already. This method ensures that the data is imported
     * only once to avoid duplication. It utilizes the {@code DbController} class to establish a database connection and import the data.
     */
	public static void importData() {
        Connection conn = DbController.createDbConnection();
		if(importdata==0) {
			importdata=1;
			DbController.importData(conn);
		}
	}
}
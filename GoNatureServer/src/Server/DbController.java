package Server;


import java.util.ArrayList;

import com.sun.javafx.binding.SelectBinding.AsString;

import entities.Park;
import entities.ParkForChange;
import logic.CancelReport;
import logic.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class DbController {
    public static Order order;
    public static String needvisaalert="no";
    
    //connect to MySQL
    @SuppressWarnings("unused")
	public static Connection createDbConnection() {
  	  try 
  		{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            System.out.println("EchoServer> Driver definition succeed");
        } catch (Exception ex) {
        	/* handle the error*/
        	System.out.println("EchoServer> Driver definition failed ");
        	 }
        try 
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/gonaturedb?serverTimezone=IST","root","Aa123456");
          
            System.out.println("SQL connection succeed");
            return conn;
            //createTableCourses(conn);
     	} catch (SQLException ex) 
     	    {/* handle any errors*/
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            }
        return null;
    }

    /**
     * Retrieves details of a specific order from the database
     * 
     * @param conn          Connection object representing the database connection
     * @param order_number  String representing the order number
     * @return ArrayList containing details of the order retrieved from the database
     */
    public static ArrayList<String> loadOrder(Connection conn, String order_number) {
        System.out.println("dbController> Order number = " + order_number);
        String sql = "SELECT * FROM orders WHERE OrderId = ?";
        System.out.println("test in LoadOrder");
        int orderid=Integer.parseInt(order_number);
        ArrayList<String> orderDetails = new ArrayList<>();
        orderDetails.add(order_number);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderid);
            try (ResultSet rs = pstmt.executeQuery()) {
                //System.out.println("Select * was executed");
                if (rs.next()) {
                    orderDetails.add(rs.getString("ParkName"));
                    orderDetails.add(rs.getString("UserId"));
                    orderDetails.add((rs.getString("DateOfVisit")));
                    orderDetails.add(rs.getString("TimeOfVisit"));
                    orderDetails.add((rs.getString("NumberOfVisitors")));
                    orderDetails.add((rs.getString("Email")));
                    System.out.println("test in preparedstatement");
                }
                pstmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Error loading order: " + e.getMessage());
        }
        return orderDetails;
    }
 
    /**
     * Searches for the existence of an order in the database
     * 
     * @param conn          Connection object representing the database connection
     * @param order_number  String representing the order number
     * @return int indicating whether the order exists (1) or not (0)
     */
    public static int searchOrder(Connection conn, String order_number) {
        String sql = "SELECT * FROM orders WHERE OrderId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order_number);
            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if the order exists
                if (rs.next()) {
                    System.out.println("dbController> Order exists.");
                    pstmt.close();
                    return 1; // Order found
                } else {
                    System.out.println("dbController> Order does not exist.");
                    pstmt.close();
                    return 0; // Order not found     
                }
                
            }
        } catch (SQLException e) {
            System.out.println("dbController> Error searching for order: " + e.getMessage());
        }
        return 0; // Return 0 or appropriate error code/value in case of exception
    }
    
    
    /**
     * Updates details of an order in the database
     * 
     * @param conn              Connection object representing the database connection
     * @param orderid           String representing the order ID
     * @param parkname          String representing the park name
     * @param date              String representing the date of visit
     * @param time              String representing the time of visit
     * @param numberofvisitors  String representing the number of visitors
     * @param discounttype      String representing the type of discount
     * @param typeacc           String representing the type of account
     * @param reservationtype   String representing the reservation type
     * @return int indicating the success of the update operation
     */
    public static int updateOrder(Connection conn, String orderid,String parkname, String date, String time, String numberofvisitors, String discounttype,
    		String typeacc,String reservationtype) {
	    	String beforechangedate="";
	    	String beforechangetime="";
	    	String beforechangepark="";
	    	String beforenumberofvisitor="";
	    	String beforewaitinglist="";
	    	String beforetotalprice="";
	    	String sql="Select * FROM orders WHERE OrderId = ?";
		    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
					pstmt.setString(1, orderid);
			        try(ResultSet rs = pstmt.executeQuery()) {
			           if (rs.next()) {	
			        	   beforechangepark+=rs.getString("ParkName");
			        	   beforechangedate+=rs.getString("DateOfVisit");
			        	   beforechangetime+=rs.getString("TimeOfVisit");
			        	   beforenumberofvisitor+=rs.getString("NumberOfVisitors");
			        	   beforewaitinglist+=rs.getString("IsInWaitingList");
			        	   beforetotalprice+=rs.getString("TotalPrice");
			           }
				    }catch (SQLException e) {
				        System.out.println("DbController> Error fetching update order: " + e.getMessage());}    
		    } catch (SQLException e2) {
					e2.printStackTrace();}
	       String beforedwell=checkDwell(conn, beforechangepark);
		   int price=checkPrice(conn, parkname);
		   int discount=discountCheck(conn, discounttype);
		   int updateoldorder=0;
		   int updateneworder=0;
		   System.out.println("beforepark " +beforechangepark+"\nbeforedate " +beforechangedate
					+"\nbeforetime " +beforechangetime + "\nbeforenumberofvisitors " +beforenumberofvisitor + "\ntypeaccount " +typeacc
					+"\nreservationtype "  +reservationtype+"\ndwelltime " +beforedwell+"\nbeforewaitinglist " +beforewaitinglist
					+"\nbeforetotalprice " +beforetotalprice);
		   if(beforewaitinglist.equals("NO")) {
			   updateoldorder =updateTotalTables(conn, beforechangepark, beforechangedate,
					   beforechangetime, beforenumberofvisitor, typeacc, reservationtype, beforedwell, "-");}
		   double total=0;
		   if(Double.parseDouble(beforetotalprice)<0)
			   total=(price*Integer.parseInt(numberofvisitors)*(1-(0.01*discount)))+Double.parseDouble(beforetotalprice);//total price
		   else {total=(price*Integer.parseInt(numberofvisitors)*(1-(0.01*discount)))-Double.parseDouble(beforetotalprice);//total price
		   }
	       int available=0;
	       int checkrow= checkRowExist(conn, typeacc, reservationtype, parkname, date);// return 1 if there is existing row
		   available=checkAvailable(conn, parkname, numberofvisitors, date, time);//return 1 is can add the number of visitors
		   System.out.println("checkrow = " +checkrow +"\navailable = " + available);
           String newdwell=checkDwell(conn, parkname);//checking dwell on the new parkname
		   if(checkrow==1&&available==1) {//check on db total tables if there is existing row with the same park name and date
				//and enough spaces for the new number of visitors
	    		if(discounttype.equals("group")) {
	    			total=total*0.88;}
	    		System.out.println("can make update for order~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	    		System.out.println("beforepark " +beforechangepark+"\nbeforedate " +beforechangedate
	    				+"\nbeforetime " +beforechangetime + "\nbeforenumberofvisitors " +beforenumberofvisitor + "\ntypeaccount " +typeacc
	    				+"\nreservationtype "  +reservationtype+"\ndwelltime " +beforedwell);
	    		if(beforewaitinglist.equals("NO")) {//case order is not in waiting list at the moment
		            if(updateoldorder==1) {
		            	updateneworder=updateTotalTables(conn, parkname, date,
		                		time, numberofvisitors, typeacc, reservationtype, newdwell, "+");
		            }
		            if(updateneworder==1) {
			            updateneworder =updateOrderNewDetails(conn,orderid,parkname,date,time,numberofvisitors,""+total);
			            return 1;}
	    		}
	    		else {//case order is in waiting list at the moment
	            	updateneworder=updateTotalTables(conn, parkname, date,
	                		time, numberofvisitors, typeacc, reservationtype, newdwell, "+");
		            if(updateneworder==1) {
			            updateneworder =updateOrderNewDetails(conn,orderid,parkname,date,time,numberofvisitors,""+total);
			            return 1;}
				}
			}
			else if(checkrow==1&& available==0) {//if there existing row but not enough available spaces on park, 
				//checking with the park name and date on total capacity tables on db
	            updateneworder =updateOrderNewDetails(conn,orderid,parkname,date,time,numberofvisitors,""+total);
				return 10;
			}
			else if(checkrow==0&& available==1){
	            updateneworder =updateOrderNewDetails(conn,orderid,parkname,date,time,numberofvisitors,""+total);
	            System.out.println("its checkrow =0 and available=1, updateneworder = "+updateneworder);
	            if(updateoldorder==1&& updateneworder==1&&beforewaitinglist.equals("NO"))
	            	insertTotalTables(conn, parkname, date, time, numberofvisitors, typeacc, reservationtype, newdwell, "+");
	            else if(updateneworder==1)
	            	insertTotalTables(conn, parkname, date, time, numberofvisitors, typeacc, reservationtype, newdwell, "+");
	            return 1;
			}else {
	            updateneworder =updateOrderNewDetails(conn,orderid,parkname,date,time,numberofvisitors,""+total);
	            return 10;
			}
		   return 0;
	    }
    
    
    /**
     * Updates details of an order when it is moved to the waiting list
     * 
     * @param conn              Connection object representing the database connection
     * @param orderid           String representing the order ID
     * @param parkname          String representing the park name
     * @param date              String representing the date of visit
     * @param time              String representing the time of visit
     * @param numberofvisitors  String representing the number of visitors
     * @param totalprice        String representing the total price
     * @return int indicating the success of the update operation
     */   
       public static int updateOrderNewDetails(Connection conn, String orderid,String parkname, String date, String time, String numberofvisitors,
    		String totalprice) {
    	double total= Double.parseDouble(totalprice);
		String sqlorders = "UPDATE orders SET ParkName = ?, DateOfVisit = ?, TimeOfVisit = ?, NumberOfVisitors = ?, IsConfirmed = ?, IsVisit = ?,"
				+ " IsCanceled = ?, TotalPrice = ?, IsInWaitingList = ? WHERE OrderId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlorders)) {
            pstmt.setString(1, parkname); 
            pstmt.setString(2, date); 
            pstmt.setString(3, time); 
            pstmt.setString(4, numberofvisitors); 
            pstmt.setString(5, "YES"); 
            pstmt.setString(6, "NO"); 
            pstmt.setString(7,"NO"); 
            pstmt.setString(8, ""+total); 
            pstmt.setString(9, "NO"); 
            pstmt.setInt(10, Integer.parseInt(orderid)); 
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("dbController> Order updated successfully.");
                if (total<0.0)
                	needvisaalert="yes";
                //need to take care of the 
                return 1;
            } else {
                System.out.println("dbController> Order not found or no change made.");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
        }
	
	return 0;
    }
  
       /**
        * Searches for the existence of a user in the database
        * 
        * @param conn      Connection object representing the database connection
        * @param username  String representing the username
        * @param password  String representing the password
        * @return int indicating whether the user exists (1) or not (0)
        */
    public static int searchUser(Connection conn, String username, String password) {
        String sql = "SELECT * FROM users WHERE Username = ? AND Password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //System.out.println("DbController> User exists.");
                    EchoServer.is_logged = rs.getString("IsLogged");
                    EchoServer.type = rs.getString("TypeUser");
                    // Debugging output
                    System.out.println("Logged: " + EchoServer.is_logged + ", Type: " + EchoServer.type);
                    return 1;
                } else {
                    System.out.println("DbController> User does not exist.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("DbController> Error searching for user: " + e.getMessage());
        }
        return 0; // Return 0 or appropriate error code/value in case of exception
    }

    /**
     * Updates the login status of a user in the database
     * 
     * @param conn      Connection object representing the database connection
     * @param username  String representing the username
     * @return int indicating the success of the update operation
     */
	public static int userLogin(Connection conn, String username) {
		String sql = "UPDATE users SET IsLogged = ? WHERE Username = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, "1");
	        pstmt.setString(2, username);	        
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("dbController> login succeed, rows affected: " + rowsAffected);
	            return 1;
	        } else {
	            System.out.println("dbController> login failed. No rows affected.");
	            return 0;
	        }
	    } catch (SQLException e) {
	        System.out.println("dbController> Error updating user login status: " + e.getMessage());
	    }
	    return 0;
	}
    /**
     * Updates the logout status of a user in the database
     * 
     * @param conn      Connection object representing the database connection
     * @param username  String representing the username
     * @return int indicating the success of the update operation
     */
	public static int userLogout(Connection conn, String username) {
		String sql = "UPDATE users SET IsLogged = ? WHERE Username = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, "0");
	        pstmt.setString(2, username);
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("dbController> logout succeed, rows affected: " + rowsAffected);
	            return 1;
	        } else {
	            System.out.println("dbController> logout failed. No rows affected.");
	            return 0;
	        }
	    } catch (SQLException e) {
	        System.out.println("dbController> Error updating user logout status: " + e.getMessage());
	    }
	    return 0;
	}
    /**
     * Retrieves details of parks from the database
     * 
     * @param conn  Connection object representing the database connection
     * @return ArrayList containing Park objects with details of parks retrieved from the database
     */
	public static ArrayList<Park> park(Connection conn) {
         	ArrayList<Park> parks = new ArrayList<>();
	        String query = "SELECT * FROM park;";
	        try (Statement stmt = conn.createStatement(); 
	             ResultSet rs = stmt.executeQuery(query)) { // try-with-resources for auto closing
	            
	            while (rs.next()) {
	                String parkName = rs.getString("Parkname");
	                String capacityOfVisitors = rs.getString("CapacityOfVisitors");
	                String pricePerPerson = rs.getString("PricePerPerson");
	                String availableSpot = rs.getString("AvailableSpot");
	                String visitTimeLimit = rs.getString("visitTimeLimit");
	                String parkManagerId = rs.getString("ParkMangerId");
	                
	                // Assuming the Park constructor matches these fields
	                Park park = new Park(parkName, capacityOfVisitors, pricePerPerson, availableSpot, visitTimeLimit, parkManagerId);
	                parks.add(park);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

		return parks;
	}
    /**
     * Retrieves the price per person for a specific park from the database
     * 
     * @param conn      Connection object representing the database connection
     * @param parkname  String representing the name of the park
     * @return int representing the price per person for the specified park
     */
		public static int checkPrice(Connection conn,String parkname) {
        String sql = "SELECT PricePerPerson FROM park WHERE ParkName = ?"; 
        int price=0;
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, parkname);
		        try(ResultSet rs = pstmt.executeQuery()) {
		           if (rs.next()) {	
		        	  price=Integer.parseInt(rs.getString("PricePerPerson"));
		           }
		    }catch (SQLException e) {
		        System.out.println("DbController> Error fetching price per person: " + e.getMessage());}    
	    } catch (SQLException e2) {
			e2.printStackTrace();}   
	    System.out.println("dbController> price per prson is: " + price);
        return price;
    }
	    /**
	     * Checks the discount percentage for a specific discount type from the database
	     * 
	     * @param conn          Connection object representing the database connection
	     * @param discounttype  String representing the type of discount
	     * @return int representing the discount percentage for the specified discount type
	     */
	public static int discountCheck(Connection conn, String discounttype) {
        String sql = "SELECT SalePercentage FROM sales WHERE SaleType = ?";
        int discount=0;
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, discounttype);
		        try(ResultSet rs = pstmt.executeQuery()) {
		           if (rs.next()) {	
		        	  discount=Integer.parseInt(rs.getString("SalePercentage"));
		           }
		    }catch (SQLException e) {
		        System.out.println("DbController> Error fetching sales: " + e.getMessage());}    
	    } catch (SQLException e2) {
			e2.printStackTrace();}
	    System.out.println("dbcontroller> discount: " + discount);
	    return discount;
    }
    /**
     * Checks the availability of spots in a park for a given date and time
     * 
     * @param conn              Connection object representing the database connection
     * @param parkname          String representing the name of the park
     * @param numberofvisitors  String representing the number of visitors
     * @param date              String representing the date of visit
     * @param time              String representing the time of visit
     * @return int indicating availability (1 if available, 0 if not available)
     */
	public static int checkAvailable(Connection conn, String parkname, String numberofvisitors, String date, String time) {
		int dwell=Integer.parseInt(checkDwell(conn, parkname));
		System.out.println("dwell is: "+ dwell);
		String[] st= new String[dwell];
		for (int i=8;i<20-dwell;i++) {
			if (time.contains(""+i)) {//inserting into st array the amount of dwell hours of 
				for(int j=0;j<dwell;j++)
					st[j]="t"+(j+i);
			}
		}
		String sql="SELECT * FROM park_used_capacity_total WHERE Parkname = ? AND date = ?";
        int[] numberofsignedvisitors= new int[dwell];
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, parkname);
				pstmt.setString(2, date);
		        try(ResultSet rs = pstmt.executeQuery()) {
		           if (rs.next()) {	
		        	   for(int k=0;k<dwell;k++) {
			        	   numberofsignedvisitors[k]=rs.getInt(st[k]);
			        	   System.out.println("DbController> number of signed people on capacity tables: "+numberofsignedvisitors[k]);
		        	   }
		           }
		    }catch (SQLException e) {
		        System.out.println("DbController> Error fetching capacity total in checkAvailable: " + e.getMessage());}    
	    } catch (SQLException e2) {
			e2.printStackTrace();}
	    
	    //System.out.println("dbcontroller> number of signed visitors: " + numberofsigned);
	    int capacity=checkCapacity(conn, parkname);
	    /*String sql2="SELECT CapacityOfVisitors FROM park WHERE Parkname = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql2)){
				pstmt.setString(1, parkname);
		        try(ResultSet rs = pstmt.executeQuery()) {
		           if (rs.next()) {	
		        	  capacity=Integer.parseInt(rs.getString("CapacityOfVisitors"));
		           }
		    }catch (SQLException e) {
		        System.out.println("DbController> Error fetching capacity total: " + e.getMessage());}    
	    } catch (SQLException e2) {
			e2.printStackTrace();}*/
	    for(int t=0;t<dwell;t++) {
	    	if(numberofsignedvisitors[t]+Integer.parseInt(numberofvisitors)>capacity)
	    		return 0;
	    }
	    return 1;
	}
    /**
     * Retrieves the maximum capacity of visitors for a specific park from the database
     * 
     * @param conn      Connection object representing the database connection
     * @param parkname  String representing the name of the park
     * @return int representing the maximum capacity of visitors for the specified park
     */	
		public static int checkCapacity(Connection conn, String parkname) {
	    int capacity=0;
	    String sql2="SELECT CapacityOfVisitors FROM park WHERE Parkname = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql2)){
				pstmt.setString(1, parkname);
		        try(ResultSet rs = pstmt.executeQuery()) {
		           if (rs.next()) {	
		        	  capacity=Integer.parseInt(rs.getString("CapacityOfVisitors"));
		           }
		    }catch (SQLException e) {
		        System.out.println("DbController> Error fetching capacity total: " + e.getMessage());}    
	    } catch (SQLException e2) {
			e2.printStackTrace();}
	    return capacity;
		
	}
	
	
	
	
	    /**
	     * Creates an order and marks it as waiting list in the database
	     * 
	     * @param conn              Connection object representing the database connection
	     * @param parkname          String representing the name of the park
	     * @param username          String representing the username
	     * @param date              String representing the date of visit
	     * @param time              String representing the time of visit
	     * @param numberofvisitors  String representing the number of visitors
	     * @param orderid           String representing the order ID
	     * @param totalprice        String representing the total price
	     * @param email             String representing the email
	     * @param typeacc           String representing the type of account
	     * @param reservationtype   String representing the reservation type
	     * @return int indicating the success of the operation (1 if successful, 0 otherwise)
	     */
		public static int waitingList(Connection conn, String parkname, String username, String date, String time,
			String numberofvisitors,String orderid,String totalprice,String email,String typeacc, String reservationtype) {
		int ordernumber=Integer.parseInt(orderid);
		System.out.println("new order number: "+ ordernumber);
		String userid=username;
		String saletype="personal";
		if(!typeacc.equals("guest")) {
			if(typeacc.equals("guide"))
				saletype="group";
			else{ 
				if(reservationtype.equals("group"))
					saletype="casual_group";
				else {saletype="casual_personal";
			//--------------------------------------------------------------------------------------------------------------------------------------------------
				}
			}
			String sqlusers="SELECT * FROM users WHERE Username = ?";
		    try (PreparedStatement pstmt = conn.prepareStatement(sqlusers)){
					pstmt.setString(1, username);
			        try(ResultSet rs = pstmt.executeQuery()) {
				           if (rs.next()) {	
				        	   userid=rs.getString("UserId");
				           }
				    }catch (SQLException e) {
				        System.out.println("DbController> Error fetching orders: " + e.getMessage());}    
			    } catch (SQLException e2) {
					e2.printStackTrace();}
		}
	    String sqlorders = "INSERT INTO orders (OrderId, ParkName, UserId, DateOfVisit, "
	    		+ "TimeOfVisit, NumberOfVisitors, IsConfirmed, IsVisit, IsCanceled, TotalPrice, IsInWaitingList, Email,SaleType) VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(sqlorders)){
				pstmt.setInt(1, ordernumber);
				pstmt.setString(2, parkname);
				pstmt.setString(3, userid);
				pstmt.setString(4, date);
				pstmt.setString(5, time);
				pstmt.setString(6, numberofvisitors);
				pstmt.setString(7, "NO");
				pstmt.setString(8, "NO");
				pstmt.setString(9, "NO");
				pstmt.setString(10, totalprice);
				pstmt.setString(11, "YES");
				pstmt.setString(12, email);
				pstmt.setString(13, saletype);
			    pstmt.executeUpdate();
				System.out.println("insert into orders succeed~~~~");
				return 1;
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	    return 0;
	}
	    /**
	     * Retrieves the maximum order ID from the database and increments it by one
	     * 
	     * @param conn  Connection object representing the database connection
	     * @return int representing the incremented maximum order ID
	     */
	public static int checkMax(Connection conn) {//checking the max order number 
		int max=0;
		String sql="SELECT MAX(OrderId) FROM orders";
        try (Statement stmt = conn.createStatement(); 
        		  ResultSet rs = stmt.executeQuery(sql)){ 
        			if (rs.next()) {
        				max=rs.getInt("MAX(OrderId)");
        			}  
        } catch (SQLException e) {

			e.printStackTrace();
		}
        System.out.println("max order id is: " +max);
		return ++max;
	}
    /**
     * Saves the order details into the database and updates the corresponding capacity tables
     * 
     * @param conn              Connection object representing the database connection
     * @param parkname          String representing the name of the park
     * @param username          String representing the username
     * @param date              String representing the date of visit
     * @param time              String representing the time of visit
     * @param numberofvisitors  String representing the number of visitors
     * @param orderId           String representing the order ID
     * @param totalprice        String representing the total price
     * @param typeacc           String representing the type of account
     * @param reservationtype   String representing the reservation type
     * @param dwelltime         String representing the dwell time
     * @param email             String representing the email
     * @return int indicating the success of the operation (1 if successful, 0 otherwise)
     */
 	public static int saveOrder(Connection conn, String parkname, String username, String date, String time,
			String numberofvisitors, String orderId, String totalprice, String typeacc,String reservationtype,String dwelltime, String email) {
		System.out.println("typeacc = " +typeacc);
		int ordernumber=Integer.parseInt(orderId);
		String saletype="personal";
		String userid="";
		if(!typeacc.equals("guest")) {
			if(typeacc.equals("guide"))
				saletype="group";
			else{ 
				if(reservationtype.equals("group"))
					saletype="casual_group";
				else {saletype="casual_personal";
			//--------------------------------------------------------------------------------------------------------------------------------------------------
				}
			}
			String sqlusers="SELECT * FROM users WHERE Username = ?";
		    try (PreparedStatement pstmt = conn.prepareStatement(sqlusers)){
					pstmt.setString(1, username);
			        try(ResultSet rs = pstmt.executeQuery()) {
				           if (rs.next()) {	
				        	   userid=rs.getString("UserId");
				           }
				    }catch (SQLException e) {
				        System.out.println("DbController> Error fetching orders: " + e.getMessage());}    
			    } catch (SQLException e2) {
					e2.printStackTrace();}}
		else {userid=username;
			saletype="personal";}
	
	    String sql = "INSERT INTO orders (OrderId, ParkName, UserId, DateOfVisit, "
	    		+ "TimeOfVisit, NumberOfVisitors, IsConfirmed, IsVisit, IsCanceled, TotalPrice, IsInWaitingList, Email, SaleType) VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){//insert into orders table the order details
				pstmt.setInt(1, ordernumber);
				pstmt.setString(2, parkname);
				pstmt.setString(3, userid);
				pstmt.setString(4, date);
				pstmt.setString(5, time);
				pstmt.setString(6, numberofvisitors);
				pstmt.setString(7, "YES");
				pstmt.setString(8, "NO");
				pstmt.setString(9, "NO");
				pstmt.setString(10, totalprice);
				pstmt.setString(11, "NO");
				pstmt.setString(12, email);
				pstmt.setString(13, saletype);
				System.out.println("--------538---------------");

			    pstmt.executeUpdate();
			    
				System.out.println("--------539---------------");
				String dwell=checkDwell(conn,parkname);
			    int update = updateTotalTables(conn,parkname,date,time,numberofvisitors, typeacc,reservationtype,dwell,"+");
			    if((update==0)) {
			    	insertTotalTables(conn,parkname,date,time,numberofvisitors,typeacc,reservationtype,dwell,"+");
			    	return 1;}
			    /*else if(update==0){
					updateRowTotalTable(conn,parkname,date,time,numberofvisitors, "+",dwelltime);

			    }*/
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	   return 0; 
	}
    /**
     * Inserts the new order's details into the relevant capacity tables in the database
     * 
     * @param conn              Connection object representing the database connection
     * @param parkname          String representing the name of the park
     * @param date              String representing the date of visit
     * @param time              String representing the time of visit
     * @param numberofvisitors  String representing the number of visitors
     * @param typeacc           String representing the type of account
     * @param reservationtype   String representing the reservation type
     * @param dwelltime         String representing the dwell time
     * @param sign              String representing the sign for the capacity update (+ or -)
     */
	private static void insertTotalTables(Connection conn, String parkname, String date, String time,
		String numberofvisitors, String typeacc, String reservationtype, String dwelltime, String sign) {
//--------------------------------------------------------------------------------------------------------------------------------------------------
		System.out.println("its insertTotalTables here~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String t="t";
		int tfield=0;
		int ordertime = 0;
		int numbervisitors=Integer.parseInt(numberofvisitors);
		String sql = "";
		int dwell=Integer.parseInt(dwelltime);
		for (int i=8; i<20-dwell;i++) {
			if(time.contains(""+i)) {
				tfield=i;
				ordertime=i;
			}
		}
		System.out.println("typeacc= "+ typeacc);
    	switch(typeacc) {//updating table values with the new order number of visitors 
			case "customer":
			case "guest":
				sql="INSERT INTO park_used_capacity_individual (Parkname, date, " + t +tfield;
				for(int i=0;i<dwell-1;i++) {
					t="t";
					tfield++;
					sql+=", " + t +tfield;
				}
				sql+=") VALUES (?, ?, ";
				//sql+=") VALUES (?, ?, ?, ?, ?, ?)" ;
				System.out.println("dbcontroller sql query: " + sql);
				break;
			case "guide":
				sql="INSERT INTO park_used_capacity_groups (Parkname, date, " + t +tfield;
				for(int i=0;i<dwell-1;i++) {
					t="t";
					tfield++;
					sql+=", " + t +tfield;
				}
				sql+=") VALUES (?, ?, ";
				//sql+=") VALUES (?, ?, ?, ?, ?, ?)" ;

				break;
			case "park employee":
				if (reservationtype.equals("customer")) {
					sql="INSERT INTO park_used_capacity_individual (Parkname, date, " + t +tfield;
					for(int i=0;i<dwell-1;i++) {
						t="t";
						tfield++;
						sql+=", " + t +tfield;
					}
					sql+=") VALUES (?, ?, ";
					//sql+=") VALUES (?, ?, ?, ?, ?, ?)" ;}
				}
				else { sql="INSERT INTO park_used_capacity_groups (Parkname, date, " + t +tfield;
				for(int i=0;i<dwell-1;i++) {
					t="t";
					tfield++;
					sql+=", " + t +tfield;
				}
				
				sql+=") VALUES (?, ?, ";
				break;		
				}
    	}
    	System.out.println("lets continue sql query!!!\n sql= "+sql);
		for (int i=0;i<dwell;i++) {
			sql+="?";
			if(i<dwell-1)
				sql+=",";
		}
		sql+=")";
    	System.out.println("lets continue sql query2!!!\n sql= "+sql);	
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, parkname);
		    pstmt.setString(2, date);
			for(int i=3;i<dwell+3;i++) {
				pstmt.setInt(i, numbervisitors);}
		    // Execute the update using executeUpdate()
		    int affectedRows = pstmt.executeUpdate();
		    if (affectedRows > 0) {
		        System.out.println("Update capacity succeeded");
		    } else {
		        System.out.println("No rows were updated");
		    }
		} catch (SQLException e) {
		    System.out.println("DbController> Error fetching capacity tables from insertTotalTables: " + e.getMessage());
		}
		tfield=ordertime;
		if(checkRowTotalTable(conn,parkname,date)==1) {
			updateRowTotalTable(conn,parkname,date,time,numberofvisitors, "+","" +dwell);
			return ;
		}
		sql="INSERT INTO park_used_capacity_total (Parkname, date, " + t +tfield;
		for(int i=0;i<dwell-1;i++) {
			t="t";
			tfield++;
			sql+=", " + t +tfield;
		}
		sql+=") VALUES (?, ?, ";
		System.out.println("lets continue sql query3!!!\n sql= "+sql);
		for (int i=0;i<dwell;i++) {
			sql+="?";
			if(i<dwell-1)
				sql+=",";
		}
		sql+=")";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, parkname);
		    pstmt.setString(2, date);
			for(int i=3;i<dwell+3;i++) {
				pstmt.setInt(i, numbervisitors);}
		    // Execute the update using executeUpdate()
		    int affectedRows = pstmt.executeUpdate();
		    if (affectedRows > 0) {
		        System.out.println("Update capacity succeeded");
		    } else {
		        System.out.println("No rows were updated");
		    }
		} catch (SQLException e) {
		    System.out.println("DbController> Error fetching capacity tables: " + e.getMessage());
		}
    		
}
	
	/**
	 * Updates the capacity values for a specific park and date in the total capacity table
	 * 
	 * @param conn              Connection object representing the database connection
	 * @param parkname          String representing the name of the park
	 * @param date              String representing the date of the visit
	 * @param time              String representing the time of the visit
	 * @param numberofvisitors  String representing the number of visitors
	 * @param sign              String representing the sign for the capacity update (+ or -)
	 * @param dwelltime         String representing the dwell time
	 */
	private static void updateRowTotalTable(Connection conn, String parkname, String date, String time,
			String numberofvisitors,String sign,String dwelltime) {
		System.out.println("this is updateRowTotalTable");
		int dwell=Integer.parseInt(dwelltime);
		String t="t";
		int tfield=0;
		for (int i=8; i<20-dwell;i++) {
			if(time.contains(""+i)) {
				tfield=i;
			}
		}
		int numbervisitors=Integer.parseInt(numberofvisitors);
		String sql="UPDATE park_used_capacity_total SET " + t +tfield+ "= "+t+tfield  + sign +" ?" ;
		for(int i=0;i<dwell-1;i++) {
			t="t";
			tfield++;
			sql+=", " + t +tfield+ " = "+t+tfield+sign+"  ?" ;
		}
		sql+="  WHERE Parkname = ? AND date= ? " ;
		System.out.println("updateRowTotalTable> "+sql);
    	int cnt=1;
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			for(int i=1;i<dwell+1;i++) {
				pstmt.setInt(i, numbervisitors);
				cnt++;}
		    pstmt.setString((cnt), parkname);
		    pstmt.setString((cnt+1), date);

		    // Execute the update using executeUpdate()
		    int affectedRows = pstmt.executeUpdate();
		    if (affectedRows > 0) {
		        System.out.println("Update capacity on total table succeeded");
		    } else {
		        System.out.println("No rows were updated");
		    }
		} catch (SQLException e) {
		    System.out.println("DbController> Error fetching capacity tables: " + e.getMessage());
		}
		
	}
	/**
	 * Checks if there is a row for a specific park and date in the total capacity table
	 * 
	 * @param conn      Connection object representing the database connection
	 * @param parkname  String representing the name of the park
	 * @param date      String representing the date of the visit
	 * @return int indicating the existence of the row (1 if exists, 0 otherwise)
	 */
	private static int checkRowTotalTable(Connection conn, String parkname, String date) {
		String sql="SELECT * FROM park_used_capacity_total WHERE Parkname = ? AND date= ? " ;
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, parkname);
				pstmt.setString(2, date);
		        try(ResultSet rs = pstmt.executeQuery()) {
			           if (rs.next()) {	
			        	   System.out.println("there is a row on db");
			        	   return 1;//means there is a row on db with the park name and date
			           }
			    }catch (SQLException e) {
			        System.out.println("DbController> Error fetching checking on totla table row: " + e.getMessage());}    
		    } catch (SQLException e2) {
				e2.printStackTrace();}
	    return 0;
	}
	/**
	 * Checks if there is an existing row for a specific park, date, and account type in the capacity tables
	 * 
	 * @param conn              Connection object representing the database connection
	 * @param typeaccount       String representing the type of account
	 * @param reservationtype   String representing the reservation type
	 * @param parkname          String representing the name of the park
	 * @param date              String representing the date of the visit
	 * @return int indicating the existence of the row (1 if exists, 0 otherwise)
	 */
	private static int checkRowExist(Connection conn,String typeaccount,String reservationtype,String parkname,String date) {
		String sql="";
        System.out.println("String typeAccount: " + typeaccount + ", String reservationType: " + reservationtype +
                ", String parkName: " + parkname + ", String date: " + date);
		switch(typeaccount) {// checking if there is any orders at all on this date and park
		case "customer":
		case "guest":
			sql="SELECT * FROM park_used_capacity_individual WHERE Parkname = ? AND date= ? " ;
			break;
		case "guide":
			sql="SELECT * FROM park_used_capacity_groups WHERE Parkname = ? AND date= ? " ;
			break;
		case "park employee":
			if (reservationtype.equals("customer"))
				sql="SELECT * FROM park_used_capacity_individual  WHERE Parkname = ? AND date= ? " ;
			else sql="SELECT * FROM park_used_capacity_groups  WHERE Parkname = ? AND date= ? " ;
			break;	
		}
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, parkname);
				pstmt.setString(2, date);
		        try(ResultSet rs = pstmt.executeQuery()) {
			           if (rs.next()) {	
			        	   return 1;//means there is a row on db with the park name and date
			           }
			    }catch (SQLException e) {
			        System.out.println("DbController> Error fetching checking row: " + e.getMessage());}    
		    } catch (SQLException e2) {
				e2.printStackTrace();}
	    return 0;
	}	
	/**
	 * Updates the capacity values in both individual and total capacity tables based on the order details
	 * 
	 * @param conn              Connection object representing the database connection
	 * @param parkname          String representing the name of the park
	 * @param date              String representing the date of the visit
	 * @param time              String representing the time of the visit
	 * @param numberofvisitors  String representing the number of visitors
	 * @param typeaccount       String representing the type of account
	 * @param reservationtype   String representing the reservation type
	 * @param dwelltime         String representing the dwell time
	 * @param sign              String representing the sign for the capacity update (+ or -)
	 * @return int indicating the success of the operation (1 if successful, 0 otherwise)
	 */
	private static int updateTotalTables(Connection conn, String parkname, String date, String time,
			String numberofvisitors, String typeaccount,String reservationtype,String dwelltime, String sign) {
		System.out.println("im h121 in updateTotalTables~~~~~~~~~~~~~~~``");

		String t="t";
		int tfield=0;
		int ordertime = 0;
		int numbervisitors=Integer.parseInt(numberofvisitors);
		String sql = "";
		System.out.println("im h222 in updateTotalTables~~~~~~~~~~~~~~~``");
		System.out.println("string dwell = " +dwelltime);
		int dwell=Integer.parseInt(dwelltime);
		System.out.println("dwell is: "+dwell +"parse = " +Integer.parseInt(dwelltime));
		for (int i=8; i<20-dwell;i++) {
			if(time.contains(""+i)) {
				System.out.println("im "+i+" in updateTotalTables~~~~~~~~~~~~~~~``");
				tfield=i;
				ordertime=i;
			}
		}
		System.out.println("im hereeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in updateTotalTables~~~~~~~~~~~~~~~``");
		System.out.println(typeaccount);
		if(checkRowExist(conn,typeaccount,reservationtype, parkname, date)==1) {
	    	switch(typeaccount) {//updating table values with the new order number of visitors 
				case "customer":
				case "guest":
					sql="UPDATE park_used_capacity_individual SET " + t +tfield+ "= "+t+tfield  + sign +" ?" ;
					for(int i=0;i<dwell-1;i++) {
						t="t";
						tfield++;
						sql+=", " + t +tfield+ " = "+t+tfield+sign+"  ?" ;
					}
					sql+="  WHERE Parkname = ? AND date= ? " ;
					System.out.println("dbcontroller sql query: " + sql);
					break;
				case "guide":
					sql="UPDATE park_used_capacity_groups SET " + t +tfield+ "= "+t+tfield  + sign +" ?" ;
					for(int i=0;i<dwell-1;i++) {
						t="t";
						tfield++;
						sql+=", " + t +tfield+ " = "+t+tfield+sign+"  ?" ;
					}
					sql+="  WHERE Parkname = ? AND date= ? " ;

					break;
				case "park employee":
					if (reservationtype.equals("customer")) {
						sql="UPDATE park_used_capacity_individual SET " + t +tfield+ "= "+t+tfield  + sign +" ?" ;
						for(int i=0;i<dwell-1;i++) {
							t="t";
							tfield++;
							sql+=", " + t +tfield+ " = "+t+tfield+sign+"  ?" ;
						}
						sql+="  WHERE Parkname = ? AND date= ? " ;}
					else { sql="UPDATE park_used_capacity_groups SET " + t +tfield+ "= "+t+tfield  + sign +" ?" ;
					for(int i=0;i<dwell-1;i++) {
						t="t";
						tfield++;
						sql+=", " + t +tfield+ " = "+t+tfield+sign+"  ?" ;
					}
					sql+="  WHERE Parkname = ? AND date= ? " ;}
					break;		
			}
	    	int cnt=1;
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				for(int i=1;i<dwell+1;i++) {
					pstmt.setInt(i, numbervisitors);
					cnt++;}
			    pstmt.setString((cnt), parkname);
			    pstmt.setString((cnt+1), date);

			    // Execute the update using executeUpdate()
			    int affectedRows = pstmt.executeUpdate();
			    if (affectedRows > 0) {
			        System.out.println("Update capacity succeeded");
			    } else {
			        System.out.println("No rows were updated");
			    }
			} catch (SQLException e) {
			    System.out.println("DbController> Error fetching capacity tables: " + e.getMessage());
			}
			//updating the total capacity table
			tfield=ordertime;
			sql="UPDATE park_used_capacity_total SET " + t +tfield+ "= "+t+tfield  + sign +" ?" ;
			for(int i=0;i<dwell-1;i++) {
				t="t";
				tfield++;
				sql+=", " + t +tfield+ " = "+t+tfield+sign+"  ?" ;
			}
			sql+="  WHERE Parkname = ? AND date= ? " ;
			System.out.println("dbcontroller total sql query: "+ sql);
			cnt=1;
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				for(int i=1;i<dwell+1;i++) {
					pstmt.setInt(i, numbervisitors);
					cnt++;}
			    pstmt.setString((cnt), parkname);
			    pstmt.setString((cnt+1), date);

			    // Execute the update using executeUpdate()
			    int affectedRows = pstmt.executeUpdate();
			    if (affectedRows > 0) {
			        System.out.println("Update total capacity table succeeded");
			        return 1;
			    } else {
			        System.out.println("No rows were updated");
			    }
			} catch (SQLException e) {
			    System.out.println("DbController> Error fetching capacity total table: " + e.getMessage());
			}
	    }
		System.out.println("no existing row-------------------------------------------");
		return 0;
	}

	
	/**
	 * Checks the dwell time limit for a specific park.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkname  String representing the name of the park
	 * @return String representing the dwell time limit for the park
	 */
	public static String checkDwell(Connection conn, String parkname) {// checking how many hours can visitor visit in park
		String dwell ="";
		String sql ="SELECT visitTimeLimit FROM park WHERE Parkname= ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, parkname);
		        try(ResultSet rs = pstmt.executeQuery()) {
			           if (rs.next()) {	
			        	   dwell=rs.getString("visitTimeLimit");
			           }
			    }catch (SQLException e) {
			        System.out.println("DbController> Error fetching capacity total: " + e.getMessage());}    
		    } catch (SQLException e2) {
				e2.printStackTrace();}
		return dwell;
	}
	/**
	 * Loads orders for the approval table based on the user ID.
	 *
	 * @param conn  Connection object representing the database connection
	 * @param userid    String representing the user ID
	 * @return ArrayList of Order objects representing orders for approval
	 */
	public static ArrayList<Order> loadOrderForApproveTable(Connection conn, String userid) {
        ArrayList<Order> approveOrderList = new ArrayList<>();
        String sql = "SELECT orderId, parkName, dateOfVisit, timeOfVisit, numberOfVisitors FROM orders WHERE userId = ? AND IsInWaitingList = ? AND IsCanceled = ? AND "
        		+ "IsVisit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userid);
            pstmt.setString(2, "NO"); 
            pstmt.setString(3, "NO");  
            pstmt.setString(4, "NO"); 
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getString("orderId"),
                        rs.getString("parkName"),
                        rs.getString("dateOfVisit"),
                        rs.getString("timeOfVisit"),
                        rs.getString("numberOfVisitors")
                    );
                    approveOrderList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return approveOrderList;
    }
	/**
	 * Loads orders for the waiting list table based on the user ID.
	 *
	 * @param conn  Connection object representing the database connection
	 * @param userid    String representing the user ID
	 * @return ArrayList of Order objects representing orders for the waiting list
	 */
	public static ArrayList<Order> loadOrderForWaitingListTable(Connection conn, String userid) {
        ArrayList<Order> waitingListOrdersList = new ArrayList<>();
        String sql = "SELECT orderId, parkName, dateOfVisit, timeOfVisit, numberOfVisitors FROM orders WHERE userId = ? AND IsInWaitingList = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userid);
            pstmt.setString(2, "YES"); 
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getString("orderId"),
                        rs.getString("parkName"),
                        rs.getString("dateOfVisit"),
                        rs.getString("timeOfVisit"),
                        rs.getString("numberOfVisitors")
                    );
                    waitingListOrdersList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return waitingListOrdersList;
	}
	/**
	 * Retrieves the user ID based on the username.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param username  String representing the username
	 * @return String representing the user ID
	 */
	public static String checkUserId(Connection conn, String username) {
		System.out.println("the user name is: " + username);
		String sql = "SELECT UserId FROM Users WHERE username = ?";
		 String userid="";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, username); 
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next()) {
		        userid = rs.getString("UserId");
		        //System.out.println("user id is: " + userid);
		        System.out.println("The UserId is: " + userid);
		    } else {
		        System.out.println("No user found with the provided user.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
	}
		return userid;
	}
	/**
	 * Deletes an order from the database.
	 *
	 * @param conn              Connection object representing the database connection
	 * @param orderid           String representing the ID of the order to be deleted
	 * @param typeaccount       String representing the type of account
	 * @param reservationtype   String representing the reservation type
	 * @return String indicating the status of the deletion operation ("succeed" or "failed")
	 */
	public static String DeleteOrder(Connection conn, String orderid, String typeaccount, String reservationtype) {
		int delete=0;
		String parkname="";
		String date="";
		String time="";
		String numberofvisitors="";
		String iswaitinglist="";
		String sql="SELECT * FROM orders WHERE OrderId = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, orderid); 
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next()) {
		    	parkname = rs.getString("ParkName");
		    	date = rs.getString("DateOfVisit");
		    	time = rs.getString("TimeOfVisit");
		    	iswaitinglist=rs.getString("IsInWaitingList");
		    	numberofvisitors = rs.getString("NumberOfVisitors");
		    } else {
		        System.out.println("DbController> failed to delete order.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();}
		String dwell=checkDwell(conn, parkname);
		//---------------------------------------------------------------------------------------------------------------------------------
		String sqlupdate = "UPDATE orders SET IsCanceled = ?, IsInWaitingList = ? WHERE OrderId = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sqlupdate)) {
			pstmt.setString(1, "YES");
			pstmt.setString(2, "NO");
			pstmt.setString(3, orderid); // Replace orderId with the actual OrderId you want to cancel

		    int rowsAffected = pstmt.executeUpdate();
		    if (rowsAffected > 0) {
		    	delete=1;
		        System.out.println("Order canceled successfully.");
		    } else {
		        System.out.println("No row found with the specified OrderId.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
		if(iswaitinglist.equals("NO"))
			updateTotalTables(conn, parkname, date, time, numberofvisitors, typeaccount, reservationtype, dwell, "-");	
		if (delete==1)
			return "succeed";
		return "failed";
	}
	/**
	 * Deletes an order automatically from the database.
	 *
	 * @param conn              Connection object representing the database connection
	 * @param orderid           String representing the ID of the order to be deleted
	 * @param typeaccount       String representing the type of account
	 * @param reservationtype   String representing the reservation type
	 * @return String indicating the status of the deletion operation ("succeed" or "failed")
	 */
	public static String DeleteOrderAuto(Connection conn, String orderid, String typeaccount, String reservationtype) {
		int delete=0;
		String parkname="";
		String date="";
		String time="";
		String numberofvisitors="";
		String iswaitinglist="";
		String isvisit="";
		String sql="SELECT * FROM orders WHERE OrderId = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, orderid); 
		    ResultSet rs = pstmt.executeQuery();
		    if (rs.next()) {
		    	parkname = rs.getString("ParkName");
		    	date = rs.getString("DateOfVisit");
		    	time = rs.getString("TimeOfVisit");
		    	isvisit=rs.getString("IsVisit");
		    	iswaitinglist=rs.getString("IsInWaitingList");
		    	numberofvisitors = rs.getString("NumberOfVisitors");
		    } else {
		        System.out.println("DbController> failed to delete order.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();}
		String dwell=checkDwell(conn, parkname);
		//---------------------------------------------------------------------------------------------------------------------------------
		String sqlupdate = "UPDATE orders SET IsCanceled = ?, IsInWaitingList = ?, IsCanceledAuto = ? WHERE OrderId = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sqlupdate)) {
			pstmt.setString(1, "YES");
			pstmt.setString(2, "NO");
			pstmt.setString(3, "YES");
			pstmt.setString(4, orderid); // Replace orderId with the actual OrderId you want to cancel

		    int rowsAffected = pstmt.executeUpdate();
		    if (rowsAffected > 0) {
		    	delete=1;
		        System.out.println("Order canceled successfully.");
		    } else {
		        System.out.println("No row found with the specified OrderId.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
		int update=0;
		if(iswaitinglist.equals("NO")) {
			update =updateTotalTables(conn, parkname, date, time, numberofvisitors, typeaccount, reservationtype, dwell, "-");	}
		if (delete==1) {
			return "succeed";}
		return "failed";
	}
	/**
	 * Updates the waiting list status of an order in the database.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param orderid   String representing the ID of the order
	 */
	public static void updateWaitingList(Connection conn, String orderid) {
		String sqlupdate = "UPDATE orders SET IsConfirmed = ? ,IsInWaitingList = ? WHERE OrderId = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sqlupdate)) {
		    pstmt.setString(1, "NO");
			pstmt.setString(2, "YES");
			pstmt.setString(3, orderid); // Replace orderId with the actual OrderId you want to cancel
		    int rowsAffected = pstmt.executeUpdate();
		    if (rowsAffected > 0) {
		        System.out.println("Row deleted successfully.");
		    } else {
		        System.out.println("No row found with the specified OrderId.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	/**
	 * Synchronizes users from an external source to the local database.
	 *
	 * @param MyUrl             String representing the URL of the local database
	 * @param MyPassword        String representing the password of the local database
	 * @param MyUserName        String representing the username of the local database
	 * @param ExternalUrl       String representing the URL of the external database
	 * @param ExternalPassword  String representing the password of the external database
	 * @param ExternalUserName  String representing the username of the external database
	 */
	public static void synchronizeUsers(String MyUrl, String MyPassword, String MyUserName, String ExternalUrl, String ExternalPassword, String ExternalUserName) {
        String selectQuery = "SELECT Fname, Lname, Email, OrganizationalRoleOrAffiliation, EmployeeNumber, Username, Password FROM usermanagement.users_external";
        String mergeQuery = "INSERT INTO gonaturedb.users (UserId, Fname, Lname, Username, Password, Email, TypeUser) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "UserId = VALUES(UserId), " +
                            "Lname = VALUES(Lname), " +
                            "Fname = VALUES(Fname), " +
                            "Email = VALUES(Email), " +
                            "TypeUser = VALUES(TypeUser), " +
                            "Password = VALUES(Password), " +
                            "Username = VALUES(Username)";

        ResultSet resultSet = null;
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");

            conn = DriverManager.getConnection(MyUrl, MyUserName, MyPassword);

            PreparedStatement selectStatement = null;
            PreparedStatement mergeStatement = null;

            try {
                Connection externalConn = DriverManager.getConnection(ExternalUrl, ExternalUserName, ExternalPassword);

                selectStatement = externalConn.prepareStatement(selectQuery);
                resultSet = selectStatement.executeQuery();

                mergeStatement = conn.prepareStatement(mergeQuery);

                while (resultSet.next()) {
                    int userId = resultSet.getInt("EmployeeNumber");
                    String fname = resultSet.getString("Fname");
                    String lname = resultSet.getString("Lname");
                    String email = resultSet.getString("Email");
                    String typeUser = resultSet.getString("OrganizationalRoleOrAffiliation");
                    String username = resultSet.getString("Username");
                    String password = resultSet.getString("Password");

                    mergeStatement.setInt(1, userId);
                    mergeStatement.setString(2, fname);
                    mergeStatement.setString(3, lname);
                    mergeStatement.setString(4, username);
                    mergeStatement.setString(5, password);
                    mergeStatement.setString(6, email);
                    mergeStatement.setString(7, typeUser); 

                    mergeStatement.executeUpdate();
                }
                System.out.println("Data import and update completed successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) resultSet.close();
                if (selectStatement != null) selectStatement.close();
                if (mergeStatement != null) mergeStatement.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Driver definition failed");
        }
    
	}
	
	/**
	 * Updates the role of a user to "guide" in the database.
	 *
	 * @param conn  Connection object representing the database connection
	 * @param id    String representing the ID of the user
	 * @return      boolean indicating whether the update was successful
	 */
	public static boolean updateGuideRole(Connection conn, String id) {
		String sql = "UPDATE users SET TypeUser = ? WHERE UserId = ? AND (TypeUser IS NULL OR TypeUser = '')";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, "guide");
	        pstmt.setString(2, id);
	        //pstmt.setString(3, "customer");
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) { 
	        	return true; 
	        }
	        return false;
	    } catch (SQLException e) {
	        System.out.println("dbController> Error updating user logout status: " + e.getMessage());
	    }
	    return false;
	}
	
	/**
	 * Retrieves the maximum capacity of a park from the database.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkName  String representing the name of the park
	 * @return          int representing the maximum capacity of the park
	 */
	public static int getParkMaxCapacity(Connection conn, String parkName) {
	    int maxCapacity = 0;

	    try (PreparedStatement stmt = conn.prepareStatement("SELECT CapacityOfVisitors FROM gonaturedb.park WHERE Parkname=?")) {
	        stmt.setString(1, parkName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                maxCapacity = rs.getInt("CapacityOfVisitors");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return maxCapacity;
	}
	
	
	
	
	
	/**
	 * Retrieves the visit time limit of a park from the database.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkName  String representing the name of the park
	 * @return          int representing the visit time limit of the park
	 */
	public static int getParkVisitTimeLimit(Connection conn, String parkName) {
		int visitTimeLimit = 0;
		try (PreparedStatement stmt = conn.prepareStatement("SELECT visitTimeLimit FROM gonaturedb.park WHERE Parkname=?")) {
	        stmt.setString(1, parkName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                visitTimeLimit = rs.getInt("visitTimeLimit");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return visitTimeLimit;
		
	}
	
	/**
	 * Retrieves the total number of group visitors and personal visitors for a specified park, month, and year.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkName  String representing the name of the park
	 * @param month     int representing the month (1-12)
	 * @param year      int representing the year
	 * @return          int[] containing total_group_visitors and total_personal_visitors
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
    public static int[] getTotalVisitorsByYearAndMonth(Connection conn, String parkName, int month, int year) throws SQLException {
        int[] visitor = new int[2]; // Array to store total_group_visitors and total_personal_visitors

        String sql = "SELECT " +
                     "SUM(CASE WHEN SaleType = 'group' OR SaleType = 'casual_group' THEN NumberOfVisitors ELSE 0 END) AS total_group_visitors, " +
                     "SUM(CASE WHEN SaleType = 'personal' OR SaleType = 'casual_personal' THEN NumberOfVisitors ELSE 0 END) AS total_personal_visitors " +
                     "FROM orders " +
                     "WHERE ParkName = ? AND YEAR(dateOfVisit) = ? AND MONTH(dateOfVisit) = ? AND  IsConfirmed= ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parkName);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            stmt.setString(4, "YES");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            	visitor[0] = rs.getInt("total_group_visitors");
            	visitor[1] = rs.getInt("total_personal_visitors");
            }
        }
        
        System.out.println("DbController>visitor=total_group_visitors = "+visitor[0]+" total_personal_visitors="+visitor[1]);
        return visitor;
    }
    
    
    /**
     * Retrieves the day of the month from a given date string.
     *
     * @param dateString    String representing the date in "yyyy-MM-dd" format
     * @return              String representing the day of the month (formatted with leading zeros if necessary)
     */
    
    public static String getDayOfMonth(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        System.out.println(date);
        String dayOfMonth = String.format("%02d", date.getDayOfMonth());
        System.out.println(dayOfMonth);
        
        return dayOfMonth;
    }
    
    /**
     * Retrieves usage data for a specified park, month, and year.
     *
     * @param conn      Connection object representing the database connection
     * @param parkName  String representing the name of the park
     * @param month     int representing the month (1-12)
     * @param year      int representing the year
     * @return          int[][] containing usage data for each day and hour
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    public static int[][] getUsageByYearAndMonth(Connection conn, String parkName, int month, int year) throws SQLException {
    	//seperate sql statmente toget the max capacity of the park 
    	 int maxCapacity = 0;
    	 String query = "SELECT CapacityOfVisitors FROM gonaturedb.park WHERE ParkName = ?";
    	 try (PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setString(1, parkName);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                	 maxCapacity = rs.getInt("CapacityOfVisitors");
                 }
             }
         }
    	System.out.println("DbController>The max capacity in park"+parkName+" is "+maxCapacity);

    	String sql = "SELECT * FROM gonaturedb.park_used_capacity_total WHERE YEAR(date) = ? AND MONTH(date) = ? AND Parkname = ? ORDER BY DATE_FORMAT(date, '%Y-%m-%d')";
    	int[][] matrixRes = new int[31][12];//31 days in month and 12 hours
    	try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            stmt.setString(3, parkName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	int dayOfMonth=Integer.parseInt(getDayOfMonth(rs.getString("date")));
                int[] hourValues = {
                    rs.getInt("t8"), rs.getInt("t9"), rs.getInt("t10"),
                    rs.getInt("t11"), rs.getInt("t12"), rs.getInt("t13"),
                    rs.getInt("t14"), rs.getInt("t15"), rs.getInt("t16"),
                    rs.getInt("t17"), rs.getInt("t18"), rs.getInt("t19")
                };
				/*
				 * System.out.println("dayOfMonth "+dayOfMonth); for (int i = 0; i <
				 * hourValues.length; i++) { System.out.print(", "+hourValues[i]); }
				 * System.out.println();
				 */
                
                // Update the matrix hour values
                for (int i = 0; i < hourValues.length; i++) {
                    if (hourValues[i] < maxCapacity) {
                    	if( matrixRes[dayOfMonth - 1][i]!=1)
                        matrixRes[dayOfMonth - 1][i] = 1; // Set value to 1 if less than maxCapacity
                    } else {
                        matrixRes[dayOfMonth - 1][i] = 0; // Set value to 0 if not
                    }
                }
            }
        }
    	catch (SQLException e) {
    		 System.out.println("error "+e.getMessage());
		}
    	
    	for (int i = 0; i < 31; i++) {
    	    System.out.print("Day " + (i + 1) + ": ");
    	    for (int j = 0; j < 12; j++) {
    	        System.out.print(matrixRes[i][j] + " ");
    	    }
    	    System.out.println();
    	}
        return matrixRes;

    }

    /**
     * Retrieves the names of parks managed by a park manager.
     *
     * @param conn      Connection object representing the database connection
     * @param username  String representing the username of the park manager
     * @return          String[] containing the names of parks managed by the park manager
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     */
    public static String[] getParksMangedByParkManger(Connection conn, String username) throws SQLException {
	    ArrayList<String> parkNames = new ArrayList<>();
	    String query = "SELECT parkname FROM gonaturedb.park " +
	                   "WHERE parkmangerid = (SELECT userid FROM gonaturedb.users WHERE username = ?)";

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, username);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                parkNames.add(rs.getString("parkname"));
	            }
	        }
	    }

	    // Convert the ArrayList to an array of strings
	    
	    String[] parkNamesArray = new String[parkNames.size()];
	    
	    for (int i = 0; i < parkNames.size(); i++) {
	    	parkNamesArray[i] = parkNames.get(i);
        }

	    return parkNamesArray;
	}
    /**
     * Retrieves the number of visitors for each hour of a specified date in a park using group time entry.
     *
     * @param conn          Connection object representing the database connection
     * @param parkName      String representing the name of the park
     * @param chosenDate    LocalDate object representing the chosen date
     * @return              int[] containing the number of visitors for each hour
     */
	public static int[] getGroupTimeEntryVisitors(Connection conn, String parkName, LocalDate chosenDate) {
	    int[] GroupTimeEntryVisitors = new int[12]; 

	    String sql = "SELECT t8,t9,t10,t11,t12,t13,t14,t15,t16,t17,t18,t19 FROM gonaturedb.park_used_capacity_groups WHERE Parkname=? AND date=?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, parkName);
	        stmt.setDate(2, java.sql.Date.valueOf(chosenDate));

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                for (int i = 0; i < 12; i++) {
	                	GroupTimeEntryVisitors[i] = rs.getInt(i + 1); 
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return GroupTimeEntryVisitors;
	}

	/**
	 * Retrieves the number of visitors for each hour of a specified date in a park using individual time entry.
	 *
	 * @param conn          Connection object representing the database connection
	 * @param parkName      String representing the name of the park
	 * @param chosenDate    LocalDate object representing the chosen date
	 * @return              int[] containing the number of visitors for each hour
	 */
	public static int[] getIndTimeEntryVisitors(Connection conn, String parkName, LocalDate chosenDate) {
		int[] IndTimeEntryVisitors = new int[12]; 

	    String sql = "SELECT t8,t9,t10,t11,t12,t13,t14,t15,t16,t17,t18,t19 FROM gonaturedb.park_used_capacity_individual WHERE Parkname=? AND date=?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, parkName);
	        stmt.setDate(2, java.sql.Date.valueOf(chosenDate));

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                for (int i = 0; i < 12; i++) {
	                	IndTimeEntryVisitors[i] = rs.getInt(i + 1); 
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return IndTimeEntryVisitors;
	}

	/*public static int registerUser(Connection conn, String fname, String lname, String username, String password,
			String email, String telephone) {
		int userid=checkMaxUserId(conn);
		userid++;
		String sql = "INSERT INTO users (UserId, Fname, Lname, Username, Password, Email, PhoneNumber, TypeUser, IsLogged) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, userid);
	        pstmt.setString(2, fname);
	        pstmt.setString(3, lname);
	        pstmt.setString(4, username);
	        pstmt.setString(5, password);
	        pstmt.setString(6, email);
	        pstmt.setString(7, telephone);
	        pstmt.setString(8, "customer");
	        pstmt.setString(9, "0");
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) { 
	        	System.out.println("DbController> userRegistration succeed");
	        	return 1; 
	        }
	    }catch (SQLException e) {
	        e.printStackTrace();
	    }

		return 0;
	}*/
	
	/*public static int checkMaxUserId(Connection conn) {//checking the max order number 
		int max=1;
		String sql="SELECT MAX(CAST(UserId AS UNSIGNED)) FROM users";
        try (Statement stmt = conn.createStatement(); 
        		  ResultSet rs = stmt.executeQuery(sql)){ 
        			if (rs.next()) {
        				max=rs.getInt("MAX(CAST(UserId AS UNSIGNED))");
        			}  
        } catch (SQLException e) {

			e.printStackTrace();
		}
        System.out.println("max order id is: " +max);
		return max;
	}*/
	
	/**
	 * Checks if a user exists in the external users table.
	 *
	 * @param conn  Connection object representing the database connection
	 * @param id    String representing the user ID
	 * @return      int indicating whether the user exists (1) or not (0)
	 */
	public static int checkExternalUser(Connection conn, String id) {
	    String sql = "SELECT * FROM external_users WHERE UserId = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) { // If rs.next() returns true, then there is at least one row in the result set
	                System.out.println("DbController> externalUser exists");
	                return 1;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}
	/**
	 * Adds a user to the external users table.
	 *
	 * @param conn  Connection object representing the database connection
	 * @param id    String representing the user ID
	 * @return      int indicating whether the user was successfully added (1) or not (0)
	 */
	public static int addExternalUser(Connection conn, String id) {
		String sql = "INSERT INTO external_users (UserId) VALUES (?);";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		    pstmt.setString(1, id);
		    pstmt.executeUpdate();
		    return 1;
		} catch (SQLException e) {
		    e.printStackTrace();
	}
		return 0;
	}
	
	/**
	 * Imports sample data into the database.
	 *
	 * @param conn  Connection object representing the database connection
	 */
	public static void importData(Connection conn) {
	    String sql = "INSERT INTO gonaturedb.users (UserId, Fname, Lname, Username, Password, Email, PhoneNumber, TypeUser)"
	            +"VALUES (3148138267, 'Ava', 'Smith', 'depm', '123456', 'avasmith@gmail.com', '0522222222', 'department manager'),"
	            +"(314, 'Liam', 'Johnson', 'guide4', '123456', 'liamjohnson@gmail.com', '0523333333', ''),"
	            +"(3148, 'Olivia', 'Williams', 'service1', '123456', 'oliviawilliams@gmail.com', '0524444444', 'service employee'),"
	            +"(31481, 'Noah', 'Brown', 'guide', '123456', 'noahbrown@gmail.com', '0525555555', ''),"
	            +"(314813, 'Emma', 'Jones', 'guide21', '123456', 'emmajones@gmail.com', '0526666666', ''),"
	            +"(3148138, 'Oliver', 'Garcia', 'guide31', '123456', 'olivergarcia@gmail.com', '0527777777', ''),"
	            +"(314813826, 'Ava', 'Smith', 'yaarhakofimm', '123456', 'avasmith@gmail.com', '0522222222', 'park manager'),"
	            +"(3148555267, 'Liam', 'Johnson', 'safarim', '123456', 'liamjohnson@gmail.com', '0523333333', 'park manager'),"
	            +"(314813828, 'Olivia', 'Williams', 'service', '123456', 'oliviawilliams@gmail.com', '0524444444', 'service employee'),"
	            +"(314813829, 'Noah', 'Brown', 'gantanahim', '123456', 'noahbrown@gmail.com', '0525555555', 'park manager'),"
	            +"(314813830, 'Emma', 'Jones', 'guide2', '123456', 'emmajones@gmail.com', '0526666666', ''),"
	            +"(314813831, 'Oliver', 'Garcia', 'guide3', '123456', 'olivergarcia@gmail.com', '0527777777', ''),"
	            +"(5, 'israel', 'israeli', 'parke', '123456', 'parke@gmail.com', '02522555546', 'park employee')";
	    try (Statement stmt = conn.createStatement()) {
	        int rowsAffected = stmt.executeUpdate(sql); 
	        System.out.println(rowsAffected + " rows inserted.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	/**
	 * Requests to change the visit time for a specified park.
	 * If no row exists with the same parkname and NULL max_cap, inserts a new row with the given parkname and visitTime.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkname  String representing the name of the park
	 * @param visitTime String representing the new visit time
	 */
	public static void requastToChangevisit(Connection conn, String parkname, String visitTime) {
		// Step 1: Check for an existing row
	    String checkSql = "SELECT 1 FROM visit_time_max_table WHERE parkname = ? AND max_cap IS NULL";
	    boolean exists = false;

	    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
	        checkStmt.setString(1, parkname);
	        try (ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next()) {
	                exists = true;  // A matching row exists
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Error checking for existing row: " + e.getMessage());
	    }

	    // Step 2: Insert a new row if no matching row exists
	    if (!exists) {
	        String insertSql = "INSERT INTO visit_time_max_table (parkname, visit_time, max_cap) VALUES (?, ?, NULL)";
	        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
	            insertStmt.setString(1, parkname);
	            insertStmt.setString(2, visitTime);

	            insertStmt.executeUpdate();
	            System.out.println("Inserted new row for park: " + parkname);
	        } catch (SQLException e) {
	            System.out.println("Error inserting new row: " + e.getMessage());
	        }
	    } else {
	        System.out.println("A row with the same parkname and NULL max_cap already exists. Skipping insertion.");
	    }
	}
	/**
	 * Requests to change the maximum capacity for a specified park.
	 * If no row exists with the same parkname and NULL visit_time, inserts a new row with the given parkname and maxCapacity.
	 *
	 * @param conn          Connection object representing the database connection
	 * @param parkname      String representing the name of the park
	 * @param maxCapacity   String representing the new maximum capacity
	 */
	public static void requastToChangeMaxCapcitiy(Connection conn, String parkname, String maxCapacity) {
	    // Step 1: Check for an existing row with the same parkname and NULL visit_time
	    String checkSql = "SELECT 1 FROM visit_time_max_table WHERE parkname = ? AND visit_time IS NULL";
	    boolean exists = false;

	    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
	        checkStmt.setString(1, parkname);
	        try (ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next()) {
	                exists = true;  // A matching row exists
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Error checking for existing row: " + e.getMessage());
	    }

	    // Step 2: Insert a new row only if no matching row exists
	    if (!exists) {
	        String insertSql = "INSERT INTO visit_time_max_table (parkname, visit_time, max_cap) VALUES (?, NULL, ?)";
	        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
	            insertStmt.setString(1, parkname);  // Set parkName
	            insertStmt.setString(2, maxCapacity);  // Set maxCapacity

	            insertStmt.executeUpdate();
	            System.out.println("Inserted new row for park: " + parkname);
	        } catch (SQLException e) {
	            System.out.println("Error occurred during insertion: " + e.getMessage());
	        }
	    } else {
	        System.out.println("A row with the same parkname and NULL visit_time already exists. Skipping insertion.");
	    }
	}
	/**
	 * Loads parks that have requested a visit time change.
	 *
	 * @param conn  Connection object representing the database connection
	 * @return      ArrayList of ParkForChange objects representing parks with requested visit time changes
	 */
	public static ArrayList<ParkForChange> LoadparkForChangevisittime(Connection conn) {

		ArrayList<ParkForChange> sendarrArrayList = new ArrayList<>();
	    String sql = "SELECT parkName, visit_time FROM visit_time_max_table WHERE visit_time IS NOT NULL";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            String parkName = rs.getString("parkName");
	            String visitTimeafter = rs.getString("visit_time");
			    String sql1 = "SELECT visitTimeLimit FROM park WHERE parkName = ?";
			    try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
			        pstmt1.setString(1, parkName);
			        ResultSet rs1 = pstmt1.executeQuery();

			        if (rs1.next()) {
			            String visitTimeLimitbefore = rs1.getString("visitTimeLimit");
			             //public ParkForChange(String parkName, String dwellBefore, String dwellAfter, String maxCapacityBefore, String maxCapacityAfter) 
			            ParkForChange parktoaddChange = new ParkForChange(parkName, visitTimeLimitbefore, visitTimeafter, null, null);
			            sendarrArrayList.add(parktoaddChange);
			        }
			        
			    } 
			    catch (SQLException e) {
			        e.printStackTrace();
			    }  
	        }
	    } 
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	   	return sendarrArrayList;
	}
	/**
	 * Approves a requested visit time change for a specified park.
	 *
	 * @param conn          Connection object representing the database connection
	 * @param parkname      String representing the name of the park
	 * @param newDwellTime  String representing the new visit time to be approved
	 */
	public static void approveVisitTime(Connection conn, String parkname, String newdwelltime) {
		String sql = "UPDATE park SET visitTimeLimit = ? WHERE ParkName = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        // Set the parameters for the query
	        pstmt.setString(1, newdwelltime);  // Set new visit time limit
	        pstmt.setString(2, parkname);  // Set the park name

	        // Execute the update
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    String sql1 = "DELETE FROM visit_time_max_table WHERE parkName = ? AND visit_time = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
	        // Set the parameters for the query
	        pstmt.setString(1, parkname);  // Set the park name
	        pstmt.setString(2, newdwelltime);  // Set the visit time

	        // Execute the deletion
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}
	/**
	 * Declines a requested visit time change for a specified park.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkname  String representing the name of the park
	 * @param visitTime String representing the visit time to be declined
	 */
	public static void decline(Connection conn, String parkname, String visittime) {
	    String sql1 = "DELETE FROM visit_time_max_table WHERE parkName = ? AND visit_time = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
	        // Set the parameters for the query
	        pstmt.setString(1, parkname);  // Set the park name
	        pstmt.setString(2, visittime);  // Set the visit time

	        // Execute the deletion
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}
	/**
	 * Loads parks that have requested a maximum capacity change.
	 *
	 * @param conn  Connection object representing the database connection
	 * @return      ArrayList of ParkForChange objects representing parks with requested maximum capacity changes
	 */
	public static ArrayList<ParkForChange> LoadparkForMaxcap(Connection conn) {
		ArrayList<ParkForChange> sendarrArrayList = new ArrayList<>();
	    String sql = "SELECT parkName, max_cap FROM visit_time_max_table WHERE max_cap IS NOT NULL";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            String parkName = rs.getString("parkName");
	            String max_cap_after = rs.getString("max_cap");
			    String sql1 = "SELECT CapacityOfVisitors FROM park WHERE parkName = ?";
			    try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
			        pstmt1.setString(1, parkName);
			        ResultSet rs1 = pstmt1.executeQuery();

			        if (rs1.next()) {
			            String max_cap_before = rs1.getString("CapacityOfVisitors");
			             //public ParkForChange(String parkName, String dwellBefore, String dwellAfter, String maxCapacityBefore, String maxCapacityAfter) 
			            ParkForChange parktoaddChange = new ParkForChange(parkName, null, null, max_cap_before, max_cap_after);
			            sendarrArrayList.add(parktoaddChange);
			        }
			        
			    } 
			    catch (SQLException e) {
			        e.printStackTrace();
			    }  
	        }
	    } 
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	   	return sendarrArrayList;
	
	}
	/**
	 * Approves a requested maximum capacity change for a specified park.
	 *
	 * @param conn              Connection object representing the database connection
	 * @param parkName          String representing the name of the park
	 * @param capacityOfVisitors    String representing the new maximum capacity to be approved
	 */
	public static void approveMaxCap(Connection conn, String ParkName, String CapacityOfVisitors) {
		String sql = "UPDATE park SET CapacityOfVisitors = ? WHERE ParkName = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        // Set the parameters for the query
	        pstmt.setString(1, CapacityOfVisitors);  // Set new visit time limit
	        pstmt.setString(2, ParkName);  // Set the park name

	        // Execute the update
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    String sql1 = "DELETE FROM visit_time_max_table WHERE parkName = ? AND max_cap = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
	        // Set the parameters for the query
	        pstmt.setString(1, ParkName);  // Set the park name
	        pstmt.setString(2, CapacityOfVisitors);  // Set the visit time

	        // Execute the deletion
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}
	/**
	 * Declines a requested maximum capacity change for a specified park.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param parkname  String representing the name of the park
	 * @param maxCap    String representing the maximum capacity to be declined
	 */
	public static void declineMaxCap(Connection conn, String parkname, String maxcap) {
	    String sql1 = "DELETE FROM visit_time_max_table WHERE parkName = ? AND max_cap = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
	        // Set the parameters for the query
	        pstmt.setString(1, parkname);  // Set the park name
	        pstmt.setString(2, maxcap);  // Set the visit time

	        // Execute the deletion
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}
	/**
	 * Retrieves the park name associated with the provided user ID from the park_workers table,
	 * and then fetches the current capacity for the corresponding park and hour from the park_used_capacity_total table.
	 *
	 * @param conn   Connection object representing the database connection
	 * @param userid String representing the user ID
	 * @return String representing the current capacity in the park at the current hour
	 */
	
	public static String getamountinpark(Connection conn, String userid) {
	    System.out.println("========"+userid);
	    String sql = "SELECT park_name FROM park_workers WHERE idpark_workers = ?";
	    String parkName = "";
	    LocalDateTime now = LocalDateTime.now();
	    String currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    String currentHourColumn = "t" + now.getHour();
	    int currentCapacity = 0;
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userid);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            parkName = rs.getString("park_name");
	            System.out.println("Park Name is: " + parkName);
	        } else {
	            System.out.println("No park found with the provided idpark_workers.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    String sql1 = "SELECT " + currentHourColumn + " FROM park_used_capacity_total WHERE Parkname = ? AND date = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
	        pstmt.setString(1, parkName);
	        pstmt.setString(2, currentDate);

	        ResultSet rs1 = pstmt.executeQuery();

	        if (rs1.next()) {
	            currentCapacity = rs1.getInt(currentHourColumn);
	            System.out.println("Current capacity for " + parkName + " at " + currentHourColumn + " is: " + currentCapacity);
	        } else {
	            System.out.println("No capacity data found for " + parkName + " at " + currentHourColumn + " on " + currentDate);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    System.out.println(String.valueOf(currentCapacity)+"-----currentHourColumn-----"+currentHourColumn+"----currentDate------"+currentDate);
	    return String.valueOf(currentCapacity);



	}
	/**
	 * Checks if the number of visitors for a specified order is greater than or equal to the provided amount of people.
	 *
	 * @param conn          Connection object representing the database connection
	 * @param orderId       String representing the order ID
	 * @param amountofpeople String representing the amount of people to compare against
	 * @return 1 if the number of visitors is greater than or equal to the provided amount, otherwise 0
	 */
	public static int checkamountofpeople(Connection conn, String orderId, String amountofpeople) {
	    String sql = "SELECT NumberOfVisitors FROM orders WHERE OrderId = ?";
	    String numberOfVisitors="";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, orderId); // Set the OrderId parameter

	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            numberOfVisitors = rs.getString("NumberOfVisitors");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    if(Integer.parseInt(numberOfVisitors)>=Integer.parseInt(amountofpeople)) {
	    	return 1;
	    }
	    return 0;
	}

	/**
	 * Updates the isVisit field in the orders table to 'YES' for the specified order ID.
	 *
	 * @param conn      Connection object representing the database connection
	 * @param OrderId   String representing the order ID
	 * @param numberofvisiter  String representing the number of visitors
	 * @param typeacc   String representing the account type
	 */
	
	public static void updateyardentable(Connection conn, String OrderId, String numberofvisiter, String typeacc) {
        int flag =0;
        String parkName="";
        String dateOfVisit = "";
        String timeOfVisit = "";
        String numberOfVisitors ="";
        String dwllString= "";
        String UserId1 = "";
        int amount=0;
	    String sql2 = "UPDATE orders SET isVisit = 'YES' WHERE OrderId = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
	        // Set the value for the userID parameter in the query
	        pstmt.setString(1, OrderId);

	        // Execute the update
	        int affectedRows = pstmt.executeUpdate();

	        // Check if the update was successful
	        if (affectedRows > 0) {
	            System.out.println("The isVisit field was successfully updated to 'YES' for OrderId: " + OrderId);
	        } else {
	            System.out.println("No record found with OrderId: " + OrderId);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error updating the isVisit field: " + e.getMessage());
	        e.printStackTrace();
	    }

		String sql = "SELECT ParkName,UserId, dateOfVisit, timeOfVisit, NumberOfVisitors FROM orders WHERE OrderId = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, OrderId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                // Extract data from result set and store in OrderInfo object
	                parkName = rs.getString("ParkName");
	                dateOfVisit = rs.getString("dateOfVisit");
	                timeOfVisit = rs.getString("timeOfVisit");
	                numberOfVisitors = rs.getString("NumberOfVisitors");
	                dwllString= checkDwell(conn, parkName);
	                UserId1 = rs.getString("UserId");
	                amount= Integer.valueOf(numberOfVisitors)-Integer.valueOf(numberofvisiter);	
	                flag=1;
	                	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    if(flag==1) {
	    	String query = "SELECT 1 FROM external_users WHERE UserId = ?";
	        
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, UserId1);  // Set the UserId you're checking for
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                // If the result set is not empty, the UserId exists
	                updateTotalTables(conn, parkName, dateOfVisit, timeOfVisit,Integer.toString(amount), "park employee" ,"customer" ,dwllString, "-");
	                return ;
	            }

	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
            updateTotalTables(conn, parkName, dateOfVisit, timeOfVisit,Integer.toString(amount), "park employee" ,"guide" ,dwllString, "-");

	    }
	    	}
	
	/**
	 * Retrieves the total price of an order with the specified order ID.
	 *
	 * @param conn     Connection object representing the database connection
	 * @param Orderid  String representing the order ID
	 * @return String representing the total price of the order
	 */

	public static String PriceAfter(Connection conn, String Orderid) {
	    String sql = "SELECT TotalPrice FROM orders WHERE OrderId = ?";
	    String TotalPrice1="";
	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, Orderid); // Set the OrderId parameter

	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	        	TotalPrice1 = rs.getString("TotalPrice");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return TotalPrice1;
	}
	
	
	/**
	 * Retrieves data for generating a cancellation report of type 1 (per park, per day) within the specified date range.
	 *
	 * @param conn   Connection object representing the database connection
	 * @param start  LocalDate representing the start date of the report period
	 * @param end    LocalDate representing the end date of the report period
	 * @return ArrayList of CancelReport objects containing cancellation data for each park and day within the specified range
	 */
	public static ArrayList<CancelReport> getDataforCancelReportType1(Connection conn, LocalDate start,LocalDate end) {
        ArrayList<CancelReport> result = new ArrayList<>();

        String query = "SELECT ParkName, DATE(dateOfVisit) AS VisitDate, " +
                "COUNT(*) AS TotalOrders, " +
                "SUM(CASE WHEN IsCanceled = 'YES' THEN 1 ELSE 0 END) AS CanceledOrdersCount " +
                "FROM gonaturedb.orders " +
                "WHERE dateOfVisit >= ? AND dateOfVisit <= ? " +
                "GROUP BY ParkName, DATE(dateOfVisit) " +
                "ORDER BY ParkName, DATE(dateOfVisit)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, start.toString());
            pstmt.setString(2, end.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                String currentParkName = null;
                CancelReport cancelReport = null;

                while (rs.next()) {
                    String parkName = rs.getString("ParkName");
                    LocalDate visitDate = rs.getDate("VisitDate").toLocalDate();
                    int totalOrders = rs.getInt("TotalOrders");
                    int canceledOrdersCount = rs.getInt("CanceledOrdersCount");

                    if (!parkName.equals(currentParkName)) {
                        if (cancelReport != null) {
                            result.add(cancelReport);
                        }
                        cancelReport = new CancelReport(parkName);
                        currentParkName = parkName;
                    }
                    cancelReport.addCancelData(visitDate, canceledOrdersCount, totalOrders);
                }

                // Add the last cancel report
                if (cancelReport != null) {
                    result.add(cancelReport);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    
}

	
}


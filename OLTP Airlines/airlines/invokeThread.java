package com.airlines;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;

import com.airline.databaseobjects.customer;
import com.airline.databaseobjects.payment;
import com.airline.databaseobjects.seatAvailability;
import com.airline.databaseobjects.trip;


public class invokeThread extends TimerTask {

		
	private int nThreads;
	invokeThread(int nThreads){
		this.nThreads=nThreads;
	}
	@Override
	public void run() {
		ProjectManager obj = new ProjectManager();
		Thread[] thr1 = new Thread[nThreads];
		System.out.println("Number of Threads:" + nThreads);
		for (int x=0; x<nThreads; x++){
			try {
				thr1[x] = new Thread(userInput(obj));
				thr1[x].start();
			} catch (Exception e) {
				System.out.println("Problem with opening thread");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
		

	}
	
	public static Runnable userInput(ProjectManager connection) throws Exception{
		int numberOfStops=2;
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		DataBaseManager obj2 = new DataBaseManager();
		//List<flight> retFlightList = new ArrayList<flight>();
		String origin = null, dest = null, date=null, seatClass = null, payMeth=null;
		String selectedFlight = null, tripType=null, tripMonth = null; 
		
		tripType = Integer.toString(NumberGenerator(2)-1); // 0 for One-Way, 1 for Round
		tripMonth = Integer.toString(NumberGenerator(12));
		seatClass = Integer.toString(NumberGenerator(2)-1); // 0 Economy, 1 Business
		String seatNum = Integer.toString(NumberGenerator(100)); // 100 Seats in Economy and 100 in Buisness
		if (seatClass.equals("0")){
			seatClass = "E";
		}else{
			seatClass = "B";
		}
		date = "01/12/2016";
		Date dt = sdf.parse(date); 
		
		
		customer custom = new customer();
		String newCust = Integer.toString(NumberGenerator(2)-1); // Randomly select between old and new customer
		String CusId = Integer.toString(NumberGenerator(30));
		if (newCust.equals("0")){// Old Customer
			String selectQuery = "select customer_name from customer where customer_id ='"+CusId+"'";
			Connection dbConnection = ProjectManager.getConnection();
			Statement stmt = null;
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
				String existName = rs.getString("CUSTOMER_NAME");
				custom.setCustomerName(existName);
				System.out.println(existName);
			}
			dbConnection.close();
		}else{ // New Customer
			custom.setCustomerName(generateString());
		}
		custom.setCustomerId(CusId);
		String TrpId = tripIdGenerator();
		custom.setEmail(generateString());
		custom.setCustomerAddress(generateString());
		custom.setTripId(TrpId);
		custom.setCustomerCity("Athens");
		
		payment newPayment = new payment();
		payMeth = Integer.toString(NumberGenerator(3));
		if (payMeth.equals("1")){
			newPayment.setPaymentMenthod("cash");
			newPayment.setCurrency("Dollars");
		}else if (payMeth.equals("2")){
			newPayment.setPaymentMenthod("credit");
			newPayment.setCurrency("Euros");
		}else{
			newPayment.setPaymentMenthod("PayPal");
			newPayment.setCurrency("Francs");
		}
		// Insert to Payment table
		String paymentId = Integer.toString(NumberGenerator(10000));
		newPayment.setPaymentId(paymentId);
		newPayment.setAmount(80);// Get Cost of Flight here
		newPayment.setTripID(TrpId);
		

		origin = "Athens";
		dest = "New York";
		selectedFlight = "4";
		
		// Insert to Trip table
		trip newTrip = new trip();	
		newTrip.setDestinationCity(dest);
		newTrip.setOriginCity(origin);
		newTrip.setFlightID(selectedFlight);
		newTrip.setTripID(TrpId);
		newTrip.setTripstatus("FALSE");
		newTrip.setTripType(tripType);
		newTrip.setPaymentID(paymentId);
		newTrip.setTripMonth(tripMonth);
		newTrip.setReservationDate(new Date()); //Today's date goes here
		newTrip.setTripDate(dt);

		
		seatAvailability  availObj = new seatAvailability();
		availObj = obj2.getseatAvailability(selectedFlight, seatClass, seatNum);
		if (availObj.isAvailibilty()==false){
			System.out.println("Seat is taken");
			checkClassAndPlane(selectedFlight,seatClass);		
		}else{
			if (numberOfStops==0){// Athens to New York
				Connection dbConnection = ProjectManager.getConnection();
				dbConnection.setAutoCommit(false);
				try {
					obj2.insertCustomer(dbConnection, custom);
					obj2.insertPayment(dbConnection, newPayment);
					obj2.updateSeatAvailability(dbConnection, availObj, seatNum);
					obj2.insertTrip(dbConnection, newTrip);
					dbConnection.commit();
				} catch (Exception e) {
					e.printStackTrace();
					dbConnection.rollback();
				} finally{
					dbConnection.setAutoCommit(true);
					dbConnection.close();
				}
			}else{ // Layover
				String origin1 = dest;
				String dest1 = "London";
				String selectedFlight1 = "7";
				seatAvailability  availObj1 = new seatAvailability();
				String seatNum1 = Integer.toString(NumberGenerator(3));
				availObj1 = obj2.getseatAvailability(selectedFlight1, seatClass, seatNum1);
				if (availObj1.isAvailibilty()==false){
					System.out.println("Seat for the second flight is taken");
					checkClassAndPlane(selectedFlight1,seatClass);
				}else{
					trip newTrip1 = new trip();	
					newTrip1.setDestinationCity(dest1);
					newTrip1.setOriginCity(origin1);
					newTrip1.setFlightID(selectedFlight1);
					newTrip1.setTripID(tripIdGenerator());
					newTrip1.setTripstatus("FALSE");
					newTrip1.setTripType(tripType);
					newTrip1.setPaymentID(paymentId);
					newTrip1.setTripMonth(tripMonth);
					newTrip1.setReservationDate(new Date()); //Today's date goes here
					newTrip1.setTripDate(dt);
					if (numberOfStops==1){// Athens to New York to London
						Connection dbConnection1 = ProjectManager.getConnection();
						dbConnection1.setAutoCommit(false);
						try {
							obj2.insertCustomer(dbConnection1, custom);
							obj2.insertPayment(dbConnection1, newPayment);
							obj2.updateSeatAvailability(dbConnection1, availObj, seatNum);
							obj2.updateSeatAvailability(dbConnection1, availObj1, seatNum1);
							obj2.insertTrip(dbConnection1, newTrip);
							obj2.insertTrip(dbConnection1, newTrip1);
							dbConnection1.commit();
						}catch (Exception e) {
							e.printStackTrace();
							dbConnection1.rollback();
						}finally{
							dbConnection1.setAutoCommit(true);
							dbConnection1.close();
						}
					}else{ // Athens to New York to London to Paris
						String origin2 = dest1;
						String dest2 = "Paris";
						String selectedFlight2 = "6";
						seatAvailability  availObj2 = new seatAvailability();
						String seatNum2 = Integer.toString(NumberGenerator(3));
						availObj2 = obj2.getseatAvailability(selectedFlight2, seatClass, seatNum2);
						if (availObj2.isAvailibilty()==false){
							System.out.println("Seat for the third flight is taken");
							checkClassAndPlane(selectedFlight2,seatClass);
						}else{
							trip newTrip2 = new trip();	
							newTrip2.setDestinationCity(dest2);
							newTrip2.setOriginCity(origin2);
							newTrip2.setFlightID(selectedFlight2);
							newTrip2.setTripID(tripIdGenerator());
							newTrip2.setTripstatus("FALSE");
							newTrip2.setTripType(tripType);
							newTrip2.setPaymentID(paymentId);
							newTrip2.setTripMonth(tripMonth);
							newTrip2.setReservationDate(new Date()); //Today's date goes here
							newTrip2.setTripDate(dt);
							Connection dbConnection2 = ProjectManager.getConnection();
							dbConnection2.setAutoCommit(false);
							try {
								obj2.insertCustomer(dbConnection2, custom);
								obj2.insertPayment(dbConnection2, newPayment);
								obj2.updateSeatAvailability(dbConnection2, availObj, seatNum);
								obj2.updateSeatAvailability(dbConnection2, availObj1, seatNum1);
								obj2.updateSeatAvailability(dbConnection2, availObj2, seatNum2);
								obj2.insertTrip(dbConnection2, newTrip);
								obj2.insertTrip(dbConnection2, newTrip1);
								obj2.insertTrip(dbConnection2, newTrip2);
								dbConnection2.commit();
							}catch (Exception e) {
								e.printStackTrace();
								dbConnection2.rollback();
							}finally{
								dbConnection2.setAutoCommit(true);
								dbConnection2.close();
							}
						}
					}
				}
			}
		}
		return null;
	}


	public static void checkClassAndPlane(String selectedFlight,String seatClass) throws SQLException{
		// Check if the whole class is booked?
		Statement stmt = null;
		String selectQuery;
		selectQuery = "select * from seatAvailability where flight_Id='"+selectedFlight+"' and seat_class='"+seatClass+"'";
		Connection dbConnection = ProjectManager.getConnection();
		stmt = dbConnection.createStatement();
		ResultSet rs = stmt.executeQuery(selectQuery);
		int flag = 0;
		while (rs.next()) {
			if(rs.getBoolean("availability")==true){
				flag=1;
			}
		}
		dbConnection.close();
		if (flag==0){
			System.out.println("All the Seats in the Class selected are taken");
			Statement stmt1 = null;
			String selectQuery1;
			selectQuery1 = "select * from seatAvailability where flight_Id='"+selectedFlight+"'";
			Connection dbConnection1 = ProjectManager.getConnection();
			stmt1 = dbConnection1.createStatement();
			ResultSet rs1 = stmt1.executeQuery(selectQuery1);
			int flag1 = 0;
			while (rs1.next()) {
				if(rs1.getBoolean("availability")==true){
					flag1=1;
				}
			}
			if (flag1==0){
				System.out.println("Plane is booked");
			}
			dbConnection1.close();
		}
	}

	public static String generateString()
	{
		String characters = "SarafianosNikolaos"; 
		Random rng = new Random();
		int length = 15;
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}

	public static int NumberGenerator(int MaxVal){
		Random rand = new Random();
		int randMonth = rand.nextInt(MaxVal)+1;
		return randMonth;
		
	}

	public static String tripIdGenerator(){
		String retString=null;
		retString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		return retString;
		
	}

	public static String cutomerIdGenerator(String customerName){
		String retString=null,s="";
		double d;
	    for (int i = 1; i <= 4; i++) {
	        d = Math.random() * 10;
	        s = s + ((int)d);
	        }
	    retString=customerName.substring(0,4)+s;
		return retString;
		
	}	
}





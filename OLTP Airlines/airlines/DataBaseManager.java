package com.airlines;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.airline.databaseobjects.customer;
import com.airline.databaseobjects.payment;
import com.airline.databaseobjects.seatAvailability;
import com.airline.databaseobjects.trip;

public class DataBaseManager {
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	StringBuffer transctionQery = new StringBuffer();

	public boolean insertPayment(Connection dbConnection, payment objpayment) throws Exception {
		PreparedStatement preparedStatement = null;


		String insertqry = "INSERT INTO payment (currency, payment_id, payment_method, trip_id, amount) VALUES(?, ?, ?, ?, ?);";

		try {
			preparedStatement = dbConnection.prepareStatement(insertqry);
			preparedStatement.setString(1, objpayment.getCurrency());
			preparedStatement.setString(2, objpayment.getPaymentId());
			preparedStatement.setString(3, objpayment.getPaymentMenthod());
			preparedStatement.setString(4, objpayment.getTripID());
			preparedStatement.setInt(5, objpayment.getAmount());
			preparedStatement.executeUpdate();
			System.out.println("Record is inserted into table!");
			preparedStatement.close();
			//dbConnection.close();
			return true;
		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}
		preparedStatement.close();
		return false;
	}

	
	public boolean insertCustomer(Connection dbConnection, customer objcustomer) throws Exception {
		PreparedStatement preparedStatement = null;
		String insertqry = "INSERT INTO customer (customer_address, customer_id, customer_name, city, email, trip_id) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			preparedStatement = dbConnection.prepareStatement(insertqry);
			preparedStatement.setString(1, objcustomer.getCustomerAddress());
			preparedStatement.setString(2, objcustomer.getCustomerId());
			preparedStatement.setString(3, objcustomer.getCustomerName());
			preparedStatement.setString(4, objcustomer.getCustomerCity());
			preparedStatement.setString(5, objcustomer.getEmail());
			preparedStatement.setString(6, objcustomer.getTripId());
			preparedStatement.executeUpdate();
			System.out.println("Record is inserted into table!");
			preparedStatement.close();
			//dbConnection.close();
			return true;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}
		preparedStatement.close();
		return false;

	}

	public boolean insertTrip(Connection dbConnection, trip objtrip) throws Exception {
		PreparedStatement preparedStatement = null;
		String insertqry = "INSERT INTO trip (destination_city, flight_id, origin_city, payment_id, reserved_date, trip_id, trip_status, trip_type, trip_month, travel_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			preparedStatement = dbConnection.prepareStatement(insertqry);
			preparedStatement.setString(1, objtrip.getDestinationCity());
			preparedStatement.setString(2, objtrip.getFlightID());
			preparedStatement.setString(3, objtrip.getOriginCity());
			preparedStatement.setString(4, objtrip.getPaymentID());
			java.util.Date utilDate = objtrip.getReservationDate();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			preparedStatement.setDate(5, sqlDate);
			preparedStatement.setString(6, objtrip.getTripID());
			preparedStatement.setString(7, objtrip.getTripstatus());
			preparedStatement.setString(8, objtrip.getTripType());
			preparedStatement.setString(9, objtrip.getTripMonth());
			java.util.Date utilDate1 = objtrip.getTripDate();
			java.sql.Date sqlDate1 = new java.sql.Date(utilDate1.getTime());
			preparedStatement.setDate(10, sqlDate1);
			preparedStatement.executeUpdate();
			System.out.println("Record is inserted into table!");
			preparedStatement.close();
			return true;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}
		preparedStatement.close();
		//dbConnection.close();
		return false;

	}

	public List<customer> getCustomerList() throws SQLException {

		Statement stmt = null;
		String selectQuery = "select * from customer";
		Connection dbConnection = ProjectManager.getConnection();
		List<customer>  retList = new ArrayList<customer>();
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
			 customer objCustomer = new customer();
			 objCustomer.setCustomerAddress(rs.getString("CUSTOMER_ADDRESS"));
			 objCustomer.setCustomerId(rs.getString("CUSTOMER_ID"));
			 objCustomer.setCustomerName(rs.getString("CUSTOMER_NAME")); 
			 retList.add(objCustomer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retList;
	}
	
	
	public List<payment> getPaymentList() throws SQLException {

		Statement stmt = null;
		String selectQuery = "select * from payment";
		Connection dbConnection = ProjectManager.getConnection();
		List<payment>  retList = new ArrayList<payment>();
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
				payment objPayment = new payment();
				objPayment.setAmount(rs.getInt("AMOUNT"));
				objPayment.setCurrency(rs.getString("CURRENCY"));
				objPayment.setPaymentId(rs.getString("PAYMENT_ID"));
				objPayment.setPaymentMenthod(rs.getString("PAYMENT_METHOD"));
				objPayment.setTripID(rs.getString("TRIP_ID"));
			 retList.add(objPayment);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retList;
	}
	
	public List<trip> getTripList() throws SQLException {
		Statement stmt = null;
		String selectQuery = "select * from trip";
		Connection dbConnection = ProjectManager.getConnection();
		List<trip>  retList = new ArrayList<trip>();
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
				trip objTrip = new trip();
				objTrip.setDestinationCity(rs.getString("DESTINATION_CITY"));
				objTrip.setFlightID(rs.getString("FLIGHT_ID"));
				objTrip.setOriginCity(rs.getString("ORIGIN_CITY"));
				objTrip.setPaymentID(rs.getString("PAYMENT_ID"));
				objTrip.setReservationDate(rs.getDate("RESERVED_DATE"));
				objTrip.setTripID(rs.getString("TRIP_ID"));
				objTrip.setTripstatus(rs.getString("TRIP_STATUS"));
				objTrip.setTripType(rs.getString("TRIP_TYPE"));
			 retList.add(objTrip);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retList;
	}
	

	public seatAvailability getseatAvailability(String flightID, String seatClass, String seatNum) throws Exception {

		Statement stmt = null;
		String selectQuery;
		selectQuery = "select * from seatAvailability where flight_Id='"+flightID+"' and seat_class='"+seatClass+"' and seat_num='"+seatNum+"'";
		Connection dbConnection = ProjectManager.getConnection();
		seatAvailability  retObj = new seatAvailability();
		try {
			stmt = dbConnection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQuery);
			while (rs.next()) {
				retObj.setFlightID(rs.getString("FLIGHT_ID"));
				retObj.setSeatClass(rs.getString("seat_class"));
				retObj.setAvailibilty(rs.getBoolean("availability"));
				retObj.setSeat_num(rs.getString("seat_num"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbConnection.close();
		stmt.close();
		return retObj;
	}
	
	
	public boolean updateSeatAvailability(Connection dbConnection, seatAvailability objseatAvailability, String seatnum ) throws Exception {
		PreparedStatement preparedStatement = null;
		String updateTableSQL = "UPDATE seatavailability SET availability = ? " + " WHERE flight_id = ? and seat_class= ? and seat_num = ?";
		try {
			preparedStatement = dbConnection.prepareStatement(updateTableSQL);
			preparedStatement.setBoolean(1,false);
			preparedStatement.setString(2,objseatAvailability.getFlightID());
			preparedStatement.setString(3,objseatAvailability.getSeatClass());
			preparedStatement.setString(4,objseatAvailability.getSeat_num());
			preparedStatement.executeUpdate();


		} catch (Exception e) {

			e.printStackTrace();
	}
		preparedStatement.close();
		return false;
		
	}
}

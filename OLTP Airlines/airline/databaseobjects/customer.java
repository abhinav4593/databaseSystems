package com.airline.databaseobjects;

import java.util.ArrayList;
import java.util.List;

public class customer {
	
	private String customerId;
	private String customerName;
	private String customerAddress;
	private String TripId;
	private String email;
	private String customerCity;
	private List<customer> listCustomer = new ArrayList<customer>();
	
	public String getCustomerCity() {
		return customerCity;
	}
	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getTripId() {
		return TripId;
	}
	public void setTripId(String tripId) {
		TripId = tripId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}

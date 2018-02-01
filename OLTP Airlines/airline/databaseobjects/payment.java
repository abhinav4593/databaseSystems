package com.airline.databaseobjects;

import java.util.ArrayList;
import java.util.List;

public class payment {
	
	private String paymentId;
	private String paymentMenthod;
	private int amount;
	private String currency;
	private String tripID;
	private List<payment> listPayment = new ArrayList<payment>(); 
	

	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getPaymentMenthod() {
		return paymentMenthod;
	}
	public void setPaymentMenthod(String paymentMenthod) {
		this.paymentMenthod = paymentMenthod;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTripID() {
		return tripID;
	}
	public void setTripID(String tripID) {
		this.tripID = tripID;
	}
}

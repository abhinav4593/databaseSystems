package com.airline.databaseobjects;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class trip {
	
	private String tripID;
	private String flightID;
	private String paymentID;
	private Date reservationDate;
	private String tripType;
	private String originCity;
	private String DestinationCity;
	private String tripstatus;
	private String tripMonth;
	private Date tripDate;
	private List<trip> listTrip = new ArrayList<trip>();
	
	public String getTripMonth() {
		return tripMonth;
	}
	public void setTripMonth(String tripMonth) {
		this.tripMonth = tripMonth;
	}
	public Date getTripDate() {
		return tripDate;
	}
	public void setTripDate(Date tripDate) {
		this.tripDate = tripDate;
	}
	
	public String getTripID() {
		return tripID;
	}
	public void setTripID(String tripID) {
		this.tripID = tripID;
	}
	public String getFlightID() {
		return flightID;
	}
	public void setFlightID(String flightID) {
		this.flightID = flightID;
	}
	public String getPaymentID() {
		return paymentID;
	}
	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}
	public Date getReservationDate() {
		return reservationDate;
	}
	public void setReservationDate(Date reservationDate) {
		this.reservationDate = reservationDate;
	}
	public String getTripType() {
		return tripType;
	}
	public void setTripType(String tripType) {
		this.tripType = tripType;
	}
	public String getOriginCity() {
		return originCity;
	}
	public void setOriginCity(String originCity) {
		this.originCity = originCity;
	}
	public String getDestinationCity() {
		return DestinationCity;
	}
	public void setDestinationCity(String destinationCity) {
		DestinationCity = destinationCity;
	}
	public String getTripstatus() {
		return tripstatus;
	}
	public void setTripstatus(String tripstatus) {
		this.tripstatus = tripstatus;
	}
}


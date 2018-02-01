package com.airline.databaseobjects;

import java.util.ArrayList;
import java.util.List;

public class seatAvailability {
	private String FlightID;
	private String seatClass;
	private String seat_num;
	private boolean availibilty;
	private List<seatAvailability> listSeatAvailability = new ArrayList<seatAvailability>();
	public String getFlightID() {
		return FlightID;
	}
	public void setFlightID(String flightID) {
		FlightID = flightID;
	}
	public String getSeatClass() {
		return seatClass;
	}
	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}
	public String getSeat_num() {
		return seat_num;
	}
	public void setSeat_num(String seat_num) {
		this.seat_num = seat_num;
	}
	public boolean isAvailibilty() {
		return availibilty;
	}
	public void setAvailibilty(boolean availibilty) {
		this.availibilty = availibilty;
	}
		
}

package com.airlines;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

import com.airline.databaseobjects.customer;
import com.airline.databaseobjects.payment;
import com.airline.databaseobjects.seatAvailability;
import com.airline.databaseobjects.trip;

public class Oltp_airline {

	public static void main(String args[]) throws Exception {
		Scanner reader = new Scanner(System.in);
		System.out.println("Type Number of Threads: ");
		int nThreads = reader.nextInt();		
		Timer tim = new Timer();
		tim.schedule(new invokeThread(nThreads),0,5000);// Every 5sec
	}
}
	
	
	
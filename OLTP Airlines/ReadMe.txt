Instructions: 

 - The user has to enter the number of threads and for the selected number a call is beind done every 5 seconds. 

 - For the number of stops (0,1,2) in the invokeThread function you should go the first line of the userInput function and change the number. 

	For example: 
		public static Runnable userInput(ProjectManager connection) throws Exception{
				int numberOfStops=2;

 - The flights in the database are 3 so as to fascilitate the requirements for 2 layovers. The first flight is from Athens to new york so we assume that all customers start from Athens. After New York if we want >1 stop we stop at London and then Paris. The respective flight ID's are 7, 4 and 6 respectively. 
 
 - We update the tables customer, trip (refers to the reservation of the customer), payment and also update the seat availability.
 
 - Seats are checked if are booked or not. If the seat is booked we check if the whole class is booked and then if the whole plane is booked. 
 
 - We enable/disable the autocommit and use commit and rollback to ensure that the transactions take place correctly and that > 1 thread cannot book the same seat.  
 
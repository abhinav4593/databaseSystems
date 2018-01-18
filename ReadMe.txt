COSC 6340 - Assignment 1
Team 5:
	Abhinav Gundlapalli - 1454321
	Nikolaos Sarafianos - 1392654
	
This submission contains 2 java files: 
	1) ProjectRun.java which contains the main function. In lines 9-11 the respective paths for the input file and the 2 output files need to be specified so please add yours there. Then just run it as a Java application (we used Eclipse).
	2) ProjectNormalization.java which contains the implemented code. 

The output is a text file containing the table and the proposed (loseless) decomposition and a .sql file that contains the queries.
	
For example using the schemas for R (not in 2NF) and R11(not in 3NF) the output is the following: 
----------------------------
TABLE 	 FORM 	 Y_N 	 REASON
----------------------------
R 	 2NF 	 N 	 K1->B K2->A K2->B 
R 	 1NF 	 Y 	 
R11 	 1NF 	 Y 	 
R11 	 2NF 	 Y 	 
R11 	 3NF 	 N 	 C->F 


2NF Decomposition
R1(K1,B)
R2(K2,A)
R3(K1,K2)

3NF Decomposition
R_1(C,F)
R_2(A,B,C)


For any questions you might have do not hestitate to contact us at: 
1) abhinav4593@gmail.com
2) nikos.sarafianos@gmail.com
# Database Systems

You will develop a Java program that generates SQL code to check normal forms. The input is a set of tables
(relations) and a potential candidate key (CK) for each table (it is initially unknown if the given CK is
indeed a CK).

Pprogram will generate SQL code based on input tables whose basic schema is defned in a text file.
Basically, you will initially have to check normal forms against a given candidate key and several nonkey
attributes. You can assume the candidate key has at most 3 attributes and there are at most 17 nonkey
(nonprime) attributes. The main goal of your program is to certify (verify) normal forms 3NF. Your program
must answer yes/no for 3NF and BCNF.

Files:
1) ProjectRun.java which contains the main function. In lines 9-11 the respective paths for the input file and the 2 output files need to be specified so please add yours there. Then just run it as a Java application (we used Eclipse).
2) ProjectNormalization.java which contains the implemented code. 

package com.airlines;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ProjectManager {
	public static Connection getConnection() {
		Connection con = null;
		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
		}

		try {

			con = DriverManager.getConnection("jdbc:postgresql://129.7.243.243:5432/team05", "team05", "team05agns");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		return con;
	}

}

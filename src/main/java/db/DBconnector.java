package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnector {

	public DBconnector() {
		// TODO Auto-generated constructor stub
	}

	public void connect() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
			   "jdbc:postgresql:datagen","postgres", "root");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connected to PostgreSQL...");
	}
	
	public static void main(String[] args) {
		DBconnector db = new DBconnector();
		db.connect();
	}

}

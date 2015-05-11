package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnector {
	
	private Connection connection = null;
	private Statement statement = null;


	public DBconnector() {
		// TODO Auto-generated constructor stub
	}

	public void connect() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection(
			   "jdbc:postgresql:datagen","postgres", "root");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connected to PostgreSQL...");
	}
	
	public void createTable() {
	    try {
	    	statement = connection.createStatement();	      
	    	String sql = "CREATE TABLE pois(poisId INT NOT NULL PRIMARY KEY,"
	    			+ " poi POINT);" ;
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Created table 'pois'...");
	}
	
	public void dropTable() {
	    try {
	    	statement = connection.createStatement();	      
	    	String sql = "DROP TABLE pois;";
	    	statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}	    	
	}
	
	public void insert(int id, String lng, String lat) {
	    try {
	    	statement = connection.createStatement();	      
	    	String sql = "INSERT INTO pois (poisId, poi) VALUES ("
	    			+ id + ", POINT(" + lng + ", " + lat   
	    			+ ") );" ;
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Inserted record on 'pois'...");
	}
	
	public static void main(String[] args) {
		DBconnector db = new DBconnector();
		db.connect();
		db.createTable();
		db.insert(1, "34.20332", "45.02345");
		//db.dropTable();
	}

}

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
			connection = DriverManager.getConnection("jdbc:postgresql:datagen",
					"postgres", "root");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connected to PostgreSQL...");
	}

	public void createTable() {
		try {
			statement = connection.createStatement();
			String sql = "CREATE TABLE pois(poisId INT NOT NULL PRIMARY KEY, location GEOGRAPHY(POINT,4326));";
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
			String sql = "INSERT INTO pois (poisId, location) VALUES (" + id
					+ ", ST_GeographyFromText('SRID=4326;POINT(" + lng + " " + lat + ")')"
					+ ");";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Inserted record on 'pois'...");
	}
	
	public void findInRange(String lng, String lat, double dist) {
		try {
			statement = connection.createStatement();
			String sql = "SELECT * FROM pois WHERE ("
					+ "SELECT ST_DWithin(ST_GeographyFromText('SRID=4326;POINT("
					+ lng + " " + lat + ")'), location, "
					+ dist  + ")"
					+ ");";
			ResultSet rs = statement.executeQuery(sql);
			Statement st = connection.createStatement();
			while (rs.next()) {
				//System.out.println(rs.getString("location"));
				sql = " SELECT ST_X(location::geometry), ST_Y(location::geometry) FROM pois WHERE poisId = "
						+ rs.getInt("poisId") + ";";
				ResultSet res = st.executeQuery(sql);
				if (res.next()) {
					System.out.println(res.getDouble("st_x") + " " + res.getDouble("st_y"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		DBconnector db = new DBconnector();
		db.connect();
		db.createTable();
		db.insert(4, "37.975500", "23.784756");
		db.insert(5, "37.975600", "23.784786");
		db.findInRange("37.974908", "23.782941", 300);
		// db.dropTable();
	}

}

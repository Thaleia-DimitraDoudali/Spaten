package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pois.Poi;
import pois.Review;

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
	
	public void createPoiTable() {
		try {
			statement = connection.createStatement();
			String sql = "CREATE TABLE pois(poisId SERIAL,"
					+ " location GEOGRAPHY(POINT,4326),"
					+ "title TEXT, adress TEXT);";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createRevTable() {
		try {
			statement = connection.createStatement();
			String sql = "CREATE TABLE reviews(revId INT NOT NULL,"
					+ " rating TEXT, reviewTitle TEXT, review TEXT);";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	public int getPoisNum() {
		int res = 0;
		try {
			statement = connection.createStatement();
			String sql = "SELECT COUNT(*) AS total FROM pois;";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next())
				res = rs.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res; 
	}
	
	public Poi getPoi(int id) {
		Poi p = null;
		try {
			statement = connection.createStatement();
			Statement st = connection.createStatement();
			String title = "", adress = "", lng = "", lat = "", rating = "", rev = "", rTitle = "";
			String sql = "SELECT * FROM pois WHERE poisId = " + id + ";";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				title = rs.getString("title");
				adress = rs.getString("adress");
				sql = " SELECT ST_X(location::geometry), ST_Y(location::geometry) FROM pois WHERE poisId = "
						+ id + ";";
				ResultSet res = st.executeQuery(sql);
				if (res.next()) {
					lng = res.getString("st_x");
					lat = res.getString("st_y");
				}
				sql = " SELECT * FROM reviews WHERE revId = "
						+ id + ";";
				res = st.executeQuery(sql);
				if (res.next()){
					rating = res.getString("rating");
					rTitle = res.getString("reviewTitle");
					rev = res.getString("review");
				}
				p = new Poi(id, title, adress, rating, rTitle, rev, lng, lat);
				while (res.next()){
					rating = res.getString("rating");
					rTitle = res.getString("reviewTitle");
					rev = res.getString("review");
					Review r = new Review(rating, rTitle, rev);
					p.addReview(r);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public void insertPoi(Poi p) {
		try {
			statement = connection.createStatement();
			String sql = "INSERT INTO pois (poisId, location, title, adress) VALUES (" + p.getPoiId()  
					+ ", ST_GeographyFromText('SRID=4326;POINT(" + p.getLongitude() + " " + p.getLatitude() + ")')"
							+ ", '" + p.getTitle() + "', '" + p.getAddress()
					+ "');";
			statement.executeUpdate(sql);
			for (Review r: p.getReviews()) {
				sql = "INSERT INTO reviews (revId, rating, reviewTitle, review) VALUES ("
					+ p.getPoiId() + ", '" + r.getRating() + "', '" + r.getReviewTitle()
					+ "', '" + r.getReview() + "')";
				statement.executeUpdate(sql);
			}
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

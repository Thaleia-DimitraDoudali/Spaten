package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import checkIns.User;
import pois.GPSTrace;
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

	public void dropPoiTable() {
		try {
			statement = connection.createStatement();
			String sql = "DROP TABLE pois;";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dropRevTable() {
		try {
			statement = connection.createStatement();
			String sql = "DROP TABLE reviews;";
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
			String title = "", adress = "", rating = "", rev = "", rTitle = "";
			double lng = -1, lat = -1;
			String sql = "SELECT * FROM pois WHERE poisId = " + id + ";";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				title = rs.getString("title");
				adress = rs.getString("adress");
				sql = " SELECT ST_X(location::geometry), ST_Y(location::geometry) FROM pois WHERE poisId = "
						+ id + ";";
				ResultSet res = st.executeQuery(sql);
				if (res.next()) {
					lat = res.getDouble("st_x");
					lng = res.getDouble("st_y");
				}
				sql = " SELECT * FROM reviews WHERE revId = " + id + ";";
				res = st.executeQuery(sql);
				if (res.next()) {
					rating = res.getString("rating");
					rTitle = res.getString("reviewTitle");
					rev = res.getString("review");
				}
				p = new Poi(id, title, adress, rating, rTitle, rev, lat, lng);
				while (res.next()) {
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
			String sql = "INSERT INTO pois (poisId, location, title, adress) VALUES ("
					+ p.getPoiId()
					+ ", ST_GeographyFromText('SRID=4326;POINT("
					+ p.getLatitude()
					+ " "
					+ p.getLongitude()
					+ ")')"
					+ ", '"
					+ p.getTitle() + "', '" + p.getAddress() + "');";
			statement.executeUpdate(sql);
			for (Review r : p.getReviews()) {
				sql = "INSERT INTO reviews (revId, rating, reviewTitle, review) VALUES ("
						+ p.getPoiId()
						+ ", '"
						+ r.getRating()
						+ "', '"
						+ r.getReviewTitle() + "', '" + r.getReview() + "')";
				statement.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public GPSTrace getBetween(double lngFrom, double latFrom, double lngTo,
			double latTo, double from, double to, long time, User usr) {
		GPSTrace res = null;
		try {
			statement = connection.createStatement();
			String sql = "CREATE TABLE endPoint(g text);";
			statement.executeUpdate(sql);
			sql = "INSERT INTO endPoint(g) VALUES(ST_EndPoint(ST_Line_SubString(ST_Makeline(ST_GeomFromText"
					+ "('POINT("
					+ latFrom
					+ " "
					+ lngFrom
					+ ")'), St_GeomFromText('POINT("
					+ latTo
					+ " "
					+ lngTo
					+ ")')), " + from + ", " + to + ")));";
			statement.executeUpdate(sql);
			sql = "SELECT * FROM endPoint";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next())
				// System.out.println(rs.getString("g"));
				sql = " SELECT ST_X(g::geometry), ST_Y(g::geometry) FROM endPoint;";
			rs = statement.executeQuery(sql);
			if (rs.next()) {
				// System.out.println(rs.getDouble("st_x") + " " +
				// rs.getDouble("st_y"));
				res = new GPSTrace(rs.getDouble("st_x"), rs.getDouble("st_y"),
						time, usr.getUserId());
				usr.getTraces().add(res);
			}
			sql = "DROP TABLE endPoint;";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public ArrayList<Poi> findInRange(int id, double lng, double lat,
			double dist) {
		ArrayList<Poi> pois = new ArrayList<Poi>();
		try {
			statement = connection.createStatement();
			String sql = "SELECT * FROM pois WHERE ("
					+ "SELECT ST_DWithin(ST_GeographyFromText('SRID=4326;POINT("
					+ lat + " " + lng + ")'), location, " + dist + ")" + ");";
			ResultSet rs = statement.executeQuery(sql);
			Statement st = connection.createStatement();
			// int i = 1;
			// Return a list with all pois found in range, except itself
			while (rs.next()) {
				sql = " SELECT ST_X(location::geometry), ST_Y(location::geometry) FROM pois WHERE poisId = "
						+ rs.getInt("poisId") + ";";
				ResultSet res = st.executeQuery(sql);
				if (res.next()) {
					// System.out.println(i + " " + res.getDouble("st_x") + " "
					// + res.getDouble("st_y"));
				}
				// i++;
				int pId = rs.getInt("poisId");
				if (pId != id) {
					pois.add(getPoi(pId));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pois;
	}

}

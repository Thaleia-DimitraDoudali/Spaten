package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pois.Poi;
import pois.Review;

public class DBconnector {

	private Connection connection = null;
	private Statement statement = null;

	public void connect() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://192.168.5.141/datagen",
					"thaleia", "thaleia");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connected to PostgreSQL...");
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
	
	public int getPoiTitle(String title) {
		try {
			statement = connection.createStatement();
			String sql = "SELECT * FROM pois WHERE title = '" + title + "';";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt("poisId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void addReview(int id, Review rev) {
		try {
			statement = connection.createStatement();
			String sql = "INSERT INTO reviews (revId, rating, reviewTitle, review) VALUES ("
					+ id
					+ ", '"
					+ rev.getRating()
					+ "', '"
					+ rev.getReviewTitle() + "', '" + rev.getReview() + "')";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertPoi(Poi p) {
		try {
			statement = connection.createStatement();
			String sql = "INSERT INTO pois (location, title, adress) VALUES ("
					+ "ST_GeographyFromText('SRID=4326;POINT("
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

	public ArrayList<Integer> findInRange(int id, double lng, double lat,
			double dist) {
		ArrayList<Integer> pois = new ArrayList<Integer>();
		try {
			statement = connection.createStatement();
			String sql = "SELECT * FROM pois WHERE ("
					+ "ST_DWithin(ST_GeographyFromText('SRID=4326;POINT("
					+ lat + " " + lng + ")'), location, " + dist + ")" + ");";
			ResultSet rs = statement.executeQuery(sql);
			//System.out.println(sql);

			while (rs.next()) {
				int pId = rs.getInt("poisId");
				if (pId != id) {
					pois.add(pId);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pois;
	}

}

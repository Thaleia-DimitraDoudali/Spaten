package checkIns;

import java.util.Calendar;
import java.util.Date;

import pois.Poi;
import pois.Review;

public class CheckIn {

	private Poi poi;
	private Review review;
	private long timestamp;
	private int userId;
	private boolean travel;
	
	public CheckIn(int id, Poi rst, long time, Review rev) {
		this.userId = id;
		this.poi = rst;
		this.timestamp = time;
		this.review = rev;
	}

	public void print() {
		String out = "User no." + userId
				+ "\t travel: " + travel
				+ "\t date: "
				+ getDate(timestamp)
				+ "\t poi chosen: " + poi.getPoiId() 
				+ " title: " + poi.getTitle()
				+ " (" + poi.getLatitude()
				+ ", " + poi.getLongitude() + ") "
				
				+ "adress: " + poi.getAddress()
				+ "\t timestamp: " + timestamp  + "\t "; 
		System.out.print(out);
		review.print();
	}
	
	public Date getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		Date dt = calendar.getTime();
		return dt;
	}
	
	public Poi getPoi() {
		return poi;
	}

	public void setpoi(Poi poi) {
		this.poi = poi;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public boolean isTravel() {
		return travel;
	}

	public void setTravel(boolean travel) {
		this.travel = travel;
	}
	
}

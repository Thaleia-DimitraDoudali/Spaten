package checkIns;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import restaurants.Restaurant;
import restaurants.Review;

public class CheckIn {

	private Restaurant restaurant;
	private Review review;
	private long timestamp;
	private int userId;
	
	public CheckIn(int id, Restaurant rst, long time, Review rev) {
		this.userId = id;
		this.restaurant = rst;
		this.timestamp = time;
		this.review = rev;
	}

	public void print() {
		String out = "User no." + userId + "\t restaurant chosen: " + restaurant.getRestId() + "\t timestamp: " + timestamp + "\t date: "
				+ getDate(timestamp) + "\t "; 
		System.out.print(out);
		review.print();
		System.out.println();
	}
	
	public Date getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);

		Date dt = calendar.getTime();
		
		return dt;
	}
	
	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
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
	
}

package checkIns;

import java.util.Calendar;

import restaurants.Restaurant;

public class CheckIn {

	private Restaurant restaurant;
	private long timestamp;
	private int userId;
	
	public CheckIn(int id, Restaurant rst, long time) {
		this.userId = id;
		this.restaurant = rst;
		this.timestamp = time;
	}

	public void print() {
		String out = "User no." + userId + "\t restaurant chosen: " + restaurant.getRestId() + "\t timestamp: " + timestamp + "\t date: "
				+ getDate(timestamp); 
		System.out.println(out);
	}
	
	public String getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
				
		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH); 
		if (mMonth == 0)
			mMonth ++;
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		String res = mMonth + "/" + mDay + "/" + mYear;
		return res;
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
	
}

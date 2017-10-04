package pois;

import java.util.Calendar;
import java.util.Date;

public class GPSTrace {

	private double longitude;
	private double latitude;
	private long timestamp;
	private int userId;

	public GPSTrace(double lat, double lng, long time, int id) {
		longitude = lng;
		latitude = lat;
		timestamp = time;
		userId = id;
	}

	public void print() {
		System.out.println("GPS Trace: User no." + userId + " at (" + latitude
				+ ", " + longitude + ") on " + getDate(timestamp));
	}
	
	public Date getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);

		Date dt = calendar.getTime();
		
		return dt;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
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


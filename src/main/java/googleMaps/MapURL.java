package googleMaps;

import java.util.Date;

public class MapURL {
	
	private int userId;
	private Date date;
	private String url;
	
	public MapURL(int id, Date dt, String s) {
		userId = id;
		date = dt;
		url = s;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

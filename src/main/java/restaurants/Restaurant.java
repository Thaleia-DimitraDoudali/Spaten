package restaurants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;

import checkIns.CheckIn;

public class Restaurant implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<CheckIn> checkIns = new ArrayList<CheckIn>();
	private List<Review> reviews = new ArrayList<Review>();
	
	private int restId;
	private String title;
	private String address;
	private String longitude;
	private String latitude;


	Restaurant(int id, String titleR, String addressR, String ratingR, String reviewTitleR, String reviewR, String longitudeR, 
			String latitudeR) {
		restId = id;
		title = titleR;
		address = addressR;
		longitude = longitudeR;
		latitude = latitudeR;
		Review rev = new Review(ratingR, reviewTitleR, reviewR);
		reviews.add(rev);
	}
	
	public void addCheckIn(CheckIn chk) {
		checkIns.add(chk);
	}
	
	//restructure it!!
	public Text getRestText() {
		return new Text("\nid = " + this.restId + "\n"  
				+ "title = " + this.title + "\n"
				+ "address = " + this.address + "\n"
				+ "longitude = " + this.longitude + "\n"
				+ "latitude = " + this.latitude + "\n");
	}
	
	public void print() {
		System.out.println("----------------Restaurant no." + restId + "-------------------");
		System.out.print("title = " + this.title + "\t");
		System.out.print("address = " + this.address + "\t");
		System.out.print("longitude = " + this.longitude + "\t");
		System.out.print("latitude = " + this.latitude + "\n");
		for (Review rev: reviews) {
			rev.print();
		}
		for (CheckIn chk: checkIns) {
			chk.print();
		}
	}

	public int getRestId() {
		return restId;
	}

	public void setRestId(int restId) {
		this.restId = restId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public List<CheckIn> getCheckIns() {
		return checkIns;
	}

	public void setCheckIns(List<CheckIn> checkIns) {
		this.checkIns = checkIns;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}

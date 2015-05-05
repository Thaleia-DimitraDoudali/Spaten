package restaurants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;

import checkIns.CheckIn;

public class Restaurant implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<CheckIn> checkIns = new ArrayList<CheckIn>();
	
	private int restId;
	private String title;
	private String address;
	private String rating;
	private String reviewTitle;
	private String review;
	private String longitude;
	private String latitude;


	Restaurant(int id, String titleR, String addressR, String ratingR, String reviewTitleR, String reviewR, String longitudeR, 
			String latitudeR) {
		restId = id;
		title = titleR;
		address = addressR;
		rating = ratingR;
		reviewTitle = reviewTitleR;
		review = reviewR;
		longitude = longitudeR;
		latitude = latitudeR;
	}
	
	public void addCheckIn(CheckIn chk) {
		checkIns.add(chk);
	}
	
	public Text getRestText() {
		return new Text("\nid = " + this.restId + "\n"  
				+ "title = " + this.title + "\n"
				+ "address = " + this.address + "\n"
				+ "rating = " + this.rating + "\n"
				+ "review title = " + this.reviewTitle + "\n"
				+ "review = " + this.review + "\n"
				+ "longitude = " + this.longitude + "\n"
				+ "latitude = " + this.latitude + "\n");
	}
	
	public void print() {
		System.out.println("----------------Restaurant no." + restId + "-------------------");
		System.out.print("title = " + this.title + "\t");
		System.out.print("address = " + this.address + "\t");
		System.out.print("rating = " + this.rating + "\t");
		System.out.print("review title = " + this.reviewTitle + "\t");
		System.out.print("review = " + this.review + "\t");
		System.out.print("longitude = " + this.longitude + "\t");
		System.out.print("latitude = " + this.latitude + "\n");
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

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
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
}

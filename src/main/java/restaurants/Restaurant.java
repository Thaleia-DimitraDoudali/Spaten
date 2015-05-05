package restaurants;

import java.io.Serializable;

import org.apache.hadoop.io.Text;

public class Restaurant implements Serializable{

	private static final long serialVersionUID = 1L;
	
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
	
	public Text getRestText() {
		return new Text("\ntitle = " + this.title + "\n"
				+ "address = " + this.address + "\n"
				+ "rating = " + this.rating + "\n"
				+ "review title = " + this.reviewTitle + "\n"
				+ "review = " + this.review + "\n"
				+ "longitude = " + this.longitude + "\n"
				+ "latitude = " + this.latitude + "\n");
	}
	
	public void print() {
		System.out.println("title = " + this.title + "\n");
		System.out.println("address = " + this.address + "\n");
		System.out.println("rating = " + this.rating + "\n");
		System.out.println("review title = " + this.reviewTitle + "\n");
		System.out.println("review = " + this.review + "\n");
		System.out.println("longitude = " + this.longitude + "\n");
		System.out.println("latitude = " + this.latitude + "\n");
	}

	public int getRestId() {
		return restId;
	}

	public void setRestId(int restId) {
		this.restId = restId;
	}
}

package restaurants;

import java.io.Serializable;

public class Restaurant implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String address;
	private String rating;
	private String reviewTitle;
	private String review;
	private String longitude;
	private String latitude;


	Restaurant(String titleR, String addressR, String ratingR, String reviewTitleR, String reviewR, String longitudeR, 
			String latitudeR) {
		title = titleR;
		address = addressR;
		rating = ratingR;
		reviewTitle = reviewTitleR;
		review = reviewR;
		longitude = longitudeR;
		latitude = latitudeR;
	}
	
	void print() {
		System.out.println("title = " + this.title + "\n");
		System.out.println("address = " + this.address + "\n");
		System.out.println("rating = " + this.rating + "\n");
		System.out.println("review title = " + this.reviewTitle + "\n");
		System.out.println("review = " + this.review + "\n");
		System.out.println("longitude = " + this.longitude + "\n");
		System.out.println("latitude = " + this.latitude + "\n");
	}
}

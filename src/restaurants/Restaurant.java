package restaurants;

public class Restaurant {
	
	private String title;
	private String address;
	private String rating;
	private String review_title;
	private String review;
	private String longitude;
	private String latitude;


	Restaurant(String title_r, String address_r, String rating_r, String review_title_r, String review_r, String longitude_r, 
			String latitude_r) {
		title = title_r;
		address = address_r;
		rating = rating_r;
		review_title = review_title_r;
		review = review_r;
		longitude = longitude_r;
		latitude = latitude_r;
	}
	
	void print() {
		System.out.println("title = " + this.title + "\n");
		System.out.println("address = " + this.address + "\n");
		System.out.println("rating = " + this.rating + "\n");
		System.out.println("review title = " + this.review_title + "\n");
		System.out.println("review = " + this.review + "\n");
		System.out.println("longitude = " + this.longitude + "\n");
		System.out.println("latitude = " + this.latitude + "\n");
	}
}

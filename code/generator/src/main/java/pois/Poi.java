package pois;

import java.util.ArrayList;
import java.util.List;

import checkIns.CheckIn;

public class Poi {

	private List<CheckIn> checkIns = new ArrayList<CheckIn>();
	private List<Review> reviews = new ArrayList<Review>();

	private int poiId;
	private String title;
	private String address;
	private double longitude;
	private double latitude;

	public Poi(int id, String titleR, String addressR, String ratingR,
			String reviewTitleR, String reviewR, double latitudeR, double longitudeR) {

		poiId = id;
		if (titleR.contains("'"))
			title = titleR.replace("'", "''");
		else
			title = titleR;
		if (addressR.contains("'"))
			address = addressR.replace("'", "''");
		else
			address = addressR;
		longitude = longitudeR;
		latitude = latitudeR;
		Review rev = new Review(ratingR, reviewTitleR, reviewR);
		reviews.add(rev);
	}

	public void addCheckIn(CheckIn chk) {
		checkIns.add(chk);
	}

	public void addReview(Review rev) {
		reviews.add(rev);
	}

	public void removeReview() {
		reviews.remove(0);
	}

	public void print() {
		System.out.println("----------------Poi no." + poiId
				+ "-------------------");
		System.out.print("title = " + this.title + "\t");
		System.out.print("address = " + this.address + "\t");
		System.out.print("latitude = " + this.latitude + "\n");
		System.out.print("longitude = " + this.longitude + "\t");
		for (Review rev : reviews) {
			rev.print();
		}
		for (CheckIn chk : checkIns) {
			chk.print();
		}
	}

	public int getPoiId() {
		return poiId;
	}

	public void setPoiId(int restId) {
		this.poiId = restId;
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

	@Override
	public boolean equals(Object obj) {
		if (poiId == ((Poi) obj).getPoiId()) {
			return true;
		}
		return false;
	}
}


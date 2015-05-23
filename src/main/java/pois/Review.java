package pois;

public class Review {

	private String rating;
	private String reviewTitle;
	private String review;
	
	public Review(String rate, String rTitle, String rev) {
		rating = rate;
		
		reviewTitle = rTitle;
		if (reviewTitle.contains("'"))
			reviewTitle = reviewTitle.replace("'", "''");
		if (reviewTitle.contains("\""))
			reviewTitle = reviewTitle.replace("\"", "");
		
		review = rev;	
		if (review.contains("'"))
			review = review.replace("'", "''");
		if (review.contains("\""))
			review = review.replace("\"", "");
	}

	public void print() {
		String out = "Review:\t rating: " + rating + " \t Title: " + reviewTitle  
				+ " \t review: " + review;
		System.out.println(out);
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

}

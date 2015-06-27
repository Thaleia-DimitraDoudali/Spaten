package parser;

import containers.CheckIn;
import containers.POI;
import containers.Review;

public class ParseCheckIn {

	public ParseCheckIn() {}
	
	public CheckIn parseLine(String line) {
		double lat = 0, lng = 0;
		long timestamp;
		int travel, userId;
		String title, address, rating, revTitle, review;
		
		String[] splt = line.split("\t");
		
		userId = Integer.parseInt(splt[0].substring(0, splt[0].length()-1));
		if (splt[3].contains("false")) travel = 0; else travel = 1;
		timestamp = Long.parseLong(splt[6].substring(2, splt[6].length()-2));
		
		String[] coords = splt[1].split(", ");
		lat = Double.parseDouble(coords[0].substring(3, coords[0].length()));
		lng = Double.parseDouble(coords[1].substring(0, coords[1].length()-3));
		title = splt[4].substring(2, splt[4].length()-2);
		address = splt[5].substring(2, splt[5].length()-2);
		POI p = new POI(lat, lng, title, address);
		
		rating = splt[7].substring(2, splt[7].length()-2);
		revTitle = splt[8].substring(2, splt[8].length()-2);
		review = splt[9].substring(2, splt[9].length()-1);
		Review rev = new Review(rating, revTitle, review);
		
		CheckIn chk = new CheckIn(userId, timestamp, travel, p, rev);
		
		return chk;
	}

}

package parseJson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBconnector;
import pois.Poi;


public class Parser {

	public Poi returnPoi(int id, JSONObject obj) throws JSONException {
		
		JSONArray array;
		String titleJ   = "";
	    String addressJ = "";
	    String ratingJ  = "";
	    String rtitleJ  = "";
	    String reviewJ  = "";
		double longitude = -1;
		double latitude  = -1;
		
		/*get title*/				
		if (obj.has("title")) {
			array= obj.getJSONArray("title");
			for (int j = 0; j < array.length(); j++) {
				titleJ = titleJ + array.getString(j);
			}
			titleJ = titleJ.replaceAll("\n", "");
		}
		
		/*get address*/
		if (obj.has("address")) {
			array = obj.getJSONArray("address");
			for (int j = 0; j < array.length(); j++) {
				addressJ = addressJ + array.getString(j);
			}
			addressJ = addressJ.replaceAll("\n", "");
		}
	
		/*get rating*/
		if (obj.has("rating")) {
			array = obj.getJSONArray("rating");
			for (int j = 0; j < array.length(); j++) {
				ratingJ = ratingJ + array.getString(j);
			}
			ratingJ = ratingJ.replaceAll("\n", "");
		}
	
		/*get review title*/
		if (obj.has("rtitle")) {
			array = obj.getJSONArray("rtitle");
			for (int j = 0; j < array.length(); j++) {
				rtitleJ = rtitleJ + array.getString(j);
			}
			rtitleJ = rtitleJ.replaceAll("\n", "");
		}
	
		/*get review*/
		if (obj.has("review")) {
			array = obj.getJSONArray("review");
			for (int j = 0; j < array.length(); j++) {
				reviewJ = reviewJ + array.getString(j);
			}
			reviewJ = reviewJ.replaceAll("\n", "");
		}
	
		/*get longitude - REVERSED*/
		if (obj.has("longitude")) {
			latitude = obj.getDouble("longitude");
		}
		
		/*get latitude - REVERSED*/
		if (obj.has("latitude")) {
			longitude = obj.getDouble("latitude");
		}
		
		/*create restaurant object*/
		Poi p = new Poi(id, titleJ, addressJ, ratingJ, rtitleJ, reviewJ, latitude, longitude);
		
		return p;
		
	}
	
	public void parseStorePois(DBconnector db, String path) throws IOException, JSONException {

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
        line = br.readLine();
        int i = 1;
        while (line != null){
			JSONObject obj = new JSONObject(line);
			Poi p = returnPoi(i+1, obj);
			//Add only those that are an actual poi
			if ((p.getLatitude() != -1) && (p.getLongitude() != -1)) {
				//If poi with that title, already exists on DB, merge their reviews
				int id = db.getPoiTitle(p.getTitle());
				if (id != -1) {
					db.addReview(id, p.getReviews().get(0));
				} else {
					//Insert Poi to DB
					db.insertPoi(p);
				}
			}
			line=br.readLine();
		}
        br.close();
	}
}

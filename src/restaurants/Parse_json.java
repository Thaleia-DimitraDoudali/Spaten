package restaurants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;


public class Parse_json {

	public static void main(String[] args) throws JSONException, FileNotFoundException, IOException, ParseException {
		
		List<Restaurant> restaurants_list = new ArrayList<Restaurant>();
		
		JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("/home/thaleia/Desktop/items-100.json"))));
		
		/*Iterate through all json objects*/
		for (int i = 0; i < arr.length(); i++) {
		    			
			String title_j   = "";
		    String address_j = "";
		    String rating_j  = "";
		    String rtitle_j  = "";
		    String review_j  = "";
			String longitude = "";
			String latitude  = "";
			
			try {	
				JSONObject obj = arr.getJSONObject(i);
						    
				/*get title*/
				JSONArray array = obj.getJSONArray("title");
				for (int j = 0; j < array.length(); j++) {
					title_j = title_j + array.getString(j);
				}
				title_j = title_j.replaceAll("\n", "");
				
				/*get address*/
				array = obj.getJSONArray("address");
				for (int j = 0; j < array.length(); j++) {
					address_j = address_j + array.getString(j);
				}
				address_j = address_j.replaceAll("\n", "");
			
				/*get rating*/
				array = obj.getJSONArray("rating");
				for (int j = 0; j < array.length(); j++) {
					rating_j = rating_j + array.getString(j);
				}
				rating_j = rating_j.replaceAll("\n", "");
			
				/*get review title*/
				array = obj.getJSONArray("rtitle");
				for (int j = 0; j < array.length(); j++) {
					rtitle_j = rtitle_j + array.getString(j);
				}
				rtitle_j = rtitle_j.replaceAll("\n", "");
			
				/*get review*/
				array = obj.getJSONArray("review");
				for (int j = 0; j < array.length(); j++) {
					review_j = review_j + array.getString(j);
				}
				review_j = review_j.replaceAll("\n", "");
			
				/*get longitude*/
				longitude = obj.getString("longitude");
				
				/*get latitude*/
				latitude = obj.getString("latitude");

			} catch (JSONException e) {}
			
			/*create restaurant object*/
			Restaurant rest = new Restaurant(title_j, address_j, rating_j, rtitle_j, review_j, longitude, latitude);
			restaurants_list.add(rest);
		}
		for (int i = 0; i < restaurants_list.size(); i++) {
			System.out.println("\n----------------RESTAURANT " + (i+1)  + "------------------\n");
			restaurants_list.get(i).print();
		}
	}

}

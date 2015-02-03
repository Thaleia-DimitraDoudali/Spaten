package restaurants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.HTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;


public class ParseJson {
	
	public static List<Restaurant> restaurantsList = new ArrayList<Restaurant>();
	
	public void parseRestaurants() throws JSONException, FileNotFoundException, IOException, ParseException {
		
		JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("/home/thaleia/Desktop/items-100.json"))));
		
		/*Iterate through all json objects*/
		for (int i = 0; i < arr.length(); i++) {
		    			
			String titleJ   = "";
		    String addressJ = "";
		    String ratingJ  = "";
		    String rtitleJ  = "";
		    String reviewJ  = "";
			String longitude = "";
			String latitude  = "";
			
			try {	
				JSONObject obj = arr.getJSONObject(i);
						    
				/*get title*/
				JSONArray array = obj.getJSONArray("title");
				for (int j = 0; j < array.length(); j++) {
					titleJ = titleJ + array.getString(j);
				}
				titleJ = titleJ.replaceAll("\n", "");
				
				/*get address*/
				array = obj.getJSONArray("address");
				for (int j = 0; j < array.length(); j++) {
					addressJ = addressJ + array.getString(j);
				}
				addressJ = addressJ.replaceAll("\n", "");
			
				/*get rating*/
				array = obj.getJSONArray("rating");
				for (int j = 0; j < array.length(); j++) {
					ratingJ = ratingJ + array.getString(j);
				}
				ratingJ = ratingJ.replaceAll("\n", "");
			
				/*get review title*/
				array = obj.getJSONArray("rtitle");
				for (int j = 0; j < array.length(); j++) {
					rtitleJ = rtitleJ + array.getString(j);
				}
				rtitleJ = rtitleJ.replaceAll("\n", "");
			
				/*get review*/
				array = obj.getJSONArray("review");
				for (int j = 0; j < array.length(); j++) {
					reviewJ = reviewJ + array.getString(j);
				}
				reviewJ = reviewJ.replaceAll("\n", "");
			
				/*get longitude*/
				longitude = obj.getString("longitude");
				
				/*get latitude*/
				latitude = obj.getString("latitude");

			} catch (JSONException e) {}
			
			/*create restaurant object*/
			Restaurant rest = new Restaurant(titleJ, addressJ, ratingJ, rtitleJ, reviewJ, longitude, latitude);
			restaurantsList.add(rest);
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException  {
		ParseJson parser = new ParseJson();
		/*parse json file with restaurants*/
		try {
			parser.parseRestaurants();
		} catch (Exception e) {}
		
		/*serialize each restaurant java object to a byte array*/
		/*Put each serialized restaurant to hbase*/
		
		HBaseRestaurants hbaseRest = new HBaseRestaurants();
		HTable htable = hbaseRest.constructHTable();
		
		Serializer ser = new Serializer();
		
		for (int i = 0; i < restaurantsList.size(); i++) {
			try {
				byte[] bytes = ser.serialize(restaurantsList.get(i));
				hbaseRest.putHTable(i, bytes, htable);
				byte[] getBytes = hbaseRest.getHTable(i, htable);
				Restaurant rst = ser.deserialize(getBytes);
			} catch (Exception e) {}
		}
		System.out.println("all done!");
	}

}

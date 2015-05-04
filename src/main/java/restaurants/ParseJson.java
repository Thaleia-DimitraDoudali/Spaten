package restaurants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class ParseJson {
	public Restaurant returnRestaurant(JSONObject obj) throws JSONException {
		
		JSONArray array;
		String titleJ   = "";
	    String addressJ = "";
	    String ratingJ  = "";
	    String rtitleJ  = "";
	    String reviewJ  = "";
		String longitude = "";
		String latitude  = "";
		
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
	
		/*get longitude*/
		if (obj.has("longitude")) {
			longitude = obj.getString("longitude");
		}
		
		/*get latitude*/
		if (obj.has("latitude")) {
			latitude = obj.getString("latitude");
		}
		
		/*create restaurant object*/
		Restaurant rest = new Restaurant(titleJ, addressJ, ratingJ, rtitleJ, reviewJ, longitude, latitude);
		
		return rest;
		
	}
	
	public ArrayList<Restaurant> createRestaurants(String path) throws IOException, JSONException {
		
		ArrayList<Restaurant> restaurantsList = new ArrayList<Restaurant>();

		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
        line=br.readLine();
        while (line != null){
			JSONObject obj = new JSONObject(line);
			Restaurant rest = returnRestaurant(obj);
			restaurantsList.add(rest);
        	line=br.readLine();
        }
        return restaurantsList;
	}
	
	
	public ArrayList<Restaurant> parseRestaurants() throws JSONException, FileNotFoundException, IOException, ParseException {
		
		List<Restaurant> restaurantsList = new ArrayList<Restaurant>();
		
		//Read local file
		//JSONArray arr = new JSONArray(new String(Files.readAllBytes(Paths.get("/home/thaleia/Desktop/thesis/items/items-100.json"))));
		
		//Read file on HDFS
		try{
			Path pt=new Path("/data/items-100-new.json");
            FileSystem fs = FileSystem.get(new Configuration());
            BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
            String line;
            line=br.readLine();
            while (line != null){
				JSONObject obj = new JSONObject(line);
				Restaurant rest = returnRestaurant(obj);
				//rest.print();
				restaurantsList.add(rest);
            	line=br.readLine();
            }
		} catch(Exception e){
			e.printStackTrace();
		}
	
		return (ArrayList<Restaurant>) restaurantsList;
	}
	
	public void storeToHBase(ArrayList<Restaurant> restList) throws IOException {
		
		/*serialize each restaurant java object to a byte array*/
		/*Put each serialized restaurant to hbase*/
		
		HBaseRestaurants hbaseRest = new HBaseRestaurants();
		HTable htable = hbaseRest.constructHTable();
		
		Serializer ser = new Serializer();
		
		for (int i = 0; i < restList.size(); i++) {
			try {
				byte[] bytes = ser.serialize(restList.get(i));
				hbaseRest.putHTable(i, bytes, htable);
				byte[] getBytes = hbaseRest.getHTable(i, htable);
				Restaurant rst = ser.deserialize(getBytes);
				//rst.print();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, Exception  {
			
		List<Restaurant> listOfRestaurants = new ArrayList<Restaurant>();
		
		ParseJson parser = new ParseJson();
		//parse json file with restaurants
		try {
			listOfRestaurants = parser.parseRestaurants();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		parser.storeToHBase((ArrayList<Restaurant>) listOfRestaurants);
		
		System.out.println("all done!");
	}

}

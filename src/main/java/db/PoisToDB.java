package db;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import parseJson.Parser;
import pois.Poi;

public class PoisToDB {

	public static void main(String[] args) {
		//Step 1: Parse json files - create poi's hash map
		Parser parser = new Parser();
		HashMap<Integer, Poi> pois = new HashMap<Integer, Poi>();
		try {
			 pois.putAll(parser.createPois(args[0]));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//Step 2: Store each poi to PostgreSQL Database
		DBconnector db = new DBconnector();
		db.connect();
		db.dropPoiTable();
		db.createPoiTable();
		db.dropRevTable();
		db.createRevTable();
		for (Map.Entry<Integer, Poi> entry : pois.entrySet()) {		
		    System.out.println(entry.getKey() + "/");
		    entry.getValue().print();
			db.insertPoi(entry.getValue());
		}
	}

}

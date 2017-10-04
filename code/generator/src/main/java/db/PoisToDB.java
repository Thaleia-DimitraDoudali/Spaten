package db;

import java.io.IOException;
import org.json.JSONException;
import parseJson.Parser;

public class PoisToDB {

	public static void main(String[] args) {
		
		//Create tables at DB
		DBconnector db = new DBconnector();
		db.connect();
		db.dropPoiTable();
		db.createPoiTable();
		db.dropRevTable();
		db.createRevTable();
		
		//Store pois to DB while parsing json file
		Parser parser = new Parser();
		try {
			parser.parseStorePois(db, args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}


package checkIns;

import java.util.ArrayList;
import java.util.List;

import restaurants.ParseJson;
import restaurants.Restaurant;

public class CreateCheckIn {
	
	List<Restaurant> restaurants = new ArrayList<Restaurant>();


	public CreateCheckIn(String file) {
		createRestaurants(file);
		for (int i = 0; i < restaurants.size(); i++) {
			System.out.println("----------------" + (i+1) + "------------------");
			restaurants.get(i).print();
		}
	}
	
	public void createRestaurants(String file) {
		
		ParseJson parser = new ParseJson();
		//parse json file with restaurants
		try {
			restaurants = parser.createRestaurants(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new CreateCheckIn(args[0]);
	}

}

package checkIns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import restaurants.ParseJson;
import restaurants.Restaurant;

public class CreateCheckIn {
	
	private List<Restaurant> restaurants = new ArrayList<Restaurant>();
	private List<Integer> users = new ArrayList<Integer>();

	public CreateCheckIn(String file, int userNum, int mean, int dev) {
		
		createRestaurants(file);
		
		for (int i = 1; i <= userNum; i++) {
			System.out.println("User no." + i + ":");
			users.add(i);
			//how many check-in's per user?
			int checkNum = createGaussianRandom(mean, dev);
			System.out.println(" number of check-ins: " + checkNum);
			//create check-in's
			for (int j = 0; j < checkNum; j++){
				//choose a random restaurant from the list of restaurants
				int restNo = createUniformIntRandom(restaurants.size());
				long timestamp = createRandomTime();
				String date = getDate(timestamp);
				System.out.println("\t restaurant chosen: " + restNo + "\t timestamp: " + timestamp + "\t date: "
						+ date);
			}
		}
	}
	
	public String getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
				
		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH); 
		if (mMonth == 0)
			mMonth ++;
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		String res = mMonth + "/" + mDay + "/" + mYear;
		return res;
	}
	
	public long createRandomTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.clear();
		calendar.set(2015, Calendar.JANUARY, 1);
		long fromMillis = calendar.getTimeInMillis();
		calendar.clear();
		calendar.set(2015, Calendar.MAY, 30);
		long toMillis = calendar.getTimeInMillis();
		long range = toMillis - fromMillis + 1;
		long res = fromMillis + (long)(Math.random() * range);
		return res;
	}
	
	public int createUniformIntRandom(int range) {
		Random r = new Random();
		int res = r.nextInt(range);
		return res;
	}
	
	public int createGaussianRandom(int mean, int dev) {
		Random r = new Random();
		double val = r.nextGaussian()*dev + mean;
		int res = (int) Math.round(val);
		return res;
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

	public List<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	public static void main(String[] args) {
		//args[0]: json file with restaurants
		//args[1]: number of users
		//args[2], args[3]: mean and standard deviation for the gaussian that determines that determines the number
		//of check-in's per user
		new CreateCheckIn(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
	}

}

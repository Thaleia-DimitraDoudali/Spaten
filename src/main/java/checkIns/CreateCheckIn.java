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
	private List<User> users = new ArrayList<User>();

	public CreateCheckIn(String file, int userNum, int mean, int dev) {
		
		createRestaurants(file);
		
		for (int i = 1; i <= userNum; i++) {
			User usr = new User(i);
			users.add(usr);
			//how many check-in's per user?
			int checkNum = createGaussianRandom(mean, dev);
			//create check-in's
			for (int j = 0; j < checkNum; j++){
				//choose a random restaurant from the list of restaurants
				int restNo = createUniformIntRandom(restaurants.size());
				long timestamp = createRandomTime();
				CheckIn chk = new CheckIn(usr.getUserId(), restaurants.get(restNo), timestamp);
				usr.addCheckIn(chk);
			}
		}
	}
	
	public void printUsers() {
		for (User usr : users) {
			System.out.println("User no." + usr.getUserId() + ":");
			System.out.println(" number of check-ins: " + usr.getCheckIns().size());
			usr.print();
		}
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
	
	public List<CheckIn> getAllCheckIns() {
		List<CheckIn> chks = new ArrayList<CheckIn>();
		for (User usr: users) {
			for (CheckIn chk: usr.getCheckIns()) {
				chks.add(chk);
			}
		}
		return chks;
	}

	public List<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public static void main(String[] args) {
		//args[0]: json file with restaurants
		//args[1]: number of users
		//args[2], args[3]: mean and standard deviation for the gaussian that determines that determines the number
		//of check-in's per user
		
		//creates list of users, each user has a list of check-in's
		CreateCheckIn chkin = new CreateCheckIn(args[0], Integer.parseInt(args[1]), 
				Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		chkin.printUsers();
		//get all check-in's ever
		List<CheckIn> chks = chkin.getAllCheckIns();
		for (CheckIn chk: chks) {
			chk.print();
		}
	}
}

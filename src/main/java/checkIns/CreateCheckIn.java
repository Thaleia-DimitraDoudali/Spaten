package checkIns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import restaurants.ParseJson;
import restaurants.Restaurant;
import restaurants.Review;

public class CreateCheckIn {
	
	HashMap<Integer, Restaurant> restaurantsMap = new HashMap<Integer, Restaurant>();
	private List<User> users = new ArrayList<User>();

	public CreateCheckIn(String file, int userNum, int mean, int dev) {
		
		createRestaurants(file);
		//printRsts();
		
		for (int i = 1; i <= userNum; i++) {
			User usr = new User(i);
			users.add(usr);
			//how many check-in's per user?
			int checkNum = createGaussianRandom(mean, dev);
			//create check-in's
			for (int j = 0; j < checkNum; j++){
				//choose a random restaurant from the list of restaurants
				int restNo = createUniformIntRandom(restaurantsMap.size());
				Restaurant rst = restaurantsMap.get(restNo);
				//If the restaurant has available reviews, then assign it to a user
				if (rst.getReviews().size() != 0) {
					Review review = rst.getReviews().get(0);
					long timestamp = createRandomTime();
					CheckIn chk = new CheckIn(usr.getUserId(), rst, timestamp, review);
					usr.addCheckIn(chk);
					rst.addCheckIn(chk);
					rst.removeReview();
				} else { //else the user looses that check-in
					//System.out.println("No review available" + restNo);
				}
				
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
	
	public void printRsts() {
		Iterator<Integer> keySetIterator = restaurantsMap.keySet().iterator();
		while(keySetIterator.hasNext()){
			  Integer key = keySetIterator.next();
			  System.out.println("key: " + key + " value: ");
			  restaurantsMap.get(key).print();
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
		int res = r.nextInt(range) + 1;
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
			restaurantsMap = parser.createRestaurants(file);
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
		
		/*creates list of users, each user has a list of check-in's*/
		CreateCheckIn chkin = new CreateCheckIn(args[0], Integer.parseInt(args[1]), 
				Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		chkin.printUsers();
		
		/*get all check-in's ever*/
		List<CheckIn> chks = chkin.getAllCheckIns();
		/*for (CheckIn chk: chks) {
			chk.print();
		}*/
		
		/*Each Restaurant now has all his check-in's*/
		//chkin.printRsts();
		
	}
}

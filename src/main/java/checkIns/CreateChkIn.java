package checkIns;

import googleMaps.Route;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import pois.Poi;
import pois.Review;
import db.DBconnector;

public class CreateChkIn {

	private List<User> users = new ArrayList<User>();

	public void createDailyCheckIn(User usr, int chkNum, int poisNum,
			DBconnector db, double dist) {
		// First poi will be random
		int restNo = createUniformIntRandom(poisNum);
		Poi p = db.getPoi(restNo);
		// User starts-off his day at 9am and will stay 2h at the first poi
		// We assign a random review
		int revNo = createUniformIntRandom(p.getReviews().size()) - 1;
		Review review = p.getReviews().get(revNo);
		long timestamp = createTimestamp();
		CheckIn chk = new CheckIn(usr.getUserId(), p, timestamp, review);
		usr.addCheckIn(chk);
		p.addCheckIn(chk);
		ArrayList<Poi> poisInRange = new ArrayList<Poi>();
		// for (int i = 1; i < chkNum; i++) {
		// Find the next poi, it will be random but in range of parameter km
		System.out.println("Finding in range from poi " + p.getTitle());
		poisInRange = db.findInRange(p.getPoiId(), p.getLongitude(),
				p.getLatitude(), dist);
		if (!poisInRange.isEmpty()){
			for (Poi poi : poisInRange)
				System.out.println(poi.getTitle());
			//Choose one random between those!!
			restNo = createUniformIntRandom(poisInRange.size()) - 1;
			p = poisInRange.get(restNo);
			System.out.println("Chose poi " + p.getTitle());
		}
	}

	public void printUsers() {
		for (User usr : users) {
			System.out.println("User no." + usr.getUserId() + ":");
			System.out.println(" number of check-ins: "
					+ usr.getCheckIns().size());
			usr.print();
		}
	}

	public long createTimestamp() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// TODO: date as parameter
		calendar.set(2015, Calendar.FEBRUARY, 1, 9 - 2, 0, 0);
		return calendar.getTimeInMillis();
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
		long res = fromMillis + (long) (Math.random() * range);
		return res;
	}

	public int createUniformIntRandom(int range) {
		Random r = new Random();
		int res = r.nextInt(range) + 1;
		return res;
	}

	public int createGaussianRandom(int mean, int dev) {
		Random r = new Random();
		double val = r.nextGaussian() * dev + mean;
		int res = (int) Math.round(val);
		return res;
	}

	public List<CheckIn> getAllCheckIns() {
		List<CheckIn> chks = new ArrayList<CheckIn>();
		for (User usr : users) {
			for (CheckIn chk : usr.getCheckIns()) {
				chks.add(chk);
			}
		}
		return chks;
	}

	public void createUserRoutes() {
		for (User usr : users) {
			Route rt = new Route();
			rt.createRoutes(usr);
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public static void main(String[] args) {
		// args[0]: json file with restaurants
		// args[1]: number of users
		// args[2], args[3]: mean and standard deviation for the gaussian that
		// determines that determines the number
		// of check-in's per user

		/* creates list of users, each user has a list of check-in's */
		// CreateCheckIn chkin = new CreateCheckIn(args[0],
		// Integer.parseInt(args[1]),
		// Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		// chkin.printUsers();

		// For each user take the list of his check-in's and create the
		// intermediate routes from google maps api
		// chkin.createUserRoutes();

		/* get all check-in's ever */
		/*
		 * List<CheckIn> chks = chkin.getAllCheckIns(); for (CheckIn chk: chks)
		 * { chk.print(); }
		 */

		/* Each Restaurant now has all his check-in's */
		// chkin.printRsts();

	}
}

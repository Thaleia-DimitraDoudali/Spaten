package checkIns;

import googleMaps.Route;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONException;

import pois.GPSTrace;
import pois.Poi;
import pois.Review;
import db.DBconnector;

public class CreateChkIn {

	private List<User> users = new ArrayList<User>();

	public void createDailyCheckIn(User usr, int chkNum, int poisNum,
			DBconnector db, double dist) {

		ArrayList<Poi> poisVisited = new ArrayList<Poi>();
		ArrayList<Poi> poisInRange = new ArrayList<Poi>();
		Route rt = new Route();

		/* Step 1: First poi will be random - starts 9am - random review as well */
		int restNo = createUniformIntRandom(poisNum);
		Poi p = db.getPoi(restNo);
		int revNo = createUniformIntRandom(p.getReviews().size()) - 1;
		Review review = p.getReviews().get(revNo);
		long timestamp = createTimestamp(9, 0, 0);
		CheckIn chk = new CheckIn(usr.getUserId(), p, timestamp, review);
		usr.addCheckIn(chk);
		p.addCheckIn(chk);
		poisVisited.add(p);
		long timeBefore = usr.getCheckIns().get(0).getTimestamp();
		usr.getTraces().add(new GPSTrace(p.getLatitude(), p.getLongitude(), timestamp, usr.getUserId()));
		/*
		 * Step 2: Every other poi will have to be in range from the previous
		 * (random choice), it should not be a poi he already visited that day,
		 * he will stay at each poi for 2h.
		 */

		for (int i = 1; i < chkNum; i++) {
			System.out.println("Check in no." + i);
			poisInRange = db.findInRange(p.getPoiId(), p.getLongitude(),
					p.getLatitude(), dist);
			for (Poi poi : poisVisited) {
				poisInRange.remove(poi);
			}
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				Poi newP = poisInRange.get(restNo);
				System.out.println("Route: (" + p.getLatitude() + ", " + p.getLongitude()
						+ ") -> (" + newP.getLatitude() + ", " + newP.getLongitude() + ")");
				String jsonRoute = rt.getRoute(p.getLongitude(),
						p.getLatitude(), newP.getLongitude(),
						newP.getLatitude());
				double duration = 0;
				try {
					duration = rt.getDuration(jsonRoute);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				long time = timeBefore + 2 * 3600 * 1000 + (long) duration
						* 1000;
				timeBefore = time;
				rt.getPoisBetween(jsonRoute, db, time, usr);
				revNo = createUniformIntRandom(newP.getReviews().size()) - 1;
				review = newP.getReviews().get(revNo);
				chk = new CheckIn(usr.getUserId(), newP, time, review);
				usr.addCheckIn(chk);
				newP.addCheckIn(chk);
				poisVisited.add(newP);
				p = newP;
				// TODO: get and store the intermediate long lats - store
				// somehow all gps traces he went by
			} else {
				/* If no pois found in range, that means the user cannot go anywhere else,
				 * so the day ends there 
				 */
				break;
			}
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

	public long createTimestamp(int hour, int min, int sec) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// TODO: date as parameter
		calendar.set(2015, Calendar.FEBRUARY, 1, hour - 2, min, sec);
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

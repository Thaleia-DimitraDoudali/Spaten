package checkIns;

import googleMaps.Route;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	Poi travelPoi = null;

	public void createDailyCheckIn(User usr, int chkNum, int poisNum,
			DBconnector db, double dist, double maxDist, double chkDurMean,
			double chkDurStDev, int startTime, int endTime, long date,
			boolean home, boolean travel, long travelDay) {

		ArrayList<Poi> poisVisited = new ArrayList<Poi>();
		ArrayList<Poi> poisInRange = new ArrayList<Poi>();
		Route rt = new Route();
		int restNo = -1, revNo = -1;
		long timeBefore = -1;
		Review review;
		CheckIn chk;
		Poi p = null;

		/* Step 1: First poi will be random - starts 9am - random review as well */
		// First poi of the day must be in maxDist range from his home
		// Determine hometown - the first check-in is his home
		if (travel && (travelDay == 1) && !home) { // pick sthn tuxi, determine
													// start of the trip
			System.out.println("1");
			restNo = createUniformIntRandom(poisNum);
			travelPoi = db.getPoi(restNo);
			p = travelPoi;
		} else if (travel && travelDay != 1 && !home) { // first poi should be
														// in maxDist range from
														// travelPoi
			System.out.println("2");
			poisInRange = db.findInRange(travelPoi.getPoiId(),
					travelPoi.getLongitude(), travelPoi.getLatitude(), maxDist);
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				p = poisInRange.get(restNo);
			}
		}
		// TODO: what if it starts with travel
		else if (!travel && home) { // if it is the home to be determined it
									// should be totally random
			System.out.println("3");
			restNo = createUniformIntRandom(poisNum);
			p = db.getPoi(restNo);
			usr.setHome(p);
		} else if (!travel && !home) { // if home is determined - all other
										// check-in's should be in maxDist range
										// from home
			System.out.println("4");
			Poi h = usr.getHome();
			poisInRange = db.findInRange(h.getPoiId(), h.getLongitude(),
					h.getLatitude(), maxDist);
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				p = poisInRange.get(restNo);
			}
		}
		
		revNo = createUniformIntRandom(p.getReviews().size()) - 1;
		review = p.getReviews().get(revNo);
		long timestamp = date + startTime * 3600 * 1000;
		chk = new CheckIn(usr.getUserId(), p, timestamp, review);
		usr.addCheckIn(chk);
		p.addCheckIn(chk);
		poisVisited.add(p);
		timeBefore = usr.getCheckIns().get(0).getTimestamp();
		usr.getTraces().add(
				new GPSTrace(p.getLatitude(), p.getLongitude(),
						timestamp, usr.getUserId()));

		/*
		 * Step 2: Every other poi will have to be in range from the previous
		 * (random choice), it should not be a poi he already visited that day,
		 * he will stay at each poi for 2h.
		 */

		for (int i = 1; i < chkNum; i++) {
			System.out.println("Check in no." + i);
			//if p is null, that means no pois where found in range from home or travelPoi
			//so break
			if (p == null)
				break;
			poisInRange = db.findInRange(p.getPoiId(), p.getLongitude(),
					p.getLatitude(), dist);
			for (Poi poi : poisVisited) {
				poisInRange.remove(poi);
			}
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				Poi newP = poisInRange.get(restNo);
				System.out.println("Route: (" + p.getLatitude() + ", "
						+ p.getLongitude() + ") -> (" + newP.getLatitude()
						+ ", " + newP.getLongitude() + ")");
				String jsonRoute = rt.getRoute(p.getLongitude(),
						p.getLatitude(), newP.getLongitude(),
						newP.getLatitude());
				double duration = 0;
				try {
					duration = rt.getDuration(jsonRoute);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				long checkDur = (long) createDoubleGaussianRandom(chkDurMean,
						chkDurStDev);
				long time = timeBefore + checkDur * 3600 * 1000
						+ (long) duration * 1000;
				if (time > (date + endTime * 3600 * 1000)) {
					System.out
							.println("Exceeded the time available for today's check-in's");
					break;
				}
				timeBefore = time;
				rt.getPoisBetween(jsonRoute, db, time, usr);
				revNo = createUniformIntRandom(newP.getReviews().size()) - 1;
				review = newP.getReviews().get(revNo);
				chk = new CheckIn(usr.getUserId(), newP, time, review);
				usr.addCheckIn(chk);
				newP.addCheckIn(chk);
				poisVisited.add(newP);
				p = newP;
			} else {
				/*
				 * If no pois found in range, that means the user cannot go
				 * anywhere else, so the day ends there
				 */
				break;
			}
		}
		/*
		 * Create map for the daily traversal - poisVisited TODO: see path for
		 * intermediate gps traces!
		 */
		String url = "https://maps.googleapis.com/maps/api/staticmap?&zoom=13&size=1000x1000";
		for (Poi poi : poisVisited) {
			url += "&markers=" + poi.getLatitude() + "," + poi.getLongitude();
		}
		System.out.println(url);
	}

	public void printUsers() {
		for (User usr : users) {
			System.out.println("User no." + usr.getUserId() + ":");
			System.out.println(" number of check-ins: "
					+ usr.getCheckIns().size());
			usr.print();
		}
	}

	public long convertToTimestamp(String date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// date will be in the format e.g. 04/29/1992
		int month = Integer.parseInt(date.substring(0, 2)) - 1;
		int day = Integer.parseInt(date.substring(3, 5));
		int year = Integer.parseInt(date.substring(6, 10));
		System.out.println(month + " " + day + " " + year);
		calendar.set(year, month, day, -2, 0, 0);
		return calendar.getTimeInMillis();
	}

	public long createTimestamp(int hour, int min, int sec) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.set(2015, Calendar.FEBRUARY, 1, hour - 2, min, sec);
		return calendar.getTimeInMillis();
	}

	public Date getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);

		Date dt = calendar.getTime();

		return dt;
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

	public double createDoubleGaussianRandom(double mean, double dev) {
		Random r = new Random();
		double val = r.nextGaussian() * dev + mean;
		return val;
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

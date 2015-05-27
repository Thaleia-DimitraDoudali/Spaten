package checkIns;

import googleMaps.MapURL;
import googleMaps.Polyline;
import googleMaps.Route;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import launch.OutCSV;

import org.json.JSONException;

import pois.GPSTrace;
import pois.Poi;
import pois.Review;
import db.DBconnector;

public class CreateChkIn {

	Poi travelPoi = null;

	public void createDailyCheckIn(User usr, int chkNum, int poisNum, DBconnector db, double dist, 
			double maxDist, double chkDurMean, double chkDurStDev, int startTime, int endTime, long date,
			boolean home, boolean travel, long travelDay, BufferedWriter outChkCSV, 
			BufferedWriter outTrCSV, BufferedWriter outMapCSV, OutCSV csv) {

		ArrayList<Poi> poisVisited = new ArrayList<Poi>();
		ArrayList<Integer> poisInRange = new ArrayList<Integer>();
		ArrayList<GPSTrace> tracesVisited = new ArrayList<GPSTrace>();
		Route rt = new Route();
		int restNo = -1, revNo = -1;
		long timeBefore = -1;
		Review review;
		CheckIn chk;
		Poi p = null;

		/*
		 * Step 1: Determine the first poi of the day to be random
		 */
		
		//If it is the first day of travel, pick a random poi and save it as travelPoi
		if (travel && (travelDay == 1) && !home) { 
			restNo = createUniformIntRandom(poisNum);
			travelPoi = db.getPoi(restNo);
			p = travelPoi;
		} //If it's not the first day of the current travel, then choose a poi in maxDist range from travelPoi 
		else if (travel && travelDay != 1 && !home) { 
			poisInRange = db.findInRange(travelPoi.getPoiId(),
					travelPoi.getLongitude(), travelPoi.getLatitude(), maxDist);
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				p = db.getPoi(poisInRange.get(restNo));
			}
		} // If it is the first ever check-in, then set as home this random poi
		else if (!travel && home) { 
			restNo = createUniformIntRandom(poisNum);
			p = db.getPoi(restNo);
			usr.setHome(p);
		} // If home location is known, then all other pois should be in maxDist range from home 
		else if (!travel && !home) {
			Poi h = usr.getHome();
			poisInRange = db.findInRange(h.getPoiId(), h.getLongitude(),
					h.getLatitude(), maxDist);
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				p = db.getPoi(poisInRange.get(restNo));
			}
		}

		if (p != null) {
			//Select random review from the poi
			revNo = createUniformIntRandom(p.getReviews().size()) - 1;
			review = p.getReviews().get(revNo);
			//Timestamp the checkin
			long timestamp = date + startTime * 3600 * 1000;
			chk = new CheckIn(usr.getUserId(), p, timestamp, review);
			chk.setTravel(travel);
			usr.addCheckIn(chk);
			//chk.print();
			csv.appendCheckIn(outChkCSV, chk);
			p.addCheckIn(chk);
			poisVisited.add(p);
			timeBefore = timestamp;
			GPSTrace tr = new GPSTrace(p.getLatitude(), p.getLongitude(), timestamp, usr.getUserId());
			usr.addGPSTrace(tr);
			//tr.print();
			csv.appendTrace(outTrCSV, tr);
			tracesVisited.add(tr);
		}
		/*
		 * Step 2: Every other poi of the day will have to be in walking distance (dist) from
		 * the initial poi and it should not be a poi he already visited that day.
		 */

		for (int i = 1; i < chkNum; i++) {
			// if p is null, that means no pois where found in range from home or travelPoi, so break
			if (p == null)
				break;
			poisInRange = db.findInRange(p.getPoiId(), p.getLongitude(),
					p.getLatitude(), dist);
			for (Poi poi : poisVisited) {
				poisInRange.remove(new Integer(poi.getPoiId()));
			}
			if (!poisInRange.isEmpty()) {
				restNo = createUniformIntRandom(poisInRange.size()) - 1;
				Poi newP = db.getPoi(poisInRange.get(restNo));
				String jsonRoute = rt.getRoute(p.getLongitude(),
						p.getLatitude(), newP.getLongitude(),
						newP.getLatitude());
				double duration = 0;
				try {
					duration = rt.getDuration(jsonRoute);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (duration != -1) {
					long checkDur = (long) createDoubleGaussianRandom(
							chkDurMean, chkDurStDev);
					long time = timeBefore + checkDur * 3600 * 1000
							+ (long) duration * 1000;
					if (time > (date + endTime * 3600 * 1000)) {
						System.out.println("Exceeded the time available for today's check-in's");
						break;
					}
					timeBefore = time;
					try {
						tracesVisited.addAll(rt.getTracesBetween(jsonRoute, db, time, usr, csv, outTrCSV));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					revNo = createUniformIntRandom(newP.getReviews().size()) - 1;
					review = newP.getReviews().get(revNo);
					chk = new CheckIn(usr.getUserId(), newP, time, review);
					chk.setTravel(travel);
					usr.addCheckIn(chk);
					//chk.print();
					csv.appendCheckIn(outChkCSV, chk);
					newP.addCheckIn(chk);
					poisVisited.add(newP);
					p = newP;
				}
			} else {
				// If no pois found in range, that means the user cannot go anywhere else, so the day ends here
				break;
			}
		}
		
		/*
		 * Step 3: Create map for the daily traversal - poisVisited
		 */
		String url = "https://maps.googleapis.com/maps/api/staticmap?&size=1000x1000";
		char letter = 'A';
		for (Poi poi : poisVisited) {
			url += "&markers=label:" + letter + "|" + poi.getLatitude() + "," + poi.getLongitude();
			letter ++;
		}
		url += "&path=color:blue|enc:";
		//Encode path points as polylines in order to shorten url
		Polyline pl = new Polyline();
		String shortUrl = url + pl.encode(tracesVisited);
		while (shortUrl.length() > 2048) {
			//Take out 50 random traces
			for (int i = 0; i < 50; i++) {
				int ch = createUniformIntRandom(tracesVisited.size());
				tracesVisited.remove(ch);
			}
			shortUrl = url + pl.encode(tracesVisited);
		}
		MapURL mp = new MapURL(usr.getUserId(), getDate(date), shortUrl);
		usr.addDailyMap(mp);
		csv.appendMap(outMapCSV, mp);
	}

	public long convertToTimestamp(String date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// date will be in the format e.g. 04/29/1992
		int month = Integer.parseInt(date.substring(0, 2)) - 1;
		int day = Integer.parseInt(date.substring(3, 5));
		int year = Integer.parseInt(date.substring(6, 10));
		calendar.set(year, month, day, -2, 0, 0);
		return calendar.getTimeInMillis();
	}

	public Date getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);

		Date dt = calendar.getTime();

		return dt;
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
}

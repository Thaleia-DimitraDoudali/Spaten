package googleMaps;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pois.GPSTrace;
import checkIns.CheckIn;
import checkIns.User;
import db.DBconnector;

public class Route {

	public Route() {
	}

	public String getRoute(double longFrom, double latFrom, double longTo,
			double latTo) {

		// Sleep for half a second
		try {
			Thread.sleep(500); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		String res = "";
		try {
			String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
					+ latFrom
					+ ","
					+ longFrom
					+ "&destination="
					+ latTo
					+ ","
					+ longTo + "&mode=walking";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			// int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			// System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			res = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public void saveRouteToFile(String rt, double longFrom, double latFrom,
			double longTo, double latTo) {
		try {
			String workingDir = System.getProperty("user.dir");
			File file = new File(workingDir + "/" + longFrom + "-" + latFrom
					+ "_" + longTo + "-" + latTo + ".json");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(rt);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<GPSTrace> getPoisBetween(String json, DBconnector db,
			long time, User usr) throws JSONException {
		ArrayList<GPSTrace> res = new ArrayList<GPSTrace>();
		int threshold = 200;
		GPSTrace tr;
		double lngFrom = -1, latFrom = -1, lngTo = -1, latTo = -1;

		JSONObject obj = new JSONObject(json);
		JSONArray jsonRoutes = obj.getJSONArray("routes");
		JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
		JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
		JSONObject jsonLeg = jsonLegs.getJSONObject(0);
		JSONArray jsonSteps = jsonLeg.getJSONArray("steps");

		for (int i = 0; i < jsonSteps.length(); i++) {
			System.out.println("i = " + i + " vs. " + jsonSteps.length());
			JSONObject jsonStep = jsonSteps.getJSONObject(i);
			JSONObject jsonPolyline = jsonStep.getJSONObject("polyline");
			String polyline = jsonPolyline.getString("points");
			System.out.println(polyline);
			Polyline pl = new Polyline();
			pl.decodePoly(polyline);
			JSONObject jsonStart = jsonStep.getJSONObject("start_location");
			lngFrom = jsonStart.getDouble("lng");
			latFrom = jsonStart.getDouble("lat");
			System.out.println("(" + latFrom + ", " + lngFrom + ")");
			JSONObject jsonEnd = jsonStep.getJSONObject("end_location");
			lngTo = jsonEnd.getDouble("lng");
			latTo = jsonEnd.getDouble("lat");
			System.out.println("(" + latTo + ", " + lngTo + ")");

			if (i == 0) {
				tr = new GPSTrace(latFrom, lngFrom, time, usr.getUserId());
				res.add(tr);
				usr.getTraces().add(tr);
			}

			JSONObject jsonDist = jsonStep.getJSONObject("distance");
			String dist = jsonDist.getString("value");
			int d = Integer.parseInt(dist);
			System.out.println(dist + "m");
			JSONObject jsonDur = jsonStep.getJSONObject("duration");
			String dur = jsonDur.getString("value");
			double du = Double.parseDouble(dur);
			System.out.println(du + "s");
			String durat = jsonDur.getString("text");
			System.out.println(durat);
			// If a step is more than threshold then make and split line
			if (d > 2 * threshold) {
				int split = Integer.parseInt(dist) / threshold;
				double from = 0;
				double to = 1.0 / split;
				System.out.println("split = " + split + " from = " + from
						+ " to = " + to);
				for (int j = 1; j <= split; j++) {
					if (to < 1) {
						time += du * from;
						// System.out.println("Splitting " + "(" + latFrom +
						// ", " + lngFrom
						// + ") -> (" + latTo + ", " + lngTo + ")");
						tr = db.getBetween(lngFrom, latFrom, lngTo, latTo,
								from, to, time, usr);
						res.add(tr);
						from = to;
						to += 1.0 / split;
					}
				}
				// TODO: timestamp each intermediate gps trace
			} else {
				tr = new GPSTrace(latTo, lngTo, time, usr.getUserId());
				res.add(tr);
				usr.getTraces().add(tr);
			}
		}

		return res;
	}

	public ArrayList<GPSTrace> getTracesBetween(String json, DBconnector db,
			long time, User usr) throws JSONException {
		
		ArrayList<GPSTrace> res = new ArrayList<GPSTrace>();
		ArrayList<String> decPol = new ArrayList<String>();
		int threshold = 200;
		GPSTrace tr;
		double lngFrom = -1, latFrom = -1, lngTo = -1, latTo = -1;

		JSONObject obj = new JSONObject(json);
		JSONArray jsonRoutes = obj.getJSONArray("routes");
		JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
		JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
		JSONObject jsonLeg = jsonLegs.getJSONObject(0);
		JSONArray jsonSteps = jsonLeg.getJSONArray("steps");

		
		for (int i = 0; i < jsonSteps.length(); i++) {
			decPol.clear();
			System.out.println("i = " + i + " vs. " + jsonSteps.length());
			JSONObject jsonStep = jsonSteps.getJSONObject(i);
			JSONObject jsonPolyline = jsonStep.getJSONObject("polyline");
			String polyline = jsonPolyline.getString("points");
			System.out.println(polyline);

			JSONObject jsonStart = jsonStep.getJSONObject("start_location");
			lngFrom = jsonStart.getDouble("lng");
			latFrom = jsonStart.getDouble("lat");
			System.out.println("(" + latFrom + ", " + lngFrom + ")");
			JSONObject jsonEnd = jsonStep.getJSONObject("end_location");
			lngTo = jsonEnd.getDouble("lng");
			latTo = jsonEnd.getDouble("lat");
			System.out.println("(" + latTo + ", " + lngTo + ")");

			/*if (i == 0) {
				tr = new GPSTrace(latFrom, lngFrom, time, usr.getUserId());
				res.add(tr);
				usr.getTraces().add(tr);
			}*/

			JSONObject jsonDist = jsonStep.getJSONObject("distance");
			String dist = jsonDist.getString("value");
			int d = Integer.parseInt(dist);
			System.out.println(dist + "m");
			JSONObject jsonDur = jsonStep.getJSONObject("duration");
			String dur = jsonDur.getString("value");
			double du = Double.parseDouble(dur);
			System.out.println(du + "s");
			String durat = jsonDur.getString("text");
			System.out.println(durat);
			
			Polyline pl = new Polyline();
			decPol = pl.decodePoly(polyline);
			System.out.println(decPol.size());
			double latit = -1, longt = -1;
			String[] parts;
			double durPol = du / decPol.size();
			for (String s: decPol) {
				parts = s.split(",");
				latit = Double.parseDouble(parts[0]);
				longt = Double.parseDouble(parts[1]);
				time += durPol;
				System.out.println(latit + ", " + longt + " " + time);
				tr = new GPSTrace(latit, longt, time, usr.getUserId());
				res.add(tr);
				usr.getTraces().add(tr);
			}

		}

		return res;
	}

	public double getDuration(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONArray jsonRoutes = obj.getJSONArray("routes");
		if (jsonRoutes.isNull(0)) {
			return -1;
		}
		JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
		JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
		JSONObject jsonLeg = jsonLegs.getJSONObject(0);
		JSONObject jsonDuration = jsonLeg.getJSONObject("duration");

		// duration in seconds
		double value = jsonDuration.getDouble("value");
		return value;
	}

	public void createRoutes(User usr) {
		int N = usr.getCheckIns().size();
		usr.print();
		for (int i = 0; i < N - 1; i++) {
			CheckIn chkFrom = usr.getCheckIns().get(i);
			double longFrom = chkFrom.getPoi().getLongitude();
			double latFrom = chkFrom.getPoi().getLatitude();
			CheckIn chkTo = usr.getCheckIns().get(i + 1);
			double longTo = chkTo.getPoi().getLongitude();
			double latTo = chkTo.getPoi().getLatitude();
			String jsonRoute = getRoute(longFrom, latFrom, longTo, latTo);
			usr.addRoute(jsonRoute);
			System.out.println(longFrom + ", " + latFrom + " " + longTo + ", "
					+ latTo);
			saveRouteToFile(jsonRoute, longFrom, latFrom, longTo, latTo);

			// System.out.println(jsonRoute.substring(0, 30));
		}
	}
}

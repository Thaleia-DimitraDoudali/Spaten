package googleMaps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import launch.OutCSV;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pois.GPSTrace;
import checkIns.User;
import db.DBconnector;

public class Route {

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
			//System.out.println("\nSending 'GET' request to URL : " + url);

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

	public ArrayList<GPSTrace> getTracesBetween(String json, DBconnector db,
			long time, User usr, OutCSV csv, BufferedWriter outTrCSV) throws JSONException {
		
		ArrayList<GPSTrace> res = new ArrayList<GPSTrace>();
		ArrayList<String> decPol = new ArrayList<String>();
		GPSTrace tr;
		double latit = -1, longt = -1;
		String[] parts;

		JSONObject obj = new JSONObject(json);
		JSONArray jsonRoutes = obj.getJSONArray("routes");
		JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
		JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
		JSONObject jsonLeg = jsonLegs.getJSONObject(0);
		JSONArray jsonSteps = jsonLeg.getJSONArray("steps");
		
		for (int i = 0; i < jsonSteps.length(); i++) {
			decPol.clear();
			JSONObject jsonStep = jsonSteps.getJSONObject(i);
			JSONObject jsonPolyline = jsonStep.getJSONObject("polyline");
			String polyline = jsonPolyline.getString("points");
			JSONObject jsonDur = jsonStep.getJSONObject("duration");
			String dur = jsonDur.getString("value");
			double du = Double.parseDouble(dur);
			
			//Decode polyline of step
			Polyline pl = new Polyline();
			decPol = pl.decodePoly(polyline);
			//Split duration, so as to timestamp each GPS trace
			double durPol = du / decPol.size();
			for (String s: decPol) {
				parts = s.split(",");
				latit = Double.parseDouble(parts[0]);
				longt = Double.parseDouble(parts[1]);
				time += durPol;
				tr = new GPSTrace(latit, longt, time, usr.getUserId());
				res.add(tr);
				usr.addGPSTrace(tr);
				//tr.print();
				csv.appendTrace(outTrCSV, tr);
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

}

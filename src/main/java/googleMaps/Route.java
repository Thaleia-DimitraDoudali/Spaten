package googleMaps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Route {
	
	public Route() {
	}
	
	public String getRoute(String longFrom, String latFrom, String longTo, String latTo) {
		
		String res = "";
	    try {
			String url= 
					"http://maps.googleapis.com/maps/api/directions/json?origin=" 
					+ longFrom + "," + latFrom +"&destination=" 
					+ longTo + "," + latTo;
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
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

	public static void main(String[] args) {
		Route rt = new Route();
		String jsonRoute = rt.getRoute("33.30333", "44.35416", "32.0876", "34.8612");
		System.out.println(jsonRoute);
	}
}

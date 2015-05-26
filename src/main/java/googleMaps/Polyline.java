package googleMaps;

import java.util.ArrayList;

import pois.GPSTrace;

public class Polyline {

	public Polyline() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<String> decodePoly(String encoded) {

		ArrayList<String> res = new ArrayList<String>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			//GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
				// (int) (((double) lng / 1E5) * 1E6));
			double la = (((double) lat / 1E5));
			double lon =  (((double) lng / 1E5));
			res.add(la + "," + lon);
		}
		return res;
	}
	
    public String encode(ArrayList<GPSTrace> path) {
    	
        long lastLat = 0;
        long lastLng = 0;

        StringBuffer result = new StringBuffer();

        for (GPSTrace tr : path) {
            long lat = Math.round(tr.getLatitude() * 1e5);
            long lng = Math.round(tr.getLongitude() * 1e5);

            long dLat = lat - lastLat;
            long dLng = lng - lastLng;

            encode(dLat, result);
            encode(dLng, result);

            lastLat = lat;
            lastLng = lng;
        }
        return result.toString();
    }

    private void encode(long v, StringBuffer result) {
        v = v < 0 ? ~(v << 1) : v << 1;
        while (v >= 0x20) {
            result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
            v >>= 5;
        }
        result.append(Character.toChars((int) (v + 63)));
    }

}

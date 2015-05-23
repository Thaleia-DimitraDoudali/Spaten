package launch;

import googleMaps.MapURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import pois.GPSTrace;
import checkIns.CheckIn;
import checkIns.User;

public class GenerateCSV {

	public BufferedWriter createWriter(String fileName) {

		try {
			String workingDir = System.getProperty("user.dir");
			File file = new File(workingDir + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			return bw;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void GenerateDailyMapCSV(List<User> users) {
		BufferedWriter bw = createWriter("daily-map.json");
		String header = "UserNo,\t Date,\t URL\n";
		String content = "";
		try {
			bw.write(header);
			for (User usr : users) {
				for (MapURL mp : usr.getDailyMap()) {
					content = "";
					content += mp.getUserId() + ",\t " 
							+ "\"" + mp.getDate() + "\",\t "
							+ "\"" + mp.getUrl() + "\"\n";
					bw.write(content);
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void GenerateGPSTraceCSV(List<User> users) {
		BufferedWriter bw = createWriter("gps-traces.json");
		String header = "UserNo,\t Date,\t LatLong,\t Timestamp\n";
		String content = "";
		try {
			bw.write(header);
			for (User usr : users) {
				for (GPSTrace tr: usr.getTraces()) {
					content = "";
					content += tr.getUserId() + ",\t " + "\""
							+ tr.getDate(tr.getTimestamp()) + "\",\t " + "\"("
							+ tr.getLatitude() + ", "
							+ tr.getLongitude() + ")\"\n ";
					bw.write(content);
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void GenerateCheckInsCSV(List<User> users) {

		BufferedWriter bw = createWriter("check-ins.json");
		String header = "UserNo,\t Poi,\t Date,\t Travel,\t Title,\t Address,\t Timestamp,\t"
				+ "RevRating,\t RevTitle,\t RevText\n";
		String content = "";
		try {
			bw.write(header);
			for (User usr : users) {
				for (CheckIn chk : usr.getCheckIns()) {
					content = "";
					content += chk.getUserId() + ",\t " + "\"("
							+ chk.getPoi().getLatitude() + ", "
							+ chk.getPoi().getLongitude() + ")\",\t " + "\""
							+ chk.getDate(chk.getTimestamp()) + "\",\t " + "\""
							+ chk.isTravel() + "\",\t " + "\"" 
							+ chk.getPoi().getTitle() + "\",\t " + "\""
							+ chk.getPoi().getAddress() + "\",\t " + "\""
							+ chk.getTimestamp() + "\",\t " + "\""
							+ chk.getReview().getRating() + "\",\t " + "\""
							+ chk.getReview().getReviewTitle() + "\",\t "
							+ "\"" + chk.getReview().getReview() + "\"\n";
					bw.write(content);
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		GenerateCSV gen = new GenerateCSV();
		gen.GenerateCheckInsCSV(null);
	}
}

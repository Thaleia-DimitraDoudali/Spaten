package launch;

import googleMaps.MapURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import checkIns.CheckIn;
import pois.GPSTrace;

public class OutCSV {
	
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
	
	public void appendMap(BufferedWriter bw, MapURL mp) {
		String content = "";
		content += mp.getUserId() + ",\t " 
				+ "\"" + mp.getDate() + "\",\t "
				+ "\"" + mp.getUrl() + "\"\n";
		try {
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendTrace(BufferedWriter bw, GPSTrace tr) {
		String content = "";
		content += tr.getUserId() + ",\t " + "\""
				+ tr.getDate(tr.getTimestamp()) + "\",\t " + "\"("
				+ tr.getLatitude() + ", "
				+ tr.getLongitude() + ")\"\n ";
		try {
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendCheckIn(BufferedWriter bw, CheckIn chk) {
		String content = "";
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
		try {
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


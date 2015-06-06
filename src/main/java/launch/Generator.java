package launch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import checkIns.CreateChkIn;
import checkIns.User;
import db.DBconnector;

public class Generator {

	public static void main(String[] args) {

		// Create command line parameters
		InputParam inp = new InputParam();
		inp.createOptions();

		// Parse command line parameters
		inp.parse(args);
		inp.setParams();

		// Number of pois in DB
		DBconnector db = new DBconnector();
		db.connect();
		int poisNum = db.getPoisNum();

		//Define parameters that are common to all user's check-in's
		CreateChkIn crChk = new CreateChkIn();
		long sdate = crChk.convertToTimestamp(inp.getStartDate());
		long edate = crChk.convertToTimestamp(inp.getEndDate());
	    long milPerDay = 1000*60*60*24; 
		int days = (int) ((edate - sdate) / milPerDay) + 1;
		long travelDays = Math.round(0.1*days); // travel days will be the 10% of all days
		boolean home = true, travel = false;
		long travelCount = 0, trDays = 0;		
		
		//Number of requests to Google Maps Directions API
		int req_api = 0;
		
		//Create output files
		OutCSV csv = new OutCSV();
		BufferedWriter outChkCSV = csv.createWriter(inp.getOutCheckIns());
		BufferedWriter outTrCSV  = csv.createWriter(inp.getOutTraces());
		BufferedWriter outMapCSV = csv.createWriter(inp.getOutMap());
		
		// For each user create their check-in's
		for (int i = inp.getUserIdStart(); i <= inp.getUserIdEnd(); i++) {

			User usr = new User(i);
			System.out.println("\n-------------User no." + i + "-------------");
			home = true;
			for (long time = sdate; time <= edate; time += milPerDay) {
				
				System.out.println("\n>DAY: " + crChk.getDate(time));
				
				//Determine whether he will travel or not
				if (!travel && !home) {
					Random r = new Random();
					int pr = r.nextInt(2); // coin toss
					if ((travelDays != 0) && (pr == 1)) {
						travel = true;
						//for how many days he will travel? 3-7 days
						trDays = crChk.createGaussianRandom(5, 2);
						if (trDays > travelDays) {
							trDays = travelDays;
						}
						travelCount = 0;
					}
				}
				if (travel) {
					travelCount++;
				}
				
				// how many check-in's per day?
				int checkNum = crChk.createGaussianRandom(inp.getChkNumMean(), inp.getChkNumStDev());
				
				// Create daily check-in
				req_api += crChk.createDailyCheckIn(usr, checkNum, poisNum, db, inp.getDist(), inp.getMaxDist(),
						inp.getChkDurMean(), inp.getChkDurStDev(), inp.getStartTime(), inp.getEndTime(), 
						time, home, travel, travelCount, outChkCSV, outTrCSV, outMapCSV, csv);
				
				if (travel && (travelCount == trDays)) {
					travel = false;
					travelDays -= trDays;
				}
				home = false;				
			}
		}
		
		System.out.println("\n Requests to Google Directions API: " + req_api);
		
		//Close output files
		try {
			outChkCSV.close();
			outTrCSV.close();
			outMapCSV.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

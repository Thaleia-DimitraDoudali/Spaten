package launch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pois.GPSTrace;
import checkIns.CheckIn;
import checkIns.CreateChkIn;
import checkIns.User;
import db.DBconnector;

public class Generator {

	private static List<User> users = new ArrayList<User>();

	public static void main(String[] args) {

		// Create command line parameters
		Options options = new Options();
		// Number of users created
		options.addOption("userNum", true, "Number of users created");
		// Gauss parameters for check-in's per day
		options.addOption("chkNumMean", true, "Mean of Gauss "
				+ "that determines the number of a user's check-in's per day");
		options.addOption("chkNumStDev", true, "Standard Deviation of Gauss "
				+ "that determines the number of a user's check-in's per day");
		// Max distance between check-in's
		options.addOption("dist", true,
				"Number of maximum diastance a user can walk between check-in's");
		// Max distance between daily check-in's - user stays in the same country
		options.addOption("maxDist", true,
				"Number of maximum diastance a user can be between different days");
		// How many hours will the user stay at each poi?
		options.addOption(
				"chkDurMean",
				true,
				"Mean of Gauss "
						+ "that determines the duration of each user's check-in per day");
		options.addOption(
				"chkDurStDev",
				true,
				"Standard Deviation of Gauss "
						+ "that determines the duration of each user's check-in per day");
		// Start and end time of day
		options.addOption("startTime", true,
				"Time for the first check-in of the day ");
		options.addOption("endTime", true,
				"Time for the last check-in of the day ");
		// Start and end date of check-in's
		options.addOption("startDate", true, "Date for the first check-in");
		options.addOption("endDate", true, "Date for the last check-in");

		// Parse command line parameters
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Integer userNum = Integer.parseInt(cmd.getOptionValue("userNum"));
		Integer chkNumMean = Integer.parseInt(cmd.getOptionValue("chkNumMean"));
		Integer chkNumStDev = Integer.parseInt(cmd
				.getOptionValue("chkNumStDev"));
		Double dist = Double.parseDouble(cmd.getOptionValue("dist"));
		Double maxDist = Double.parseDouble(cmd.getOptionValue("maxDist"));
		Double chkDurMean = Double
				.parseDouble(cmd.getOptionValue("chkDurMean"));
		Double chkDurStDev = Double.parseDouble(cmd
				.getOptionValue("chkDurStDev"));
		Integer startTime = Integer.parseInt(cmd.getOptionValue("startTime"));
		Integer endTime = Integer.parseInt(cmd.getOptionValue("endTime"));
		String startDate = cmd.getOptionValue("startDate");
		String endDate = cmd.getOptionValue("endDate");

		// Number of pois in DB
		DBconnector db = new DBconnector();
		db.connect();
		int poisNum = db.getPoisNum();

		CreateChkIn crChk = new CreateChkIn();
		long sdate = crChk.convertToTimestamp(startDate);
		long edate = crChk.convertToTimestamp(endDate);
	    long milPerDay = 1000*60*60*24; 
		int days = (int) ((edate - sdate) / milPerDay) + 1;
		System.out.println("days = " + days);
		long travelDays = Math.round(0.1*days); // travel days will be the 10% of check-in's
		boolean home = true, travel = false;
		long travelCount = 0, trDays = 0;
		System.out.println("travel days = " + travelDays);

		// For each user create their check-in's
		for (int i = 1; i <= userNum; i++) {

			User usr = new User(i);
			users.add(usr);
			System.out.println("-------------User no." + i + "-------------");
			home = true;
			for (long time = sdate; time <= edate; time += 86400000) {
				System.out.println(">DAY: " + crChk.getDate(time));
				//Determine whether he will travel or not
				if (!travel && !home) {
					Random r = new Random();
					int pr = r.nextInt(2); // coin toss
					System.out.println("travel days = " + travelDays + travel);
					if ((travelDays != 0) && (pr == 1)) {
						travel = true;
						//for how many days he will travel?
						trDays = crChk.createGaussianRandom(5, 2);
						System.out.println("TRAVELLLLL " + trDays);
						if (trDays > travelDays) {
							trDays = travelDays;
						}
						travelCount = 0;
					}
				}
				// how many check-in's per day?
				int checkNum = crChk.createGaussianRandom(chkNumMean,
						chkNumStDev);
				// Create daily check-in
				if (travel) {
					travelCount++;
					System.out.println("Travel Day no." + travelCount);
				}
				System.out.println("home = " + home + " travel = " + travel);
				crChk.createDailyCheckIn(usr, checkNum, poisNum, db, dist, maxDist,
						chkDurMean, chkDurStDev, startTime, endTime, time, home, travel, travelCount);
				if (travel && (travelCount == trDays)) {
					travel = false;
					travelDays -= trDays;
					System.out.println("travel days = " + travelDays);
				}
				home = false;
				
				
			}
			
			//Export output to CSV files
			GenerateCSV gen = new GenerateCSV();
			gen.GenerateCheckInsCSV(users);
			gen.GenerateDailyMapCSV(users);
			gen.GenerateGPSTraceCSV(users);
			
			// Print check-in's
			for (CheckIn chk : usr.getCheckIns()) {
				chk.print();
			}
			// Print GPS traces
			for (GPSTrace tr : usr.getTraces()) {
				tr.print();
			}
		}
	}

}

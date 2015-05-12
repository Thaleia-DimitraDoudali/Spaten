package launch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import restaurants.Restaurant;
import restaurants.Review;
import checkIns.CheckIn;
import checkIns.CreateChkIn;
import checkIns.User;
import db.DBconnector;

public class Generator {

	private static List<User> users = new ArrayList<User>();
	
	public static void main(String[] args) {
		
		//Create command line parameters
		Options options = new Options();
		options.addOption("userNum", true, "Number of users created");
		options.addOption("chkNumMean", true, "Mean of Gauss "
				+ "that determines the number of a user's check-in's per day");
		options.addOption("chkNumStDev", true, "Standard Deviation of Gauss "
				+ "that determines the number of a user's check-in's per day");
		
		//Parse command line parameters
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			 cmd = parser.parse( options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Integer userNum = Integer.parseInt(cmd.getOptionValue("userNum"));
		Integer chkNumMean = Integer.parseInt(cmd.getOptionValue("chkNumMean"));
		Integer chkNumStDev = Integer.parseInt(cmd.getOptionValue("chkNumStDev"));
		
		//Number of pois in DB
		DBconnector db = new DBconnector();
		db.connect();
		int poisNum = db.getPoisNum();

		//For each user create their check-in's
		for (int i = 1; i <= userNum; i++) {
			
			User usr = new User(i);
			users.add(usr);
			
			CreateChkIn crChk = new CreateChkIn();
			//how many check-in's per day?
			int checkNum = crChk.createGaussianRandom(chkNumMean, chkNumStDev);
			crChk.createDailyCheckIn(usr, checkNum, poisNum, db);
		}
	}

}

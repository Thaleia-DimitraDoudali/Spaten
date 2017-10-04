package launch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class InputParam {

	private Options options = new Options();
	private CommandLine cmd;
	private Integer userIdStart, userIdEnd, chkNumMean, chkNumStDev, startTime, endTime;
	private Double dist, maxDist, chkDurMean, chkDurStDev;
	private String startDate, endDate, outCheckIns, outTraces, outMaps, key;


	public void setParams() {
		userIdStart = Integer.parseInt(cmd.getOptionValue("userIdStart"));
		userIdEnd = Integer.parseInt(cmd.getOptionValue("userIdEnd"));
		chkNumMean = Integer.parseInt(cmd.getOptionValue("chkNumMean"));
		chkNumStDev = Integer.parseInt(cmd.getOptionValue("chkNumStDev"));
		dist = Double.parseDouble(cmd.getOptionValue("dist"));
		maxDist = Double.parseDouble(cmd.getOptionValue("maxDist"));
		chkDurMean = Double.parseDouble(cmd.getOptionValue("chkDurMean"));
		chkDurStDev = Double.parseDouble(cmd.getOptionValue("chkDurStDev"));
		startTime = Integer.parseInt(cmd.getOptionValue("startTime"));
		endTime = Integer.parseInt(cmd.getOptionValue("endTime"));
		startDate = cmd.getOptionValue("startDate");
		endDate = cmd.getOptionValue("endDate");
		outCheckIns = cmd.getOptionValue("outCheckIns");
		outTraces = cmd.getOptionValue("outTraces");
		outMaps = cmd.getOptionValue("outMaps");
		key = cmd.getOptionValue("key");
	}
	
	public void print() {
		System.out.println(userIdStart + " " + userIdEnd + " "
				+chkNumMean + " " + chkNumStDev + " "
				+dist + " " + maxDist + " " + chkDurMean + " "
				+ chkDurStDev + " " + startTime + " " + endTime + " "
				+ startDate + " " + endDate + " " + outCheckIns + " " 
				+ outTraces + " " + outMaps + " ");
	}

	public void parse(String[] in) {
		CommandLineParser parser = new GnuParser();
		try {
			cmd = parser.parse(options, in);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void createOptions() {
		// Number of users created
		options.addOption("userIdStart", true, "First user id created");
		options.addOption("userIdEnd", true, "Last user id created");
		// Gauss parameters for check-in's per day
		options.addOption("chkNumMean", true, "Mean of Gauss "
				+ "that determines the number of a user's check-in's per day");
		options.addOption("chkNumStDev", true, "Standard Deviation of Gauss "
				+ "that determines the number of a user's check-in's per day");
		// Max distance between check-in's
		options.addOption("dist", true,
				"Number of maximum diastance a user can walk between check-in's");
		// Max distance between daily check-in's
		options.addOption("maxDist", true,
				"Number of maximum diastance a user can be between different days");
		// How many hours will the user stay at each poi?
		options.addOption( "chkDurMean", true, "Mean of Gauss "
				+ "that determines the duration of each user's check-in per day");
		options.addOption("chkDurStDev", true, "Standard Deviation of Gauss "
				+ "that determines the duration of each user's check-in per day");
		// Start and end time of day
		options.addOption("startTime", true,
				"Time for the first check-in of the day ");
		options.addOption("endTime", true,
				"Time for the last check-in of the day ");
		// Start and end date of check-in's
		options.addOption("startDate", true, "Date for the first check-in");
		options.addOption("endDate", true, "Date for the last check-in");
		//Output files
		options.addOption("outCheckIns", true, "The csv file where the check-ins will be stored");
		options.addOption("outTraces", true, "The csv file where the gps traces will be stored");
		options.addOption("outMaps", true, "The csv file where the daily maps will be stored");
		//Key
		options.addOption("key", true, "The key for google maps directions api");
	}
	
	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public CommandLine getCmd() {
		return cmd;
	}

	public void setCmd(CommandLine cmd) {
		this.cmd = cmd;
	}

	public Integer getChkNumMean() {
		return chkNumMean;
	}

	public void setChkNumMean(Integer chkNumMean) {
		this.chkNumMean = chkNumMean;
	}

	public Integer getChkNumStDev() {
		return chkNumStDev;
	}

	public void setChkNumStDev(Integer chkNumStDev) {
		this.chkNumStDev = chkNumStDev;
	}

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	public Double getDist() {
		return dist;
	}

	public void setDist(Double dist) {
		this.dist = dist;
	}

	public Double getMaxDist() {
		return maxDist;
	}

	public void setMaxDist(Double maxDist) {
		this.maxDist = maxDist;
	}

	public Double getChkDurMean() {
		return chkDurMean;
	}

	public void setChkDurMean(Double chkDurMean) {
		this.chkDurMean = chkDurMean;
	}

	public Double getChkDurStDev() {
		return chkDurStDev;
	}

	public void setChkDurStDev(Double chkDurStDev) {
		this.chkDurStDev = chkDurStDev;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getOutCheckIns() {
		return outCheckIns;
	}


	public void setOutCheckIns(String outCheckIns) {
		this.outCheckIns = outCheckIns;
	}


	public String getOutTraces() {
		return outTraces;
	}


	public void setOutTraces(String outTraces) {
		this.outTraces = outTraces;
	}


	public String getOutMap() {
		return outMaps;
	}


	public void setOutMap(String outMap) {
		this.outMaps = outMap;
	}


	public Integer getUserIdStart() {
		return userIdStart;
	}


	public void setUserIdStart(Integer userIdStart) {
		this.userIdStart = userIdStart;
	}


	public Integer getUserIdEnd() {
		return userIdEnd;
	}


	public void setUserIdEnd(Integer userIdEnd) {
		this.userIdEnd = userIdEnd;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


}


package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;

import threads.RegionThreadNF;
import containers.CheckIn;
import containers.CheckInList;
import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;

public class GetNewsFeedQuery extends AbstractQueryClient {

	private User user;
	private UserList friendList;
	private long executionTime;
	private long timestamp;
	private String outFile;
	private boolean print;

	public GetNewsFeedQuery() {
	}

	public GetNewsFeedQuery(User usr) {
		this.user = usr;
	}

	@Override
	public void executeQuery() throws Exception {

		List<RegionThreadNF> threads = new LinkedList<RegionThreadNF>();

		BufferedWriter bw = null;
		if (print) {
			bw = this.createWriter(this.outFile);

			bw.write("Getting the newd feed of user no."
					+ this.user.getUserId() + "\n");
		}
		if (this.friendList.getUserList().size() == 0) {
			if (print) {
				bw.write("User no." + this.user.getUserId()
						+ " has no friends.\n");
				bw.close();
			}
			return;
		}

		for (UserList usrList : this.getSplittedUserList()) {
			RegionThreadNF thread = new RegionThreadNF();
			thread.setUsrList(usrList);
			thread.setDate(timestamp);
			thread.setTable(this.table);
			threads.add(thread);
		}

		for (Thread t : threads) {
			t.start();
		}

		List<CheckInList> intermediateResults = new LinkedList<CheckInList>();
		for (RegionThreadNF t : threads) {
			t.join();
			intermediateResults.add(t.getResults());
		}

		CheckInList chkList = new CheckInList();
		chkList = this.mergeResults(intermediateResults);

		if (print) {
			bw.write("The news feed of user no." + this.user.getUserId()
					+ " are:\n");
			for (CheckIn chk : chkList.getCheckInList()) {
				bw.write(chk.toNFstring() + "\n");
			}

			bw.close();
		}

	}

	public CheckInList mergeResults(List<CheckInList> chkll) {
		CheckInList chkList = new CheckInList();
		for (CheckInList chkl : chkll) {
			chkList.getCheckInList().addAll(chkl.getCheckInList());
		}
		// Sort check ins based on timestamp
		Collections.sort(chkList.getCheckInList(), new Comparator<CheckIn>() {
			public int compare(CheckIn chk1, CheckIn chk2) {
				if (chk1.getTimestamp() > chk2.getTimestamp()) {
					return 1;
				} else if (chk1.getTimestamp() < chk2.getTimestamp()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return chkList;
	}

	public List<UserList> getSplittedUserList() throws Exception {
		List<UserList> spltUserList = new LinkedList<UserList>();

		UserList regionKeys = this.getRegionsKeys();
		// add the last friend id + 1 as region key, in order to determine the
		// end
		regionKeys.add(new User(this.friendList.getUserList()
				.get(this.friendList.getUserList().size() - 1).getUserId()));
		int limit = regionKeys.getUserList().size();

		// 1 region
		if (limit == 2) {
			// call coprocessor for friend list
			spltUserList.add(this.friendList);
		} else {
			int i = 1; // first split key is 1, so we ignore it
			UserList tempFriendList = new UserList(this.user.getUserId());
			// Serially traverse the list and when the regionKey comes accross
			// split it.
			for (User usr : this.friendList.getUserList()) {
				if (usr.getUserId() < regionKeys.getUserList().get(i)
						.getUserId()) {
					tempFriendList.add(usr);
				} else if (usr.getUserId() >= regionKeys.getUserList().get(i)
						.getUserId()) {
					i++;
					if (limit == i) {
						tempFriendList.getUserList().add(usr);
					}
					// call coprocessor for the temp friend list
					spltUserList.add(tempFriendList);
					tempFriendList = new UserList(this.user.getUserId());
					tempFriendList.getUserList().add(usr);
				}
			}
		}

		return spltUserList;
	}

	public UserList getRegionsKeys() throws Exception {
		try {
			byte[] firstKey = this.friendList.getUserList().get(0)
					.getKeyBytes();
			byte[] lastKey = this.friendList.getUserList()
					.get(this.friendList.getUserList().size() - 1)
					.getKeyBytes();

			List<HRegionLocation> regionsInRange = this.table
					.getRegionsInRange(firstKey, lastKey);
			UserList regionKeys = new UserList();

			for (HRegionLocation loc : regionsInRange) {
				byte[] startKey = loc.getRegionInfo().getStartKey();
				User current = new User();
				current.parseBytes(startKey);
				regionKeys.add(current);
			}
			return regionKeys;
		} catch (IOException ex) {
			Logger.getLogger(GetMostVisitedPOIQuery.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return null;
	}

	public long convertToTimestamp(String date) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// date will be in the format e.g. 04/29/1992
		int month = Integer.parseInt(date.substring(0, 2)) - 1;
		int day = Integer.parseInt(date.substring(3, 5));
		int year = Integer.parseInt(date.substring(6, 10));
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTimeInMillis();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isPrint() {
		return print;
	}

	public void setPrint(boolean print) {
		this.print = print;
	}

	public UserList getFriendList() {
		return friendList;
	}

	public void setFriendList(UserList friendList) {
		this.friendList = friendList;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public boolean parsePrint(String in) {
		if (in.equals("1"))
			return true;
		else if (in.equals("0"))
			return false;
		return false;
	}
	
	public void runQuery(String[] in) throws Exception {
		boolean pr = this.parsePrint(in[4]);
		GetFriendsQuery clientFriend = new GetFriendsQuery(in[0]);
		
		this.executionTime = System.currentTimeMillis();

		clientFriend.openConnection("friends");
		this.openConnection("check-ins");
		
		clientFriend.setPrint(pr);
		clientFriend.setProtocol(FriendsProtocol.class);
		clientFriend.setOutFile(in[2]);
		clientFriend.executeSerializedQuery();

		this.setUser(clientFriend.getUser());
		this.setPrint(pr);
		this.friendList = clientFriend.getFriendList();
		this.setTimestamp(this.convertToTimestamp(in[1]));
		this.setOutFile(in[3]);
		this.executeQuery();
				
		clientFriend.closeConnection();
		this.closeConnection();
		
		this.executionTime = System.currentTimeMillis() - this.executionTime;
		System.out.println("\n\nQuery executed in " + this.executionTime / 1000 + "s\n\n");
	}

	public static void main(String[] args) throws Exception {
		GetNewsFeedQuery clientNF = new GetNewsFeedQuery();
		clientNF.runQuery(args);
	}

}

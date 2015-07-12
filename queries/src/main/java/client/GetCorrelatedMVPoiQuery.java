package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;

import threads.RegionThreadCMVP;
import containers.MostVisitedPOI;
import containers.MostVisitedPOIList;
import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;

public class GetCorrelatedMVPoiQuery extends AbstractQueryClient implements
		Runnable {

	private UserList friendList;
	private String outFile;
	private MostVisitedPOI mvp;
	private MostVisitedPOIList resultList;
	private boolean print;

	public GetCorrelatedMVPoiQuery() {
	}

	public GetCorrelatedMVPoiQuery(User usr) {
		this.user = usr;
	}

	@Override
	public void executeQuery() throws Exception {

		List<RegionThreadCMVP> threads = new LinkedList<RegionThreadCMVP>();

		BufferedWriter bw = null;
		if (print) {
			bw = this.createWriter(this.outFile);

			bw.write("Getting the visits of friends of user no."
					+ this.user.getUserId() + " to his most visited poi: "
					+ this.getMvp().getPoi().toString() + "\n");
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
			RegionThreadCMVP thread = new RegionThreadCMVP();
			thread.setUsrList(usrList);
			thread.setTable(this.table);
			thread.setMvp(this.mvp);
			threads.add(thread);
		}

		for (Thread t : threads) {
			t.start();
		}

		List<MostVisitedPOIList> intermediateResults = new LinkedList<MostVisitedPOIList>();
		for (RegionThreadCMVP t : threads) {
			t.join();
			intermediateResults.add(t.getResults());
		}

		this.resultList = this.mergeResults(intermediateResults);
		if (print) {
			bw.write("The friends of user no." + this.user.getUserId()
					+ " that have been to his most visited POI are:\n");
			for (MostVisitedPOI p : this.resultList.getMvpList()) {
				bw.write(p.toString() + "\n");
			}

			bw.close();
		}
	}

	public MostVisitedPOIList mergeResults(List<MostVisitedPOIList> mvpll) {
		MostVisitedPOIList mvpList = new MostVisitedPOIList();

		for (MostVisitedPOIList mvpl : mvpll) {
			for (MostVisitedPOI mvp : mvpl.getMvpList()) {
				if (mvp.getCounter() > 0) {
					mvpList.getMvpList().add(mvp);
				}
			}
		}

		return mvpList;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MostVisitedPOI getMvp() {
		return mvp;
	}

	public void setMvp(MostVisitedPOI mvp) {
		this.mvp = mvp;
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

	public MostVisitedPOIList getResultList() {
		return resultList;
	}

	public void setResultList(MostVisitedPOIList resultList) {
		this.resultList = resultList;
	}

	public boolean parsePrint(String in) {
		if (in.equals("1"))
			return true;
		else if (in.equals("0"))
			return false;
		return false;
	}

	public void runQuery() throws Exception {

		GetFriendsQuery clientFriend = new GetFriendsQuery();
		GetMostVisitedPOIQuery clientMVP = new GetMostVisitedPOIQuery(
				this.getUser());

		this.executionTime = System.currentTimeMillis();

		clientMVP.openConnection("check-ins");
		this.openConnection("check-ins");
		clientFriend.openConnection("friends");

		UserList usrList = new UserList();
		usrList.add(this.getUser());
		clientMVP.setOutFile(this.getOutFile());
		clientMVP.setPrint(this.isPrint());
		clientMVP.setUser(this.getUser());
		clientMVP.setFriendList(usrList);
		clientMVP.executeQuery();

		clientFriend.setOutFile(this.getOutFile());
		clientFriend.setPrint(this.isPrint());
		clientFriend.setUser(this.getUser());
		clientFriend.setProtocol(FriendsProtocol.class);
		clientFriend.executeSerializedQuery();

		if (clientMVP.getResultList().getMvpList().size() > 0) {
			this.setMvp(clientMVP.getResultList().getMvpList().get(0));
			this.setFriendList(clientFriend.getFriendList());
			this.executeQuery();
		}
		clientFriend.closeConnection();
		clientMVP.closeConnection();
		this.closeConnection();

		this.executionTime = System.currentTimeMillis() - this.executionTime;
		// System.out.println("\n\nQuery executed in " + this.executionTime /
		// 1000 + "s\n\n");

	}

	public static void main() throws Exception {
		GetCorrelatedMVPoiQuery clientCMVP = new GetCorrelatedMVPoiQuery();
		clientCMVP.runQuery();
	}

	public void run() {
		try {
			this.runQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;

import threads.RegionThreadMVPOI;
import containers.MostVisitedPOI;
import containers.MostVisitedPOIList;
import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;
import coprocessors.MostVisitedPOIProtocol;

public class GetMostVisitedPOIQuery extends AbstractQueryClient {

	private User user;
	private UserList friendList;
	private Class<FriendsProtocol> protocol = FriendsProtocol.class;
	private long executionTime;
	private String outFile;
	private MostVisitedPOIList resultList;
	private boolean print;

	public GetMostVisitedPOIQuery() {
	}

	public GetMostVisitedPOIQuery(User usr) {
		this.user = usr;
	}

	@Override
	public void executeQuery() throws Exception {

		List<RegionThreadMVPOI> threads = new LinkedList<RegionThreadMVPOI>();
		BufferedWriter bw = null;
		if (print) {
			bw = this.createWriter(this.outFile);
			bw.write("Getting the most visited POIs of friends of user no."
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
			RegionThreadMVPOI thread = new RegionThreadMVPOI();
			thread.setProtocol(this.protocol);
			thread.setUsrList(usrList);
			thread.setTable(this.table);
			threads.add(thread);
		}

		for (Thread t : threads) {
			t.start();
		}

		List<MostVisitedPOIList> intermediateResults = new LinkedList<MostVisitedPOIList>();
		for (RegionThreadMVPOI t : threads) {
			t.join();
			intermediateResults.add(t.getResults());
		}

		this.resultList = this.mergeResults(intermediateResults);

		if (print) {
			bw.write("The most visited POIs of the friends of user no."
					+ this.user.getUserId() + " are:\n");
			for (MostVisitedPOI p : this.resultList.getMvpList()) {
				bw.write(p.toString() + "\n");
			}

			bw.close();
		}
	}

	public MostVisitedPOIList callCoprocessor(UserList list) throws Exception {
		MostVisitedPOIList resultsLocal = new MostVisitedPOIList();
		MostVisitedPOIProtocol prot = this.table.coprocessorProxy(
				MostVisitedPOIProtocol.class, list.getUserList().get(0)
						.getKeyBytes());
		try {
			resultsLocal.parseCompressedBytes(prot.getMostVisitedPOI(list
					.getDataBytes()));
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,
					ex);
		}
		return resultsLocal;
	}

	public void executeSerializedQuery() throws Exception {

		List<MostVisitedPOIList> intermediateResults = new LinkedList<MostVisitedPOIList>();
		System.out
				.println("Getting the most visited POIs of friends of user no."
						+ this.user.getUserId());
		this.executionTime = System.currentTimeMillis();

		for (UserList usrList : this.getSplittedUserList()) {
			intermediateResults.add(this.callCoprocessor(usrList));
		}

		@SuppressWarnings("unused")
		MostVisitedPOIList mvpList = new MostVisitedPOIList();
		mvpList = this.mergeResults(intermediateResults);

		this.executionTime = System.currentTimeMillis() - this.executionTime;
		System.out.println("Query executed in " + this.executionTime / 1000
				+ "s");
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

	public MostVisitedPOIList mergeResults(List<MostVisitedPOIList> mvpll) {
		MostVisitedPOIList mvpList = new MostVisitedPOIList();

		for (MostVisitedPOIList mvpl : mvpll) {
			mvpList.getMvpList().addAll(mvpl.getMvpList());
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

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
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

	public Class<FriendsProtocol> getProtocol() {
		return protocol;
	}

	public void setProtocol(Class<FriendsProtocol> protocol) {
		this.protocol = protocol;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
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

	public void runQuery(String[] in) throws Exception {
		boolean pr = this.parsePrint(in[3]);
		GetFriendsQuery clientFriend = new GetFriendsQuery(in[0]);

		this.executionTime = System.currentTimeMillis();
		
		this.openConnection("check-ins");
		clientFriend.openConnection("friends");
		
		clientFriend.setPrint(pr);
		clientFriend.setProtocol(FriendsProtocol.class);
		clientFriend.setOutFile(in[1]);
		clientFriend.executeSerializedQuery();

		this.setPrint(pr);
		this.setUser(clientFriend.getUser());		
		this.friendList = clientFriend.getFriendList();
		this.setOutFile(in[2]);
		this.executeQuery();
				
		clientFriend.closeConnection();
		this.closeConnection();
		
		this.executionTime = System.currentTimeMillis() - this.executionTime;
		System.out.println("\n\nQuery executed in " + this.executionTime / 1000 + "s\n\n");
	}

	public static void main(String[] args) throws Exception {
		GetMostVisitedPOIQuery clientMVP = new GetMostVisitedPOIQuery();
		clientMVP.runQuery(args);
	}

}

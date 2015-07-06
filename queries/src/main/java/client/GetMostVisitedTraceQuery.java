package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;

import threads.RegionThreadMVTR;
import containers.MostVisitedTrace;
import containers.MostVisitedTraceList;
import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;
import coprocessors.MostVisitedTraceProtocol;

public class GetMostVisitedTraceQuery extends AbstractQueryClient {

	private User user;
	private UserList friendList;
	private Class<FriendsProtocol> protocol = FriendsProtocol.class;
	private long executionTime;
    private String outFile;

	public GetMostVisitedTraceQuery() {}
    
	public GetMostVisitedTraceQuery(User usr) {
		this.user = usr;
	}

	@Override
	public void executeQuery() throws Exception {

		List<RegionThreadMVTR> threads = new LinkedList<RegionThreadMVTR>();
		BufferedWriter bw = this.createWriter(this.outFile);
		
		bw.write("Getting the most visited traces of friends of user no."
						+ this.user.getUserId() + "\n");

		if (this.friendList.getUserList().size() == 0) {
			bw.write("User no." + this.user.getUserId() + " has no friends.\n");
			bw.close();
			return;
		}
		
		this.executionTime = System.currentTimeMillis();

		for (UserList usrList : this.getSplittedUserList()) {
			RegionThreadMVTR thread = new RegionThreadMVTR();
			thread.setProtocol(this.protocol);
			thread.setUsrList(usrList);
			thread.setTable(this.table);
			threads.add(thread);
		}

		for (Thread t : threads) {
			t.start();
		}

		List<MostVisitedTraceList> intermediateResults = new LinkedList<MostVisitedTraceList>();
		for (RegionThreadMVTR t : threads) {
			t.join();
			intermediateResults.add(t.getResults());
		}

		MostVisitedTraceList mvtrList = new MostVisitedTraceList();
		mvtrList = this.mergeResults(intermediateResults);
		
		this.executionTime = System.currentTimeMillis() - this.executionTime;
		bw.write("Query executed in " + this.executionTime / 1000 + "s\n");
		
		bw.write("The most visited trace of the friends of user no." + this.user.getUserId() + " are:\n");
		for (MostVisitedTrace p: mvtrList.getMvtrList()) {
			bw.write(p.toString() + "\n");
		}
		
		bw.close();
	}

	public MostVisitedTraceList callCoprocessor(UserList list) throws Exception {
		MostVisitedTraceList resultsLocal = new MostVisitedTraceList();
		MostVisitedTraceProtocol prot = this.table.coprocessorProxy(
				MostVisitedTraceProtocol.class, list.getUserList().get(0)
						.getKeyBytes());
		try {
			resultsLocal.parseCompressedBytes(prot.getMostVisitedTrace(list
					.getDataBytes()));
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,
					ex);
		}
		return resultsLocal;
	}

	public void executeSerializedQuery() throws Exception {

		List<MostVisitedTraceList> intermediateResults = new LinkedList<MostVisitedTraceList>();
		System.out
				.println("Getting the most visited traces of friends of user no."
						+ this.user.getUserId());
		this.executionTime = System.currentTimeMillis();

		for (UserList usrList : this.getSplittedUserList()) {
			intermediateResults.add(this.callCoprocessor(usrList));
		}

		@SuppressWarnings("unused")
		MostVisitedTraceList mvtrList = new MostVisitedTraceList();
		mvtrList = this.mergeResults(intermediateResults);

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

	public MostVisitedTraceList mergeResults(List<MostVisitedTraceList> mvpll) {
		MostVisitedTraceList mvtrList = new MostVisitedTraceList();

		for (MostVisitedTraceList mvtr : mvpll) {
			mvtrList.getMvtrList().addAll(mvtr.getMvtrList());
		}

		return mvtrList;
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
	
	public void runQuery(String[] in) throws Exception {
		GetFriendsQuery clientFriend = new GetFriendsQuery(in[0]);
		GetMostVisitedTraceQuery clientMVP = new GetMostVisitedTraceQuery(clientFriend.getUser());
		
		clientFriend.setProtocol(FriendsProtocol.class);
    	clientFriend.setOutFile(in[1]);
		clientFriend.openConnection("friends");
		clientFriend.executeSerializedQuery();
		clientFriend.closeConnection();

		clientMVP.openConnection("gps-traces");
		clientMVP.friendList = clientFriend.getFriendList();
		clientMVP.setOutFile(in[2]);
		clientMVP.executeQuery();
		clientMVP.closeConnection();
	}

	public static void main(String[] args) throws Exception {
		GetMostVisitedTraceQuery clientMVP = new GetMostVisitedTraceQuery();
		clientMVP.runQuery(args);
	}
}

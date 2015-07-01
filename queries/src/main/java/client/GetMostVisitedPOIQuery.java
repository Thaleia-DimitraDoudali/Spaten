package client;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;

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

	@Override
	public void executeQuery() {

	}
	
	public MostVisitedPOI callCoprocessor(UserList list) throws Exception {
		System.out.println("Getting the most visited POIs of friends of user no." + this.user.getUserId());
    	this.executionTime = System.currentTimeMillis();
    	MostVisitedPOI resultsLocal = new MostVisitedPOI();
		MostVisitedPOIProtocol prot = this.table.coprocessorProxy(MostVisitedPOIProtocol.class, list.getUserList().get(0).getKeyBytes());
        try {
        	resultsLocal.parseBytes(prot.getMostVisitedPOI(list.getDataBytes()));
        	resultsLocal.print();
        } catch (IOException ex) {
        	Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }             
        this.executionTime = System.currentTimeMillis() - this.executionTime;
        
        System.out.println("Query executed in " + this.executionTime/1000 + "s");
        return resultsLocal;
	}

	public void executeSerializedQuery() throws Exception {

		// Get friends
		GetFriendsQuery client = new GetFriendsQuery();
		client.setProtocol(FriendsProtocol.class);
		client.setUser(this.user);
		client.openConnection("friends");
		client.executeSerializedQuery();
		this.friendList = client.getFriendList();
		client.closeConnection();
		this.friendList.print();

        List<MostVisitedPOI> intermediateResults = new LinkedList<MostVisitedPOI>();
		
		UserList regionKeys = this.getRegionsKeys();
		//add the last friend id + 1 as region key, in order to determine the end
		regionKeys.add(new User(this.friendList.getUserList().get(this.friendList.getUserList().size()-1).getUserId()));
		int limit = regionKeys.getUserList().size();
        
		// 1 region
		if (limit == 2) {
			// call coprocessor for friend list
			// call coprocessor for the temp friend list
			System.out.println("here");
        	intermediateResults.add(this.callCoprocessor(this.friendList));
		} else {
			int i = 1; // first split key is 1, so we ignore it
			UserList tempFriendList = new UserList(this.user.getUserId());
			// Serially traverse the list and when the regionKey comes accross
			// split it.
			for (User usr : this.friendList.getUserList()) {
				if (usr.getUserId() < regionKeys.getUserList().get(i).getUserId()) {
					tempFriendList.add(usr);
				} else if (usr.getUserId() >= regionKeys.getUserList().get(i).getUserId()){
					i++;
					if (limit == i) {
						tempFriendList.getUserList().add(usr);
					}
					// call coprocessor for the temp friend list
		        	intermediateResults.add(this.callCoprocessor(tempFriendList));
					
					tempFriendList.getUserList().clear();
					tempFriendList.getUserList().add(usr);
				} 
			}
		}
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
				current.print();
				regionKeys.add(current);
			}
			return regionKeys;
		} catch (IOException ex) {
			Logger.getLogger(GetMostVisitedPOIQuery.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return null;
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

	public static void main(String[] args) throws Exception {
		GetMostVisitedPOIQuery client = new GetMostVisitedPOIQuery();
		client.setUser(new User(1));
		client.openConnection("check-ins");
		client.executeSerializedQuery();
		client.closeConnection();
	}
}

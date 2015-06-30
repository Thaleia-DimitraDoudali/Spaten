package client;

import hbase_schema.CheckInsTable;
import hbase_schema.FriendsTable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.util.Bytes;

import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;

public class GetMostVisitedPOIQuery extends AbstractQueryClient{

	private User user;
	private UserList friendList;
	private Class<FriendsProtocol> protocol = FriendsProtocol.class;
    private long executionTime;
	
	@Override
	public void executeQuery() {
		
	}

	public void executeSerializedQuery() throws Exception {
		
		//Get friends
		GetFriendsQuery client = new GetFriendsQuery();
        client.setProtocol(FriendsProtocol.class);
        client.setUser(this.user);
        client.openConnection("friends");
        client.executeSerializedQuery();
        this.friendList = client.getFriendList();
        client.closeConnection();
        this.friendList.print();

        for (User usr : this.getRegionsKeys().getUserList()) {
        	usr.print();
        }
        
	}
	
    public UserList getRegionsKeys() throws Exception{
        try {
            byte[] firstKey = this.friendList.getUserList().get(0).getKeyBytes(); 
            byte[] lastKey = this.friendList.getUserList().get(this.friendList.getUserList().size() - 1).getKeyBytes();
    		
            List<HRegionLocation> regionsInRange = this.table.getRegionsInRange(firstKey, lastKey);
            UserList regionKeys = new UserList();

            for (HRegionLocation loc : regionsInRange) {
                byte[] startKey = loc.getRegionInfo().getStartKey();
                User current = new User();
                current.parseBytes(startKey);
                regionKeys.add(current);
            }
            return regionKeys;
        } catch (IOException ex) {
            Logger.getLogger(GetMostVisitedPOIQuery.class.getName()).log(Level.SEVERE, null, ex);
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

package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HRegionLocation;

import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;

public class GetFriendsQuery extends AbstractQueryClient{

	private User user;
	private UserList friendList;
	private Class<FriendsProtocol> protocol = FriendsProtocol.class;
    private long executionTime;
	
	
	@Override
	public void executeQuery() {
		
	}

    public void executeSerializedQuery() throws Exception {
    	friendList = new UserList(this.user.getUserId());
    	this.executionTime = System.currentTimeMillis();
		System.out.println("Getting friends of user no." + this.user.getUserId());

        FriendsProtocol prot = this.table.coprocessorProxy(FriendsProtocol.class, this.user.getKeyBytes());
        try {
        	friendList.parseCompressedBytes(prot.getFriends(this.user.getKeyBytes())); 
        } catch (IOException ex) {
        	Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }             
        this.executionTime = System.currentTimeMillis() - this.executionTime;
        
        System.out.println("Query executed in " + this.executionTime/1000 + "s");
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
    	GetFriendsQuery client = new GetFriendsQuery();
        client.setProtocol(FriendsProtocol.class);
        client.setUser(new User(1));
        client.openConnection("friends");
        client.executeSerializedQuery();
        client.friendList.print();
        client.closeConnection();
    }
}

package hbase_schema;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import containers.User;
import containers.UserList;

public class FriendsTable {
	
	protected String tableName;
    protected HTable table;
    
    public FriendsTable(String nm) {
    	this.tableName = nm;
    }

	public void createTable() throws Exception {
		
        Configuration hbaseConf = HBaseConfiguration.create();
        HBaseAdmin admin = new HBaseAdmin(hbaseConf);
        if (admin.tableExists(this.tableName)) {
            admin.disableTable(this.tableName);
            admin.deleteTable(this.tableName);
        }
        HTableDescriptor descriptor = new HTableDescriptor(this.tableName);
        descriptor.addFamily(new HColumnDescriptor("friends"));

        admin.createTable(descriptor);
        admin.close();
        this.table = new HTable(hbaseConf, this.tableName);
    }
	
	public void putSingleFriend(byte[] row, byte[] data) throws IOException {		
		Put p = new Put(row);
		//qualifier will be data, which contains just the friend id
		p.add(Bytes.toBytes("friends"), data, data);
		System.out.println("...single friend inserted.");
	    this.table.put(p);
	}
	
	public void getSingleFriend(byte[] row, byte[] fr_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("friends".getBytes(), fr_qualifier);
			User friend = new User();
			friend.parseBytes(buffer);
			System.out.println("> friend id = " + friend.getUserId());
			System.out.println("...single friend retrieved.");
		}
	}
	
	public void putListFriend(byte[] row, byte[] data) throws IOException {		
		Put p = new Put(row);
		//just one qualifier set to 1
		p.add(Bytes.toBytes("friends"), Bytes.toBytes(1), data);
		System.out.println("...list of friends inserted.");
	    this.table.put(p);
	}
	
	public void getListFriend(byte[] row) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("friends".getBytes(), Bytes.toBytes(1));
			UserList friendList = new UserList();
			friendList.parseBytes(buffer);
			System.out.println("> friend ids = " + friendList.toString());
			System.out.println("...list of friends friend retrieved.");
		}
	}
	
	public void getListFriendCompressed(byte[] row) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("friends".getBytes(), Bytes.toBytes(1));
			UserList friendList = new UserList();
			friendList.parseCompressedBytes(buffer);
			System.out.println("> friend ids = " + friendList.toString());
			System.out.println("...list of friends friend retrieved.");
		}
	}
		
	public static void main(String[] args) throws Exception {
		FriendsTable fr = new FriendsTable("friends");
		
		System.out.println("Creating HBase friends table...");
		fr.createTable();
		System.out.println("...done");
		
		//put a friend of user 1
		User usr = new User();
		usr.setUserId(1);
		User friend = new User();
		friend.setUserId(2);
		System.out.println("Put friend with user id " + friend.getUserId() + " of user " + usr.getUserId());
		fr.putSingleFriend(usr.getBytes(), friend.getBytes());
		
		//get a specific friend of user 1
		System.out.println("Get friend with user id " + friend.getUserId() + " of user " + usr.getUserId());
		fr.getSingleFriend(usr.getBytes(), friend.getBytes());
		
		//put a list of friends for user 1
		UserList usrList = new UserList();
		User fr1 = new User();
		fr1.setUserId(3);
		usrList.getUserList().add(fr1);
		User fr2 = new User();
		fr2.setUserId(4);
		usrList.getUserList().add(fr2);
		User fr3 = new User();
		fr3.setUserId(5);
		usrList.getUserList().add(fr3);
		System.out.println("Put friend list of user " + usr.getUserId());
		fr.putListFriend(usr.getBytes(), usrList.getBytes());
		
		//Get this list
		System.out.println("Get friend list of user " + usr.getUserId());
		fr.getListFriend(usr.getBytes());		
		
		//Put compressed
		System.out.println("Put friend list of user " + usr.getUserId());
		fr.putListFriend(usr.getBytes(), usrList.getCompressedBytes());
		
		//Get this list
		System.out.println("Get friend list of user " + usr.getUserId());
		fr.getListFriendCompressed(usr.getBytes());
	}
}

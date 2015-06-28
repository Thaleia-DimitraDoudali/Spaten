package hbase_schema;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

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

import parser.ParseCheckIn;
import parser.ParseFriend;
import containers.User;
import containers.UserList;

public class FriendsTable implements QueriesTable{
	
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
	
	public void putSingle(byte[] row, byte[] fr_qualifier, byte[] data) throws IOException {		
		Put p = new Put(row);
		//qualifier will be data, which contains just the friend id
		p.add(Bytes.toBytes("friends"), fr_qualifier, data);
		System.out.println("...single friend inserted.");
	    this.table.put(p);
	}
	
	public void getSingle(byte[] row, byte[] fr_qualifier) throws Exception {
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
	
	public void putList(byte[] row, byte[] fr_qualifier, byte[] data) throws IOException {		
		Put p = new Put(row);
		//just one qualifier set to 1
		p.add(Bytes.toBytes("friends"), fr_qualifier, data);
		System.out.println("...list of friends inserted.");
	    this.table.put(p);
	}
	
	public void getList(byte[] row, byte[] fr_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("friends".getBytes(), fr_qualifier);
			UserList friendList = new UserList();
			friendList.parseBytes(buffer);
			System.out.println("> friend ids = " + friendList.toString());
			System.out.println("...list of friends friend retrieved.");
		}
	}
	
	public void getListCompressed(byte[] row, byte[] fr_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("friends".getBytes(), fr_qualifier);
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
		
		ParseFriend pr = new ParseFriend();	
		FileReader flr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(flr);
		
		String line = br.readLine();
		UserList userList = pr.parseLine(line);
		
		//put a friend of user 1
		fr.putSingle(Bytes.toBytes(userList.getUserList().get(0).getUserId()), 
				Bytes.toBytes(userList.getUserList().get(1).getUserId()),
				Bytes.toBytes(userList.getUserList().get(1).getUserId()));
		
		//get a specific friend of user 1
		fr.getSingle(userList.getUserList().get(0).getBytes(), userList.getUserList().get(1).getBytes());
		
		//put a list of friends for user 1
		line = br.readLine();
		UserList usrList = pr.parseLine(line);
		
		UserList usrl = new UserList(1);
		usrl.add(userList.getUserList().get(1));
		usrl.add(usrList.getUserList().get(1));

		
		fr.putList(Bytes.toBytes(usrl.getUserId()), Bytes.toBytes(usrl.getUserId()), 
				usrl.getBytes());
		
		//Get this list
		fr.getList(Bytes.toBytes(usrl.getUserId()), Bytes.toBytes(usrl.getUserId()));		
		
		//Put compressed
		fr.putList(Bytes.toBytes(usrl.getUserId()), Bytes.toBytes(usrl.getUserId()), usrl.getCompressedBytes());
		
		//Get this list
		fr.getListCompressed(Bytes.toBytes(usrl.getUserId()), Bytes.toBytes(usrl.getUserId()));
	}
}

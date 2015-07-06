package hbase_schema;

import java.io.BufferedReader;
import java.io.FileReader;
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

import parser.ParseFriend;
import containers.User;

public class FriendsTable implements QueriesTable {

	protected String tableName;
	protected HTable table;
	private int regionsNo, usersNo;

	public FriendsTable(String nm, String u, String r) {
		this.tableName = nm;
		this.regionsNo = Integer.parseInt(r);
		this.usersNo = Integer.parseInt(u);
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

		admin.createTable(descriptor, this.getSplitKeys());
		admin.close();
		this.table = new HTable(hbaseConf, this.tableName);
	}

	public void putSingle(byte[] row, byte[] fr_qualifier, byte[] data)
			throws IOException {
		Put p = new Put(row);
		p.add(Bytes.toBytes("friends"), fr_qualifier, data);
		System.out.println("...single friend inserted.");
		this.table.put(p);
	}

	public void getSingle(byte[] row, byte[] fr_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("friends".getBytes(), fr_qualifier);
			User user = new User();
			user.parseBytes(row);
			User friend = new User();
			friend.parseBytes(buffer);
			System.out.println("> User " + user.getUserId() + " has friend the user " + friend.getUserId());
			System.out.println("...single friend retrieved.");
		}
	}
	
	public byte[][] getSplitKeys() {
		int keyNo = usersNo / regionsNo;
		byte[][] keys = new byte[regionsNo][Integer.SIZE];

		int c = 1;
		for (int i = 0; i < regionsNo; i++) {
			keys[i] = Bytes.toBytes(c);
			c += keyNo;
		}
		return keys;
	}

	public static void main(String[] args) throws Exception {

		byte[] row, data, qualifier;
		int[] friends;
		String line;
		User usr1, usr2;

		FriendsTable fr = new FriendsTable("friends", args[1], args[2]);
		System.out.println("Creating HBase friends table...");
		fr.createTable();
		System.out.println("...done");

		ParseFriend pr = new ParseFriend();
		FileReader flr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(flr);

		line = br.readLine();
		while (line != null) {
			friends = pr.parseLine(line);
			usr1 = new User(friends[0]);
			usr2 = new User(friends[1]);
			System.out.println(line);
			row = usr1.getKeyBytes();
			qualifier = usr2.getQualifierBytes();
			data = usr2.getDataBytes();
			fr.putSingle(row, qualifier, data);
			//fr.getSingle(row, qualifier);
			line = br.readLine();
		}
		br.close();
	}

}

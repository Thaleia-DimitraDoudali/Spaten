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

import parser.ParseCheckIn;
import containers.CheckIn;
import containers.CheckInList;
import containers.User;
import containers.UserList;

public class CheckInsTable {

	protected String tableName;
    protected HTable table;
	
	public CheckInsTable(String nm) {
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
        descriptor.addFamily(new HColumnDescriptor("checkIns"));

        admin.createTable(descriptor);
        admin.close();
        this.table = new HTable(hbaseConf, this.tableName);
    }
	
	public void putSingle(byte[] row, byte[] qualifier, byte[] data) throws IOException {		
		Put p = new Put(row);
		//qualifier will be the timestamp
		p.add(Bytes.toBytes("checkIns"), qualifier, data);
		System.out.println("...single check-in inserted.");
	    this.table.put(p);
	}
	
	public void getSingle(byte[] row, byte[] chk_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("checkIns".getBytes(), chk_qualifier);
			CheckIn chk = new CheckIn();
			chk.parseBytes(buffer);
			System.out.println("> " + chk.toString());
			System.out.println("...single check-in retrieved.");
		}
	}
	
	public void putList(byte[] row, byte[] chk_qualifier, byte[] data) throws IOException {		
		Put p = new Put(row);
		//just one qualifier set to 1
		p.add(Bytes.toBytes("checkIns"), chk_qualifier, data);
		System.out.println("...list of check-ins inserted.");
	    this.table.put(p);
	}
	
	public void getList(byte[] row, byte[] chk_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("checkIns".getBytes(), chk_qualifier);
			CheckInList chkList = new CheckInList();
			chkList.parseBytes(buffer);
			System.out.println("> check-ins: " + chkList.toString());
			System.out.println("...list of check-ins retrieved.");
		}
	}
	
	public void getListCompressed(byte[] row, byte[] chk_qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("checkIns".getBytes(), chk_qualifier);
			CheckInList chkList = new CheckInList();
			chkList.parseCompressedBytes(buffer);
			System.out.println("> check-ins: " + chkList.toString());
			System.out.println("...list of check-ins retrieved.");
		}
	}
	
	public static void main(String[] args) throws Exception {
		CheckInsTable chkTable = new CheckInsTable("check-ins");
		
		System.out.println("Creating HBase checkIns table...");
		chkTable.createTable();
		System.out.println("...done");
		
		ParseCheckIn pr = new ParseCheckIn();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		line = br.readLine();
		
		//Checking serialization and deserialition
		CheckIn chk1 = new CheckIn();
		CheckIn chk2 = new CheckIn();
		chk1 = pr.parseLine(line);
		line = br.readLine();
		chk2 = pr.parseLine(line);
		CheckInList chkList1 = new CheckInList(1);
		CheckInList chkList2 = new CheckInList(1);
		chkList1.getCheckInList().add(chk1);
		chkList1.getCheckInList().add(chk2);

		byte[] bytes = chk1.getBytes();
		chkTable.putSingle(Bytes.toBytes(chk1.getUserId()), Bytes.toBytes(chk1.getTimestamp()), bytes);
		chkTable.getSingle(Bytes.toBytes(chk1.getUserId()), Bytes.toBytes(chk1.getTimestamp()));
		bytes = chk2.getBytes();
		chkTable.putSingle(Bytes.toBytes(chk2.getUserId()), Bytes.toBytes(chk2.getTimestamp()), bytes);
		chkTable.getSingle(Bytes.toBytes(chk2.getUserId()), Bytes.toBytes(chk2.getTimestamp()));
		
		bytes = chkList1.getCompressedBytes();
		chkTable.putList(Bytes.toBytes(chkList1.getUserId()), Bytes.toBytes(chkList1.getCheckInList().get(0).getTimestamp()), bytes);
		chkTable.getListCompressed(Bytes.toBytes(chkList1.getUserId()), Bytes.toBytes(chkList1.getCheckInList().get(0).getTimestamp()));

	}
}

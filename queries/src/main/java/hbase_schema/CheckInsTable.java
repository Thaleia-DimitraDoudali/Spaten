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

public class CheckInsTable {

	protected String tableName;
    protected HTable table;
	
	public CheckInsTable(String nm) {
    	this.tableName = nm;
	}

	public void createTable() throws Exception {
		
        Configuration hbaseConf = HBaseConfiguration.create();
        HBaseAdmin admin = new HBaseAdmin(hbaseConf);
        /*if (admin.tableExists(this.tableName)) {
            admin.disableTable(this.tableName);
            admin.deleteTable(this.tableName);
        }*/
        HTableDescriptor descriptor = new HTableDescriptor(this.tableName);
        descriptor.addFamily(new HColumnDescriptor("checkIns"));

       // admin.createTable(descriptor);
        admin.close();
        this.table = new HTable(hbaseConf, this.tableName);
    }
	
	public void putSingle(byte[] row, byte[] qualifier, byte[] data) throws IOException {		
		Put p = new Put(row);
		p.add(Bytes.toBytes("checkIns"), qualifier, data);
		//System.out.println("...single check-in inserted.");
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
	
	public static void main(String[] args) throws Exception {
		
		String line;
		CheckIn chk;
		byte[] row, qualifier, data;
		
		CheckInsTable chkTable = new CheckInsTable("check-ins");		
		System.out.println("Creating HBase checkIns table...");
		chkTable.createTable();
		System.out.println("...done");
		
		ParseCheckIn pr = new ParseCheckIn();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);

		line = br.readLine();
		while (line != null) {
			chk = pr.parseLine(line);
			chk.print();
			row  = chk.getKeyBytes();
			qualifier = chk.getQualifierBytes();
			data = chk.getDataBytes();
			
			chkTable.putSingle(row, qualifier, data);
			//chkTable.getSingle(row, qualifier);
			
			line = br.readLine();
		}
	
	}
}

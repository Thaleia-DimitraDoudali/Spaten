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
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import parser.ParseCheckIn;
import containers.CheckIn;
import containers.User;

public class CheckInsTable {

	protected String tableName;
	protected HTable table;
	private int regionsNo, usersNo;

	public CheckInsTable(String nm, String u, String r) {
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
		descriptor.addFamily(new HColumnDescriptor("checkIns"));

		admin.createTable(descriptor, this.getSplitKeys());
		admin.close();
		this.table = new HTable(hbaseConf, this.tableName);
	}

	public void putSingle(byte[] row, byte[] qualifier, byte[] data)
			throws IOException {
		Put p = new Put(row);
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

	public void scan() throws Exception {
		Scan scan = new Scan();
		scan.addFamily("checkIns".getBytes());
		ResultScanner scanner = table.getScanner(scan);

		for (Result result = scanner.next(); result != null; result = scanner
				.next()) {
			byte[] key = result.getRow();
			System.out.print(key + " ");
			User usr = new User();
			usr.parseBytes(key);
			usr.print();
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

		String line;
		CheckIn chk;
		byte[] row, qualifier, data;

		CheckInsTable chkTable = new CheckInsTable("check-ins", args[1], args[2]);
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
			row = chk.getKeyBytes();
			qualifier = chk.getQualifierBytes();
			data = chk.getDataBytes();
			chkTable.putSingle(row, qualifier, data);
			//chkTable.getSingle(row, qualifier);

			line = br.readLine();
		}
		br.close();
	}
}

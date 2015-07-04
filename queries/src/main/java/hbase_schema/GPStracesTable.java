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

import containers.GPStrace;
import parser.ParseGPStrace;

public class GPStracesTable implements QueriesTable {

	protected String tableName;
	protected HTable table;
	private int regionsNo, usersNo;

	public GPStracesTable(String nm, String u, String r) {
		this.tableName = nm;
		this.usersNo = Integer.parseInt(u);
		this.regionsNo = Integer.parseInt(r);
	}

	public void createTable() throws Exception {
		Configuration hbaseConf = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(hbaseConf);
		if (admin.tableExists(this.tableName)) {
			admin.disableTable(this.tableName);
			admin.deleteTable(this.tableName);
		}
		HTableDescriptor descriptor = new HTableDescriptor(this.tableName);
		descriptor.addFamily(new HColumnDescriptor("gpsTraces"));

		admin.createTable(descriptor, this.getSplitKeys());
		admin.close();
		this.table = new HTable(hbaseConf, this.tableName);
	}

	public void putSingle(byte[] row, byte[] qualifier, byte[] data)
			throws IOException {
		Put p = new Put(row);
		p.add(Bytes.toBytes("gpsTraces"), qualifier, data);
		System.out.println("...single gps-trace inserted.");
		this.table.put(p);
	}

	public void getSingle(byte[] row, byte[] qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("gpsTraces".getBytes(), qualifier);
			GPStrace tr = new GPStrace();
			tr.parseBytes(buffer);
			System.out.println("> " + tr.toString());
			System.out.println("...single gps-trace retrieved.");
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
		GPStrace tr;
		byte[] row, qualifier, data;
		
		GPStracesTable trTable = new GPStracesTable("gps-traces", args[1], args[2]);
		System.out.println("Creating HBase gps-traces table...");
		trTable.createTable();
		System.out.println("...done");
		
		ParseGPStrace pr = new ParseGPStrace();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);

		line = br.readLine();
		while (line != null){	
			if (!line.equals(" ")) {
				tr = pr.parseLine(line);
				tr.print();
				data = tr.getDataBytes();
				qualifier = tr.getQualifierBytes();
				row = tr.getKeyBytes();
				trTable.putSingle(row, qualifier, data);
				trTable.getSingle(row, qualifier);
			}
			line = br.readLine();
		}
		br.close();
	}

}

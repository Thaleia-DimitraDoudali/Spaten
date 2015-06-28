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

import containers.CheckIn;
import containers.CheckInList;
import containers.GPStrace;
import containers.GPStraceList;
import parser.ParseGPStrace;

public class GPStracesTable implements QueriesTable {

	protected String tableName;
	protected HTable table;

	public GPStracesTable(String nm) {
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
		descriptor.addFamily(new HColumnDescriptor("gpsTraces"));

		admin.createTable(descriptor);
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

	public void putList(byte[] row, byte[] qualifier, byte[] data)
			throws IOException {
		Put p = new Put(row);
		p.add(Bytes.toBytes("gpsTraces"), qualifier, data);
		System.out.println("...list of gps-traces inserted.");
	    this.table.put(p);
	}

	public void getList(byte[] row, byte[] qualifier) throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("gpsTraces".getBytes(), qualifier);
			GPStraceList trList = new GPStraceList();
			trList.parseBytes(buffer);
			System.out.println("> gps-traces: " + trList.toString());
			System.out.println("...list of gps-traces retrieved.");
		}
	}

	public void getListCompressed(byte[] row, byte[] chk_qualifier)
			throws Exception {
		Get g = new Get(row);
		Result rs = table.get(g);
		if (!rs.isEmpty()) {
			byte[] buffer = rs.getValue("gpsTraces".getBytes(), chk_qualifier);
			GPStraceList trList = new GPStraceList();
			trList.parseCompressedBytes(buffer);
			System.out.println("> check-ins: " + trList.toString());
			System.out.println("...list of gps-traces retrieved.");
		}
	}

	public static void main(String[] args) throws Exception {

		GPStracesTable trTable = new GPStracesTable("gps-traces");
		System.out.println("Creating HBase gps-traces table...");
		trTable.createTable();
		System.out.println("...done");
		
		ParseGPStrace pr = new ParseGPStrace();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);

		String line = br.readLine();
		GPStrace tr1 = new GPStrace();
		tr1 = pr.parseLine(line);
		// tr1.print();
		line = br.readLine();
		GPStrace tr2 = new GPStrace();
		tr2 = pr.parseLine(line);
		// tr2.print();

		// byte[] bytes = tr1.getBytes();
		// tr2.parseBytes(bytes);
		// tr2.print();
		
		byte[] bytes = tr1.getBytes();
		trTable.putSingle(Bytes.toBytes(tr1.getUserId()), Bytes.toBytes(tr1.getTimestamp()), bytes);
		trTable.getSingle(Bytes.toBytes(tr1.getUserId()), Bytes.toBytes(tr1.getTimestamp()));
		bytes = tr2.getBytes();
		trTable.putSingle(Bytes.toBytes(tr2.getUserId()), Bytes.toBytes(tr2.getTimestamp()), bytes);
		trTable.getSingle(Bytes.toBytes(tr2.getUserId()), Bytes.toBytes(tr2.getTimestamp()));

		GPStraceList trl1 = new GPStraceList(1);
		trl1.add(tr1);
		trl1.add(tr2);
		GPStraceList trl2 = new GPStraceList();

		bytes = trl1.getBytes();
		trl2.parseBytes(bytes);
		trl2.print();
		
		//bytes = trl1.getBytes();
		//trTable.putList(Bytes.toBytes(trl1.getUserId()), Bytes.toBytes(trl1.getTraceList().get(0).getTimestamp()), bytes);
		//trTable.getList(Bytes.toBytes(trl1.getUserId()), Bytes.toBytes(trl1.getTraceList().get(0).getTimestamp()));
		
		//bytes = trl1.getCompressedBytes();
		//trTable.putList(Bytes.toBytes(trl1.getUserId()), Bytes.toBytes(trl1.getTraceList().get(0).getTimestamp()), bytes);
		//trTable.getListCompressed(Bytes.toBytes(trl1.getUserId()), Bytes.toBytes(trl1.getTraceList().get(0).getTimestamp()));
	}

}

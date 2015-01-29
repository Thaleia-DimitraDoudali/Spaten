package restaurants;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HBase_restaurants {
	
	public HTable construct_HTable() throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HTable hTable = new HTable(conf, "restaurants_HTable");
		return hTable;
	}
	
	public void put_HTable(int row, byte[] data, HTable table) throws IOException {
		Put p = new Put(Bytes.toBytes(row));
		//how you define a family and a qualifier?
		p.add(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"), data);
	    table.put(p);
	}
	
	public byte[] get_HTable(int row, HTable table) throws IOException {
	    Get g = new Get(Bytes.toBytes(row));
	    Result r = table.get(g);
	    byte [] value = r.getValue(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"));
	    return value;
	}
}	

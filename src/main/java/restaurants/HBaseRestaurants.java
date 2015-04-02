package restaurants;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;


public class HBaseRestaurants {
	
	public HTable constructHTable() throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HTable hTable = new HTable(conf, "restaurantsHTable");
		return hTable;
	}
	
	public void scanHTable(HTable htable) throws IOException, ClassNotFoundException {
		Scan scan = new Scan();
		ResultScanner scanner = htable.getScanner(scan);
		
		byte[] resBytes;
		for (Result result = scanner.next(); result != null; result = scanner.next()) {
			resBytes = result.getValue(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"));
			Serializer ser = new Serializer();
			Restaurant rst = ser.deserialize(resBytes);
			System.out.println("----Restaurant "+ result.getRow().toString() + " ");
			rst.print();
		}

	}
	
	public void putHTable(int row, byte[] data, HTable table) throws IOException {
		Put p = new Put(Bytes.toBytes(row));
		p.add(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"), data);
	    table.put(p);
	}
	
	public byte[] getHTable(int row, HTable table) throws IOException {
	    Get g = new Get(Bytes.toBytes(row));
	    Result r = table.get(g);
	    byte [] value = r.getValue(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"));
	    return value;
	}
}	

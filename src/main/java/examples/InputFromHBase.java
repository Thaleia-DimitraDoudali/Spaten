package examples;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.Job;

import restaurants.Restaurant;
import restaurants.Serializer;

public class InputFromHBase {

	public static class MyMapper extends TableMapper<Text, Text> {

		public void map(ImmutableBytesWritable row, Result value, Context context) 
				throws InterruptedException, IOException {
			
			try {
				byte[] resBytes = value.getValue(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"));
				Serializer ser = new Serializer();
				Restaurant rst = ser.deserialize(resBytes);
				rst.print();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		Configuration config = HBaseConfiguration.create();
		Job job = new Job(config, "InputFromHBase");
		job.setJarByClass(InputFromHBase.class);    

		Scan scan = new Scan();
		scan.setCaching(500);        
		scan.setCacheBlocks(false);  

		TableMapReduceUtil.initTableMapperJob(
				"restaurantsHTable",        // input HBase table name
				scan,             			// Scan instance to control CF and attribute selection
				MyMapper.class,   			// mapper
				null,             			// mapper output key
				null,             			// mapper output value
				job);
		
		job.setOutputFormatClass(NullOutputFormat.class);

		job.waitForCompletion(true);

	}

}

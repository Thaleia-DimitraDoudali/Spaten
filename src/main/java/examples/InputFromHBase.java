package examples;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;

import restaurants.Restaurant;
import restaurants.Serializer;

public class InputFromHBase {

	public static class MyMapper extends TableMapper<Text, Text> {

		public void map(ImmutableBytesWritable row, Result value, Context context) 
				throws InterruptedException, IOException {
			
			Serializer ser = new Serializer();
			Restaurant rst = null;			
			try {
				byte[] resBytes = value.getValue(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"));
				rst = ser.deserialize(resBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			context.write(new Text(row.toString()), rst.getRestText());
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
		
    	job.setOutputKeyClass(Text.class);
    	job.setOutputValueClass(Text.class);
    	job.setOutputFormatClass(TextOutputFormat.class);
    	
        FileOutputFormat.setOutputPath(job, new Path(args[0]));

		job.waitForCompletion(true);
		
		//After execution, copyToLocal the args[0] file

	}

}

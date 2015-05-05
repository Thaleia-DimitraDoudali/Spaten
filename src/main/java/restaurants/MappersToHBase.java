package restaurants;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class MappersToHBase {
	
	public static class Map extends Mapper<LongWritable, Text, Text, Put> {
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
		
			Random r = new Random();
			double row = r.nextDouble();
			
			ParseJson parser = new ParseJson();
			JSONObject obj=null;
			try {
				obj = new JSONObject(value.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			Restaurant rest = null;
			try {
				rest = parser.returnRestaurant(Integer.parseInt(key.toString()), obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}		
			
			Serializer ser = new Serializer();
			byte[] bytes = ser.serialize(rest);					
			Put p = new Put(Bytes.toBytes(row));
			//Put p = new Put(Bytes.toBytes(key.toString()));
			p.add(Bytes.toBytes("restaurant"), Bytes.toBytes("rest"), bytes);
			
			context.write(new Text(key.toString()), p);
				
		}
		
	}
	
    public static void main(String[] args) throws Exception {
    	
    	Configuration config = HBaseConfiguration.create();
    	Job job = new Job(config, "MappersToHBase");
    	job.setJarByClass(MappersToHBase.class); 
    	job.setOutputKeyClass(Text.class);
    	job.setOutputValueClass(Text.class);
    	job.setMapOutputKeyClass(Text.class);
    	job.setMapOutputValueClass(Text.class);

    	job.setInputFormatClass(TextInputFormat.class);
    	job.setOutputFormatClass(TextOutputFormat.class);

    	FileInputFormat.setInputPaths(job, new Path(args[0]));
  
    	job.setMapperClass(Map.class);
    	TableMapReduceUtil.initTableReducerJob("restaurantsHTable",	null, job);
    	job.setNumReduceTasks(0); 
    	
    	job.waitForCompletion(true);
    	
    	//Job finished see what is on hbase table
    	HBaseRestaurants hbaseRest = new HBaseRestaurants();
		HTable htable = hbaseRest.constructHTable();
		hbaseRest.scanHTable(htable);
    	

    }

}

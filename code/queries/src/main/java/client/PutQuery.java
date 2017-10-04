package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import containers.CheckIn;
import parser.ParseCheckIn;

public class PutQuery extends AbstractQueryClient implements Runnable{
	
	private String file;
	
	public void putSingle(byte[] row, byte[] qualifier, byte[] data)
			throws IOException {
		Put p = new Put(row);
		p.add(Bytes.toBytes("checkIns"), qualifier, data);
		//System.out.println("...single check-in inserted.");
		this.table.put(p);
	}
	
	@Override
	public void executeQuery() throws Exception {
		
		String line;
		CheckIn chk;
		byte[] row, qualifier, data;
		
		this.executionTime = System.currentTimeMillis();		
		this.openConnection("check-ins");
		
		ParseCheckIn pr = new ParseCheckIn();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		line = br.readLine();
		//while (line != null) {
			chk = pr.parseLine(line);
			//chk.print();
			row = chk.getKeyBytes();
			qualifier = chk.getQualifierBytes();
			data = chk.getDataBytes();
			this.putSingle(row, qualifier, data);
			//chkTable.getSingle(row, qualifier);

			line = br.readLine();
		//}
		br.close();
		
		this.closeConnection();		
		this.executionTime = System.currentTimeMillis() - this.executionTime;
	}
	
	public void run() {
		try {
			this.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}


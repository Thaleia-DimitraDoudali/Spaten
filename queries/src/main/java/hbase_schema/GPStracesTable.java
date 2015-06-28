package hbase_schema;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import containers.GPStrace;
import containers.GPStraceList;
import parser.ParseGPStrace;

public class GPStracesTable implements QueriesTable{

	public GPStracesTable() {
		// TODO Auto-generated constructor stub
	}

	public void createTable() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void putSingle(byte[] row, byte[] chk_qualifier, byte[] data)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void getSingle(byte[] row, byte[] fr_qualifier) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void putList(byte[] row, byte[] chk_qualifier, byte[] data)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void getList(byte[] row, byte[] chk_qualifier) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void getListCompressed(byte[] row, byte[] chk_qualifier)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) throws Exception {
		
		ParseGPStrace pr = new ParseGPStrace();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		GPStrace tr1 = new GPStrace();
		tr1 = pr.parseLine(line);
		//tr1.print();
		line = br.readLine();
		GPStrace tr2 = new GPStrace();
		tr2 = pr.parseLine(line);
		//tr2.print();
		
		//byte[] bytes = tr1.getBytes();
		//tr2.parseBytes(bytes);
		//tr2.print();
		
		
		GPStraceList trl1 = new GPStraceList(1);
		trl1.add(tr1);
		trl1.add(tr2);
		GPStraceList trl2 = new GPStraceList();
		
		byte[] bytes = trl1.getBytes();
		trl2.parseBytes(bytes);
		trl2.print();
	}

}

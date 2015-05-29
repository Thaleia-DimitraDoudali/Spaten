package parseJson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class TransformJSONFile {
	 
	public TransformJSONFile(String source, String dest) throws Exception {
				
	    BufferedReader br = new BufferedReader(new FileReader(source));
	    BufferedWriter bw = new BufferedWriter(new FileWriter(dest));
	    
	    
	    String line;
        line=br.readLine();
        while (line != null){
            if (line.contains("[{")) {
              	line = line.substring(1);
            }
            if (line.contains("}]")) {
               	line = line.substring(0, line.length()-1) + "\n";
            }
            if (line.contains("},")) {
               	line = line.substring(0, line.length()-1) + "\n";
            }
            bw.write(line);
            line = br.readLine();
        }
	    
	    br.close();
	    bw.close();
	}
	
	public static void main(String[] args) {
		
		String source = "/home/thaleia/Desktop/thesis/items/items-1k.json";
		String dest = "/home/thaleia/Desktop/thesis/items/items-1k-new.json";
		
		try {
			new TransformJSONFile(source, dest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
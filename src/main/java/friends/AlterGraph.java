package friends;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AlterGraph {

	public AlterGraph() {}
	
	public BufferedWriter createWriter(String fileName) {

		try {
			String workingDir = System.getProperty("user.dir");
			File file = new File(workingDir + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			return bw;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedReader createReader(String path) {
		try {
	
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			return br;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void alter(BufferedReader br, BufferedWriter bw) throws IOException {
		
		String line = null, out = null;
        line=br.readLine();
        int new_user_id = 1; 
        String old_user_id, old_friend_id;
        String[] line_nums;
        while (line != null){
        	//parse line
        	line_nums = line.split("\t");
        	old_user_id = line_nums[0];
        	old_friend_id = line_nums[1];
        	
        	//left column
        	out = old_user_id + "\t";
        	//right column
        	out += old_friend_id + "\n";
        	
        	System.out.println(out);
        	bw.write(out);
            line = br.readLine();
        }
        br.close();
        bw.close();
	}
	
	
	public static void main(String[] args) {
		
		AlterGraph gr = new AlterGraph();
		
		BufferedWriter bw = gr.createWriter("friends_graph.net");
		BufferedReader br = gr.createReader(args[0]);
		
		try {
			gr.alter(br, bw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

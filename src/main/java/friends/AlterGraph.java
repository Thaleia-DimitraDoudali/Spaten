package friends;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AlterGraph {

	public AlterGraph() {
	}

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
		line = br.readLine();
		int new_user_id = 1, new_friend = 0;
		int old_user = 0, old_friend = 0, keep_old_user = 0;
		String[] line_nums;

		// parse line
		if (line != null) {
			line_nums = line.split("\t");
			old_user = Integer.parseInt(line_nums[0]);
			old_friend = Integer.parseInt(line_nums[1]);
			keep_old_user = old_user;
		}

		while (line != null) {

			if (keep_old_user != old_user) {
				keep_old_user = old_user;
				new_user_id++;
			}

			// left column
			out = new_user_id + "\t";
			// right column
			new_friend = old_friend % 100;
			out += new_friend + "\n";

			System.out.println(out);
			bw.write(out);
			line = br.readLine();
			// parse line
			if (line != null) {
				line_nums = line.split("\t");
				old_user = Integer.parseInt(line_nums[0]);
				old_friend = Integer.parseInt(line_nums[1]);
			}
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

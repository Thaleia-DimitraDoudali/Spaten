package parser;

public class ParseFriend {

	public int[] parseLine(String line) {
		
		int[] id = new int[2];
		
		String splt[] = line.split("\t");
		id[0] = Integer.parseInt(splt[0]);
		id[1] = Integer.parseInt(splt[1]);
	
		return id;
	}

}


package parser;

import containers.User;
import containers.UserList;

public class ParseFriend {

	public UserList parseLine(String line) {
		
		int id1, id2;
		UserList userList = new UserList();
		
		String splt[] = line.split("\t");
		id1 = Integer.parseInt(splt[0]);
		id2 = Integer.parseInt(splt[1]);
		
		User usr1 = new User(id1);
		userList.add(usr1);
		User usr2 = new User(id2);
		userList.add(usr2);
		
		return userList;
	}

}

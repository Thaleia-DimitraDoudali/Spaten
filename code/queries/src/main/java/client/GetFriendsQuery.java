package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import containers.User;
import containers.UserList;
import coprocessors.FriendsProtocol;

public class GetFriendsQuery extends AbstractQueryClient {

	private User user;
	private UserList friendList;
	private Class<FriendsProtocol> protocol = FriendsProtocol.class;
	private long executionTime;
	private String outFile;
	private boolean print;

	public GetFriendsQuery() {
	}

	public GetFriendsQuery(String id) {
		int usrId = Integer.parseInt(id);
		this.user = new User(usrId);
	}

	@Override
	public void executeQuery() {

	}

	public void executeSerializedQuery() throws Exception {

		friendList = new UserList(this.user.getUserId());

		this.executionTime = System.currentTimeMillis();

		BufferedWriter bw = null;
		if (print) {
			bw = this.createWriter(this.outFile);
			bw.write("Getting friends of user no." + this.user.getUserId()
					+ "\n");
		}

		FriendsProtocol prot = this.table.coprocessorProxy(
				FriendsProtocol.class, this.user.getKeyBytes());
		try {
			friendList.parseCompressedBytes(prot.getFriends(this.user
					.getKeyBytes()));
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null,
					ex);
		}

		this.executionTime = System.currentTimeMillis() - this.executionTime;
		if (print) {
			bw.write("Query executed in " + this.executionTime / 1000 + "s\n");

			bw.write("Friends of user no." + this.user.getUserId() + " are:\n");
			for (User usr : this.friendList.getUserList()) {
				bw.write(usr.getUserId() + " ");
			}
			bw.write("\n");
			bw.close();
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserList getFriendList() {
		return friendList;
	}

	public void setFriendList(UserList friendList) {
		this.friendList = friendList;
	}

	public Class<FriendsProtocol> getProtocol() {
		return protocol;
	}

	public void setProtocol(Class<FriendsProtocol> protocol) {
		this.protocol = protocol;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public boolean isPrint() {
		return print;
	}

	public void setPrint(boolean print) {
		this.print = print;
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public void runQuery(String[] in) throws Exception {
		GetFriendsQuery client = new GetFriendsQuery(in[0]);
		client.setOutFile(in[1]);
		client.setProtocol(FriendsProtocol.class);
		client.openConnection("friends");
		client.executeSerializedQuery();
		client.closeConnection();
	}

	public static void main(String[] args) throws Exception {
		GetFriendsQuery client = new GetFriendsQuery();
		client.runQuery(args);
	}
}

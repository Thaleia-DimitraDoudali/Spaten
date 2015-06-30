package coprocessors;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

import containers.User;

public interface FriendsProtocol extends CoprocessorProtocol{

	public byte[] getFriends(User usr) throws Exception;
}

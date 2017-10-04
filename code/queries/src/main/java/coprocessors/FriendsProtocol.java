package coprocessors;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public interface FriendsProtocol extends CoprocessorProtocol{

	public byte[] getFriends(byte[] row) throws Exception;
}


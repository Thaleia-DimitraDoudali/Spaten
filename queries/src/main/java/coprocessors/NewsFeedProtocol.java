package coprocessors;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public interface NewsFeedProtocol extends CoprocessorProtocol{
	
	public byte[] getNewsFeed(byte[] row, byte[] date) throws Exception;
}

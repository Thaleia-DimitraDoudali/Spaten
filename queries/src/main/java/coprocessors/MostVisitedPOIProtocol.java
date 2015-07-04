package coprocessors;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public interface MostVisitedPOIProtocol extends CoprocessorProtocol{
	
	public byte[] getMostVisitedPOI(byte[] row) throws Exception;
}

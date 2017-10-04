package coprocessors;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public interface MostVisitedTraceProtocol extends CoprocessorProtocol{
	
	public byte[] getMostVisitedTrace(byte[] row) throws Exception;
}


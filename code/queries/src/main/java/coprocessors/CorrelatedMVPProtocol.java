package coprocessors;

import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public interface CorrelatedMVPProtocol extends CoprocessorProtocol {
	
	public byte[] getCorrelatedMVP(byte[] row, byte[] data) throws Exception;
}

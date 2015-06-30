package coprocessors;

import java.io.IOException;
import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;
import containers.User;

public interface MostVisitedPOIProtocol extends CoprocessorProtocol{
	
	public byte[] getMostVisitedPOI(User usr) throws IOException;
}

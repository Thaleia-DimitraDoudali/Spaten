package coprocessors;

import java.io.IOException;

import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;

public class MostVisitedPOIEndpoint extends BaseEndpointCoprocessor implements MostVisitedPOIProtocol{

	public byte[] getMostVisitedPOI(byte[] row) throws IOException {
		byte[] result = null;
        HRegion region =  ((RegionCoprocessorEnvironment)getEnvironment()).getRegion();

		
		
		return result;
	}

}

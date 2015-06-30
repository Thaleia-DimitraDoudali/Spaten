package coprocessors;

import java.io.IOException;

import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;

import containers.User;

public class MostVisitedPOIEndpoint extends BaseEndpointCoprocessor implements MostVisitedPOIProtocol{

	public byte[] getMostVisitedPOI(User usr) throws IOException {
		byte[] result = null;
		System.out.println("Getting friends of user no." + usr.getUserId());
        HRegion region =  ((RegionCoprocessorEnvironment)getEnvironment()).getRegion();

		
		
		return result;
	}

}

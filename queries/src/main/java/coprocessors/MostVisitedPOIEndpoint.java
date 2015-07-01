package coprocessors;

import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;

import containers.CheckIn;
import containers.MostVisitedPOI;
import containers.User;
import containers.UserList;

public class MostVisitedPOIEndpoint extends BaseEndpointCoprocessor implements MostVisitedPOIProtocol{

	public byte[] getMostVisitedPOI(byte[] row) throws Exception {
		byte[] result = null;
        HRegion region =  ((RegionCoprocessorEnvironment)getEnvironment()).getRegion();
		
        UserList usrList = new UserList();
        usrList.parseBytes(row);
        System.out.println("inside coprocessor");
       // for (User usr: usrList.getUserList()) {
          //  Get g = new Get(usr.getKeyBytes());
        	Get g = new Get(usrList.getUserList().get(0).getKeyBytes());
            Result rs = region.get(g);
			CheckIn chk = new CheckIn();
    		if (!rs.isEmpty() && (rs != null)) {
    			System.out.println("found a checkin1");
    			for(Map.Entry<byte[], byte[]> e : rs.getFamilyMap("checkIns".getBytes()).entrySet()) {
    				System.out.println("here");
    				chk.parseBytes(e.getValue());
    				chk.print();
    			}
    		}
    		MostVisitedPOI mvp = new MostVisitedPOI(usrList.getUserList().get(0), chk.getPoi(), 42);
    		mvp.print();
        //}
		
		return mvp.getDataBytes();
	}

}

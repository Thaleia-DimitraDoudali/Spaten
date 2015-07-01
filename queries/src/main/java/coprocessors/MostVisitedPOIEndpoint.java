package coprocessors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;

import containers.CheckIn;
import containers.MostVisitedPOI;
import containers.POI;
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
        
        	HashMap<String, MostVisitedPOI> cache = new HashMap<String, MostVisitedPOI>();
        	String key = "";
        	Get g = new Get(usrList.getUserList().get(0).getKeyBytes());
            Result rs = region.get(g);
    		if (!rs.isEmpty() && (rs != null)) {
    			System.out.println("found a checkin1");
    			for(Map.Entry<byte[], byte[]> e : rs.getFamilyMap("checkIns".getBytes()).entrySet()) {
    				CheckIn chk = new CheckIn();
    	    		MostVisitedPOI mvp = new MostVisitedPOI();
    				chk.parseBytes(e.getValue());
    				key = chk.getPoi().getLatitude() + " " + chk.getPoi().getLongitude();
    				if (!cache.containsKey(key)) {
    					mvp = new MostVisitedPOI(usrList.getUserList().get(0), chk.getPoi(), 1);
    					cache.put(key, mvp);
    				} else {
    					mvp = cache.get(key);
    					int c = mvp.getCounter() + 1;
    					mvp.setCounter(c);
    					cache.remove(key);
    					cache.put(key, mvp);
    				}
    			}
    		}
    		
    		for (Map.Entry<String, MostVisitedPOI> entry : cache.entrySet()) {
    		    System.out.println(entry.getKey()+" : "+entry.getValue());
    		}
        //}
		
		return cache.get(key).getDataBytes();
	}

}

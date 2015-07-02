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
import containers.MostVisitedPOIList;
import containers.POI;
import containers.User;
import containers.UserList;

public class MostVisitedPOIEndpoint extends BaseEndpointCoprocessor implements
		MostVisitedPOIProtocol {

	public byte[] getMostVisitedPOI(byte[] row) throws Exception {
		byte[] result = null;
		MostVisitedPOIList mvpList = new MostVisitedPOIList();
		
		HRegion region = ((RegionCoprocessorEnvironment) getEnvironment())
				.getRegion();

		UserList usrList = new UserList();
		usrList.parseBytes(row);

		for (User usr : usrList.getUserList()) {

			HashMap<String, MostVisitedPOI> cache = new HashMap<String, MostVisitedPOI>();
			String key = "";
			
			Get g = new Get(usr.getKeyBytes());
			Result rs = region.get(g);
			
			if (!rs.isEmpty() && (rs != null)) {
				for (Map.Entry<byte[], byte[]> e : rs.getFamilyMap("checkIns".getBytes()).entrySet()) {
					CheckIn chk = new CheckIn();
					MostVisitedPOI mvp = new MostVisitedPOI();
					chk.parseBytes(e.getValue());
					key = chk.getPoi().getLatitude() + " " + chk.getPoi().getLongitude();
					if (!cache.containsKey(key)) {
						mvp = new MostVisitedPOI(usr, chk.getPoi(), 1);
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
			//Go through the hash set and keep the most visited poi
			MostVisitedPOI finalMVP = new MostVisitedPOI(null, null, 0);
			for (Map.Entry<String, MostVisitedPOI> entry : cache.entrySet()) {
				if (entry.getValue().getCounter() >= finalMVP.getCounter()) {
					finalMVP = new MostVisitedPOI();
					finalMVP = entry.getValue();
				}
			}
			finalMVP.print();
			mvpList.add(finalMVP);
		}
		result = mvpList.getDataBytes();
		return result;
	}

}

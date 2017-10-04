package coprocessors;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.HRegion;

import containers.GPStrace;
import containers.MostVisitedTrace;
import containers.MostVisitedTraceList;
import containers.User;
import containers.UserList;

public class MostVisitedTraceEndpoint extends BaseEndpointCoprocessor implements MostVisitedTraceProtocol{

	public byte[] getMostVisitedTrace(byte[] row) throws Exception {
		byte[] result = null;
		MostVisitedTraceList mvtrList = new MostVisitedTraceList();
		
		HRegion region = ((RegionCoprocessorEnvironment) getEnvironment()).getRegion();

		UserList usrList = new UserList();
		usrList.parseBytes(row);
		
		for (User usr : usrList.getUserList()) {

			HashMap<String, MostVisitedTrace> cache = new HashMap<String, MostVisitedTrace>();
			String key = "";
			
			Get g = new Get(usr.getKeyBytes());
			Result rs = region.get(g);
			
			if (!rs.isEmpty() && (rs != null)) {
				for (Map.Entry<byte[], byte[]> e : rs.getFamilyMap("gpsTraces".getBytes()).entrySet()) {
					GPStrace tr = new GPStrace();
					MostVisitedTrace mvtr = new MostVisitedTrace();
					tr.parseBytes(e.getValue());
					key = tr.getLatitude() + " " + tr.getLongitude();
					if (!cache.containsKey(key)) {
						mvtr = new MostVisitedTrace(usr, tr, 1);
						cache.put(key, mvtr);
					} else {
						mvtr = cache.get(key);
						int c = mvtr.getCounter() + 1;
						mvtr.setCounter(c);
						cache.remove(key);
						cache.put(key, mvtr);
					}
				}
			} else {
				break;
			}
			//Go through the hash set and keep the most visited poi
			MostVisitedTrace finalMVTR = new MostVisitedTrace(null, null, 0);
			for (Map.Entry<String, MostVisitedTrace> entry : cache.entrySet()) {
				if (entry.getValue().getCounter() >= finalMVTR.getCounter()) {
					finalMVTR = new MostVisitedTrace();
					finalMVTR = entry.getValue();
				}
			}
			mvtrList.add(finalMVTR);
		}
		result = mvtrList.getCompressedBytes();
		return result;
	}

}


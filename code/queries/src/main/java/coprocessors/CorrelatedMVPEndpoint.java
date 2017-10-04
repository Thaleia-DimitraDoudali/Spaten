package coprocessors;

import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.HRegion;

import containers.CheckIn;
import containers.MostVisitedPOI;
import containers.MostVisitedPOIList;
import containers.User;
import containers.UserList;

public class CorrelatedMVPEndpoint extends BaseEndpointCoprocessor implements CorrelatedMVPProtocol {

	public byte[] getCorrelatedMVP(byte[] row, byte[] data) throws Exception {
		byte[] result = null;
		MostVisitedPOIList mvpList = new MostVisitedPOIList();
		
		HRegion region = ((RegionCoprocessorEnvironment) getEnvironment()).getRegion();

		MostVisitedPOI mvpoi = new MostVisitedPOI();
		mvpoi.parseBytes(data);
		
		UserList usrList = new UserList();
		usrList.parseBytes(row);

		for (User usr : usrList.getUserList()) {
			
			Get g = new Get(usr.getKeyBytes());
			Result rs = region.get(g);
			
			MostVisitedPOI mvp = new MostVisitedPOI(usr, mvpoi.getPoi(), 0);

			if (!rs.isEmpty() && (rs != null)) {								
				for (Map.Entry<byte[], byte[]> e : rs.getFamilyMap("checkIns".getBytes()).entrySet()) {
					CheckIn chk = new CheckIn();
					chk.parseBytes(e.getValue());
					boolean lat = (chk.getPoi().getLatitude() == mvpoi.getPoi().getLatitude());
					boolean lng = (chk.getPoi().getLongitude() == mvpoi.getPoi().getLongitude());
					
					if (lat && lng) {
						mvp.setCounter(mvp.getCounter()+1);
					}
				}
			} else {
				break;
			}
			mvpList.add(mvp);
		}
		result = mvpList.getCompressedBytes();
		return result;
	}

}


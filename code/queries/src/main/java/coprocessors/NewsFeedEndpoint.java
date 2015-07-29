package coprocessors;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.util.Bytes;

import containers.CheckIn;
import containers.CheckInList;
import containers.User;
import containers.UserList;

public class NewsFeedEndpoint extends BaseEndpointCoprocessor implements NewsFeedProtocol {
	
	public byte[] getNewsFeed(byte[] row, byte[] date) throws Exception {
		byte[] result = null;
		CheckInList chkList = new CheckInList();

		HRegion region = ((RegionCoprocessorEnvironment) getEnvironment()).getRegion();

		UserList usrList = new UserList();
		usrList.parseBytes(row);
		
		ByteBuffer buffer = ByteBuffer.wrap(date);
		long dateFrom = buffer.getLong();
		long milPerDay = 1000*60*60*24;
		long dateTo = dateFrom + milPerDay;

		for (User usr : usrList.getUserList()) {
			Get g = new Get(usr.getKeyBytes());
            g.setFilter(new ColumnRangeFilter(Bytes.toBytes(dateFrom), true, Bytes.toBytes(dateTo), false));
			Result rs = region.get(g);
			if (!rs.isEmpty() && (rs != null)) {
				for (Map.Entry<byte[], byte[]> e : rs.getFamilyMap("checkIns".getBytes()).entrySet()) {
					CheckIn chk = new CheckIn();
					chk.parseBytes(e.getValue());
					chkList.add(chk);
				}
			} else {
				break;
			}
		}
		
		result = chkList.getCompressedBytes();
		return result;
	}
	
}

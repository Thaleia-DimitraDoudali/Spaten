package coprocessors;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.coprocessor.BaseEndpointCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.HRegion;
	
import java.util.Map;

import containers.User;
import containers.UserList;

public class FriendsEndpoint extends BaseEndpointCoprocessor implements FriendsProtocol{

	public byte[] getFriends(byte[] row) throws Exception {
		byte[] result = null;
		UserList friendList = new UserList();
		User user = new User();
		user.parseBytes(row);
        HRegion region =  ((RegionCoprocessorEnvironment)getEnvironment()).getRegion();

        //Get friends of usr
        Get g = new Get(row);
        Result rs = region.get(g);
		if (!rs.isEmpty() && (rs != null)) {
			for(Map.Entry<byte[], byte[]> e : rs.getFamilyMap("friends".getBytes()).entrySet()) {
				User friend = new User();
				friend.parseBytes(e.getValue());
				if ((friend.getUserId() != 0 ) && (friend.getUserId() != user.getUserId())) {
					friendList.add(friend);
				}
			}
		}
		result = friendList.getCompressedBytes();
		return result;
	}

}


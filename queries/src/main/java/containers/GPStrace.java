package containers;

import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.util.Bytes;

public class GPStrace implements Serializable {

	private int userId;
	private double latitude;
	private double longitude;
	private long timestamp;

	public GPStrace() {
	}

	public GPStrace(int id, double x, double y, long t) {
		this.userId = id;
		this.latitude = x;
		this.longitude = y;
		this.timestamp = t;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return this.userId + " (" + this.latitude + ", " + this.longitude + ") " + this.timestamp;
	}
	
	public void print() {
		System.out.println(this.toString());
	}

	public void parseBytes(byte[] bytes) throws Exception {
		int index = 0;
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		this.userId = buffer.getInt(index);
		index += Integer.SIZE / 8;
		this.latitude = buffer.getDouble(index);
		index += Double.SIZE / 8;
		this.longitude = buffer.getDouble(index);
		index += Double.SIZE / 8;
		this.timestamp = buffer.getLong(index);
	}

	public byte[] getBytes() throws Exception {
		int totalSize = Integer.SIZE / 8 // user id
				+ Double.SIZE / 8 // x
				+ Double.SIZE / 8 // y
				+ Long.SIZE / 8; // timestamp

		byte[] serializable = new byte[totalSize];
		ByteBuffer buffer = ByteBuffer.wrap(serializable);
		buffer.put(Bytes.toBytes(this.userId));
		buffer.put(Bytes.toBytes(this.latitude));
		buffer.put(Bytes.toBytes(this.longitude));
		buffer.put(Bytes.toBytes(this.timestamp));

		return serializable;
	}
}

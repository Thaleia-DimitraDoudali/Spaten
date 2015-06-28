package containers;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.util.Bytes;

public class CheckIn implements Serializable {

	private int userId;
	private long timestamp;
	private int travel;
	private POI poi;
	private Review review;

	public CheckIn() {
		this.poi = new POI();
		this.review = new Review();
	}

	public CheckIn(int id, long t, int tr, POI p, Review r) {
		this.userId = id;
		this.timestamp = t;
		this.travel = tr;
		this.poi = p;
		this.review = r;
	}

	@Override
	public String toString() {
		return  this.userId + " - " + this.timestamp + " - " + this.travel + " - " + this.poi.toString()
				+ " " + this.review.toString();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int isTravel() {
		return travel;
	}

	public void setTravel(int travel) {
		this.travel = travel;
	}

	public POI getPoi() {
		return poi;
	}

	public void setPoi(POI poi) {
		this.poi = poi;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public void parseBytes(byte[] bytes) throws Exception {
		int index = 0;
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		this.userId = buffer.getInt(index);
		index += Integer.SIZE / 8;
		this.timestamp = buffer.getLong(index);
		index += Long.SIZE / 8;
		this.travel = buffer.getInt(index);
		index += Integer.SIZE / 8;

		this.poi.parseBytes(Arrays.copyOfRange(bytes, index, bytes.length));
		index += this.poi.getTotalSize();

		this.review.parseBytes(Arrays.copyOfRange(bytes, index, bytes.length));
	}

	public byte[] getBytes() throws Exception {
		try {
			int totalSize = Integer.SIZE / 8 //user id 
					+ Long.SIZE / 8 // timestamp
					+ Integer.SIZE / 8 // travel
					+ this.poi.getBytes().length // poi bytes length
					+ this.review.getBytes().length; // review bytes length

			byte[] serializable = new byte[totalSize];
			ByteBuffer buffer = ByteBuffer.wrap(serializable);
			buffer.put(Bytes.toBytes(this.userId));
			buffer.put(Bytes.toBytes(this.timestamp));
			buffer.put(Bytes.toBytes(this.travel));
			buffer.put(this.poi.getBytes());
			buffer.put(this.review.getBytes());

			return serializable;
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(POI.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

}

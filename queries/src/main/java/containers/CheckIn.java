package containers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.util.Bytes;

import parser.ParseCheckIn;

public class CheckIn implements Serializable {

	private long timestamp;
	private int travel;
	private POI poi;
	private Review review;

	public CheckIn() {
		this.poi = new POI();
		this.review = new Review();
	}

	public CheckIn(long t, int tr, POI p, Review r) {
		this.timestamp = t;
		this.travel = tr;
		this.poi = p;
		this.review = r;
	}

	@Override
	public String toString() {
		return this.timestamp + " - " + this.travel + " - " + this.poi.toString()
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

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public void parseBytes(byte[] bytes) throws Exception {
		int index = 0;
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

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
			int totalSize = Long.SIZE / 8 // timestamp
					+ Integer.SIZE / 8 // travel
					+ this.poi.getBytes().length // poi bytes length
					+ this.review.getBytes().length; // review bytes length

			byte[] serializable = new byte[totalSize];
			ByteBuffer buffer = ByteBuffer.wrap(serializable);

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

	public static void main(String[] args) throws Exception {
		
		ParseCheckIn pr = new ParseCheckIn();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		line = br.readLine();
		
		//Checking serialization and deserialization
		CheckIn chk1 = new CheckIn();
		CheckIn chk2 = new CheckIn();
		chk1 = pr.parseLine(line);
		System.out.println(chk1.toString());
		byte[] bytes = chk1.getBytes();
		
		chk2.parseBytes(bytes);
		System.out.println(chk2.toString());

	}
}

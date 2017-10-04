package containers;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.util.Bytes;

public class MostVisitedPOI {

	private User user;
	private POI poi;
	private int counter;
	
	public MostVisitedPOI() {
		this.user = new User();
		this.poi = new POI();
	}
	
	public MostVisitedPOI(User usr, POI p, int c) {
		this.user = usr;
		this.poi = p;
		this.counter = c;
	}
	
	public String toString() {
		return user.toString() + "\t" + poi.toString() + "\t" + counter;
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public void parseBytes(byte[] bytes) throws Exception {
		int index = 0;
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		this.user.parseBytes(bytes);
		index += this.user.getDataBytes().length;

		this.poi.parseBytes(Arrays.copyOfRange(bytes, index, bytes.length));
		index += this.poi.getTotalSize();
		
		this.counter = buffer.getInt(index);
	}
	
	public byte[] getDataBytes() throws Exception {
		try {
			int totalSize = this.user.getDataBytes().length //user 
					+ this.poi.getDataBytes().length // poi bytes length
					+ Integer.SIZE / 8; //counter

			byte[] serializable = new byte[totalSize];
			ByteBuffer buffer = ByteBuffer.wrap(serializable);
			buffer.put(this.user.getDataBytes());
			buffer.put(this.poi.getDataBytes());
			buffer.put(Bytes.toBytes(this.counter));

			return serializable;
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(POI.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public POI getPoi() {
		return poi;
	}

	public void setPoi(POI poi) {
		this.poi = poi;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public static void main(String[] args) throws Exception {
		User usr = new User(1);
		POI poi = new POI(12312, 123123, "lala", "lalalla");
		int c = 45;
		
		MostVisitedPOI mvp1 = new MostVisitedPOI(usr, poi, c);
		byte[] bytes = mvp1.getDataBytes();
		
		MostVisitedPOI mvp2 = new MostVisitedPOI();
		mvp2.parseBytes(bytes);
		mvp2.print();
	}
}


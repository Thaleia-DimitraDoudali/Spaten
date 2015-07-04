package containers;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.util.Bytes;

public class MostVisitedTrace {

	private User user;
	private GPStrace trace;
	private int counter;
	
	public MostVisitedTrace() {
		this.user = new User();
		this.trace = new GPStrace();
	}
	
	public MostVisitedTrace(User usr, GPStrace tr, int c) {
		this.user = usr;
		this.trace = tr;
		this.counter = c;
	}
	
	public String toString() {
		return user.toString() + "\t" + trace.toPOIString() + "\t" + counter;
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public void parseBytes(byte[] bytes) throws Exception {
		int index = 0;
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		this.user.parseBytes(bytes);
		index += this.user.getDataBytes().length;
		this.counter = buffer.getInt(index);
		index += Integer.SIZE / 8;
		this.trace.parseBytes(Arrays.copyOfRange(bytes, index, bytes.length));
	}
	
	public byte[] getDataBytes() throws Exception {
		try {
			int totalSize = this.user.getDataBytes().length //user 
					+ Integer.SIZE / 8 //counter
					+ this.trace.getDataBytes().length; // poi bytes length

			byte[] serializable = new byte[totalSize];
			ByteBuffer buffer = ByteBuffer.wrap(serializable);
			buffer.put(this.user.getDataBytes());
			buffer.put(Bytes.toBytes(this.counter));
			buffer.put(this.trace.getDataBytes());

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

	public GPStrace getTrace() {
		return trace;
	}

	public void setTrace(GPStrace trace) {
		this.trace = trace;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

}

package containers;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.util.Bytes;

public class POI implements Serializable{

	private double latitude;
	private double longitude;
	private String title;
	private String address;
	
	private int totalSize;
		
	public POI() {}
	
	public POI(double lat, double lng, String t, String adr) {
		this.latitude = lat;
		this.longitude = lng;
		this.title = t;
		this.address = adr;
	}
	
	@Override
	public String toString() {
		return "(" + this.latitude + ", " + this.longitude + ") " + this.title + " - " + this.address;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public void parseBytes(byte[] bytes) throws Exception {
        int index = 0;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        this.totalSize = buffer.getInt(index);
        index += Integer.SIZE / 8;
        this.latitude = buffer.getDouble(index);
        index += Double.SIZE / 8;
        this.longitude = buffer.getDouble(index);
        index += Double.SIZE / 8;
        
        int bytesOnTitle = buffer.getInt(index);
        index += Integer.SIZE / 8;
        this.title = new String(bytes, index, bytesOnTitle);
        index += bytesOnTitle;
        
        int bytesOnAddress = buffer.getInt(index);
        index += Integer.SIZE / 8;
        this.address = new String(bytes, index, bytesOnAddress);
        index += bytesOnAddress;
        
	}

	public byte[] getDataBytes() throws Exception {
		try {
			totalSize = Integer.SIZE / 8											//totalSize 
					+ Double.SIZE / 8												//latitude
					+ Double.SIZE / 8												//longitude
					+ Integer.SIZE / 8 + this.title.getBytes("UTF-8").length		//title string size + string
					+ Integer.SIZE / 8 + this.address.getBytes("UTF-8").length;		//address string size + string
			
            byte[] serializable = new byte[totalSize];
            ByteBuffer buffer = ByteBuffer.wrap(serializable);
            
            buffer.put(Bytes.toBytes(totalSize));
            buffer.put(Bytes.toBytes(this.latitude));
            buffer.put(Bytes.toBytes(this.longitude));
            buffer.put(Bytes.toBytes(this.title.getBytes("UTF-8").length));
            buffer.put(this.title.getBytes("UTF-8"));
            buffer.put(Bytes.toBytes(this.address.getBytes("UTF-8").length));
            buffer.put(this.address.getBytes("UTF-8"));

            return serializable;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(POI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
	}

	public byte[] getQualifierBytes() throws Exception {
		return null;
	}

	public byte[] getKeyBytes() throws Exception {
		return null;
	}

}

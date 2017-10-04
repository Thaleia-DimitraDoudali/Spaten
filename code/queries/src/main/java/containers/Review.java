package containers;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.util.Bytes;

public class Review implements Serializable{

	private String rating;
	private String revTitle;
	private String review;
	
	public Review() {}
	
	public Review(String rat, String revT, String rev) {
		this.rating = rat;
		this.revTitle = revT;
		this.review = rev;
	}
	
	@Override
	public String toString() {
		return this.rating + " - " + this.revTitle + " - " + this.review;
	}
	
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRevTitle() {
		return revTitle;
	}

	public void setRevTitle(String revTitle) {
		this.revTitle = revTitle;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void parseBytes(byte[] bytes) throws Exception {
        int index = 0;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        int bytesOnRating = buffer.getInt(index);
        index += Integer.SIZE / 8;
        this.rating = new String(bytes, index, bytesOnRating);
        index += bytesOnRating;
        int bytesOnRevTitle = buffer.getInt(index);
        index += Integer.SIZE / 8;
        this.revTitle = new String(bytes, index, bytesOnRevTitle);
        index += bytesOnRevTitle;
        int bytesOnReview = buffer.getInt(index);
        index += Integer.SIZE / 8;
        this.review = new String(bytes, index, bytesOnReview);
	}

	public byte[] getDataBytes() throws Exception {
		try {
			int totalSize =  Integer.SIZE / 8 + this.rating.getBytes("UTF-8").length	//rating string size + string
					+ Integer.SIZE / 8 + this.revTitle.getBytes("UTF-8").length			//revTitle string size + string
					+ Integer.SIZE / 8 + this.review.getBytes("UTF-8").length;			//revTitle string size + string
			
            byte[] serializable = new byte[totalSize];
            ByteBuffer buffer = ByteBuffer.wrap(serializable);
            
            buffer.put(Bytes.toBytes(this.rating.getBytes("UTF-8").length));
            buffer.put(this.rating.getBytes("UTF-8"));
            buffer.put(Bytes.toBytes(this.revTitle.getBytes("UTF-8").length));
            buffer.put(this.revTitle.getBytes("UTF-8"));
            buffer.put(Bytes.toBytes(this.review.getBytes("UTF-8").length));
            buffer.put(this.review.getBytes("UTF-8"));

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


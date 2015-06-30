package containers;

import java.nio.ByteBuffer;


public class User implements Serializable {

	private long userId;
	
	public User() {}
	
	public User(int id) {
		this.userId = id;
	}
	
	@Override
	public String toString() {
		return  this.userId + "";
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void parseBytes(byte[] bytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.userId = buffer.getLong();		
	}
	
	public byte[] getDataBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(this.userId);
        return buffer.array();	
    }

	public byte[] getQualifierBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(this.userId);
        return buffer.array();
	}

	public byte[] getKeyBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(this.userId);
        return buffer.array();
	}
}

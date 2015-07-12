package containers;

import java.nio.ByteBuffer;

public class User implements Serializable {

	private int userId;
	
	public User() {}
	
	public User(int id) {
		this.userId = id;
	}
	
	@Override
	public String toString() {
		return  "User no." + this.userId + "";
	}
	
	public void print() {
		System.out.println(toString());
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void parseBytes(byte[] bytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
        buffer = ByteBuffer.wrap(bytes);
        this.userId = buffer.getInt();		
	}
	
	public byte[] getDataBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
        buffer.putInt(this.userId);
        return buffer.array();
    }

	public byte[] getQualifierBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
        buffer.putInt(this.userId);
        return buffer.array();	
    }

	public byte[] getKeyBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
        buffer.putInt(this.userId);
        return buffer.array();
    }
}

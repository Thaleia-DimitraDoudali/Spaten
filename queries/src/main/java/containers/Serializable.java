package containers;

public interface Serializable {

	public void parseBytes(byte[] bytes) throws Exception;
	
	public byte[] getBytes() throws Exception;
}

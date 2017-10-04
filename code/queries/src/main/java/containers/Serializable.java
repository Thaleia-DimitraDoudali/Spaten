package containers;

public interface Serializable {

	public void parseBytes(byte[] bytes) throws Exception;
	public byte[] getDataBytes() throws Exception;
	public byte[] getQualifierBytes() throws Exception;
	public byte[] getKeyBytes() throws Exception;

}


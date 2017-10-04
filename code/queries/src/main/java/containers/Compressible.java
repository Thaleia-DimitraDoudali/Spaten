package containers;

public interface Compressible {

	public byte[] getCompressedBytes();
	
	public void parseCompressedBytes(byte[] array);
}

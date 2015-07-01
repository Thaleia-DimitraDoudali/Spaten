package containers;

import java.util.List;

public class MostVisitedPOIList implements Serializable, Compressible{

	private static final int COMPRESSION_LEVEL = 4;
	private List<MostVisitedPOI> mvpList;
	
	public MostVisitedPOIList() {
		// TODO Auto-generated constructor stub
	}

	public byte[] getCompressedBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseCompressedBytes(byte[] array) {
		// TODO Auto-generated method stub
		
	}

	public void parseBytes(byte[] bytes) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public byte[] getDataBytes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getQualifierBytes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getKeyBytes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<MostVisitedPOI> getMvpList() {
		return mvpList;
	}

	public void setMvpList(List<MostVisitedPOI> mvpList) {
		this.mvpList = mvpList;
	}

}

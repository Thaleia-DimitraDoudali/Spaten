package containers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class MostVisitedPOIList implements Serializable, Compressible{

	private static final int COMPRESSION_LEVEL = 4;
	private List<MostVisitedPOI> mvpList;
	
	public MostVisitedPOIList() {
		this.mvpList = new LinkedList<MostVisitedPOI>();
	}
	
	public void add(MostVisitedPOI p) {
		this.mvpList.add(p);
	}
	
	public String toString() {
		String res = "";
		for (MostVisitedPOI p: this.mvpList) {
			res += p.toString() + "\n";
		}
		return res;
	}
	
	public void print() {
		System.out.println(this.toString());
	}

	public byte[] getCompressedBytes() {
		byte[] serialization = null;
		try {
			serialization = this.getDataBytes();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

        Deflater deflater = new Deflater();
        deflater.setLevel(COMPRESSION_LEVEL);
        deflater.setInput(serialization);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        deflater.finish();
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            stream.write(buffer, 0, count);
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] result = stream.toByteArray();
        return result;
	}

	public void parseCompressedBytes(byte[] array) {
        Inflater inflater = new Inflater();
        inflater.setInput(array);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = 0;
            try {
                count = inflater.inflate(buffer);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            stream.write(buffer, 0, count);
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] decompressed = stream.toByteArray();

        try {
			this.parseBytes(decompressed);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void parseBytes(byte[] bytes) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.mvpList = new LinkedList<MostVisitedPOI>();
        int sizeOfList = buffer.getInt();
        for (int i = 0; i < sizeOfList; i++) {
            int byteSize = buffer.getInt();
            byte[] poiSerial = new byte[byteSize];
            buffer.get(poiSerial, 0, byteSize);
            MostVisitedPOI p = new MostVisitedPOI();
            p.parseBytes(poiSerial);
            this.mvpList.add(p);
        }			
	}

	public byte[] getDataBytes() throws Exception {
		// number of pois stored
        int numberOfBytes = Integer.SIZE / 8;		
        for (MostVisitedPOI p: this.mvpList) {
            numberOfBytes += Integer.SIZE / 8;
            numberOfBytes += p.getDataBytes().length;
        }

        byte[] bytes = new byte[numberOfBytes];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        buffer.putInt(this.mvpList.size());
        for (MostVisitedPOI p: this.mvpList) {
            byte[] bytesPoi = p.getDataBytes();
            buffer.putInt(bytesPoi.length);
            buffer.put(bytesPoi);
        }
        return bytes;
	}

	public byte[] getQualifierBytes() throws Exception {
		return null;
	}

	public byte[] getKeyBytes() throws Exception {
		return null;
	}

	public List<MostVisitedPOI> getMvpList() {
		return mvpList;
	}

	public void setMvpList(List<MostVisitedPOI> mvpList) {
		this.mvpList = mvpList;
	}

}


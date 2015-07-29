package containers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class MostVisitedTraceList implements Serializable, Compressible{

	private static final int COMPRESSION_LEVEL = 4;
	private List<MostVisitedTrace> mvtrList;
	
	public MostVisitedTraceList() {
		this.mvtrList = new LinkedList<MostVisitedTrace>();
	}
	
	public void add(MostVisitedTrace p) {
		this.mvtrList.add(p);
	}
	
	public String toString() {
		String res = "";
		for (MostVisitedTrace p: this.mvtrList) {
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
        this.mvtrList = new LinkedList<MostVisitedTrace>();
        int sizeOfList = buffer.getInt();
        for (int i = 0; i < sizeOfList; i++) {
            int byteSize = buffer.getInt();
            byte[] trSerial = new byte[byteSize];
            buffer.get(trSerial, 0, byteSize);
            MostVisitedTrace p = new MostVisitedTrace();
            p.parseBytes(trSerial);
            this.mvtrList.add(p);
        }			
	}

	public byte[] getDataBytes() throws Exception {
		// number of traces stored
        int numberOfBytes = Integer.SIZE / 8;		
        for (MostVisitedTrace p: this.mvtrList) {
            numberOfBytes += Integer.SIZE / 8;
            numberOfBytes += p.getDataBytes().length;
        }

        byte[] bytes = new byte[numberOfBytes];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        buffer.putInt(this.mvtrList.size());
        for (MostVisitedTrace p: this.mvtrList) {
            byte[] bytesTr = p.getDataBytes();
            buffer.putInt(bytesTr.length);
            buffer.put(bytesTr);
        }
        return bytes;
	}

	public byte[] getQualifierBytes() throws Exception {
		return null;
	}

	public byte[] getKeyBytes() throws Exception {
		return null;
	}

	public List<MostVisitedTrace> getMvtrList() {
		return mvtrList;
	}

	public void setMvtrList(List<MostVisitedTrace> mvpList) {
		this.mvtrList = mvpList;
	}

}

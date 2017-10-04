package containers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CheckInList implements Serializable, Compressible {

	private static final int COMPRESSION_LEVEL = 4;
	private List<CheckIn> checkInList;

	public CheckInList() {
		this.checkInList = new LinkedList<CheckIn>();
	}

	public List<CheckIn> getCheckInList() {
		return checkInList;
	}

	public void setCheckInList(List<CheckIn> checkInList) {
		this.checkInList = checkInList;
	}
	
	public void add(CheckIn chk) {
		this.checkInList.add(chk);
	}

	@Override
	public String toString() {
		String res = "";
		for (CheckIn chk : checkInList) {
			res += chk.toString() + "\n";
		}
		return res;
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
		this.checkInList = new LinkedList<CheckIn>();
		int sizeOfList = buffer.getInt();
		for (int i = 0; i < sizeOfList; i++) {
			int byteSize = buffer.getInt();
			byte[] chkSerial = new byte[byteSize];
			buffer.get(chkSerial, 0, byteSize);
			CheckIn chk = new CheckIn();
			chk.parseBytes(chkSerial);
			this.checkInList.add(chk);
		}
	}

	public byte[] getDataBytes() throws Exception {

		// number of checkIns stored
		int numberOfBytes = Integer.SIZE / 8; // list length
		for (CheckIn chk : this.checkInList) {
			numberOfBytes += Integer.SIZE / 8;
			numberOfBytes += chk.getDataBytes().length;
		}

		byte[] bytes = new byte[numberOfBytes];
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		buffer.putInt(this.checkInList.size());
		for (CheckIn chk : this.checkInList) {
			byte[] bytesChk = chk.getDataBytes();
			buffer.putInt(bytesChk.length);
			buffer.put(bytesChk);
		}
		return bytes;
	}

	public byte[] getQualifierBytes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getKeyBytes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

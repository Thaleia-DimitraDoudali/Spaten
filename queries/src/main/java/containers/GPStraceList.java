package containers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class GPStraceList implements Serializable, Compressible {

	private static final int COMPRESSION_LEVEL = 4;
	int userId;
	private List<GPStrace> traceList;

	public GPStraceList() {
		this.traceList = new LinkedList<GPStrace>();
	}

	public GPStraceList(int id) {
		this.userId = id;
		this.traceList = new LinkedList<GPStrace>();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<GPStrace> getTraceList() {
		return traceList;
	}

	public void setTraceList(List<GPStrace> traceList) {
		this.traceList = traceList;
	}
	
	public void add(GPStrace tr) {
		this.traceList.add(tr);
	}
	
	@Override
	public String toString() {
		String res = "User no." + this.userId + "\n";
		for (GPStrace tr: traceList) {
			res += tr.toString() + "\n";
		}
		return res;
	}
	
	public void print() {
		System.out.println(toString());
	}

	public byte[] getCompressedBytes() {
		byte[] serialization = null;
		try {
			serialization = this.getBytes();
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
		this.traceList = new LinkedList<GPStrace>();
		this.userId = buffer.getInt();
		int sizeOfList = buffer.getInt();
		for (int i = 0; i < sizeOfList; i++) {
			int byteSize = buffer.getInt();
			byte[] trSerial = new byte[byteSize];
			buffer.get(trSerial, 0, byteSize);
			GPStrace tr = new GPStrace();
			tr.parseBytes(trSerial);
			this.traceList.add(tr);
		}
	}

	public byte[] getBytes() throws Exception {
		// Sort gps traces based on timestamp, before serializing them
		Collections.sort(this.traceList, new Comparator<GPStrace>() {

			public int compare(GPStrace tr1, GPStrace tr2) {
				if (tr1.getTimestamp() > tr2.getTimestamp()) {
					return 1;
				} else if (tr1.getTimestamp() < tr2.getTimestamp()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		int numberOfBytes = Integer.SIZE / 8; // user id
		numberOfBytes += Integer.SIZE / 8; // list length
		for (GPStrace tr : this.traceList) {
			numberOfBytes += Integer.SIZE / 8;
			numberOfBytes += tr.getBytes().length;
		}

		byte[] bytes = new byte[numberOfBytes];
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		buffer.putInt(this.userId);
		buffer.putInt(this.traceList.size());
		for (GPStrace tr : this.traceList) {
			byte[] bytesTr = tr.getBytes();
			buffer.putInt(bytesTr.length);
			buffer.put(bytesTr);
		}
		return bytes;
	}

}

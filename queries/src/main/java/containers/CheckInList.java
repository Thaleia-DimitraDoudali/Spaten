package containers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import parser.ParseCheckIn;

public class CheckInList implements Serializable, Compressible{

	private static final int COMPRESSION_LEVEL = 4;
	int userId;
	private List<CheckIn> checkInList;
	
	public CheckInList() {
		this.checkInList = new LinkedList<CheckIn>();
	}
	
	public CheckInList(int id) {
		this.checkInList = new LinkedList<CheckIn>();
		this.userId = id;
	}

	public List<CheckIn> getCheckInList() {
		return checkInList;
	}

	public void setCheckInList(List<CheckIn> checkInList) {
		this.checkInList = checkInList;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		String res = this.userId + " ";
		for (CheckIn chk: checkInList) {
			res += chk.toString() + "\n";
		}
		return res;
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
        this.checkInList = new LinkedList<CheckIn>();
        this.userId = buffer.getInt();
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

	public byte[] getBytes() throws Exception {
		//Sort check ins based on timestamp, before serializing them
				Collections.sort(this.checkInList, new Comparator<CheckIn>(){

					public int compare(CheckIn chk1, CheckIn chk2) {
						if (chk1.getTimestamp() > chk2.getTimestamp()) {
							return 1;
						} else  if (chk1.getTimestamp() < chk2.getTimestamp()) {
							return -1;
						} else {
							return 0;
						}
					}
		        });
				// number of checkIns stored
		        int numberOfBytes = Integer.SIZE / 8; //user id
		        numberOfBytes += Integer.SIZE / 8; // list length
		        for (CheckIn chk : this.checkInList) {
		            numberOfBytes += Integer.SIZE / 8;
		            numberOfBytes += chk.getBytes().length;
		        }

		        byte[] bytes = new byte[numberOfBytes];
		        ByteBuffer buffer = ByteBuffer.wrap(bytes);

		        buffer.putInt(this.userId);
		        buffer.putInt(this.checkInList.size());
		        for (CheckIn chk: this.checkInList) {
		            byte[] bytesChk = chk.getBytes();
		            buffer.putInt(bytesChk.length);
		            buffer.put(bytesChk);
		        }
		        return bytes;
	}
	
	public static void main(String[] args) throws Exception {
		ParseCheckIn pr = new ParseCheckIn();
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		line = br.readLine();
		
		//Checking serialization and deserialition
		CheckIn chk1 = new CheckIn();
		CheckIn chk2 = new CheckIn();
		chk1 = pr.parseLine(line);
		line = br.readLine();
		chk2 = pr.parseLine(line);
		CheckInList chkList1 = new CheckInList(1);
		CheckInList chkList2 = new CheckInList(1);
		chkList1.getCheckInList().add(chk1);
		chkList1.getCheckInList().add(chk2);
		byte[] bytes = chkList1.getBytes();
		chkList2.parseBytes(bytes);
		System.out.println(chkList2.toString());
	}
}

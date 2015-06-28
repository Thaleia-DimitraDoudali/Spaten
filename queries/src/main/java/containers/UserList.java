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

public class UserList implements Serializable, Compressible{
	
	private static final int COMPRESSION_LEVEL = 4;
	private int userId;
	private List<User> userList;
	
	public UserList() {
        this.userList = new LinkedList<User>();
	}
	
	public UserList(int id) {
        this.userList = new LinkedList<User>();
        this.userId = id;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	@Override
	public String toString() {
		String res = "";
		for (User usr: userList) {
			res += usr.getUserId() + " ";
		}
		return res;
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public void add(User usr) {
		this.userList.add(usr);
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
        this.userList = new LinkedList<User>();
        int sizeOfList = buffer.getInt();
        for (int i = 0; i < sizeOfList; i++) {
            int byteSize = buffer.getInt();
            byte[] userSerial = new byte[byteSize];
            buffer.get(userSerial, 0, byteSize);
            User usr = new User();
            usr.parseBytes(userSerial);
            this.userList.add(usr);
        }		
	}

	public byte[] getBytes() throws Exception {
		//Sort users based on userId, before serializing them
		Collections.sort(this.userList, new Comparator<User>(){

			public int compare(User usr1, User usr2) {
				if (usr1.getUserId() > usr2.getUserId()) {
					return 1;
				} else  if (usr1.getUserId() < usr2.getUserId()) {
					return -1;
				} else {
					return 0;
				}
			}
        });
		// number of users stored
        int numberOfBytes = Integer.SIZE / 8;		
        for (User usr : this.userList) {
            numberOfBytes += Integer.SIZE / 8;
            numberOfBytes += usr.getBytes().length;
        }

        byte[] bytes = new byte[numberOfBytes];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        buffer.putInt(this.userList.size());
        for (User usr : this.userList) {
            byte[] bytesPoi = usr.getBytes();
            buffer.putInt(bytesPoi.length);
            buffer.put(bytesPoi);
        }
        return bytes;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}

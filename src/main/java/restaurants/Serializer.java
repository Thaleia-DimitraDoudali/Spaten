package restaurants;

import java.io.*;

public class Serializer {

	public byte[] serialize(Restaurant rest) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(rest);
	    return out.toByteArray();
	}
	public Restaurant deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (Restaurant) is.readObject();
	}
	
}

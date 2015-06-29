package hbase_schema;

import java.io.IOException;

public interface QueriesTable {

	public void createTable() throws Exception;
	public void putSingle(byte[] row, byte[] qualifier, byte[] data) throws IOException;
	public void getSingle(byte[] row, byte[] qualifier) throws Exception;

}

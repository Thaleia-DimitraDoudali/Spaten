package hbase_schema;

import java.io.IOException;

public interface QueriesTable {

	public void createTable() throws Exception;
	public void putSingle(byte[] row, byte[] chk_qualifier, byte[] data) throws IOException;
	public void getSingle(byte[] row, byte[] fr_qualifier) throws Exception;
	public void putList(byte[] row, byte[] chk_qualifier, byte[] data) throws IOException;
	public void getList(byte[] row, byte[] chk_qualifier) throws Exception;
	public void getListCompressed(byte[] row, byte[] chk_qualifier) throws Exception;

}

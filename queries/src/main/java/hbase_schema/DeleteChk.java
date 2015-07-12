package hbase_schema;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;

import containers.User;
import client.AbstractQueryClient;

public class DeleteChk {

    protected HTable table;
	
	public void openConnection(String tableName) {
        try {
            Thread.currentThread().setContextClassLoader(HBaseConfiguration.class.getClassLoader());
            this.table = new HTable(HBaseConfiguration.create(), tableName);
        } catch (IOException ex) {
            Logger.getLogger(AbstractQueryClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection() {
        try {
            this.table.close();
        } catch (IOException ex) {
            Logger.getLogger(AbstractQueryClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
	public void deleteSingle(byte[] row) throws IOException {
		Delete d = new Delete(row);
		this.table.delete(d);
	}
	
	public static void main(String[] args) throws Exception {
		
		int start = Integer.parseInt(args[0]);
		int end = Integer.parseInt(args[1]);
		
		DeleteChk dlchk = new DeleteChk();
		dlchk.openConnection("check-ins");
		
		for (int i = start; i <= end; i++) {
			User usr = new User(i);
			dlchk.deleteSingle(usr.getKeyBytes());
		}
		
		dlchk.closeConnection();
	}
}

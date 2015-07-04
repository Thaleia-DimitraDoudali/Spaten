package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;

/**
 *
 * @author Giannis Giannakopoulos
 */

public abstract class AbstractQueryClient {
    
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
    
	public BufferedWriter createWriter(String fileName) {

		try {
			String workingDir = System.getProperty("user.dir");
			File file = new File(workingDir + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			return bw;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    public HTable getTable() {
        return table;
    }

    public void setTable(HTable table) {
        this.table = table;
    }
    
    public abstract void executeQuery() throws Exception;
    
}
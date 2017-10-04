package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;

import containers.User;

/**
 *
 * @author Giannis Giannakopoulos
 */

public abstract class AbstractQueryClient {
    
    protected HTable table;
	protected long executionTime;
	protected User user;
	protected long mergeTime;
	
	public long getMergeTime() {
		return mergeTime;
	}

	public void setMergeTime(long mergeTime) {
		this.mergeTime = mergeTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected String type;
    
    public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

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

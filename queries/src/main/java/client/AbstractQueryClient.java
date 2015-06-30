package client;

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

    public HTable getTable() {
        return table;
    }

    public void setTable(HTable table) {
        this.table = table;
    }
    
    public abstract void executeQuery();
    
}
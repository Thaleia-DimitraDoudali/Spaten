package threads;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;

import containers.CheckInList;
import containers.UserList;
import coprocessors.NewsFeedProtocol;

public class RegionThreadNF extends Thread{

    private long executionTime;
    private UserList usrList;
    private long date;
    private CheckInList results;
	private HTable table;
	
	public CheckInList getResults() {
		return results;
	}

	public void setResults(CheckInList results) {
		this.results = results;
	}

	public RegionThreadNF() {
		super();
	}
	
	@Override
    public void run() {
        this.executionTime = System.currentTimeMillis();
		try {
			NewsFeedProtocol prot =  this.table.coprocessorProxy(NewsFeedProtocol.class, usrList.getUserList().get(0).getKeyBytes());
			this.results = new CheckInList();
			this.results.parseCompressedBytes(prot.getNewsFeed(usrList.getDataBytes(), Bytes.toBytes(date)));
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
        this.executionTime = System.currentTimeMillis()-this.executionTime;	
	}
	
	@Override
    public synchronized void start() {
        super.start(); 
    }

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public UserList getUsrList() {
		return usrList;
	}

	public void setUsrList(UserList usrList) {
		this.usrList = usrList;
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

}


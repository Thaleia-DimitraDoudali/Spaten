package threads;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

import containers.MostVisitedTraceList;
import containers.User;
import containers.UserList;
import coprocessors.MostVisitedTraceProtocol;

public class RegionThreadMVTR extends Thread{
	
    private long executionTime;
    private UserList usrList;
    private MostVisitedTraceList results;
    private User firstKeyOfRegion;
	private HTable table;
    private Class<?extends CoprocessorProtocol> protocol;
    
	public RegionThreadMVTR() {
		super();
	}
	
	@Override
    public void run() {
        this.executionTime = System.currentTimeMillis();
        try {
            MostVisitedTraceProtocol prot = this.table.coprocessorProxy(MostVisitedTraceProtocol.class, 
            		usrList.getUserList().get(0).getKeyBytes());
        	this.results = new MostVisitedTraceList();
        	this.results.parseCompressedBytes(prot.getMostVisitedTrace(usrList.getDataBytes()));
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

	public MostVisitedTraceList getResults() {
		return results;
	}

	public void setResults(MostVisitedTraceList results) {
		this.results = results;
	}

	public User getFirstKeyOfRegion() {
		return firstKeyOfRegion;
	}

	public void setFirstKeyOfRegion(User firstKeyOfRegion) {
		this.firstKeyOfRegion = firstKeyOfRegion;
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

	public Class<? extends CoprocessorProtocol> getProtocol() {
		return protocol;
	}

	public void setProtocol(Class<? extends CoprocessorProtocol> protocol) {
		this.protocol = protocol;
	}

	public UserList getUsrList() {
		return usrList;
	}

	public void setUsrList(UserList usrList) {
		this.usrList = usrList;
	}

}


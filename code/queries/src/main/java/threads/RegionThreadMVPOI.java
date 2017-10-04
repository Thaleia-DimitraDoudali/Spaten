package threads;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

import containers.MostVisitedPOIList;
import containers.User;
import containers.UserList;
import coprocessors.MostVisitedPOIProtocol;

public class RegionThreadMVPOI extends Thread{
	
    private long executionTime;
    private UserList usrList;
    private MostVisitedPOIList results;
    private User firstKeyOfRegion;
	private HTable table;
    private Class<?extends CoprocessorProtocol> protocol;
    
	public RegionThreadMVPOI() {
		super();
	}
	
	@Override
    public void run() {
        this.executionTime = System.currentTimeMillis();
        try {
            MostVisitedPOIProtocol prot = this.table.coprocessorProxy(MostVisitedPOIProtocol.class, usrList.getUserList().get(0).getKeyBytes());
        	this.results = new MostVisitedPOIList();
        	this.results.parseCompressedBytes(prot.getMostVisitedPOI(usrList.getDataBytes()));
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

	public MostVisitedPOIList getResults() {
		return results;
	}

	public void setResults(MostVisitedPOIList results) {
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


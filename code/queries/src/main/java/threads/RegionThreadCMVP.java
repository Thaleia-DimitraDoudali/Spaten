package threads;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.client.HTable;

import containers.MostVisitedPOI;
import containers.MostVisitedPOIList;
import containers.UserList;
import coprocessors.CorrelatedMVPProtocol;

public class RegionThreadCMVP extends Thread{

    private long executionTime;
    private UserList usrList;
	private MostVisitedPOIList results;
	private MostVisitedPOI mvp;
	private HTable table;
	
	public RegionThreadCMVP() {
		super();
	}
	
	@Override
    public void run() {
        this.executionTime = System.currentTimeMillis();
        try {
            CorrelatedMVPProtocol prot = this.table.coprocessorProxy(CorrelatedMVPProtocol.class, usrList.getUserList().get(0).getKeyBytes());
        	this.results = new MostVisitedPOIList();
        	this.results.parseCompressedBytes(prot.getCorrelatedMVP(usrList.getDataBytes(), this.mvp.getDataBytes()));
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

	public MostVisitedPOIList getResults() {
		return results;
	}

	public void setResults(MostVisitedPOIList results) {
		this.results = results;
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

	public MostVisitedPOI getMvp() {
		return mvp;
	}

	public void setMvp(MostVisitedPOI mvp) {
		this.mvp = mvp;
	}

}


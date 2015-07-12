package client;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import containers.User;

public class RWQuery {

	private int threadsNo;
	private int usersNo;
	private double prc;
	private String file;
	private List<String> files;

	public int getThreadsNo() {
		return threadsNo;
	}

	public void setThreadsNo(int threadsNo) {
		this.threadsNo = threadsNo;
	}
	
	public int createUniformIntRandom(int range) {
		Random r = new Random();
		int res = r.nextInt(range) + 1;
		return res;
	}

	public void executeAll() throws InterruptedException {
		
		List <Thread> threads = new LinkedList<Thread>();
		List <AbstractQueryClient> queries = new LinkedList<AbstractQueryClient>();		
		List <AbstractQueryClient> puts = new LinkedList<AbstractQueryClient>();

		
		int writeNo = (int) (this.prc * this.threadsNo);
		int readNo = this.threadsNo - writeNo;
		

		for (int i = 0; i < readNo; i+=3) {
			
			GetMostVisitedPOIQuery q1 = new GetMostVisitedPOIQuery();
			int N = this.createUniformIntRandom(usersNo);
			q1.setUser(new User(N));
			q1.setPrint(false);
			q1.setOutFile("MVP.out");
			q1.setType("Most Visited POI");
			queries.add(q1);
			Thread t1 = new Thread(q1);
			threads.add(t1);
						
			GetNewsFeedQuery q3 = new GetNewsFeedQuery();
			N = this.createUniformIntRandom(usersNo);
			q3.setUser(new User(N));
			q3.setPrint(false);
			q3.setTimestamp(q3.convertToTimestamp("02-01-2015"));
			q3.setOutFile("NF.out");
			q3.setType("News Feed");
			queries.add(q3);
			Thread t3 = new Thread(q3);
			threads.add(t3);
			
			GetCorrelatedMVPoiQuery q4 = new GetCorrelatedMVPoiQuery();
			N = this.createUniformIntRandom(usersNo);
			q4.setUser(new User(N));
			q4.setPrint(false);
			q4.setOutFile("CMVP.out");
			q4.setType("Correlated Most Visited POI");
			queries.add(q4);
			Thread t4 = new Thread(q4);
			threads.add(t4);
			
		}		
		
		for (int i = 0; i < writeNo; i++) {
			PutQuery pq = new PutQuery();
			pq.setUser(new User(0));
			pq.setFile(file);
			pq.setType("Put");
			puts.add(pq);
			Thread t = new Thread(pq);
			threads.add(t);
		}
	
		for (Thread t : threads) {
			t.start();
		}
		
		for (Thread t : threads) {
			t.join();
		}
		
		int i = 1;
		long mean = 0;
		double thr = 0;
		for (AbstractQueryClient q: queries) {
			System.out.println("[Thread no." + i + "] Query executed in " + q.executionTime / 1000 + "s"
					+ " user No." + q.getUser().getUserId() + " type: \"" + q.getType() + "\"");
			i++;
			mean += q.executionTime / 1000;
		}
		i = 1;
		for (AbstractQueryClient q : puts) {
			System.out.println("[Thread no." + i + "] Put executed in " + q.executionTime / 1000 + "s"
					+ " user No." + q.getUser().getUserId() + " type: \"" + q.getType() + "\"");
			i++;
		}
		mean = mean / queries.size();
		thr = (double) queries.size() / mean; 
		System.out.println("\n Mean query execution time (latency) = " + mean + "s");
		System.out.println("\n Queries per second (throughput) = " + thr);
	}

	public int getUsersNo() {
		return usersNo;
	}

	public void setUsersNo(int usersNo) {
		this.usersNo = usersNo;
	}

	public double getPrc() {
		return prc;
	}

	public void setPrc(double prc) {
		this.prc = prc;
	}

	public static void main(String[] args) throws Exception {

		RWQuery genq = new RWQuery();
		genq.threadsNo = Integer.parseInt(args[0]);
		genq.usersNo = Integer.parseInt(args[1]);
		genq.setPrc(Double.parseDouble(args[2]));
		genq.setFile(args[3]);
		genq.executeAll();
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}

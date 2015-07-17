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
		List <AbstractQueryClient> type_queries = new LinkedList<AbstractQueryClient>();
		
		GetMostVisitedPOIQuery q1 = new GetMostVisitedPOIQuery();
		q1.setType("Most Visited POI");
		GetNewsFeedQuery q3 = new GetNewsFeedQuery();
		q3.setTimestamp(q3.convertToTimestamp("02-01-2015"));
		q3.setType("News Feed");
		GetCorrelatedMVPoiQuery q4 = new GetCorrelatedMVPoiQuery();
		q4.setType("Correlated Most Visited POI");
		
		type_queries.add(q1);
		type_queries.add(q3);
		type_queries.add(q4);
		
		List <AbstractQueryClient> puts = new LinkedList<AbstractQueryClient>();
		
		int writeNo = (int) (this.prc * this.threadsNo);
		int readNo = this.threadsNo - writeNo;
		

		int lim = 0;
		for (int i = 0; i < readNo; i+=1) {
			
			AbstractQueryClient q = type_queries.get(lim);
			lim ++;
			if (lim == 3) {
				lim = 0;
			}
			if (q.getType().equals("Most Visited POI")) {
				GetMostVisitedPOIQuery qq = new GetMostVisitedPOIQuery();
				qq.setOutFile("MVP.out");
				qq.setType("Most Visited POI");
				qq.setPrint(false);
				int N = this.createUniformIntRandom(usersNo);
				qq.setUser(new User(N));
				queries.add(qq);
				Thread t = new Thread(qq);
				threads.add(t);
			} else if (q.getType().equals("News Feed")) {
				GetNewsFeedQuery qq = new GetNewsFeedQuery();
				qq.setTimestamp(qq.convertToTimestamp("02-01-2015"));
				qq.setOutFile("NF.out");
				qq.setType("News Feed");
				qq.setPrint(false);
				int N = this.createUniformIntRandom(usersNo);
				qq.setUser(new User(N));
				queries.add(qq);
				Thread t = new Thread(qq);
				threads.add(t);
			} else if (q.getType().equals("Correlated Most Visited POI")) {
				GetCorrelatedMVPoiQuery qq = new GetCorrelatedMVPoiQuery();
				qq.setOutFile("CMVP.out");
				qq.setType("Correlated Most Visited POI");
				qq.setPrint(false);
				int N = this.createUniformIntRandom(usersNo);
				qq.setUser(new User(N));
				queries.add(qq);
				Thread t = new Thread(qq);
				threads.add(t);
			}
			
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
		double mean = 0, max = -1, m = 0;
		double latency;
		double thr = 0;
		
		for (AbstractQueryClient q: queries) {
			System.out.println("[Thread no." + i + "] Query executed in " + q.executionTime / 1000 + "s"
					+ " user No." + q.getUser().getUserId() + " type: \"" + q.getType() + "\" merge time = "
							+ q.mergeTime / 1000 + "s");
			i++;
			m = (double) (q.executionTime / 1000);
			mean += (double) (q.executionTime / 1000);
			if (m >= max) {
				max = m;
			}
		}
		i = 1;
		for (AbstractQueryClient q : puts) {
			System.out.println("[Thread no." + i + "] Put executed in " + q.executionTime / 1000 + "s"
					+ " user No." + q.getUser().getUserId() + " type: \"" + q.getType() + "\"");
			i++;
		}
		thr = (double) queries.size() / max; 
		latency = (double) mean / queries.size();
		System.out.println("\n Max query execution time = " + max + "s");
		System.out.println("\n Total query execution time = " + mean + "s");
		System.out.println("\n Mean query execution time (latency) = " + latency + "s");
		System.out.println("\n Queries per second (throughput) = " + thr + " queries/s");
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

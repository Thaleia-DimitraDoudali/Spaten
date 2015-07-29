package client;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import containers.User;

public class GeneralQuery {

	private int threadsNo;
	private int usersNo;

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
		
		for (int i = 0; i < this.threadsNo; i+=4) {
			
			GetMostVisitedPOIQuery q1 = new GetMostVisitedPOIQuery();
			int N = this.createUniformIntRandom(usersNo);
			q1.setUser(new User(N));
			q1.setPrint(false);
			q1.setOutFile("MVP.out");
			q1.setType("Most Visited POI");
			queries.add(q1);
			Thread t1 = new Thread(q1);
			threads.add(t1);
			
			GetMostVisitedTraceQuery q2 = new GetMostVisitedTraceQuery();
			N = this.createUniformIntRandom(usersNo);
			q2.setUser(new User(N));
			q2.setPrint(false);
			q2.setOutFile("MVTR.out");
			q2.setType("Most Visited Trace");
			queries.add(q2);
			Thread t2 = new Thread(q2);
			threads.add(t2);
			
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
		
		for (Thread t : threads) {
			t.start();
		}
		
		for (Thread t : threads) {
			t.join();
		}
		
		int i = 1;
		long mean = 0;
		for (AbstractQueryClient q: queries) {
			System.out.println("[Thread no." + i + "] Query executed in " + q.executionTime / 1000 + "s"
					+ " user No." + q.getUser().getUserId() + " type: \"" + q.getType() + "\"");
			i++;
			mean += q.executionTime / 1000;
		}
		mean = mean / queries.size();
		System.out.println("\n Mean query execution time = " + mean + "s");
	}

	public static void main(String[] args) throws Exception {

		GeneralQuery genq = new GeneralQuery();
		genq.threadsNo = Integer.parseInt(args[0]);
		genq.usersNo = Integer.parseInt(args[1]);
		genq.executeAll();
	}

}

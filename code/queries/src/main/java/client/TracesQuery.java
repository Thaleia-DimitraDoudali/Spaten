package client;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import containers.User;

public class TracesQuery {

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
		
		for (int i = 0; i < this.threadsNo; i+=1) {
			
			GetMostVisitedTraceQuery q2 = new GetMostVisitedTraceQuery();
			int N = this.createUniformIntRandom(usersNo);
			q2.setUser(new User(N));
			q2.setPrint(false);
			q2.setOutFile("MVTR.out");
			q2.setType("Most Visited Trace");
			queries.add(q2);
			Thread t2 = new Thread(q2);
			threads.add(t2);
			
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
		mean = mean / queries.size();
		thr = (double) queries.size() / mean; 
		System.out.println("\n Mean query execution time = " + mean + "s");
		System.out.println("\n Queries per second (throughput) = " + thr + " query/s");
	}

	public static void main(String[] args) throws Exception {

		TracesQuery genq = new TracesQuery();
		genq.threadsNo = Integer.parseInt(args[0]);
		genq.usersNo = Integer.parseInt(args[1]);
		genq.executeAll();
	}

}


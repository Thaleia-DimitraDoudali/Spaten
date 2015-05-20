package checkIns;

import java.util.ArrayList;
import java.util.List;

import pois.GPSTrace;
import pois.Poi;

public class User {

	private int userId;
	private List<CheckIn> checkIns = new ArrayList<CheckIn>();
	private List<String> routes = new ArrayList<String>();
	private List<GPSTrace> traces = new ArrayList<GPSTrace>();
	private Poi home;
	
	public User(int id) {
		this.userId = id;
	}
	
	public void print() {
		for (CheckIn chk: checkIns) {
			chk.print();
		}

	}
	
	public void addGPSTrace(GPSTrace tr) {
		traces.add(tr);
	}
	
	public void addRoute(String rt) {
		routes.add(rt);
	}
	
	public void addCheckIn(CheckIn chk) {
		this.checkIns.add(chk);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<CheckIn> getCheckIns() {
		return checkIns;
	}

	public void setCheckIns(List<CheckIn> checkIns) {
		this.checkIns = checkIns;
	}

	public List<String> getRoutes() {
		return routes;
	}

	public void setRoutes(List<String> routes) {
		this.routes = routes;
	}

	public List<GPSTrace> getTraces() {
		return traces;
	}

	public void setTraces(List<GPSTrace> traces) {
		this.traces = traces;
	}

	public Poi getHome() {
		return home;
	}

	public void setHome(Poi home) {
		this.home = home;
	}

}

package checkIns;

import googleMaps.MapURL;

import java.util.ArrayList;
import java.util.List;

import pois.GPSTrace;
import pois.Poi;

public class User {

	private int userId;
	private List<CheckIn> checkIns = new ArrayList<CheckIn>();
	private List<GPSTrace> traces = new ArrayList<GPSTrace>();
	private List<MapURL> dailyMap = new ArrayList<MapURL>();
	private Poi home;
	private Poi travelPoi;
	
	public User(int id) {
		this.userId = id;
	}
	
	public void print() {
		for (CheckIn chk: checkIns) {
			chk.print();
		}

	}
	
	public void addDailyMap(MapURL mp) {
		dailyMap.add(mp);
	}
	
	public void addGPSTrace(GPSTrace tr) {
		traces.add(tr);
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

	public List<MapURL> getDailyMap() {
		return dailyMap;
	}

	public void setDailyMap(List<MapURL> dailyMap) {
		this.dailyMap = dailyMap;
	}

	public Poi getTravelPoi() {
		return travelPoi;
	}

	public void setTravelPoi(Poi travelPoi) {
		this.travelPoi = travelPoi;
	}

}


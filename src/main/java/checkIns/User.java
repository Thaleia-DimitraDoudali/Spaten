package checkIns;

import java.util.ArrayList;
import java.util.List;

public class User {

	private int userId;
	private List<CheckIn> checkIns = new ArrayList<CheckIn>();
	
	public User(int id) {
		this.userId = id;
	}
	
	public void print() {
		for (CheckIn chk: checkIns) {
			chk.print();
		}

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

}

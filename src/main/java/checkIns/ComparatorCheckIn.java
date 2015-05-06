package checkIns;

import java.util.Comparator;

public class ComparatorCheckIn implements Comparator<CheckIn>{

	public ComparatorCheckIn() {
	}

	public int compare(CheckIn arg0, CheckIn arg1) {
		if (arg0.getTimestamp() < arg1.getTimestamp())
			return -1;
		else if (arg0.getTimestamp() > arg1.getTimestamp())
			return 1;
		else 
			return 0;
	}

}

package parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import containers.GPStrace;

public class ParseGPStrace {

	public GPStrace parseLine(String line) {
		int id;		
		double x, y;
		String date;
		long t;
		
		String[] splt = line.split("\t");
		
		String[] idsplt = splt[0].split(" ");
		if (idsplt.length > 1)
			id = Integer.parseInt(idsplt[1].substring(0, idsplt[1].length()-1));
		else 
			id = Integer.parseInt(idsplt[0].substring(0, idsplt[0].length()-1));

		date = splt[1].substring(2, splt[1].length()-2);
		t = dateToTimestamp(date);
	    
		String[] coords = splt[2].split(", ");
		x = Double.parseDouble(coords[0].substring(3, coords[0].length()));
		y = Double.parseDouble(coords[1].substring(0, coords[1].length()-2));
		
		GPStrace tr = new GPStrace(id, x, y, t);
		
		return tr;
	}
	
	public long dateToTimestamp(String s) {
		try {
			DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
		    Date result = df.parse(s);
			
			Calendar c = Calendar.getInstance();
		    c.setTime(result);
		    long time = c.getTimeInMillis();
		    return time;
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return -1;
	}

}

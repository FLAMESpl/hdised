package pl.polsl.hdised.monitoring;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class Helpers {

	public static Integer tryParseInt(String text) {
		
		Integer number;
		try {
			number = Integer.parseInt(text);
		} catch (NumberFormatException ex) {
			number = null;
		}
		return number;
	}
	
	public static Long tryParseLong(String text) {
		
		Long number;
		try {
			number = Long.parseLong(text);
		} catch (NumberFormatException ex) {
			number = null;
		}
		return number;
	}
	
	public static float parseFloat(String text) {
		
		return Float.parseFloat(text.replace(',', '.'));
	}
	
	public static Calendar parseCalendar(String text) throws NumberFormatException {
		
		String[] tokens = text.split("[\\s-:]");
		
		return new GregorianCalendar(
					Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[1]) - 1,
					Integer.parseInt(tokens[2]),
					Integer.parseInt(tokens[3]),
					Integer.parseInt(tokens[4])
				);
	}
	
	public static Calendar tryParseCalendar(String text) {

		Calendar calendar;
		try {
			calendar = parseCalendar(text);
		} catch (NumberFormatException ex) {
			calendar = null;
		}
		return calendar;
	}
	
	public static String toString(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(calendar.getTimeZone());
		return sdf.format(calendar.getTime());
	}
}


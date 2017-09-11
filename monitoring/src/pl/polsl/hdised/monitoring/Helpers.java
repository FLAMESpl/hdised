package pl.polsl.hdised.monitoring;

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
	
	public static Calendar parseDateTime(String text) throws NumberFormatException {
		
		String[] tokens = text.split("[\\s-:]");
		
		return new GregorianCalendar(
					Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[1]),
					Integer.parseInt(tokens[2]),
					Integer.parseInt(tokens[3]),
					Integer.parseInt(tokens[4])
				);
	}
}

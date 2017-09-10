package pl.polsl.hdised.streamer;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class Helpers {

	public static Integer tryParse(String text) {
		
		Integer number;
		try {
			number = Integer.parseInt(text);
		} catch (NumberFormatException ex) {
			number = null;
		}
		return number;
	}
	
	public static Calendar parseDateTime(String line) throws NumberFormatException {
		
		String unparsedDate = line.substring(0, line.indexOf(';'));
		String[] tokens = unparsedDate.split("[\\s-:]");
		
		return new GregorianCalendar(
					Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[1]),
					Integer.parseInt(tokens[2]),
					Integer.parseInt(tokens[3]),
					Integer.parseInt(tokens[4])
				);
	}
}

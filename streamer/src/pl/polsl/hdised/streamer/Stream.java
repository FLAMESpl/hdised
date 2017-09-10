package pl.polsl.hdised.streamer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Stream {

	private PrintWriter writer;
	private BufferedReader reader;
	private boolean eof = false;
	private long counter = 1;
	private String actualLine = null;
	private MeasureType type;
	private Calendar lastTime;
	
	public Stream(PrintWriter writer, BufferedReader reader, MeasureType type, Calendar startTime, Calendar firstTime, String actualLine) throws FileNotFoundException {
		
		this.writer = writer;
		this.type = type;
		this.reader = reader;
		this.actualLine = actualLine;
		this.lastTime = firstTime;
		
		calculateNewCounterValue(startTime, firstTime);
	}
	
	public boolean isFinished() {
		return eof;
	}
	
	public void tick() throws IOException, NumberFormatException {
		
		System.out.println(String.format("%1s:%2d", type.getId(), counter));
		if (!eof) {
			if (counter == 0) {
				
				Calendar newTime;
				do {
					sendMeasure(actualLine);
					actualLine = reader.readLine();
					
					if (actualLine == null || actualLine.equals("")) {
						eof = true;
						System.out.println(type.toString() + " stream has reached its end.");
						return;
					}
					
					newTime = Helpers.parseDateTime(actualLine);
					
				} while (newTime.equals(lastTime));
				
				calculateNewCounterValue(lastTime, newTime);
				lastTime = newTime;
			}
			
			counter--;
		}
	}
	
	public void ensureClosed() throws IOException {
		reader.close();
	}
	
	private void sendMeasure(String line) {
		System.out.println(type.getId() + line);
		writer.println(type.getId() + line);
	}
	
	private void calculateNewCounterValue(Calendar previousTime, Calendar nextTime) {
		counter = TimeUnit.MINUTES.convert(nextTime.getTimeInMillis() - previousTime.getTimeInMillis(), TimeUnit.MILLISECONDS);;
	}
}

package pl.polsl.hdised.streamer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StreamManager {

	private Stream tankStream;
	private Stream refuelStream;
	private Stream nozzleStream;
	
	public StreamManager(Path tankPath, Path refuelPath, Path nozzlePath, PrintWriter writer) throws FileNotFoundException, IOException {

		BufferedReader tankReader = createReader(tankPath);
		BufferedReader refuelReader = createReader(refuelPath);
		BufferedReader nozzleReader = createReader(nozzlePath);
		
		String tankLine = tankReader.readLine();
	    String refuelLine = refuelReader.readLine();
	    String nozzleLine = nozzleReader.readLine();		
	    		
		Calendar tankTime = Helpers.parseDateTime(tankLine);
		Calendar refuelTime = Helpers.parseDateTime(refuelLine);
		Calendar nozzleTime = Helpers.parseDateTime(nozzleLine);
		
		List<Long> epochs = new ArrayList<>();
		epochs.add(tankTime.toInstant().toEpochMilli());
		epochs.add(refuelTime.toInstant().toEpochMilli());
		epochs.add(nozzleTime.toInstant().toEpochMilli());
		
		Calendar startTime = Calendar.getInstance();
		startTime.setTimeInMillis(epochs.stream().min((a, b) -> Long.compare(a, b)).get());
		
		tankStream = new Stream(writer, tankReader, MeasureType.TANK, startTime, tankTime, tankLine);
		refuelStream = new Stream(writer, refuelReader, MeasureType.REFUEL, startTime, refuelTime, refuelLine);
		nozzleStream = new Stream(writer, nozzleReader, MeasureType.NOZZLE, startTime, nozzleTime, nozzleLine);
	}
	
	public Stream getTankStream() {
		return tankStream;
	}
	
	public Stream getRefuelStream() {
		return refuelStream;
	}
	
	public Stream getNozzleStream() {
		return nozzleStream;
	}
	
	public Boolean isFinished() {
		return tankStream.isFinished() && refuelStream.isFinished() && nozzleStream.isFinished();
	}
	
	public void ensureClosed() throws IOException {
		tankStream.ensureClosed();
		refuelStream.ensureClosed();
		nozzleStream.ensureClosed();
	}
	
	private BufferedReader createReader(Path path) throws FileNotFoundException {
		return new BufferedReader(new FileReader(path.toFile()));
	}
}

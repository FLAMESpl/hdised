package pl.polsl.hdised.monitoring;

import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import pl.polsl.hdised.monitoring.model.*;

public class Monitor {

	private int resolutionInSeconds;
	private Map<Calendar, Float> dataPoints;
	
	public Monitor(int resolutionInSeconds) {
		
		this.resolutionInSeconds = resolutionInSeconds;
		this.dataPoints = new TreeMap<>();
	}
	
	public void accept(String line) {
		
		String metric = line.substring(1);
		char type = line.charAt(0);
		switch (type) {
			case 'T':
				aggregate(new Tank(metric));
				break;
			case 'R':
				aggregate(new Refuel(metric));
				break;
			case 'N':
				aggregate(new Nozzle(metric));
				break;
			default:
				throw new IllegalArgumentException(String.format("Unrecognized type %1s of metric.", type));
		}
	}
	
	public void save(File file) {
		
	}
	
	private void aggregate(Tank tank) {
		
	}
	
	private void aggregate(Refuel refuel) {
		
	}
	
	private void aggregate(Nozzle nozzle) {
		
	}
}

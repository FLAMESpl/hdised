package pl.polsl.hdised.monitoring.model;

import java.util.Calendar;

import pl.polsl.hdised.monitoring.Helpers;

public class Nozzle {

	private int id;
	private int tankId;
	private Calendar timestamp;
	private float fuelCounter;
	
	public Nozzle(String line) {
		
		String[] tokens = line.split(";");
		timestamp = Helpers.parseCalendar(tokens[0]);
		id = Integer.parseInt(tokens[2]);
		tankId = Integer.parseInt(tokens[3]);
		fuelCounter = Helpers.parseFloat(tokens[5]);
	}
	
	public int getId() {
		return id;
	}
	
	public int getTankId() {
		return tankId;
	}
	
	public Calendar getTimestamp() {
		return timestamp;
	}
	
	public float getFuelCounter() {
		return fuelCounter;
	}
}

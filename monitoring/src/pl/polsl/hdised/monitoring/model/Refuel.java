package pl.polsl.hdised.monitoring.model;

import java.util.Calendar;

import pl.polsl.hdised.monitoring.Helpers;

public class Refuel {
	
	private int tankId;
	private Calendar timestamp;
	private float fuelVolume;
	
	public Refuel(String line) {
		
		String[] tokens = line.split(";");
		timestamp = Helpers.parseCalendar(tokens[0]);
		tankId = Integer.parseInt(tokens[1]);
		fuelVolume = Helpers.parseFloat(tokens[2]);
	}
	
	public int getTankId() {
		return tankId;
	}
	
	public Calendar getTimestamp() {
		return timestamp;
	}
	
	public float getFuelVolume() {
		return fuelVolume;
	}
}

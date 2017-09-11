package pl.polsl.hdised.monitoring.model;

import java.util.Calendar;

import pl.polsl.hdised.monitoring.Helpers;

public class Tank {

	private int id;
	private Calendar timestamp;
	private float fuelVolume;
	
	public Tank(String line) {

		String[] tokens = line.split(";");
		timestamp = Helpers.parseDateTime(tokens[0]);
		id = Integer.parseInt(tokens[3]);
		fuelVolume = Helpers.parseFloat(tokens[5]);
	}
	
	public int getId() {
		return id;
	}
	
	public Calendar getTimestamp() {
		return timestamp;
	}
	
	public float getFuelVolume() {
		return fuelVolume;
	}
}

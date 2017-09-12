package pl.polsl.hdised.monitoring.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import pl.polsl.hdised.monitoring.Helpers;

public class Tank {

	private int id;
	private Calendar timestamp;
	private float fuelVolume;
	private Float fuelVolumeDelta = null;
	private Map<Integer, Nozzle> nozzles;
	
	public Tank(String line) {

		String[] tokens = line.split(";");
		timestamp = Helpers.parseDateTime(tokens[0]);
		id = Integer.parseInt(tokens[3]);
		fuelVolume = Helpers.parseFloat(tokens[5]);
		nozzles = new HashMap<>();
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
	
	public float getFuelDelta() {
		return fuelVolumeDelta;
	}
	
	public void setFuelVolumeDelta(float fuelVolumeDelta) {
		this.fuelVolumeDelta = fuelVolumeDelta;
	}
	
	public Map<Integer, Nozzle> getNozzles() {
		return nozzles;
	}
}

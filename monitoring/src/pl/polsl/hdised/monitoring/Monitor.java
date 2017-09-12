package pl.polsl.hdised.monitoring;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pl.polsl.hdised.monitoring.model.*;

public class Monitor {

	private Map<Integer, Map<Calendar, Float>> tankDeltas;
	private Map<Integer, Map<Integer, Map<Calendar, Float>>> nozzleDeltas;
	private Map<Integer, Float> lastTankMeasures;
	private Map<Integer, Map<Integer, Float>> lastNozzleMeasures;
	private Map<Integer, Map<Calendar, Float>> refuels;
	
	public Monitor() {
		
		this.lastTankMeasures = new HashMap<>();
		this.lastNozzleMeasures = new HashMap<>();
		this.tankDeltas = new HashMap<>();
		this.nozzleDeltas = new HashMap<>();
		this.refuels = new HashMap<>();
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
	
	public void save(File file, int resolutionInMinutes) {
		
	}
	
	private void aggregate(Tank tank) {
		
		int tankId = tank.getId();
		Float lastMeasure = lastTankMeasures.get(tankId);
		lastTankMeasures.put(tankId, tank.getFuelVolume());
		
		if (lastMeasure != null) {
			Map<Calendar, Float> tankDeltasDataPoints = tankDeltas.get(tankId);
			
			if (tankDeltasDataPoints == null)
			{
				tankDeltasDataPoints = new TreeMap<>();
				tankDeltas.put(tankId, tankDeltasDataPoints);
			}
			
			tankDeltasDataPoints.put(tank.getTimestamp(), lastMeasure - tank.getFuelVolume());
		}
	}
	
	private void aggregate(Refuel refuel) {
		
		int tankId = refuel.getTankId();
		Map<Calendar, Float> refuelsByTank = refuels.get(tankId);
		
		if (refuelsByTank == null) {
			refuelsByTank = new TreeMap<>();
			refuels.put(tankId, refuelsByTank);
		}
		
		refuelsByTank.put(refuel.getTimestamp(), refuel.getFuelVolume());
	}
	
	private void aggregate(Nozzle nozzle) {
		
		int tankId = nozzle.getTankId();
		int nozzleId = nozzle.getId();
		Map<Integer, Float> nozzlesByTank = lastNozzleMeasures.get(tankId);
		
		if (nozzlesByTank == null) {
			nozzlesByTank = new HashMap<>();
			lastNozzleMeasures.put(tankId, nozzlesByTank);
			
		} else {
				Float lastMeasure = nozzlesByTank.get(nozzleId);
				
				if (lastMeasure != null) {
				Map<Integer, Map<Calendar, Float>> nozzleDeltasByTank = nozzleDeltas.get(tankId);
				
				if (nozzleDeltasByTank == null) {
					nozzleDeltasByTank = new HashMap<>();
					nozzleDeltas.put(tankId, nozzleDeltasByTank);
				};
				
				Map<Calendar, Float> nozzleDataPoints = nozzleDeltasByTank.get(nozzleId);
				
				if (nozzleDataPoints == null) {
					nozzleDataPoints = new TreeMap<>();
					nozzleDeltasByTank.put(nozzleId, nozzleDataPoints);
				}
				
				nozzleDataPoints.put(nozzle.getTimestamp(), lastMeasure - nozzle.getFuelCounter());
			}
		}

		nozzlesByTank.put(nozzleId, nozzle.getFuelCounter());
	}
}

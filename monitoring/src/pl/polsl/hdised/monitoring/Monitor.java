package pl.polsl.hdised.monitoring;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
	
	public void save(File file, int resolutionInMinutes, Calendar start, Calendar end) 
			throws FileNotFoundException, IOException {
		
		Calendar actual = (Calendar)start.clone();
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		Map<Integer, Integer> tanksSeriesPositions = new HashMap<>();
		
		try {
			while (actual.compareTo(end) <= 0) {
				String actualText = Helpers.toString(actual);
				System.out.println(actualText);
				Calendar next = (Calendar)actual.clone();
				next.add(Calendar.MINUTE, resolutionInMinutes);
				Map<Integer, Float> tanksSeries = new HashMap<>(); 
				
				for (Map.Entry<Integer, Map<Calendar, Float>> tankEntry : tankDeltas.entrySet()) {
					int tankId = tankEntry.getKey();
					Integer position = tanksSeriesPositions.get(tankId);
					Map<Integer, Map<Calendar, Float>> nozzles = nozzleDeltas.get(tankId);
					
					if (nozzles == null) {
						System.out.println(String.format("No nozzle deltas for tank %1d at %2s", tankId, actualText));
						continue;
					}
					
					if (position == null) {
						position = tanksSeriesPositions.size();
						tanksSeriesPositions.put(tankId, position);
					}
					
					float nozzleDeltasSum = 0f;
					
					for (Map.Entry<Integer, Map<Calendar, Float>> nozzleEntry : nozzles.entrySet()) {
						nozzleDeltasSum += sumInRange(nozzleEntry.getValue(), actual, next);
					}
					
					float tankDeltasSum = sumInRange(tankEntry.getValue(), actual, next);
					
					Map<Calendar, Float> refuelEntry = refuels.get(tankId);
					float refuelsSum = refuelEntry == null ? 0 : sumInRange(refuelEntry, start, next);
					
					float balance = tankDeltasSum + nozzleDeltasSum - refuelsSum;
					tanksSeries.put(position, balance);
				}
				
				if (tanksSeries.isEmpty()) {
					System.out.println("No complete datapoints at " + actualText);
				}
				
				output.write(formatRow(tanksSeriesPositions, tanksSeries, actual));
				output.newLine();
				
				actual = next;
			}
		} finally {
			output.close();
		}

		writeHeaders(file, tanksSeriesPositions);
		int x = 1;
	}
	
	private float sumInRange(Map<Calendar, Float> source, Calendar start, Calendar end) {
		return (float)source.entrySet().stream()
				.filter(x -> x.getKey().compareTo(start) >= 0 && x.getKey().compareTo(end) < 0)
				.mapToDouble(x -> x.getValue())
				.sum();
	}
	
	private String formatRow(Map<Integer, Integer> positions, Map<Integer, Float> values, Calendar timestamp) {
		
		StringBuilder builder = new StringBuilder(Helpers.toString(timestamp));
		if (values != null)
			positions.entrySet().stream()
				.sorted((x, y) -> Integer.compare(x.getValue(), y.getValue()))
				.forEach(x -> { 
					Float value = values.get(x.getValue());
					builder.append(";");
					
					if (value != null)
						builder.append(String.format("%f", value));
				});
		
		return builder.toString();
	}
	
	private void writeHeaders(File outputFile, Map<Integer, Integer> positions) 
			throws FileNotFoundException, IOException {

		StringBuilder header = new StringBuilder("Timestamp");
		positions.entrySet().stream()
			.sorted((x, y) -> Integer.compare(x.getValue(), y.getValue()))
			.forEach(x -> header.append(";" + x.getKey().toString()));
		
		List<String> lines = Files.readAllLines(outputFile.toPath());
		lines.add(0, header.toString());
		Files.write(outputFile.toPath(), lines);
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

package pl.polsl.hdised.monitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

public class Monitoring {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 6) {
			System.err.println("Specify in application's arguments server'port, output file, "
					+ "data points resolution (in minutes),"
					+ "maximum number of iterations (0 means infinite),"
					+ "start and end of data series.");
			return;
		}
		
		Integer port = Helpers.tryParseInt(args[0]);
		if (port == null) {
			System.err.println("Server port must be an integral number.");
			return;
		}
		
		Integer dataPointsResolutionInMinutes = Helpers.tryParseInt(args[2]);
		if (dataPointsResolutionInMinutes == null) {
			System.err.println("Resolution must be an integral number.");
			return;
		}
		
		Long maxIterations = Helpers.tryParseLong(args[3]);
		if (maxIterations == null) {
			System.err.println("Maximum number of iterations must be an integral number");
			return;
		}
		
		Calendar start = Helpers.tryParseCalendar(args[4]);
		if (start == null) {
			System.err.println("Unparsable start date. Must be `yy-MM-dd HH:mm:ss`.");
			return;
		}
		
		Calendar end = Helpers.tryParseCalendar(args[5]);
		if (end == null) {
			System.err.println("Unparsable end date. Must be `yy-MM-dd HH:mm:ss`.");
			return;
		}
		
		Path outputPath = Paths.get(args[1]);
		File outputFile = outputPath.toFile();
		
		try {
			if (!outputFile.createNewFile()) {
				System.err.println("Specified file already exists.");
				//return;
			}
		} catch (Exception ex) {
			System.err.println("File occured while creating output file.");
			return;
		}
		
		System.out.println("Connecting...");
		Socket socket = new Socket("localhost", port);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		System.out.println("Connected.");
		
		try {
			Monitor monitor = new Monitor();
			String message;
			Long it = 1l;
			while ((message = reader.readLine()) != null) {
				System.out.println(message);
				monitor.accept(message);
				
				if (maxIterations != 0) {
					it++;
					if (it > maxIterations) {
						System.out.println("Reached maximum number of iterations.");
						break;
					}
				}
			}
			
			monitor.save(outputFile, dataPointsResolutionInMinutes, start, end);
			
		} catch (SocketException ex) {
			System.err.println("Server has disconnected.");
		} finally {
			reader.close();
			socket.close();
		}
		
		System.out.println("Stopping...");
	}
}

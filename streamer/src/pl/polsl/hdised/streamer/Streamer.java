package pl.polsl.hdised.streamer;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Streamer {
	
	public static void main(String[] args) {
		
		if (args.length != 3)
		{
			System.err.println("Specify path to the data sets directory, server port and interval time (in milliseconds) in application's arguments.");
			return;
		}
		
		Integer port = Helpers.tryParse(args[1]);
		
		if (port == null) {
			System.err.println("Port must be a number.");
			return;
		}
		
		Integer intervalTime = Helpers.tryParse(args[2]);
		
		if (intervalTime == null) {
			System.err.println("Execution time must be a number");
		}
		
		if (!Files.exists(Paths.get(args[0]))) {
			
			System.err.println("Given directory does not exist.");
			return;
		}
		
		Path tankDataPath = Paths.get(args[0], "tankMeasures.log");
		
		if (!Files.exists(tankDataPath)) {
			System.err.println("Tank measures log not found.");
			return;
		}
		
		Path refuelDataPath = Paths.get(args[0], "refuel.log");
		
		if (!Files.exists(refuelDataPath)) {
			System.err.println("Refuel log not found.");
			return;
		}
		
		Path nozzleDataPath = Paths.get(args[0], "nozzleMeasures.log");
		
		if (!Files.exists(nozzleDataPath)) {
			System.err.println("Nozzle measures log not found.");
			return;
		}
		
		System.out.println("Starting streamer.");
		
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Waiting for client to connect.");
			Socket socket = server.accept();
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			
			System.out.println("Initializing streams.");
			StreamManager streamManager = new StreamManager(tankDataPath, refuelDataPath, nozzleDataPath, writer);
			
			try {
				while (!streamManager.isFinished()) {
					
					streamManager.getNozzleStream().tick();
					streamManager.getRefuelStream().tick();
					streamManager.getTankStream().tick();
					
					Thread.sleep(intervalTime);
				}
			} finally {
				System.out.println("Closing streams.");
				streamManager.ensureClosed();
				writer.close();
				socket.close();
				server.close();
			}
			
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		System.out.println("Streamer has ended.");
	}
}

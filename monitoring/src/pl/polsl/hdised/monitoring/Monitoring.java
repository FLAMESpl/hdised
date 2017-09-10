package pl.polsl.hdised.monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class Monitoring {

	public static void main(String[] args) throws Exception {
		
		int port = Integer.parseInt(args[0]);
		
		System.out.println("Connecting...");
		Socket socket = new Socket("localhost", port);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		System.out.println("Connected.");
		
		try {
			String message;
			while ((message = reader.readLine()) != null) {
				System.out.println(message);
			}
		} catch (SocketException ex) {
			System.err.println("Server has disconnected.");
		} finally {
			reader.close();
			socket.close();
		}
		
		System.out.println("Stopping...");
	}
}

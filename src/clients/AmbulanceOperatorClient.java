package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import config.Config;
import models.call.CallInterface;

public class AmbulanceOperatorClient {
	
	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	static Registry registry;
	
	public static void main(String[] args) throws IOException, NotBoundException {

		
		
		ServerSocket server = new ServerSocket(
				1050
		);
		
		registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
		System.out.println("Address: " + server.getInetAddress() + ":" + server.getLocalPort());
		System.out.println("Welcome to regional operator client..");
		
		while (true) {
			System.out.println("Connected! Waiting for emergency..");
			Socket socket = server.accept();
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
		    String callId = in.readLine();
			CallInterface call = (CallInterface) registry.lookup(callId);
		    System.out.println("Emergency callout received!");
			System.out.println(call.getContent());
		    
			boolean callActive = true;
			while(callActive) {
				System.out.println("Please enter the field you wish to update");
				System.out.println("(1) Patient ID");
				System.out.println("(2) Issues");
				System.out.println("(3) Action taken");
				System.out.println("(4) Location");
				System.out.println("(-1) End call");
				System.out.print("Response: ");

				try {
					int option = Integer.parseInt(stdIn.readLine());
					
					if (option == -1 ) {
						callActive = false;
						continue;
					}
					
					System.out.print("Please enter the value: ");
					String value = stdIn.readLine();
					
					
					switch (option) {
						case 1: 
							call.setPatientId(Integer.parseInt(value));
							break;
						case 2: 
							call.setIssue(value);
							break;
						case 3: 
							call.setActionTaken(value);
							break;
						case 4: 
							call.setLocation(value);
							break;
						case 5: 
							call.setPatientId(Integer.parseInt(value));
							break;
						default:
							System.out.println("Invalid field");
							return;
					}
					call.save();
					System.out.println("Call updated & saved to database");
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void printOptions() {
		System.out.println("Please enter an option to update the details");
	}
}

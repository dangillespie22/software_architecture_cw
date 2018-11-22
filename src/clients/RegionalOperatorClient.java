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
import models.patient.PatientInterface;

public class RegionalOperatorClient {

	static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, NotBoundException {
		ServerSocket server = new ServerSocket(
				Integer.parseInt(Config.getPropValues().getProperty("regionalport"))
		);
		System.out.println("Address: " + server.getInetAddress() + ":" + server.getLocalPort());
		System.out.println("Welcome to regional operator client..");
		
		while (true) {
			System.out.println(".. waiting for callouts");
			Socket socket = server.accept();
			System.out.println("Server connected!");
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
		    int action = Integer.parseInt(in.readLine());
		    switch(action) {
		    	case 1:
		    		System.out.println("Emergency callout received");
		    		String patientId = in.readLine();
		    		
		    		Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
					PatientInterface patient = (PatientInterface) registry.lookup(patientId);
					System.out.println("Patient received: " + patient.getFirstName() + " " + patient.getLastName());
					out.println(1);
		    }
		    socket.close();
		}
	}
}

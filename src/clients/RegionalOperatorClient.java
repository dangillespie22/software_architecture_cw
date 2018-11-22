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

import app.ApplicationController;
import clients.call_operator.actions.ServerRequest;
import config.Config;
import models.call.CallInterface;

public class RegionalOperatorClient {
	
	private static final int EMERGENCY_CALLOUT = 1;
	
	static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	static Registry registry;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, NotBoundException {
		ServerSocket server = new ServerSocket(
				requestPort()
		);
		
		registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
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
		    	case EMERGENCY_CALLOUT:
		    		System.out.println("Emergency callout received");
		    		String callId = in.readLine();
					CallInterface call = (CallInterface) registry.lookup(callId);
					System.out.println("Call information received!\n" + call.getContent());
					System.out.println("Dispatching ambulance");
					out.println(1);
		    }
		    socket.close();
		}
	}
	
	private static int requestPort() {
		ServerRequest request = new ServerRequest(ApplicationController.REGIONAL_CLIENT_REGISTER, "");
		if (request.send()) {
			return Integer.parseInt(request.getResponse());
		} else {
			return -1;
		}
	}
}

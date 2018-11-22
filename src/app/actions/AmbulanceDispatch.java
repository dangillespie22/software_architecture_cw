package app.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import config.Config;

public class AmbulanceDispatch {

	private PrintWriter notifyChannel;
	private int patientId;
	
	public AmbulanceDispatch(int patientId, PrintWriter notifyChannel) {
		this.patientId = patientId;
		this.notifyChannel = notifyChannel;
	}
	
	public void dispatch() {
		try {
			Socket socket = new Socket(Config.getPropValues().getProperty("ip"), Integer.parseInt(Config.getPropValues().getProperty("regionalport")));
			System.out.println("Address: " + socket.getInetAddress() + ":" + socket.getPort());
			PrintWriter o = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader i = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    o.println(1);
		    
		    PatientLookup pLookup = new PatientLookup();
			pLookup.lookupAndBind(patientId, o);
		    notifyChannel.println(Integer.parseInt(i.readLine()));
			
			socket.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}

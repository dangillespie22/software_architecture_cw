package app.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import config.Config;

public class AmbulanceDispatch {

	private PrintWriter notifyChannel;
	private int callId;
	private int port;
	
	public AmbulanceDispatch(int callId, PrintWriter notifyChannel, int port) {
		this.callId = callId;
		this.notifyChannel = notifyChannel;
		this.port = port;
	}
	
	public void dispatch() {
		try {
			Socket socket = new Socket(Config.getPropValues().getProperty("ip"), port);
			System.out.println("Address: " + socket.getInetAddress() + ":" + socket.getPort());
			PrintWriter o = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader i = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			o.println(1);
				    
			CallLookup callLookup = new CallLookup();
			callLookup.lookupAndBind(callId, o);
			notifyChannel.println(Integer.parseInt(i.readLine()));
				
			socket.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}

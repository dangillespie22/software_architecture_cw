package clients.call_operator.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import config.Config;

public class ServerRequest {
	
	Socket socket;
	int requestId;
	String data;
	
	String  response;
	boolean success = false;

	public ServerRequest(int requestId, String data) {
		this.requestId = requestId;
		this.data = data;
	}
	
	public ServerRequest(int requestId) {
		this.requestId = requestId;
	}
	
	public boolean send() {
		try {
			socket = new Socket(
				Config.getPropValues().getProperty("ip"),
				Integer.parseInt(Config.getPropValues().getProperty("port"))
			);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.write(requestId);
			out.println(data);
			
			response = in.readLine();
			success = !response.equals("-1");
			
			socket.close();
		} catch (NumberFormatException | IOException e) { e.printStackTrace(); }
		
		return success;
	}
	
	public String getResponse() {
		return response;
	}
}

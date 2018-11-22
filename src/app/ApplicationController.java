package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.IntStream;

import app.actions.AmbulanceDispatch;
import app.actions.CallLookup;
import app.actions.MedicalRecordLookup;
import app.actions.PatientLookup;
import config.*;
import models.call.Call;

import java.net.ServerSocket;
import java.net.Socket;

public class ApplicationController {
	
	public static final int PATIENT_LOOKUP = 1;
	public static final int CALL_LOOKUP = 2;
	public static final int MEDICAL_RECORD_LOOKUP = 3;
	public static final int AMBULANCE_DISPATCH = 4;
	public static final int NEW_CALL = 99;
	public static final int REGIONAL_CLIENT_REGISTER = 98;
	
	private static ArrayList<Integer> regionalPorts = new ArrayList<>();
	private static ArrayList<Integer> usedPorts = new ArrayList<>();
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		populatePorts();
		try
		{
			ServerSocket server = new ServerSocket(Integer.parseInt(Config.getPropValues().getProperty("port")));
			System.out.println("Starting server! ");
			while (true) {
				System.out.println("Waiting for requests..");
				Socket socket = server.accept();
				System.out.println("New client connected.");
				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    System.out.println("Waiting for request..");
			    
			    switch (in.read()) {	
			    	case PATIENT_LOOKUP:
			    		int patientId = Integer.parseInt(in.readLine());
			    		PatientLookup lookup = new PatientLookup();
			    		lookup.lookupAndBind(patientId, out);
			    		break;
			    	case CALL_LOOKUP:
			    		int callId = Integer.parseInt(in.readLine());
			    		CallLookup callLookup = new CallLookup();
			    		callLookup.lookupAndBind(callId, out);
			    		break;
			    	case MEDICAL_RECORD_LOOKUP:
			    		int medicalRecordId = Integer.parseInt(in.readLine());
			    		MedicalRecordLookup recordLookup = new MedicalRecordLookup();
			    		recordLookup.lookupAndBind(medicalRecordId, out);
			    		break;
			    	case AMBULANCE_DISPATCH: 
			    		int pId = Integer.parseInt(in.readLine());
			    		if (usedPorts.isEmpty()) {
			    			System.out.println("No hospital avaiable to take the emergency callout..");
			    			break;
			    		}
			    		int regionalPort = usedPorts.get(0);
			    		AmbulanceDispatch ambulance = new AmbulanceDispatch(pId, out, regionalPort);
			    		ambulance.dispatch();
		    			break;
			    	case NEW_CALL:
			    		in.readLine();
			    		Call call = new Call();
		    			String reference = call.bindRMI();
	    				out.println(reference);
	    				break;
			    	case REGIONAL_CLIENT_REGISTER:
			    		in.readLine();
			    		if (!regionalPorts.isEmpty()) {
			    			int port = regionalPorts.get(0);
			    			regionalPorts.remove(0);
			    			usedPorts.add(port);
			    			System.out.println("Registering new client on port: " + port);
			    			out.println(port);
			    		}
			    		break;
		    		default:
		    			out.println(-1);
		    			break;
			    }
			    System.out.println("Ending request");
			    socket.close();
			}
		}
		catch (IOException ioe)
		{
			System.err.println("Error in I/O");
			System.err.println(ioe.getMessage());
			System.exit(-1);
		}
	}
	
	public static void populatePorts() {
		try {
			int portStart = Integer.parseInt(Config.getPropValues().getProperty("regional_port_start"));
			int portRange = Integer.parseInt(Config.getPropValues().getProperty("regional_port_range"));
			IntStream.range(portStart, portStart + portRange).forEach(
		        n -> {
		        	regionalPorts.add(n);
		        }
		    );
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}

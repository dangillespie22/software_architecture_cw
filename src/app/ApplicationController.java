package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import app.actions.AmbulanceDispatch;
import app.actions.CallLookup;
import app.actions.MedicalRecordLookup;
import app.actions.PatientLookup;
import config.*;
import models.call.Call;

import java.net.ServerSocket;
import java.net.Socket;

public class ApplicationController {
	
	static ArrayList<Integer> regionalPorts = new ArrayList<>();
	private static final int PATIENT_LOOKUP = 1;
	private static final int CALL_LOOKUP = 2;
	private static final int MEDICAL_RECORD_LOOKUP = 3;
	private static final int AMBULANCE_DISPATCH = 4;
	private static final int NEW_CALL = 5;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		try
		{
			ServerSocket server = new ServerSocket(Integer.parseInt(Config.getPropValues().getProperty("port")));
			System.out.println("Starting server! Waiting for requests..");
			while (true) {
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
			    		AmbulanceDispatch ambulance = new AmbulanceDispatch(pId, out);
			    		ambulance.dispatch();
		    			break;
			    	case NEW_CALL:
			    		in.readLine();
			    		Call call = new Call();
		    			String reference = call.bindRMI();
	    				out.println(reference);
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
}

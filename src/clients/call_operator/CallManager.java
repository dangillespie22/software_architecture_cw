package clients.call_operator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import app.ApplicationController;
import clients.call_operator.actions.PatientAction;
import clients.call_operator.actions.RegistryLookup;
import clients.call_operator.actions.ServerRequest;
import models.call.CallInterface;
import models.medical_record.MedicalRecordInterface;

public class CallManager {

	private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private CallInterface call;
	
	public void startNewCall() {
		startRinging();
	}
	
	private void handleRequest(int request) throws IOException {
		switch (request) {
			case ApplicationController.PATIENT_LOOKUP:
				System.out.print("Please enter the patients id: ");
				int patientNo = Integer.parseInt(stdIn.readLine());
				call.setPatientId(patientNo);
				call.save();
				PatientAction pAction = new PatientAction(patientNo, stdIn);
				pAction.start();
				break;
			case ApplicationController.CALL_LOOKUP:
				updateCall();
				break;
			case ApplicationController.MEDICAL_RECORD_LOOKUP:
				System.out.print("Please enter the medical record id: ");
				String recordId = stdIn.readLine();
				requestMedicalRecord(recordId);
				break;
			case ApplicationController.AMBULANCE_DISPATCH:
				System.out.println("Requesting ambulance callout");
				System.out.print("Please enter an address: ");
				String address = stdIn.readLine();
				requestAmbulance(address);
				call.setActionTaken("ambulance dispatched");
				call.save();
				break;
		}
	}
	
	private void updateCall() {
		System.out.println("Please enter the field you wish to update");
		System.out.println("(1) Patient ID");
		System.out.println("(2) Issues");
		System.out.println("(3) Action taken");
		System.out.println("(4) Location");
		System.out.println("(-1) Back");
		System.out.print("Response: ");

		try {
			int option = Integer.parseInt(stdIn.readLine());
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
				case -1:
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
	
	private void requestMedicalRecord(String patientId) {
		try {
			String reference = requestReference(ApplicationController.MEDICAL_RECORD_LOOKUP, patientId);
			if (reference != null) {
				RegistryLookup lookup = new RegistryLookup();
				MedicalRecordInterface record = lookup.medicalRecordByReference(reference);
				System.out.println("Record received: " + record.getId());
			} else {
				System.out.println("No such record");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void requestAmbulance(String patientId) {
		ServerRequest request;
		try {
			request = new ServerRequest(ApplicationController.AMBULANCE_DISPATCH, Integer.toString(call.getId()));
			if (request.send()) {
				System.out.println("Ambulance request sent to regional hospital");
			} else {
				System.out.println("There was an error processing this callout");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public String requestReference(int requestId, String details) { 
		ServerRequest request = new ServerRequest(requestId, details);
		if (request.send()) {
			return request.getResponse();
		}
		return null;
	}
	
	
	private void startRinging() {
		try {
			System.out.println("The phone is ringing!! Enter 1 to accept or -1 to decline!");
			System.out.print("Response: ");
			int input = Integer.parseInt(stdIn.readLine());
			switch(input) {
				case 1:
					acceptCall();
					break;
				case -1:
					rejectCall();
					break;
				default:
					System.out.println("Invalid response!");
					break;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void acceptCall() throws IOException {
		
		String reference = requestReference(ApplicationController.NEW_CALL, "");
		if (reference != null) {
			RegistryLookup lookup = new RegistryLookup();
			call = lookup.callByReference(reference);
		} else {
			return;
		}
		
		System.out.println("New call has begun..");
		
		while(true) {
			printIntro();
			int input = Integer.parseInt(stdIn.readLine());
			if (input != -1) {
				handleRequest(input);
			} else {
				System.out.println("Ending call!");
				call.setCallEnd(new Timestamp(System.currentTimeMillis()));
				call.save();
				break;
			}
			System.out.println("-----------");
		}
	}
	
	private void rejectCall() { 
		System.out.println("Hopefully they didn't die!");
	}
	
	private void printIntro() {
		System.out.println("Please make a selection from the set of options available:");
		System.out.println("(1) Request patient details");
		System.out.println("(2) Update call details");
		System.out.println("(3) Request medical records");
		System.out.println("(4) Request ambulance callout");
		System.out.println("(-1) End call");
		System.out.print("Response: ");
	}
}

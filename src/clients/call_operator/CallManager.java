package clients.call_operator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import clients.call_operator.actions.PatientAction;
import clients.call_operator.actions.RegistryLookup;
import clients.call_operator.actions.ServerRequest;
import models.call.CallInterface;
import models.medical_record.MedicalRecordInterface;
import models.patient.PatientInterface;

public class CallManager {
	
	private static final int PATIENT_LOOKUP = 1;
	private static final int CALL_LOOKUP = 2;
	private static final int MEDICAL_RECORD_LOOKUP = 3;
	private static final int AMBULANCE_DISPATCH = 4;
	private static final int NEW_CALL = 5;

	private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private CallInterface call;
	
	public void startNewCall() {
		startRinging();
	}
	
	private void handleRequest(int request) throws IOException {
		switch (request) {
			case PATIENT_LOOKUP:
				System.out.print("Please enter the patients id: ");
				int patientNo = Integer.parseInt(stdIn.readLine());
				PatientAction pAction = new PatientAction(patientNo, stdIn);
				pAction.start();
				break;
			case CALL_LOOKUP:
				System.out.print("Please enter the call id: ");
				String callId = stdIn.readLine();
				requestCall(request, callId);
				break;
			case MEDICAL_RECORD_LOOKUP:
				System.out.print("Please enter the medical record id: ");
				String recordId = stdIn.readLine();
				requestMedicalRecord(request, recordId);
				break;
			case AMBULANCE_DISPATCH:
				System.out.println("Requesting ambulance callout");
				System.out.print("Please enter patient id: ");
				String patientId = stdIn.readLine();
				requestAmbulance(request, patientId);
				break;
		}
	}
	
	private void requestCall(int requestId, String callId) {
		try {
			String reference = requestReference(requestId, callId);
			if (reference != null) {
				RegistryLookup lookup = new RegistryLookup();
				CallInterface call = lookup.callByReference(reference);
				System.out.println("Call received: " + call.getId());
			} else {
				System.out.println("No such Call!");
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void requestMedicalRecord(int requestId, String patientId) {
		try {
			String reference = requestReference(requestId, patientId);
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
	
	public String requestReference(int requestId, String details) { 
		ServerRequest request = new ServerRequest(requestId, details);
		if (request.send()) {
			return request.getResponse();
		}
		return null;
	}
	
	public void requestAmbulance(int requestId, String patientId) {
		ServerRequest request = new ServerRequest(requestId, patientId);
		if (request.send()) {
			System.out.println("Ambulance request sent to regional hospital");
		} else {
			System.out.println("There was an error processing this callout");
		}
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
		
		String reference = requestReference(NEW_CALL, "");
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
		System.out.println("(2) Request call details");
		System.out.println("(3) Request medical records");
		System.out.println("(4) Request ambulance callout");
		System.out.println("(-1) End call");
		System.out.print("Response: ");
	}
	
}

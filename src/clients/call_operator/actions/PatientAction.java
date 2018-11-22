package clients.call_operator.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.RemoteException;

import models.medical_record.MedicalRecord;
import models.medical_record.MedicalRecordInterface;
import models.patient.PatientInterface;

public class PatientAction {

	private static final int MEDICAL_HISTORY = 1;
	private static final int AMBULANCE_RESPONSE = 2;
	private static final int RETURN = 3;
	
	private PatientInterface patient;
	private BufferedReader stdIn;
	
	public PatientAction(int patientId, BufferedReader stdIn) {
		this.stdIn = stdIn;
		lookupPatient(1, patientId);
	}
	
	public void start() {
		if (patient == null) {
			System.out.println("No patient found..");
			return;
		}
		
		try {
			while (true) {
				printOptions();
				int response = Integer.parseInt(stdIn.readLine());
				switch (response) {
					case MEDICAL_HISTORY:
						ServerRequest recordRequest = new ServerRequest(3, Integer.toString(patient.getMedicalRecordId()));
						if (recordRequest.send()) {
							String reference = recordRequest.getResponse();
							RegistryLookup l = new RegistryLookup();
							MedicalRecordInterface record = l.medicalRecordByReference(reference);
							System.out.println("Record ID: " + record.getId());
							record.getIssues().forEach(action -> {
								System.out.println(action);
							});
						} else {
							System.out.println("No results..");
						}
						break;
					case AMBULANCE_RESPONSE:
						ServerRequest request = new ServerRequest(4, Integer.toString(patient.getId()));
						if (request.send()) {
							System.out.println("Ambulance request sent to regional hospital");
						} else {
							System.out.println("There was an error processing this callout");
						}
						break;
					case RETURN:
						return;
				}
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void lookupPatient(int requestId, int patientId) {
		try {
			String reference = requestReference(requestId, Integer.toString(patientId));
			if (reference != null) {
				RegistryLookup lookup = new RegistryLookup();
				patient = lookup.patientByReference(reference);
				System.out.println("Patient received: " + patient.getFirstName() + " " + patient.getLastName());
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
	
	public void printOptions() {
		System.out.println("Please enter an option:");
		System.out.println("(1) Request medical history");
		System.out.println("(2) Request ambulance response");
		System.out.println("(3) Return to main menu");
		System.out.print("Response: ");
	}
}

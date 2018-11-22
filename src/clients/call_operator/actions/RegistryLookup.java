package clients.call_operator.actions;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import config.Config;
import models.call.CallInterface;
import models.medical_record.MedicalRecordInterface;
import models.patient.PatientInterface;

public class RegistryLookup {

	public PatientInterface patientByReference(String reference) {
		try {
			Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
			System.out.println("Registry found in reg lookup");
			PatientInterface patient = (PatientInterface) registry.lookup(reference);
			return patient;
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CallInterface callByReference(String reference) {
		try {
			Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
			System.out.println("Registry found in reg lookup");
			CallInterface call = (CallInterface) registry.lookup(reference);
			return call;
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public MedicalRecordInterface medicalRecordByReference(String reference) {
		try {
			Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
			System.out.println("Registry found in reg lookup");
			MedicalRecordInterface record = (MedicalRecordInterface) registry.lookup(reference);
			return record;
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

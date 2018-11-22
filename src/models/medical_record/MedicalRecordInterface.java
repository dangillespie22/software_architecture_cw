package models.medical_record;

import java.util.ArrayList;
import java.rmi.RemoteException;

import models.interfaces.KwikRMI;

public interface MedicalRecordInterface extends KwikRMI {

	public int getId() throws RemoteException;

	public void setId(int id) throws RemoteException;
	
	public ArrayList<String> getIssues() throws RemoteException;

	public void setIssues(ArrayList<String> issues) throws RemoteException;
}

package models.patient;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import models.interfaces.KwikRMI;

public interface PatientInterface extends KwikRMI {

	public Timestamp getDateOfBirth() throws RemoteException;

	public void setDateOfBirth(Timestamp dateOfBirth) throws RemoteException;

	public String getLastName() throws RemoteException;

	public void setLastName(String lastName) throws RemoteException;

	public String getFirstName() throws RemoteException;

	public void setFirstName(String firstName) throws RemoteException;

	public String getAddress() throws RemoteException;

	public void setAddress(String address) throws RemoteException;
	
	public String getPostcode() throws RemoteException;

	public void setPostcode(String postcode) throws RemoteException;

	public int getId() throws RemoteException;

	public void setId(int id) throws RemoteException;
	
	public int getMedicalRecordId() throws RemoteException;
	
	public void setMedicalRecordId(int id) throws RemoteException;
	
	public String getContent() throws RemoteException;
}

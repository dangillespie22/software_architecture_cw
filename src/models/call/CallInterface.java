package models.call;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import models.interfaces.DatabaseObject;
import models.interfaces.KwikRMI;

public interface CallInterface extends KwikRMI, DatabaseObject{

	public String getLocation() throws RemoteException;
	
	public void setLocation(String location) throws RemoteException;
	
	public String getActionTaken() throws RemoteException;
	
	public void setActionTaken(String actionTaken) throws RemoteException;
	
	public String getIssue() throws RemoteException;
	
	public void setIssue(String issue) throws RemoteException;
	
	public int getPatientId() throws RemoteException;
	
	public void setPatientId(int patientId) throws RemoteException;
	
	public Timestamp getCallStart() throws RemoteException;
	
	public void setCallStart(Timestamp callStart) throws RemoteException;
	
	public Timestamp getCallEnd() throws RemoteException;
	
	public void setCallEnd(Timestamp callStart) throws RemoteException;
	
	public int getId() throws RemoteException;
	
	public String getContent() throws RemoteException;
}
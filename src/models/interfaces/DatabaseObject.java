package models.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DatabaseObject extends Remote  {

	public void save() throws RemoteException; 
}

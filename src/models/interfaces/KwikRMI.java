package models.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KwikRMI extends Remote {

	public String bindRMI() throws RemoteException;
}

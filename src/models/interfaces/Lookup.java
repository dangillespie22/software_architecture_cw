package models.interfaces;

import java.io.PrintWriter;

public interface Lookup {

	public Object byId(int id);
	
	public void lookupAndBind(int id, PrintWriter notifySocket);
	
}
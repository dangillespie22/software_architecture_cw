package models.call;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import config.Config;
import db.DatabaseConnection;
import db.PrimaryKeyHandler;

public class Call implements CallInterface {

	private int id;
	private int patientId;
	private String issue;
	private String actionTaken;
	private String location;
	private Timestamp callStart;
	private Timestamp callEnd;
	
	private String updateQuery = "REPLACE INTO call_out VALUES(?,?,?,?,?,?,?)";
	
	public Call(int id, int patientId, String issue, String action, String location) {
		this.id = id;
		this.callStart = new Timestamp(System.currentTimeMillis());
		this.patientId = patientId;
		this.issue = issue;
		this.actionTaken = action;
		this.location = location;
	}
	
	public Call() {
		try {
			this.id = PrimaryKeyHandler.getNextCallId();
			this.callStart = new Timestamp(System.currentTimeMillis());
			
			save();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Call(int id) {
		this.id = id;
	}
	
	public static Call rehydrateFromId(int id) {
		return new Call(id);
	}
	
	public int getId() {
		return id;
	}
	
	public void save() {
		try {
			DatabaseConnection con = new DatabaseConnection();
			PreparedStatement statement = con.prepareStatement(updateQuery);
			statement.setInt(1, id);
			statement.setInt(2, patientId);
			statement.setString(3, issue);
			statement.setString(4, actionTaken);
			statement.setString(5, location);
			statement.setTimestamp(6, callStart);
			statement.setTimestamp(7, callEnd);
			statement.executeQuery();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getActionTaken() {
		return actionTaken;
	}
	
	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}
	
	public String getIssue() {
		return issue;
	}
	
	public void setIssue(String issue) {
		this.issue = issue;
	}
	
	public int getPatientId() {
		return patientId;
	}
	
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	
	public Timestamp getCallStart() {
		return callStart;
	}
	
	public void setCallStart(Timestamp callStart) {
		this.callStart = callStart;
	}
	
	public Timestamp getCallEnd() {
		return callEnd;
	}
	public void setCallEnd(Timestamp callEnd) {
		this.callEnd = callEnd;
	}
	
	public String getContent() {
		return "PatientID: " + patientId + 
				"\nIssue: " + issue + 
				"\nAction Taken: " + actionTaken + 
				"\nLocation: " + location;
	}

	public String bindRMI() {
		String reference = null;
		try {
			Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
			CallInterface stub = (CallInterface) UnicastRemoteObject.exportObject(this, 0);
			reference = "call" + id;
			registry.rebind(reference, stub);
		} catch (IOException e) {
			System.out.println("Binding failed for call: " + id);
			e.printStackTrace();
		}
		return reference;
	}
}

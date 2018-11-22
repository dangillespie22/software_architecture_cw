package models.medical_record;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import config.Config;
import db.DatabaseConnection;
import db.PrimaryKeyHandler;
import models.interfaces.DatabaseObject;
import models.medical_record.MedicalRecordInterface;

public class MedicalRecord implements MedicalRecordInterface, DatabaseObject {

	private int id;
	private ArrayList<String> issues;
	private String updateQuery = "REPLACE INTO medical_record VALUES(?,?)";
	
	public MedicalRecord() {
		try {
			this.id = PrimaryKeyHandler.getNextMedicalRecordId();
			this.issues = new ArrayList<>();
			this.save();
		} catch (SQLException e) { e.printStackTrace(); }
	}
	
	public void save() {
		try {
			DatabaseConnection con = new DatabaseConnection();
			PreparedStatement statement = con.prepareStatement(updateQuery);
			statement.setInt(1, id);
			statement.setString(2, issues.toString());
			statement.executeQuery();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static MedicalRecord rehydrateFromId(int id) {
		return new MedicalRecord(id);
	}
	
	public void addIssue(String issue) {
		issues.add(issue);
	}
	
	private MedicalRecord(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<String> getIssues() {
		return issues;
	}

	public void setIssues(ArrayList<String> issues) {
		this.issues = issues;
	}

	public String bindRMI() {
		String reference = null;
		try {
			Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
			MedicalRecordInterface record = (MedicalRecordInterface) UnicastRemoteObject.exportObject(this, 0);
			reference = "medicalrecord" + id;
			System.out.println("Binding: " + reference);
			registry.rebind(reference, record);	
		} catch (IOException e) {
			System.out.println("Binding failed for call: " + id);
			e.printStackTrace();
		}
		return reference;
	}
}

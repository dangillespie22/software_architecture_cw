package models.patient;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import config.Config;
import db.DatabaseConnection;
import models.interfaces.DatabaseObject;
import models.medical_record.MedicalRecord;

public class Patient implements PatientInterface, DatabaseObject {
	
	private int id;
	private int medicalRecordId;
	private String firstName;
	private String lastName;
	private Timestamp dateOfBirth;
	private String house_address;
	private String postcode;
	private String updateQuery = "REPLACE INTO patient VALUES(?,?,?,?,?,?,?)";

	public Patient(String firstName, String lastName) {
		this.setFirstName(firstName);
		this.setLastName(lastName);
		MedicalRecord medicalRecord = new MedicalRecord();
		this.medicalRecordId = medicalRecord.getId();
		this.save();
	}
	
	private Patient(int id) {
		this.id = id;
	}
	
	public static Patient rehydrateFromId(int id) {
		return new Patient(id);
	}
	
	public void save() {
		try {
			DatabaseConnection con = new DatabaseConnection();
			PreparedStatement statement = con.prepareStatement(updateQuery);
			statement.setInt(1, id);
			statement.setInt(2, medicalRecordId);
			statement.setString(3, firstName);
			statement.setString(4, lastName);
			statement.setString(5, house_address);
			statement.setString(6, postcode);
			statement.setTimestamp(7, dateOfBirth);
			statement.executeQuery();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Timestamp getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Timestamp dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getAddress() {
		return house_address;
	}

	public void setAddress(String address) {
		this.house_address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getMedicalRecordId() {
		return this.medicalRecordId;
	}
	
	public void setMedicalRecordId(int medicalRecordId) {
		this.medicalRecordId = medicalRecordId;
	}

	public String bindRMI() {
		String reference = null;
		try {
			Registry registry = LocateRegistry.getRegistry(Config.getPropValues().getProperty("ip"));
			PatientInterface stub = (PatientInterface) UnicastRemoteObject.exportObject(this, 0);
			reference = "patient" + id;
			registry.rebind(reference, stub);
			
		} catch (IOException e) {
			System.out.println("Binding failed for call: " + id);
			e.printStackTrace();
		}
		return reference;
	}
	
	public String getContent() {
		return "Name: " + firstName + " " + lastName + "\n" + 
				"Address: " + house_address + "\n" + 
				"Postcode: " + postcode + "\n" + 
				"Date of birth: " + dateOfBirth;
	}
}

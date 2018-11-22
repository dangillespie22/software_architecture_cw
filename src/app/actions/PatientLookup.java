package app.actions;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseConnection;
import models.interfaces.Lookup;
import models.patient.Patient;

public class PatientLookup implements Lookup {
	
	private DatabaseConnection connection;
	private String lookupById = "SELECT * from patient where id = ?";

	public PatientLookup() {
		connection = new DatabaseConnection();
	}
	
	public Patient byId(int id) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(lookupById);
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.first()) {
				Patient patient = Patient.rehydrateFromId(rs.getInt("id"));
				patient.setMedicalRecordId(rs.getInt("medical_record_id"));
				patient.setFirstName(rs.getString("first_name"));
				patient.setLastName(rs.getString("last_name"));
				patient.setAddress(rs.getString("house_address"));
				patient.setPostcode(rs.getString("postcode"));
				patient.setDateOfBirth(rs.getTimestamp("date_of_birth"));
				
				return patient;
			}
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void lookupAndBind(int id, PrintWriter notifySocket) {
		Patient p = byId(id);
		if (p != null) {
			String reference = p.bindRMI();
			if (reference != null) {
				notifySocket.println(reference);
			}
		} else {
			notifySocket.println(-1);
		}
	}
}

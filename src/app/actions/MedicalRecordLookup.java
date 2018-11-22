package app.actions;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import db.DatabaseConnection;
import models.interfaces.Lookup;
import models.medical_record.MedicalRecord;

public class MedicalRecordLookup implements Lookup {

	private DatabaseConnection connection;
	private String lookupById = "SELECT * from medical_record where id = ?";
	
	public MedicalRecordLookup() {
		connection = new DatabaseConnection();
	}
	
	public MedicalRecord byId(int id) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(lookupById);
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.first()) {
				MedicalRecord record = MedicalRecord.rehydrateFromId(rs.getInt("id"));
				record.setIssues(new ArrayList<>());
				return record;
			}
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}


	public void lookupAndBind(int id, PrintWriter notifySocket) {
		MedicalRecord record = byId(id);
		if (record != null) {
			String reference = record.bindRMI();
			if (reference != null) {
				notifySocket.println(reference);
			}
		} else {
			notifySocket.println(-1);
		}
	}
}

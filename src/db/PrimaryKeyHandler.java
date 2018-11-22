package db;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class PrimaryKeyHandler {
	
	private final static String CALL_ID_QUERY = "SELECT id FROM call_out ORDER BY id DESC LIMIT 1";
	private final static String PATIENT_ID_QUERY = "SELECT id FROM patient ORDER BY id DESC LIMIT 1";
	private final static String MEDICAL_RECORD_ID_QUERY = "SELECT id FROM medical_record ORDER BY id DESC LIMIT 1";

	public static int getNextCallId() throws SQLException {
		
		int id = 1;
		DatabaseConnection con = new DatabaseConnection();
		ResultSet rs = con.executeQueryRS(CALL_ID_QUERY);
		if (rs.first()) {
			id = rs.getInt(1);
		}
		con.close();
		
		id++;
		return id;
	}
	
	public static int getNextPatientId() throws SQLException {
		
		int id = 1;
		DatabaseConnection con = new DatabaseConnection();
		ResultSet rs = con.executeQueryRS(PATIENT_ID_QUERY);
		if (rs.first()) {
			id = rs.getInt(1);
		}
		con.close();
		
		id++;
		return id;
	}
	
	public static int getNextMedicalRecordId() throws SQLException {
		
		int id = 1;
		DatabaseConnection con = new DatabaseConnection();
		ResultSet rs = con.executeQueryRS(MEDICAL_RECORD_ID_QUERY);
		if (rs.first()) {
			id = rs.getInt(1);
		}
		con.close();
		
		id++;
		return id;
	}
}

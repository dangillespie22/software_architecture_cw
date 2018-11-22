package app.actions;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseConnection;
import models.call.Call;
import models.interfaces.Lookup;

public class CallLookup implements Lookup {

	private DatabaseConnection connection;
	private String lookupById = "SELECT * from call_out where id = ?";
	
	public CallLookup() {
		connection = new DatabaseConnection();
	}
	
	public Call byId(int id) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(lookupById);
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.first()) {
				Call call = Call.rehydrateFromId(rs.getInt("id"));
				call.setPatientId(rs.getInt("patient_id"));
				call.setIssue(rs.getString("issue"));
				call.setActionTaken(rs.getString("action_taken"));
				call.setLocation(rs.getString("location"));
				call.setCallStart(rs.getTimestamp("call_start"));
				call.setCallEnd(rs.getTimestamp("call_end"));
				
				return call;
			}
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void lookupAndBind(int id, PrintWriter notifySocket) {
		Call call = byId(id);
		if (call != null) {
			String reference = call.bindRMI();
			if (reference != null) {
				System.out.println("Writing reference: " + reference);
				notifySocket.println(reference);
			}
		} else {
			notifySocket.println(-1);
		}
	}
}

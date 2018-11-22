package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import config.Config;

public class DatabaseConnection {

	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement;
	
	public DatabaseConnection() {
		try {
			String dbUser = Config.getPropValues().getProperty("dbuser");
			String dbPass = Config.getPropValues().getProperty("dbuser");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/medical_records?user="+ dbUser+ "&password="+ dbPass);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public PreparedStatement prepareStatement(String query) {
		try {
			preparedStatement = connection.prepareStatement(query);
			return preparedStatement;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
public void executeQuery(String query) {
		try {
			statement.executeQuery(query);
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQueryRS(String query) {
		try {
			return statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close() {
		try {
			if (statement != null) {
				statement.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

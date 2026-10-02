package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class DAO {
	protected static Connection db;
	private Dotenv dotenv = Dotenv.load();
	
	private String BASE_URL = dotenv.get("AZURE_ENDPOINT"); 
	//private String PROJECT_NAME = "matchservice";
	public DAO(){
		if(db == null) {
			try {
				String URL =  BASE_URL;
				String USERNAME = dotenv.get("DB_USERNAME");
				String PASSWD = dotenv.get("DB_PASSWORD");
				
				Properties prop = new Properties();
				prop.setProperty("user", USERNAME);
				prop.setProperty("password", PASSWD);
				
				db = DriverManager.getConnection(URL, prop);
				
			} catch (SQLException e) {
				System.err.println("SQLException: " + e.getMessage());
			}
			
		}
	}
}
package Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	public static final String DATABASE_NAME = "homemmsdb";
	// private static final int DATABASE_VERSION = 2;
	public final String TABLE_USER = "tblUser";
	public final String TABLE_MESSAGE = "tblMessage";
	public final String USERID = "userID";
	public final String NAMEDISPLAY = "nameDisplay";
	public final String PASSWORD = "password";
	public final String AVATAR = "avatar";
	public final String STATUS_USER = "status";
	public final String MID = "mID";
	public final String TITLE = "title";
	public final String SENDER = "sender";
	public final String RECIEVER = "reciever";
	public final String CONTENTTEXT = "contentText";
	public final String CONTENTIMAGE = "contentImage";
	public final String CONTENTAUDIO = "contentAudio";
	public final String CONTENTVIDEO = "contentVideo";
	public final String TIMESTAMP = "timestamp";
	public final String STATUS_MSG = "status";
	
	public DatabaseHandler() {

	}

	public void createTableUser() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
					+ "userID VARCHAR(20) NOT NULL PRIMARY KEY,"
					+ "nameDisplay VARCHAR(25), " 
					+ "password VARCHAR(25), "
					+ "avatar VARCHAR(25), " 
					+ "status VARCHAR(10) " 
					+ ")";
			stmt.executeUpdate(CREATE_TABLE_USER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}
	}
	
	public void createTableMessage() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			
			String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE + "("
					+ "mID VARCHAR(20) NOT NULL PRIMARY KEY,"
					+ "title VARCHAR(50), " 
					+ "sender VARCHAR(20) NOT NULL, "
					+ "reciever VARCHAR(100) NOT NULL, " 
					+ "contentText VARCHAR(2000), " 
					+ "contentImage VARCHAR(25), "
					+ "contentAudio VARCHAR(25), "
					+ "contentVideo VARCHAR(25), "
					+ "timestamp VARCHAR(30), "
					+ "status VARCHAR(20) "
					+ ")";
			stmt.executeUpdate(CREATE_TABLE_MESSAGE);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}
	}

}

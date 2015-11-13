package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MysqlConnect {
	public static Connection getConnectMysql() {
		//creates three different Connection objects
        Connection conn = null;
 
        try {
        	// Load the Connector/J driver if dont add mysql connector java to Extenal Libs.
        	//Class.forName("com.mysql.jdbc.Driver").newInstance();
        	
            String url = "jdbc:mysql://localhost:3306/homemmsdb";
            String user = "homemms";
            String password = "123456";
 
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Connected to the database" + DatabaseHandler.DATABASE_NAME);
            }
        } catch (SQLException ex) {
            System.out.println("An error occurred. Maybe user/password is invalid" + ex);
        }
        return conn;
	}
	
	public static void closeConnectMysql(Connection conn){
		if (conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

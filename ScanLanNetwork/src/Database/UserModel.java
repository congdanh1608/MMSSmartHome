package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.User;

public class UserModel {

	public DatabaseHandler handler;

	public UserModel() {
		handler = new DatabaseHandler();
	}

	public void AddUser(User obj) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "INSERT INTO " + handler.TABLE_USER + " (" + handler.USERID + ", " + handler.NAMEDISPLAY + ", "
					+ handler.PASSWORD + ", " + handler.AVATAR + ", " + handler.STATUS_USER + ")" + " VALUES ('"
					+ obj.getId() + "','" + obj.getNameDisplay() + "','" + obj.getPassword() + "','" + obj.getAvatar()
					+ "','" + obj.getStatus() + "')";

			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public void DeleteUser(String userID) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "DELETE FROM " + handler.TABLE_USER + " WHERE " + handler.USERID + "='" + userID + "'";

			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public void UpdateStatusUser(String userID, String status) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "UPDATE " + handler.TABLE_USER + " SET " + handler.STATUS_USER + "='" + status + "'"
					+ " WHERE " + handler.USERID + "='" + userID + "'";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public void UpdatePasswordUser(String userID, String password) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "UPDATE " + handler.TABLE_USER + " SET " + handler.PASSWORD + "='" + password + "'" + " WHERE "
					+ handler.USERID + "='" + userID + "'";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public void UpdateAvatarUser(String userID, String avatar) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "UPDATE " + handler.TABLE_USER + " SET " + handler.AVATAR + "='" + avatar + "'" + " WHERE "
					+ handler.USERID + "='" + userID + "'";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public void UpdateNameDisplayUser(String userID, String nameDisplay) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "UPDATE " + handler.TABLE_USER + " SET " + handler.NAMEDISPLAY + "='" + nameDisplay + "'"
					+ " WHERE " + handler.USERID + "='" + userID + "'";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public void UpdateInfoUser(String userID, String nameDisplay, String password, String avatar, String status) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "UPDATE " + handler.TABLE_USER + " SET " + handler.NAMEDISPLAY + "='" + nameDisplay + "',"
					+ handler.PASSWORD + "='" + password + "'," + handler.AVATAR + "='" + avatar + "',"
					+ handler.STATUS_USER + "='" + status + "'" + " WHERE " + handler.USERID + "='" + userID + "'";
			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public User getUser(String userID) {
		Connection conn = null;
		Statement stmt = null;
		User user = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_USER + " WHERE " + handler.USERID + "='" + userID + "'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				user = new User(rset.getString(handler.USERID), rset.getString(handler.NAMEDISPLAY),
						rset.getString(handler.PASSWORD), rset.getString(handler.AVATAR),
						rset.getString(handler.STATUS_USER));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return user;
	}

	public User getUserWithPass(String userID, String pass) {
		Connection conn = null;
		Statement stmt = null;
		User user = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_USER + " WHERE " + handler.USERID + "='" + userID + "' AND "
					+ handler.PASSWORD + "='" + pass + "'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				user = new User(rset.getString(handler.USERID), rset.getString(handler.NAMEDISPLAY),
						rset.getString(handler.PASSWORD), rset.getString(handler.AVATAR),
						rset.getString(handler.STATUS_USER));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return user;
	}

	public List<User> getAllUser() {
		List<User> users = new ArrayList<User>();
		Connection conn = null;
		Statement stmt = null;
		User user = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_USER;
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				user = new User(rset.getString(handler.USERID), rset.getString(handler.NAMEDISPLAY),
						rset.getString(handler.PASSWORD), rset.getString(handler.AVATAR),
						rset.getString(handler.STATUS_USER));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return users;
	}
}

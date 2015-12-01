package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Model.User;
import presistence.ContantsHomeMMS;

public class MessageModel {
	public DatabaseHandler handler;

	public MessageModel() {
		handler = new DatabaseHandler();
	}

	public void AddMessage(Message obj) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "INSERT INTO " + handler.TABLE_MESSAGE + " (" + handler.MID + ", " + handler.TITLE + ", "
					+ handler.SENDER + ", " + handler.RECIEVER + ", " + handler.CONTENTTEXT + ", "
					+ handler.CONTENTIMAGE + ", " + handler.CONTENTAUDIO + ", " + handler.CONTENTVIDEO + ", "
					+ handler.TIMESTAMP + ", " + handler.STATUS_MSG + ") VALUES ('" + obj.getmId() + "','"
					+ obj.getTitle() + "','" + obj.getSender().getId() + "','"
					+ Utils.convertListUserToIDString(obj.getReceiver()) + "','" + obj.getContentText() + "','"
					+ obj.getContentImage() + "','" + obj.getContentAudio() + "','" + obj.getContentVideo() + "','"
					+ obj.getTimestamp() + "','" + obj.getStatus() + "')";

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

	public void DeleteMessage(String mID) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "DELETE FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.MID + "='" + mID + "'";

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

	public void UpdateStatusMessage(String mID, String status) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "UPDATE " + handler.TABLE_MESSAGE + " SET " + handler.STATUS_MSG + "='" + status + "'"
					+ " WHERE " + handler.MID + "='" + mID + "'";

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

	public Message getMessage(String mID) {
		Connection conn = null;
		Statement stmt = null;
		Message message = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.MID + "='" + mID + "'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				message = new Message(rset.getString(handler.MID),
						Utils.getUserFromSender(rset.getString(handler.SENDER)),
						Utils.getListUserFromReciver(rset.getString(handler.RECIEVER)), rset.getString(handler.TITLE),
						rset.getString(handler.CONTENTTEXT), rset.getString(handler.CONTENTIMAGE),
						rset.getString(handler.CONTENTAUDIO), rset.getString(handler.CONTENTVIDEO),
						rset.getString(handler.STATUS_MSG), rset.getString(handler.TIMESTAMP));
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
		return message;
	}

	public User getSenderOfMessage(String mID) {
		User user = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT sender FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.MID + "='" + mID + "'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				user = Utils.getUserFromSender(rset.getString(handler.SENDER));
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

	public List<User> getRecieverOfMessage(String mID) {
		List<User> users = new ArrayList<User>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT " + handler.RECIEVER + " FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.MID
					+ "='" + mID + "'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				users = Utils.getListUserFromReciver(rset.getString(handler.RECIEVER));
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

	// Trả về list message mà user được nhận. (Trường hợp user clear cache và
	// request all old message.)
	public List<Message> getListMessageOfReciever(String userID) {
		List<Message> messages = new ArrayList<Message>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.RECIEVER + " like '%" + userID
					+ "%'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				Message message = new Message(rset.getString(handler.MID),
						Utils.getUserFromSender(rset.getString(handler.SENDER)),
						Utils.getListUserFromReciver(rset.getString(handler.RECIEVER)), rset.getString(handler.TITLE),
						rset.getString(handler.CONTENTTEXT), rset.getString(handler.CONTENTIMAGE),
						rset.getString(handler.CONTENTAUDIO), rset.getString(handler.CONTENTVIDEO),
						rset.getString(handler.STATUS_MSG), rset.getString(handler.TIMESTAMP));

				messages.add(message);
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
		return messages;
	}

	// Trả về list message mà user đã gửi. (Trường hợp user clear cache và
	// request all old message.)
	public List<Message> getListMessageOfSender(String userID) {
		List<Message> messages = new ArrayList<Message>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.SENDER + "='" + userID
					+ "'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				Message message = new Message(rset.getString(handler.MID),
						Utils.getUserFromSender(rset.getString(handler.SENDER)),
						Utils.getListUserFromReciver(rset.getString(handler.RECIEVER)), rset.getString(handler.TITLE),
						rset.getString(handler.CONTENTTEXT), rset.getString(handler.CONTENTIMAGE),
						rset.getString(handler.CONTENTAUDIO), rset.getString(handler.CONTENTVIDEO),
						rset.getString(handler.STATUS_MSG), rset.getString(handler.TIMESTAMP));

				messages.add(message);
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
		return messages;
	}

	// Trả về list message mà user được nhận. (Trường hợp kiểm tra và gửi các
	// message new)
	public List<Message> getListNewMessageOfReciever(String userID) {
		List<Message> messages = new ArrayList<Message>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.STATUS_MSG + "='"
					+ ContantsHomeMMS.MessageStatus.sending.name() + "' AND " + handler.RECIEVER + " like '%" + userID + "%'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				Message message = new Message(rset.getString(handler.MID),
						Utils.getUserFromSender(rset.getString(handler.SENDER)),
						Utils.getListUserFromReciver(rset.getString(handler.RECIEVER)), rset.getString(handler.TITLE),
						rset.getString(handler.CONTENTTEXT), rset.getString(handler.CONTENTIMAGE),
						rset.getString(handler.CONTENTAUDIO), rset.getString(handler.CONTENTVIDEO),
						rset.getString(handler.STATUS_MSG), rset.getString(handler.TIMESTAMP));

				messages.add(message);
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
		return messages;
	}

	// Trả về list message mà lien quan user. (Trường hợp sau khi login)
	public List<Message> getAllMessageRelateUser(String userID) {
		List<Message> messages = new ArrayList<Message>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_MESSAGE + " WHERE " + handler.SENDER + "='" + userID
					+ "' OR " + handler.RECIEVER + " like '%" + userID + "%'";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				Message message = new Message(rset.getString(handler.MID),
						Utils.getUserFromSender(rset.getString(handler.SENDER)),
						Utils.getListUserFromReciver(rset.getString(handler.RECIEVER)), rset.getString(handler.TITLE),
						rset.getString(handler.CONTENTTEXT), rset.getString(handler.CONTENTIMAGE),
						rset.getString(handler.CONTENTAUDIO), rset.getString(handler.CONTENTVIDEO),
						rset.getString(handler.STATUS_MSG), rset.getString(handler.TIMESTAMP));

				messages.add(message);
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
		return messages;
	}

	public List<Message> get12Message() {
		List<Message> messages = new ArrayList<Message>();
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = MysqlConnect.getConnectMysql();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM " + handler.TABLE_MESSAGE + " ORDER BY " + handler.TIMESTAMP + " DESC LIMIT 12";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				Message message = new Message(rset.getString(handler.MID),
						Utils.getUserFromSender(rset.getString(handler.SENDER)),
						Utils.getListUserFromReciver(rset.getString(handler.RECIEVER)), rset.getString(handler.TITLE),
						rset.getString(handler.CONTENTTEXT), rset.getString(handler.CONTENTIMAGE),
						rset.getString(handler.CONTENTAUDIO), rset.getString(handler.CONTENTVIDEO),
						rset.getString(handler.STATUS_MSG), rset.getString(handler.TIMESTAMP));

				messages.add(message);
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
		return messages;
	}

}

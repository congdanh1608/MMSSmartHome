package Socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.thesis.ServerGUI;

import Database.MessageModel;
import Database.UserModel;
import Database.Utils;
import Helper.JsonHelper;
import Model.Message;
import Model.User;
import SSH.PushFile;
import Socket.Server.ClientThread_;
import presistence.ContantsHomeMMS;
import presistence.ContantsHomeMMS.Command;
import presistence.ContantsHomeMMS.FirstStatus;

public class SocketControl {
	// public static String IP = "0.0.0.0", Name = "None",
	// Mac = "00:00:00:00:00:00";

	public User user;
	private Message message;
	private UserModel userModel;
	private MessageModel messageModel;
	private List<Message> messagesReceive;

	private ClientThread_ ct;
	private Socket socket;
	private ServerGUI sGui;

	private static PrintWriter printWriter;

	public SocketControl(ClientThread_ ct, Socket socket, ServerGUI sGui) {
		this.ct = ct;
		this.socket = socket;
		this.sGui = sGui;

		user = new User();
		message = new Message();
		userModel = new UserModel();
		messageModel = new MessageModel();
		messagesReceive = new ArrayList<Message>();
	}

	protected void getCommand(String msg) {
		String cmd = getCommandMsg(msg);
		if (cmd != null) {
			Command command = Command.valueOf(cmd);
			String userID = null, passUser = null;
			boolean firstRun = true;
			switch (command) {
			case HASREGISTER:
				// Load user ID + pass from client.
				userID = loadIDUser(msg);
				firstRun = loadFirstRun(msg);
				// Check user was registered?
				// database to client.
				ContantsHomeMMS.FirstStatus hasRegister = checkUserHasRegister(userID, firstRun);
				// If user was registered, sever will ask registered and send
				// user and check msg need send to client...
				switch (hasRegister) {
				case REGISTERED: {
					// Update status of user.
					userModel.UpdateStatusUser(userID, ContantsHomeMMS.UserStatus.online.name());
					System.out.println(userID + " online.");
					sendAskWasRegitered();
					user = userModel.getUser(userID);

					// Compare data and send client if have note need send to
					// client.
					checkNewMessageSendToClient();
				}
					break;

				case NOTREGISTERED: { // If was not registed, server will ask
										// not registed.
					sendAskWasNotRegistered();
				}
					break;

				case REQUESTLOGIN: { // Server will ask login - request client
										// login.
					sendAskRequestLogin();
				}
					break;

				default:
					break;
				}
				break;

			case LOGIN: // User send info login to Server. Then server will
						// check user, pass?
				userID = loadIDUser(msg);
				passUser = loadPassUser(msg);
				User userInDB = userModel.getUser(userID);
				if (userInDB.getPassword().equals(passUser)) { // Password true
																// -> Response
																// all msg +
																// list user;
					sendAskLoginSuccess();

					// Update status of user.
					userModel.UpdateStatusUser(userID, ContantsHomeMMS.UserStatus.online.name());
					System.out.println(user.getId() + " online.");

					user = userModel.getUser(userID);

					// Send all message relate with user.
					checkSendAllMessageToClient();
				} else { // Password false; --> Response wrong pass.
					sendAskLoginFail();
				}
				break;

			case INFOREGISTER:
				// Get user info register and save in database.
				user = getInfoClient(msg);
				// After register successfull, send List user in database.
				sendAskWasRegitered();
				System.out.println(user.getNameDisplay() + " login");
				break;

			case RECIEVER:
				// user1-user2-user3
				getInfoMessage(msg);
				break;

			case MSGKEY:
				// temp = getMessage(msg);
				// sGui.updateMessages(temp);
				// message.setContentText(temp);
				break;

			case PUSHKEY: // Client push to me(PI).
				// String tempA = getNameFileA(msg);
				// String tempP = getNameFileP(msg);
				// String tempV = getNameFileV(msg);
				// if (tempA != null) {
				// message.setContentAudio(tempA);
				// sGui.updateAudioFile(tempA);
				// }
				// if (tempP != null) {
				// message.setContentImage(tempP);
				// sGui.updatePhotoFile(tempP);
				// }
				// if (tempV != null) {
				// message.setContentVideo(tempV);
				// sGui.updateVideoFile(tempV);
				// }
				break;

			case PULLKEY: // Client pull from me(PI)
				break;

			case ENDNOTE:
				saveMessage(message);
				// update Message to GUI
				sGui.UpdateMessage();
				break;

			case DISCONNECT:
				// sGui.updateOnClear();
				// ct.close();
				break;

			default:
				break;
			}
		}
	}

	protected void SendMsg(Socket sk, String msg) {
		if (sk != null) {
			try {
				System.out.println("Server send " + msg);
				printWriter = new PrintWriter(sk.getOutputStream(), true);
				printWriter.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkUserIsReceive(String userID) {
		for (User receive : message.getReceiver()) {
			if (userID.equals(receive.getId())) {
				return true;
			}
		}
		return false;
	}

	public boolean checkReceiveEndNote(String msg) {
		String cmd = getCommandMsg(msg);
		if (cmd != null) {
			Command command = Command.valueOf(cmd);
			if (command.equals(Command.ENDNOTE)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkNewClientConnect(String msg) {
		String cmd = getCommandMsg(msg);
		if (cmd != null) {
			Command command = Command.valueOf(cmd);
			if (command.equals(ContantsHomeMMS.FirstStatus.REGISTERED)) {
				return true;
			}
		}
		return false;
	}

	// Set status for user.
	public void setStatusForUser(String status) {
		if (user != null) {
			userModel.UpdateStatusUser(user.getId(), status);
		}
	}

	// Set status for all user.
	public void setStatusForAllUser(String status) {
		userModel.UpdateStatusAllUser(status);
	}

	public void checkNewMessageSendToClient() {
		messagesReceive = checkNewNoteForClient(user.getId());
		if (messagesReceive.size() > 0) {
			for (int i = 0; i < messagesReceive.size(); i++) {
				System.out.println("Message " + i + " sending to " + user.getNameDisplay() + "at socket:" + socket.getPort());
				SendMsg(socket,
						createRecieverMessage(createMessageJson(messagesReceive.get(i), ContantsHomeMMS.isNewMsg)));
				// Check if mms has file attach then send it to
				// client.
				if (checkHasAttachFile(messagesReceive.get(i))) {
					String tempA = messagesReceive.get(i).getContentAudio();
					String tempP = messagesReceive.get(i).getContentImage();
					String tempV = messagesReceive.get(i).getContentVideo();
					if (tempA != null && !tempA.equals("") && !tempA.equals("null")) {
						System.out.println("Sent " + tempA);
						PushFile.sendFileToClient(tempA);
					}
					if (tempP != null && !tempP.equals("") && !tempP.equals("null")) {
						System.out.println("Sent " + tempP);
						PushFile.sendFileToClient(tempP);
					}
					if (tempV != null && !tempV.equals("") && !tempV.equals("null")) {
						System.out.println("Sent " + tempV);
						PushFile.sendFileToClient(tempV);
					}
				}

				// Update status of Message was sent.
				messageModel.UpdateStatusMessage(messagesReceive.get(i).getmId(),
						ContantsHomeMMS.MessageStatus.sent.name());
			}
		}
	}

	private void checkSendAllMessageToClient() {
		messagesReceive = getAllNoteForClient(user.getId());
		if (messagesReceive.size() > 0) {
			for (int i = 0; i < messagesReceive.size(); i++) {
				System.out.println("Message " + i + " sending to " + user.getNameDisplay());
				// Compare status sending or sent to know new msg or old msg.
				if (messagesReceive.get(i).getStatus().equals(ContantsHomeMMS.MessageStatus.sending)) {
					SendMsg(socket,
							createRecieverMessage(createMessageJson(messagesReceive.get(i), ContantsHomeMMS.isNewMsg)));
				} else {
					SendMsg(socket,
							createRecieverMessage(createMessageJson(messagesReceive.get(i), ContantsHomeMMS.isOldMsg)));
				}
				// Check if mms has file attach then send it to
				// client.
				if (checkHasAttachFile(messagesReceive.get(i))) {
					String tempA = messagesReceive.get(i).getContentAudio();
					String tempP = messagesReceive.get(i).getContentImage();
					String tempV = messagesReceive.get(i).getContentVideo();
					if (tempA != null && !tempA.equals("") && !tempA.equals("null")) {
						System.out.println("Sent " + tempA);
						PushFile.sendFileToClient(tempA);
					}
					if (tempP != null && !tempP.equals("") && !tempP.equals("null")) {
						System.out.println("Sent " + tempP);
						PushFile.sendFileToClient(tempP);
					}
					if (tempV != null && !tempV.equals("") && !tempV.equals("null")) {
						System.out.println("Sent " + tempV);
						PushFile.sendFileToClient(tempV);
					}
				}

				// Update status of Message was sent for message user was
				// receive;
				if (messagesReceive.get(i).getSender().getId().equals(user.getId())) {
					// do not change status
				} else {
					messageModel.UpdateStatusMessage(messagesReceive.get(i).getmId(),
							ContantsHomeMMS.MessageStatus.sent.name());
				}
				// wait between every send msg
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	protected String getCommandMsg(String msg) {
		String cmd = null;
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				cmd = jsonObj.isNull(ContantsHomeMMS.cmdKey) ? null : jsonObj.getString(ContantsHomeMMS.cmdKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cmd;
	}

	protected User getInfoClient(String msg) {
		User user = JsonHelper.loadJsonInfoRegister(msg);
		userModel.AddUser(user);
		return user;
	}

	protected void getInfoMessage(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				String mID = jsonObj.isNull(ContantsHomeMMS.mIDKey) ? null : jsonObj.getString(ContantsHomeMMS.mIDKey);
				String receiver = jsonObj.isNull(ContantsHomeMMS.recieverKey) ? null
						: jsonObj.getString(ContantsHomeMMS.recieverKey);
				String title = jsonObj.isNull(ContantsHomeMMS.titleKey) ? null
						: jsonObj.getString(ContantsHomeMMS.titleKey);
				String text = jsonObj.isNull(ContantsHomeMMS.contentTextKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentTextKey);
				String audio = jsonObj.isNull(ContantsHomeMMS.contentAudioKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentAudioKey);
				String video = jsonObj.isNull(ContantsHomeMMS.contentVideoKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentVideoKey);
				String image = jsonObj.isNull(ContantsHomeMMS.contentImageKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentImageKey);
				String time = jsonObj.isNull(ContantsHomeMMS.timeKey) ? null
						: jsonObj.getString(ContantsHomeMMS.timeKey);

				message.setmId(mID);
				message.setSender(user);
				message.setReceiver(Utils.getListUserFromReciver(receiver));
				message.setTitle(title);
				message.setContentText(text);
				message.setContentAudio(audio);
				message.setContentImage(image);
				message.setContentVideo(video);
				message.setTimestamp(time);
				message.setStatus(ContantsHomeMMS.sending);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected String getMessage(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				return jsonObj.isNull(ContantsHomeMMS.contentTextKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentTextKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getNameFileA(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				String fNameA = jsonObj.isNull(ContantsHomeMMS.contentAudioKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentAudioKey);
				if (fNameA != null)
					return fNameA;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getNameFileP(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				String fNameP = jsonObj.isNull(ContantsHomeMMS.contentImageKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentImageKey);
				if (fNameP != null)
					return fNameP;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getNameFileV(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				String fNameV = jsonObj.isNull(ContantsHomeMMS.contentVideoKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentVideoKey);
				if (fNameV != null)
					return fNameV;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * private RecieverNote checkNoteForClient(String reciever) { RecieverNote
	 * rNote = new RecieverNote(); rNote = JsonbBuilder.loadRecieverNote(); if
	 * (rNote.getReciever() != null && rNote.getReciever().equals(reciever)) {
	 * return rNote; } return null; }
	 */

	private List<Message> checkNewNoteForClient(String reciever) {
		return messageModel.getListNewMessageOfReciever(reciever);
	}

	private List<Message> getAllNoteForClient(String reciever) {
		return messageModel.getAllMessageRelateUser(reciever);
	}

	// create Message Json.
	private String createMessageJson(Message msg, String newStatus) {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.mIDKey, msg.getmId());
			jsonObj.put(ContantsHomeMMS.titleKey, msg.getTitle());
			jsonObj.put(ContantsHomeMMS.recieverKey, Utils.convertListUserToIDString(msg.getReceiver()));
			jsonObj.put(ContantsHomeMMS.senderKey, msg.getSender().getId());
			jsonObj.put(ContantsHomeMMS.contentTextKey, msg.getContentText());
			jsonObj.put(ContantsHomeMMS.contentImageKey, msg.getContentImage());
			jsonObj.put(ContantsHomeMMS.contentVideoKey, msg.getContentVideo());
			jsonObj.put(ContantsHomeMMS.contentAudioKey, msg.getContentAudio());
			jsonObj.put(ContantsHomeMMS.timeKey, msg.getTimestamp());
			jsonObj.put(ContantsHomeMMS.isNewMsgKey, newStatus);
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.RECIEVERNOTE);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// create String (Json) message contain Reciever info + Note json.
	protected String createRecieverMessage(String msg) {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.recieverNoteKey, msg);
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.RECIEVERNOTE);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkHasAttachFile(Message msg) {
		if ((msg.getContentAudio() != null && !msg.getContentAudio().equals("")
				&& !msg.getContentAudio().equals("null"))
				|| (msg.getContentImage() != null && !msg.getContentImage().equals("")
						&& !msg.getContentImage().equals("null"))
				|| (msg.getContentVideo() != null && !msg.getContentVideo().equals("")
						&& !msg.getContentVideo().equals("null")))
			return true;
		return false;
	}

	// Save in mysql database.
	protected void saveMessage(Message msg) {
		messageModel.AddMessage(msg);
		System.out.println("saved message in database.");
	}

	protected String loadIDUser(String msg) {
		return JsonHelper.loadJsonIDClient(msg);
	}

	protected String loadPassUser(String msg) {
		return JsonHelper.loadJsonPassClient(msg);
	}

	protected boolean loadFirstRun(String msg) {
		return JsonHelper.loadJsonFirstRun(msg);
	}

	private ContantsHomeMMS.FirstStatus checkUserHasRegister(String userID, Boolean firstRun) {
		if (userModel.getUser(userID) != null && !firstRun) { // User was
																// registered.
			return FirstStatus.REGISTERED;
		} else if (userModel.getUser(userID) != null && firstRun) { // User need
																	// to login.
			return FirstStatus.REQUESTLOGIN;
		} else
			return FirstStatus.NOTREGISTERED; // User was not registered.s
	}

	protected void sendAskLoginSuccess() {
		SendMsg(socket, JsonHelper.createJsonLoginSuccess());
	}

	protected void sendAskLoginFail() {
		SendMsg(socket, JsonHelper.createJsonLoginFail());
	}

	protected void sendAskWasRegitered() {
		SendMsg(socket, JsonHelper.createJsonRegisted());
	}

	protected void sendAskWasNotRegistered() {
		SendMsg(socket, JsonHelper.createJsonNotRegisted());
	}

	protected void sendAskRequestLogin() {
		SendMsg(socket, JsonHelper.createJsonRequestLogin());
	}
}

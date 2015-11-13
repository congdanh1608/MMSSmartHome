package Socket;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Database.DatabaseHandler;
import Database.MessageModel;
import Database.UserModel;
import Database.Utils;
import Helper.JsonHelper;
import Model.Message;
import Model.User;
import SSH.PushFile;
import presistence.ContantsHomeMMS;
import presistence.ContantsHomeMMS.Command;

public class SocketControl {
	// public static String IP = "0.0.0.0", Name = "None",
	// Mac = "00:00:00:00:00:00";

	private User user;
	private Message message;
	private DatabaseHandler handler;
	private UserModel userModel;
	private MessageModel messageModel;
	private List<Message> messagesReceive;

	private Server server;
	// private ServerGUI sGui;

	public SocketControl(Server server) {
		this.server = server;
		// this.sGui = sGui;

		user = new User();
		message = new Message();
		handler = new DatabaseHandler();
		userModel = new UserModel();
		messageModel = new MessageModel();
		messagesReceive = new ArrayList<Message>();
	}

	protected void getCommand(String msg) {
		String cmd = getCommandMsg(msg);
		if (cmd != null) {
			Command command = Command.valueOf(cmd);
			String temp = null;
			switch (command) {
			case HASREGISTER:
				// Load user ID from client.
				String userID = loadIDUser(msg);
				// Check user was registered?
				// database to client.
				boolean hasRegister = checkUserHasRegister(userID);
				// If user was registered, sever will ask registered and send user and check msg need send to client...
				if (hasRegister) {
					//Update status of user.
					userModel.UpdateStatusUser(userID, ContantsHomeMMS.UserStatus.online.name());
					sendAskWasRegitered();
					user = userModel.getUser(userID);
					
					// sGui.updateInfo(IP, Name, Mac);
					System.out.println(user.getNameDisplay() + " login");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Compare data and send client if have note need send to
					// client.
					messagesReceive = checkNoteForClient(user.getId());
					if (messagesReceive.size() > 0) {
						for (int i = 0; i < messagesReceive.size(); i++) {
							System.out.println("Message " + i +" sending to" + user.getNameDisplay());
							server.SendMsg(createRecieverMessage(createMessageJson(messagesReceive.get(i))));
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
							
							//Update status of Message was sent.
							messageModel.UpdateStatusMessage(messagesReceive.get(i).getmId(), 
									ContantsHomeMMS.MessageStatus.sent.name());
						}
					}
				}else{	// If was not registed, server will ask not registed.
					sendAskWasNotRegistered();
				}
				break;

			case INFOREGISTER:
				//Get user info register and save in database.
				user = getInfoClient(msg);
				//After register successfull, send List user in database.
				sendAskWasRegitered();			
				System.out.println(user.getNameDisplay() + " login");
				break;
				
			case RECIEVER:
				// user1-user2-user3
				getInfoMessage(msg);
				// sGui.updateReciever(temp);
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
				break;

			case DISCONNECT:
				// sGui.updateOnClear();
				break;

			default:
				break;
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

	private List<Message> checkNoteForClient(String reciever) {
		return messageModel.getListNewMessageOfReciever(reciever);
	}

	// create Message Json.
	private String createMessageJson(Message msg) {
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

	protected boolean checkUserHasRegister(String userID) {
		if (userModel.getUser(userID) != null) {
			return true;
		} else {
			return false;
		}
	}

	protected void sendAskWasRegitered() {
		server.SendMsg(JsonHelper.createJsonRegisted());
	}

	protected void sendAskWasNotRegistered() {
		server.SendMsg(JsonHelper.createJsonNotRegisted());
	}
}

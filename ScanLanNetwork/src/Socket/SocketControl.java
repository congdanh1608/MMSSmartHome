package Socket;

import org.json.JSONException;
import org.json.JSONObject;

import Database.JsonbBuilder;
import Model.Note;
import Model.RecieverNote;
import Socket.KeyString.Command;

import com.thesis.ServerGUI;

public class SocketControl {
	public static String IP = "0.0.0.0", Name = "None",
			Mac = "00:00:00:00:00:00";

	private Server server;
	private ServerGUI sGui;

	public SocketControl(Server server, ServerGUI sGui) {
		this.server = server;
		this.sGui = sGui;
	}

	protected void getCommand(String msg, Note note, RecieverNote recieveNote) {
		String cmd = getCommandMsg(msg);
		if (cmd != null) {
			Command command = Command.valueOf(cmd);
			String temp = null;
			switch (command) {
			case INFOKEY:
				getInfoClient(msg);
				sGui.updateInfo(IP, Name, Mac);
				note.setSender(IP);

				// compair data and send client if have note need send to
				// client.
				recieveNote = checkNoteForClient(IP);
				if (recieveNote != null) {
					server.SendMsg(createRecieverNote(recieveNote.getNoteJson()));
				}

				break;

			case RECIEVER:
				temp = getInfoReciever(msg);
				sGui.updateReciever(temp);
				note.setReciever(temp);
				break;

			case MSGKEY:
				temp = getMessage(msg);
				sGui.updateMessages(temp);
				note.setMessage(temp);
				break;

			case PUSHKEY: // Client push to me(PI).
				String tempA = getNameFileA(msg);
				String tempP = getNameFileP(msg);
				String tempV = getNameFileV(msg);
				if (tempA != null) {
					note.setFileAttachA(tempA);
					sGui.updateAudioFile(tempA);
				}
				if (tempP != null) {
					note.setFileAttachP(tempP);
					sGui.updateVideoFile(tempP);
				}
				if (tempV != null) {
					note.setFileAttachV(tempV);
					sGui.updatePhotoFile(tempV);
				}	
				break;

			case PULLKEY: // Client pull from me(PI)
				break;

			case ENDNOTE:
				saveNote(note);
				break;

			case DISCONNECT:
				sGui.updateOnClear();
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
				cmd = jsonObj.isNull(KeyString.cmdKey) ? null : jsonObj
						.getString(KeyString.cmdKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cmd;
	}

	protected void getInfoClient(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				IP = jsonObj.isNull(KeyString.IPKey) ? null : jsonObj
						.getString(KeyString.IPKey);
				Name = jsonObj.isNull(KeyString.NameKey) ? null : jsonObj
						.getString(KeyString.NameKey);
				Mac = jsonObj.isNull(KeyString.MacKey) ? null : jsonObj
						.getString(KeyString.MacKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected String getInfoReciever(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				return jsonObj.isNull(KeyString.recieverKey) ? null : jsonObj
						.getString(KeyString.recieverKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getMessage(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				return jsonObj.isNull(KeyString.msgKey) ? null : jsonObj
						.getString(KeyString.msgKey);
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
				String fNameA = jsonObj.isNull(KeyString.ActtachAKey) ? null
						: jsonObj.getString(KeyString.ActtachAKey);
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
				String fNameP = jsonObj.isNull(KeyString.ActtachPKey) ? null
						: jsonObj.getString(KeyString.ActtachPKey);
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
				String fNameV = jsonObj.isNull(KeyString.ActtachVKey) ? null
						: jsonObj.getString(KeyString.ActtachVKey);
				if (fNameV != null)
					return fNameV;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private RecieverNote checkNoteForClient(String reciever) {
		RecieverNote rNote = new RecieverNote();
		rNote = JsonbBuilder.loadRecieverNote();
		if (rNote.getReciever() != null && rNote.getReciever().equals(reciever)) {
			return rNote;
		}
		return null;
	}

	// create String (Json) message contain Reciever info + Note json.
	protected String createRecieverNote(String msg) {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(KeyString.recieverNoteKey, msg);
			jsonObj.put(KeyString.cmdKey, Command.RECIEVERNOTE);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected boolean saveNote(Note note) {
		return JsonbBuilder.saveNote(note);
	}

}

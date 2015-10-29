package Socket;

import org.json.JSONException;
import org.json.JSONObject;

import Socket.KeyString.Command;

import com.thesis.ServerGUI;

public class SocketControl {
	public static String IP = "0.0.0.0", Name = "None", Mac = "00:00:00:00:00:00";

	private Server server;
	private ServerGUI sGui;
	
	public SocketControl(Server server, ServerGUI sGui) {
		this.server = server;
		this.sGui = sGui;
	}

	protected void getCommand(String msg) {
		String cmd = getCommandMsg(msg);
		if (cmd != null) {
			Command command = Command.valueOf(cmd);
			switch (command) {
			case INFOKEY:
				getInfoClient(msg);
				sGui.updateInfo(IP, Name, Mac);
				break;
				
			case MSGKEY:
				sGui.updateMessages(getMessage(msg));
				break;
				
			case PUSHKEY:		//Client push to me(PI).
				sGui.updateFileName(getInfoFile(msg));
				break;
				
			case PULLKEY:		//Client pull from me(PI)
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
				cmd = jsonObj.isNull(KeyString.cmdKey) ? null : jsonObj.getString(KeyString.cmdKey);
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
				IP = jsonObj.isNull(KeyString.IPKey) ? null : jsonObj.getString(KeyString.IPKey);
				Name = jsonObj.isNull(KeyString.NameKey) ? null : jsonObj
						.getString(KeyString.NameKey);
				Mac = jsonObj.isNull(KeyString.MacKey) ? null : jsonObj.getString(KeyString.MacKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected String getMessage(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				return jsonObj.isNull(KeyString.msgKey) ? null : jsonObj.getString(KeyString.msgKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String getInfoFile(String msg) {
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				return jsonObj.isNull(KeyString.NameFileKey) ? null : jsonObj.getString(KeyString.NameFileKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}

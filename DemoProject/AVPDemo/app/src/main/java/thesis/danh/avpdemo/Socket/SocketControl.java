package thesis.danh.avpdemo.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import thesis.danh.avpdemo.Socket.KeyString.Command;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class SocketControl {
    public static String IP = null, Name = null, Mac = null;
    private Client client;

    public SocketControl(Client client) {
        this.client = client;
    }

    public SocketControl(Client client, String IP, String Name, String Mac) {
        this.client = client;
        this.IP = IP;
        this.Name = Name;
        this.Mac = Mac;
    }

    protected void getCommand() {
        String cmd = getCommandMsg(client.getMsg());
        if (cmd != null) {
            Command command = Command.valueOf(cmd);
            switch (command) {
                case INFOKEY:

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

    protected void sendCommandMsg(String cmd) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.cmdKey, cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //create String (Json) message contain info: files were sent...
    protected String createInfoMessage(String fName, Command cmd) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.NameFileKey, fName);
            jsonObj.put(KeyString.cmdKey, cmd);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) message contain message of user.
    protected String createSendMessage(String msg) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.msgKey, msg);
            jsonObj.put(KeyString.cmdKey, Command.MSGKEY);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) message contain info of client: IP, Mac , Name...
    protected String createInfoClient() {
        if (IP != null || Name != null || Mac != null) {
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put(KeyString.IPKey, IP);
                jsonObj.put(KeyString.NameKey, Name);
                jsonObj.put(KeyString.MacKey, Mac);
                jsonObj.put(KeyString.cmdKey, Command.INFOKEY);
                return jsonObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

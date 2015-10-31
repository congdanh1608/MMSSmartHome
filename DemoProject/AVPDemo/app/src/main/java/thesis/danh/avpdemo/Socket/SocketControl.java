package thesis.danh.avpdemo.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import thesis.danh.avpdemo.Database.NoteJson;
import thesis.danh.avpdemo.MainActivity;
import thesis.danh.avpdemo.Model.Device;
import thesis.danh.avpdemo.Model.Note;
import thesis.danh.avpdemo.Socket.KeyString.Command;
import thesis.danh.avpdemo.Utils;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class SocketControl {
    public static String IP = null, Name = null, Mac = null;
    private Client client;

    public SocketControl(Client client) {
        this.client = client;
    }

    public SocketControl(Client client, Device device) {
        this.client = client;
        this.IP = device.getIPAddress();
        this.Name = device.getDeviceName();
        this.Mac = device.getMacAddress();
    }

    protected void getCommand(String msg) {
        String cmd = getCommandMsg(msg);
        if (cmd != null) {
            Command command = Command.valueOf(cmd);
            switch (command) {
                case INFOKEY:
                    break;

                case RECIEVERNOTE:
                    String temp = getRecieverNote(msg);
                    Note tempNote = getRecieveInfo(temp);
                    //Update to textview
                    if (temp!=null) {
                        MainActivity.updateRecieveInfo(tempNote);
                        MainActivity.updateRecieverNote(temp);
                    }
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
    protected String createInfoMessage(String fName, KeyString.TypeFile typeFile, Command cmd) {
        try {
            JSONObject jsonObj = new JSONObject();

            switch (typeFile) {
                case Audio:
                    jsonObj.put(KeyString.ActtachAKey, fName);
                    break;

                case Video:
                    jsonObj.put(KeyString.ActtachVKey, fName);
                    break;

                case Photo:
                    jsonObj.put(KeyString.ActtachPKey, fName);
                    break;

                default:
                    break;
            }

            jsonObj.put(KeyString.cmdKey, cmd);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) message contain info reciever.
    protected String createInfoReciever(String msg) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.recieverKey, msg);
            jsonObj.put(KeyString.cmdKey, Command.RECIEVER);
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

    //create String (Json) message notice end note.
    protected String createNoticeEndNote() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.cmdKey, Command.ENDNOTE);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) message notice end note.
    protected String createNoticeDisconnect() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.cmdKey, Command.DISCONNECT);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //get reciever Note from server.
    protected String getRecieverNote(String msg) {
        try {
            JSONObject jsonObj = new JSONObject(msg);
            if (jsonObj != null) {
                return jsonObj.isNull(KeyString.recieverNoteKey) ? null : jsonObj.getString(KeyString.recieverNoteKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Note getRecieveInfo(String msg){
        Note note = NoteJson.loadNote(msg);
        return note;
    }
}

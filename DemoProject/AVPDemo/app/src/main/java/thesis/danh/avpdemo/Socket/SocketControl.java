package thesis.danh.avpdemo.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import thesis.danh.avpdemo.Database.NoteJson;
import thesis.danh.avpdemo.Database.Utils;
import thesis.danh.avpdemo.MainActivity;
import thesis.danh.avpdemo.Model.Message;
import thesis.danh.avpdemo.Model.User;
import thesis.danh.avpdemo.Socket.KeyString.Command;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class SocketControl {
    private Client client;
    private User user;

    public SocketControl(Client client) {
        this.client = client;
    }

    public SocketControl(Client client, User user) {
        this.client = client;
        this.user = user;
    }

    protected void getCommand(String msg) {
        String cmd = getCommandMsg(msg);
        if (cmd != null) {
            Command command = Command.valueOf(cmd);
            switch (command) {
                case INFOKEY:
                    break;

                case RECIEVERNOTE:

                    Message messageReceive = new Message();
                    messageReceive = getRecieveInfo(getRecieverNote(msg));
                    //Update to textview
                    if (messageReceive != null) {
                        MainActivity.updateRecieveInfo(messageReceive);
//                        MainActivity.updateRecieverNote(temp);
                        String tempA = messageReceive.getContentAudio();
                        String tempP = messageReceive.getContentImage();
                        String tempV = messageReceive.getContentVideo();
                        if (tempA != null && !tempA.equals("")) {
                            RecieveFile.recieveFileFromServer(tempA);
                        }
                        if (tempP != null && !tempP.equals("")) {
                            RecieveFile.recieveFileFromServer(tempP);
                        }
                        if (tempV != null && !tempV.equals("")) {
                            RecieveFile.recieveFileFromServer(tempV);
                        }
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
                    jsonObj.put(KeyString.contentAudioKey, fName);
                    break;

                case Video:
                    jsonObj.put(KeyString.contentVideoKey, fName);
                    break;

                case Photo:
                    jsonObj.put(KeyString.contentImageKey, fName);
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
    protected String createInfoMessage(Message message) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.mIDKey, message.getmId());
            jsonObj.put(KeyString.recieverKey, Utils.convertListUserToString(message.getReceiver()));
            jsonObj.put(KeyString.titleKey, message.getTitle());
            jsonObj.put(KeyString.contentTextKey, message.getContentText());
            jsonObj.put(KeyString.contentAudioKey, message.getContentAudio());
            jsonObj.put(KeyString.contentImageKey, message.getContentImage());
            jsonObj.put(KeyString.contentVideoKey, message.getContentVideo());
            jsonObj.put(KeyString.timeKey, message.getTimestamp());
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
            jsonObj.put(KeyString.contentTextKey, msg);
            jsonObj.put(KeyString.cmdKey, Command.MSGKEY);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) message contain info of client: IP, Mac , Name...
    protected String createInfoClient() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(KeyString.IDUserKey, user.getId());
            jsonObj.put(KeyString.NameKey, user.getNameDisplay());
            jsonObj.put(KeyString.PassKey, user.getPassword());
            jsonObj.put(KeyString.AvatarKey, user.getAvatar());
            jsonObj.put(KeyString.StatusKey, user.getStatus());
            jsonObj.put(KeyString.cmdKey, Command.INFOKEY);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
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

    private Message getRecieveInfo(String msg) {
        return NoteJson.loadNote(msg);
    }
}

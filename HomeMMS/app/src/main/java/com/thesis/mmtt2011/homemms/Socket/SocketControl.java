package com.thesis.mmtt2011.homemms.Socket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.fragment.InboxFragment;
import com.thesis.mmtt2011.homemms.helper.JsonHelper;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS.*;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;
import com.thesis.mmtt2011.homemms.persistence.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by CongDanh on 22/10/2015.
 */
public class SocketControl {
    private static Client client;
    private User myUser;
    private HomeMMSDatabaseHelper homeMMSDatabaseHelper;
    private Context context;

    public SocketControl(Client client) {
        this.client = client;
    }

    public SocketControl(Client client, User myUser, Context context) {
        this.client = client;
        this.myUser = myUser;
        homeMMSDatabaseHelper = new HomeMMSDatabaseHelper(context);
        this.context = context;
    }

    protected void getCommand(String msg) {
        String cmd = getCommandMsg(msg);
        if (cmd != null) {
            Command command = Command.valueOf(cmd);
            switch (command) {
                case HASREGISTER:
                    if (getHasRegister(msg)){
                        //if device was registed, server will send list myUser in database.
                        getListUserSaveToDatabase(msg);
                    }else {
                        //If deivce was not registed, myUser will show register acitvity and send info of client to server.
                        //Show register activity.
                        showDialogRegister(context);
                        //Send info of client to Server.
//                        client.SendMessageInfoOfClient();
                    }
                    break;

                case INFOREGISTER:
                    break;

                case RECIEVERNOTE:
                    Message messageReceive = new Message();
                    messageReceive = getRecieveInfo(getRecieverNote(msg));
                    //Update to textview
                    if (messageReceive != null) {
                        //Save message to database.
                        homeMMSDatabaseHelper.addMessage(messageReceive);
                        //Notify fragment myUser received a new message with mID.
                        InboxFragment.UpdateNewMessageReceive(messageReceive.getId());
                        Log.d("Received", "Updated new message to Inbox");

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
                cmd = jsonObj.isNull(ContantsHomeMMS.cmdKey) ? null : jsonObj.getString(ContantsHomeMMS.cmdKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cmd;
    }

    protected void sendCommandMsg(String cmd) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(ContantsHomeMMS.cmdKey, cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //create String (Json) message contain info: files were sent...
    protected String createInfoMessage(String fName, ContantsHomeMMS.TypeFile typeFile, Command cmd) {
        try {
            JSONObject jsonObj = new JSONObject();

            switch (typeFile) {
                case Audio:
                    jsonObj.put(ContantsHomeMMS.contentAudioKey, fName);
                    break;

                case Video:
                    jsonObj.put(ContantsHomeMMS.contentVideoKey, fName);
                    break;

                case Photo:
                    jsonObj.put(ContantsHomeMMS.contentImageKey, fName);
                    break;

                default:
                    break;
            }

            jsonObj.put(ContantsHomeMMS.cmdKey, cmd);
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
            jsonObj.put(ContantsHomeMMS.mIDKey, message.getId());
            jsonObj.put(ContantsHomeMMS.recieverKey, Utils.convertListUserToString(message.getReceiver()));
            jsonObj.put(ContantsHomeMMS.titleKey, message.getTitle());
            jsonObj.put(ContantsHomeMMS.contentTextKey, message.getContentText());
            jsonObj.put(ContantsHomeMMS.contentAudioKey, message.getContentAudio());
            jsonObj.put(ContantsHomeMMS.contentImageKey, message.getContentImage());
            jsonObj.put(ContantsHomeMMS.contentVideoKey, message.getContentVideo());
            jsonObj.put(ContantsHomeMMS.timeKey, message.getTimestamp());
            jsonObj.put(ContantsHomeMMS.cmdKey, Command.RECIEVER);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) message contain message of myUser.
    protected String createSendMessage(String msg) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(ContantsHomeMMS.contentTextKey, msg);
            jsonObj.put(ContantsHomeMMS.cmdKey, Command.MSGKEY);
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //create String (Json) contain Mac to send to Pi when first connect.
    protected String createJsonIDClient(){
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(ContantsHomeMMS.IDUserKey, myUser.getId());
            jsonObj.put(ContantsHomeMMS.cmdKey, Command.HASREGISTER);
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
            jsonObj.put(ContantsHomeMMS.IDUserKey, myUser.getId());
            jsonObj.put(ContantsHomeMMS.NameKey, myUser.getNameDisplay());
            jsonObj.put(ContantsHomeMMS.PassKey, myUser.getPassword());
            jsonObj.put(ContantsHomeMMS.AvatarKey, myUser.getAvatar());
            jsonObj.put(ContantsHomeMMS.StatusKey, myUser.getStatus());
            jsonObj.put(ContantsHomeMMS.cmdKey, Command.INFOREGISTER);
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
            jsonObj.put(ContantsHomeMMS.cmdKey, Command.ENDNOTE);
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
            jsonObj.put(ContantsHomeMMS.cmdKey, Command.DISCONNECT);
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
                return jsonObj.isNull(ContantsHomeMMS.recieverNoteKey) ? null : jsonObj.getString(ContantsHomeMMS.recieverNoteKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Message getRecieveInfo(String msg) {
        return JsonHelper.loadNote(msg, context);
    }

    private boolean getHasRegister(String msg){
        return JsonHelper.loadHasRegister(msg);
    }

    private void getListUserSaveToDatabase(String msg){
        List<User>  userList = JsonHelper.loadUserDatabase(msg);
        for (User u : userList) {
            if (homeMMSDatabaseHelper.getUserWith_(context, u.getId())!=null) {
                homeMMSDatabaseHelper.UpdateUser_(u);
            }else{
                homeMMSDatabaseHelper.addUser(u);
            }
        }
    }




    public static void showDialogRegister(Context context){
        final EditText name = new EditText(context);
        final EditText pass = new EditText(context);
        final EditText avatar = new EditText(context);
        name.setText("Danh");
        pass.setText("123");
        avatar.setText("avatar.png");
        new AlertDialog.Builder(context)
                .setView(name)
                .setView(pass)
                .setView(avatar)
                .setTitle("Register form")
                .setMessage("input your info")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.myUser.setNameDisplay(name.getText().toString());
                        MainActivity.myUser.setAvatar(avatar.getText().toString());
                        MainActivity.myUser.setPassword(avatar.getText().toString());
                        client.SendMessageInfoOfClient();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}

package com.thesis.mmtt2011.homemms.helper;

import android.content.Context;
import android.util.Log;

import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xpie on 11/3/2015.
 */
public class JsonHelper {
    private static final String TAG = "JsonHelper";

    private JsonHelper() {
        //no instance
    }

    /**
     * Creates a String array out of a json array.
     *
     * @param json The String containing the json array.
     * @return An array with the extracted strings or an
     * empty String array if an exception occurred.
     */
    public static String[] jsonArrayToStringArray(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            String[] stringArray = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                stringArray[i] = jsonArray.getString(i);
            }
            return stringArray;
        } catch (JSONException e) {
            Log.e(TAG, "Error during Json processing: ", e);
        }
        return new String[0];
    }

    /**
     * Creates an int array out of a json array.
     *
     * @param json The String containing the json array.
     * @return An array with the extracted integers or an
     * empty int array if an exception occurred.
     */
    public static int[] jsonArrayToIntArray(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            int[] intArray = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                intArray[i] = jsonArray.getInt(i);
            }
            return intArray;
        } catch (JSONException e) {
            Log.e(TAG, "Error during Json processing: ", e);
        }
        return new int[0];
    }

    public static Message loadNote(String msg, Context context) {
        Message message = new Message();
        if (msg != null) {
            try {
                JSONObject jsonObj = new JSONObject(msg);

                //Get list receiver
                String receiversStr = jsonObj.isNull(ContantsHomeMMS.recieverKey) ? null : jsonObj.getString(ContantsHomeMMS.recieverKey);
                List<User> receivers = new ArrayList<User>();
                receivers = HomeMMSDatabaseHelper.getListReceiver(context, receiversStr);

                //Get Sender myUser.
                String senderStr = jsonObj.isNull(ContantsHomeMMS.senderKey) ? null : jsonObj.getString(ContantsHomeMMS.senderKey);
                User sender = new User();
                sender = HomeMMSDatabaseHelper.getUser(context, senderStr);

                message.setMId(jsonObj.isNull(ContantsHomeMMS.mIDKey) ? null : jsonObj.getString(ContantsHomeMMS.mIDKey));
                message.setTitle(jsonObj.isNull(ContantsHomeMMS.titleKey) ? null : jsonObj.getString(ContantsHomeMMS.titleKey));
                message.setContentText(jsonObj.isNull(ContantsHomeMMS.contentTextKey) ? null
                        : jsonObj.getString(ContantsHomeMMS.contentTextKey));
                message.setContentAudio(jsonObj.isNull(ContantsHomeMMS.contentAudioKey) ? null
                        : jsonObj.getString(ContantsHomeMMS.contentAudioKey));
                message.setContentImage(jsonObj.isNull(ContantsHomeMMS.contentImageKey) ? null
                        : jsonObj.getString(ContantsHomeMMS.contentImageKey));
                message.setContentVideo(jsonObj.isNull(ContantsHomeMMS.contentVideoKey) ? null
                        : jsonObj.getString(ContantsHomeMMS.contentVideoKey));
//                Dung ham convert from String receiver to List User receiver.
                message.setReceiver(receivers);
                message.setSender(sender);
                message.setTimestamp(jsonObj.isNull(ContantsHomeMMS.timeKey) ? null
                        : jsonObj.getString(ContantsHomeMMS.timeKey));
                //Set Message status of receive (received or read)
                String msgStatus = jsonObj.isNull(ContantsHomeMMS.isNewMsgKey) ? ContantsHomeMMS.isNewMsg
                        : jsonObj.getString(ContantsHomeMMS.isNewMsgKey);
                if (msgStatus.equals(ContantsHomeMMS.isNewMsg)) {
                    message.setStatus(ContantsHomeMMS.MessageStatus.received.name());
                }else if (msgStatus.equals(ContantsHomeMMS.isOldMsg)){
                    message.setStatus(ContantsHomeMMS.MessageStatus.read.name());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return message;
    }

    public static boolean loadLogin(String msg) {
        if (msg != null) {
            try {
                JSONObject jsonObj = new JSONObject(msg);
                String login = jsonObj.isNull(ContantsHomeMMS.loginKey) ? null : jsonObj.getString(ContantsHomeMMS.loginKey);
                if (login != null && login.equals(ContantsHomeMMS.loginSuccess))     //Login successful.
                    return true;
                else if (login != null && login.equals(ContantsHomeMMS.loginFail))  //Login fail.
                    return false;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public static ContantsHomeMMS.FirstStatus loadHasRegister(String msg) {
        if (msg != null) {
            try {
                JSONObject jsonObj = new JSONObject(msg);
                String hasRegister = jsonObj.isNull(ContantsHomeMMS.registerKey) ? null : jsonObj.getString(ContantsHomeMMS.registerKey);
                if (hasRegister != null) {
                    if (hasRegister.equals(ContantsHomeMMS.registered))     //Was Registered.
                        return ContantsHomeMMS.FirstStatus.REGISTERED;
                    else if (hasRegister.equals(ContantsHomeMMS.notRegistered))//Was not Registered.
                        return ContantsHomeMMS.FirstStatus.NOTREGISTERED;
                    else if (hasRegister.equals(ContantsHomeMMS.requestloginKey)) {          //Was Login
                        return ContantsHomeMMS.FirstStatus.REQUESTLOGIN;
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<User> loadUserDatabase(String msg) {
        List<User> userList = new ArrayList<User>();
        if (msg != null) {
            try {
                JSONObject jsonObj = new JSONObject(msg);
                String usersJson = jsonObj.isNull(ContantsHomeMMS.userDatabase) ? null : jsonObj.getString(ContantsHomeMMS.userDatabase);
                if (usersJson != null) {
                    JSONArray users = new JSONArray(usersJson);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = new JSONObject(users.getString(i));
                        String IDUser = user.getString(ContantsHomeMMS.IDUserKey);
                        String NameUser = user.getString(ContantsHomeMMS.NameKey);
                        String AvatarUser = user.getString(ContantsHomeMMS.AvatarKey);
                        String StatusUser = user.getString(ContantsHomeMMS.StatusKey);

                        User u = new User(IDUser, NameUser, null, AvatarUser, StatusUser);
                        userList.add(u);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return userList;
    }
}

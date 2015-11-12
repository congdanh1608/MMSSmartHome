package thesis.danh.avpdemo.Database;

import org.json.JSONException;
import org.json.JSONObject;

import thesis.danh.avpdemo.Model.Message;
import thesis.danh.avpdemo.Socket.KeyString;

/**
 * Created by CongDanh on 30/10/2015.
 */
public class NoteJson {

    public static Message loadNote(String msg) {
        Message message = new Message();
        if (msg != null) {
            try {
                JSONObject jsonObj = new JSONObject(msg);
                message.setmId(jsonObj.isNull(KeyString.mIDKey) ? null : jsonObj.getString(KeyString.mIDKey));
                message.setTitle(jsonObj.isNull(KeyString.titleKey) ? null : jsonObj.getString(KeyString.titleKey));
                message.setContentText(jsonObj.isNull(KeyString.contentTextKey) ? null
                        : jsonObj.getString(KeyString.contentTextKey));
                message.setContentAudio(jsonObj.isNull(KeyString.contentAudioKey) ? null
                        : jsonObj.getString(KeyString.contentAudioKey));
                message.setContentImage(jsonObj.isNull(KeyString.contentImageKey) ? null
                        : jsonObj.getString(KeyString.contentImageKey));
                message.setContentVideo(jsonObj.isNull(KeyString.contentVideoKey) ? null
                        : jsonObj.getString(KeyString.contentVideoKey));
                //Dung ham convert from String receiver to List User receiver.
//                message.setReceiver(jsonObj.isNull(KeyString.recieverKey) ? null
//                        : jsonObj.getString(KeyString.recieverKey));
//                message.setSender(jsonObj.isNull(KeyString.senderKey) ? null
//                        : jsonObj.getString(KeyString.senderKey));
                message.setTimestamp(jsonObj.isNull(KeyString.timeKey) ? null
                        : jsonObj.getString(KeyString.timeKey));
                message.setStatus("received");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return message;
    }
}

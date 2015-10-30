package thesis.danh.avpdemo.Database;

import org.json.JSONException;
import org.json.JSONObject;

import thesis.danh.avpdemo.Model.Note;
import thesis.danh.avpdemo.Socket.KeyString;

/**
 * Created by CongDanh on 30/10/2015.
 */
public class NoteJson {

    public static Note loadNote(String msg) {
        Note note = new Note();
        if (msg != null) {
            try {
                JSONObject jsonObj = new JSONObject(msg);
                note.setTitle(jsonObj.isNull(KeyString.titleKey) ? null
                        : jsonObj.getString(KeyString.titleKey));
                note.setMessage(jsonObj.isNull(KeyString.msgKey) ? null
                        : jsonObj.getString(KeyString.msgKey));
                note.setFileAttachA(jsonObj.isNull(KeyString.ActtachAKey) ? null
                        : jsonObj.getString(KeyString.ActtachAKey));
                note.setFileAttachP(jsonObj.isNull(KeyString.ActtachPKey) ? null
                        : jsonObj.getString(KeyString.ActtachPKey));
                note.setFileAttachV(jsonObj.isNull(KeyString.ActtachVKey) ? null
                        : jsonObj.getString(KeyString.ActtachVKey));
                note.setReciever(jsonObj.isNull(KeyString.recieverKey) ? null
                        : jsonObj.getString(KeyString.recieverKey));
                note.setSender(jsonObj.isNull(KeyString.senderKey) ? null
                        : jsonObj.getString(KeyString.senderKey));
                note.setTime(jsonObj.isNull(KeyString.timeKey) ? null
                        : jsonObj.getString(KeyString.timeKey));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return note;
    }
}

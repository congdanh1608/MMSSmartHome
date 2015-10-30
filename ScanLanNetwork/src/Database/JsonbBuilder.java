package Database;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.thesis.Utils;

import Model.Note;
import Model.RecieverNote;
import Socket.KeyString;

public class JsonbBuilder {

	public static boolean saveNote(Note note) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(KeyString.titleKey,
					note.getTitle() != null ? note.getTitle() : "");
			jsonObj.put(KeyString.msgKey,
					note.getMessage() != null ? note.getMessage() : "");
			jsonObj.put(KeyString.ActtachAKey,
					note.getFileAttachA() != null ? note.getFileAttachA() : "");
			jsonObj.put(KeyString.ActtachVKey,
					note.getFileAttachV() != null ? note.getFileAttachV() : "");
			jsonObj.put(KeyString.ActtachPKey,
					note.getFileAttachP() != null ? note.getFileAttachP() : "");
			jsonObj.put(KeyString.timeKey, Utils.getCurrentTime());
			jsonObj.put(KeyString.senderKey,
					note.getSender() != null ? note.getSender() : "");
			jsonObj.put(KeyString.recieverKey,
					note.getReciever() != null ? note.getReciever() : "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (jsonObj != null) {
			System.out.println(jsonObj.toString());
			Utils.createFile("data.json", jsonObj.toString());
			return true;
		}

		return false;
	}

	public static Note loadNote() {
		Note note = new Note();
		String msg = Utils.readFile("data.json");
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
	
	public static RecieverNote loadRecieverNote() {
		RecieverNote rNote = new RecieverNote();
		String msg = Utils.readFile("data.json");
		if (msg != null) {
			try {
				JSONObject jsonObj = new JSONObject(msg);
				rNote.setReciever(jsonObj.isNull(KeyString.recieverKey) ? null
						: jsonObj.getString(KeyString.recieverKey));
				rNote.setNoteJson(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rNote;
	}
}

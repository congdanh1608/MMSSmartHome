package Database;

import org.json.JSONException;
import org.json.JSONObject;

import Model.Message;
import Model.RecieverNote;
import presistence.ContantsHomeMMS;

import com.thesis.UtilsMain;

public class JsonbBuilder {

	public static boolean saveNote(Message note) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(ContantsHomeMMS.titleKey,
					note.getTitle() != null ? note.getTitle() : "");
			jsonObj.put(ContantsHomeMMS.contentTextKey,
					note.getContentText() != null ? note.getContentText() : "");
			jsonObj.put(ContantsHomeMMS.contentAudioKey,
					note.getContentAudio() != null ? note.getContentAudio() : "");
			jsonObj.put(ContantsHomeMMS.contentVideoKey,
					note.getContentVideo() != null ? note.getContentVideo() : "");
			jsonObj.put(ContantsHomeMMS.contentImageKey,
					note.getContentImage() != null ? note.getContentImage() : "");
			jsonObj.put(ContantsHomeMMS.timeKey, UtilsMain.getCurrentTime());
			jsonObj.put(ContantsHomeMMS.senderKey,
					note.getSender() != null ? note.getSender() : "");
			jsonObj.put(ContantsHomeMMS.recieverKey,
					note.getReceiver() != null ? note.getReceiver() : "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (jsonObj != null) {
			System.out.println(jsonObj.toString());
			UtilsMain.createFile("data.json", jsonObj.toString());
			return true;
		}

		return false;
	}

	public static Message loadNote() {
		Message note = new Message();
		String msg = UtilsMain.readFile("data.json");
		if (msg != null) {
			try {
				JSONObject jsonObj = new JSONObject(msg);
				note.setTitle(jsonObj.isNull(ContantsHomeMMS.titleKey) ? null
						: jsonObj.getString(ContantsHomeMMS.titleKey));
				note.setContentText(jsonObj.isNull(ContantsHomeMMS.contentTextKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentTextKey));
				note.setContentAudio(jsonObj.isNull(ContantsHomeMMS.contentAudioKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentAudioKey));
				note.setContentImage(jsonObj.isNull(ContantsHomeMMS.contentImageKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentImageKey));
				note.setContentVideo(jsonObj.isNull(ContantsHomeMMS.contentVideoKey) ? null
						: jsonObj.getString(ContantsHomeMMS.contentVideoKey));
				note.setReceiver(jsonObj.isNull(ContantsHomeMMS.recieverKey) ? null
						: Database.Utils.getListUserFromReciver(jsonObj.getString(ContantsHomeMMS.recieverKey)));
				note.setSender(jsonObj.isNull(ContantsHomeMMS.senderKey) ? null
						: Database.Utils.getUserFromSender(jsonObj.getString(ContantsHomeMMS.senderKey)));
				note.setTimestamp(jsonObj.isNull(ContantsHomeMMS.timeKey) ? null : jsonObj
						.getString(ContantsHomeMMS.timeKey));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return note;
	}

	public static RecieverNote loadRecieverNote() {
		RecieverNote rNote = new RecieverNote();
		String msg = UtilsMain.readFile("data.json");
		if (msg != null) {
			try {
				JSONObject jsonObj = new JSONObject(msg);
				rNote.setReciever(jsonObj.isNull(ContantsHomeMMS.recieverKey) ? null
						: jsonObj.getString(ContantsHomeMMS.recieverKey));
				rNote.setNoteJson(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rNote;
	}
}

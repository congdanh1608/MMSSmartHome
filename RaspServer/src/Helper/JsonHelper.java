package Helper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Model.User;

import com.mysql.fabric.xmlrpc.base.Array;

import Database.UserModel;
import presistence.ContantsHomeMMS;
import presistence.ContantsHomeMMS.Command;

public class JsonHelper {
	private static UserModel userModel = new UserModel();
	
	public static String loadJsonIDClient(String msg) {
		String IDUser = null;
		if (msg != null) {
			try {
				JSONObject jsonObj = new JSONObject(msg);
				IDUser = jsonObj.isNull(ContantsHomeMMS.IDUserKey) ? null
						: jsonObj.getString(ContantsHomeMMS.IDUserKey);
					return IDUser;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String loadJsonPassClient(String msg) {
		String PassUser = null;
		if (msg != null) {
			try {
				JSONObject jsonObj = new JSONObject(msg);
				PassUser = jsonObj.isNull(ContantsHomeMMS.PassKey) ? null
						: jsonObj.getString(ContantsHomeMMS.PassKey);
					return PassUser;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static boolean loadJsonFirstRun(String msg) {
		boolean firstRun = true;
		if (msg != null) {
			try {
				JSONObject jsonObj = new JSONObject(msg);
				firstRun = jsonObj.isNull(ContantsHomeMMS.firstRun) ? true
						: jsonObj.getBoolean(ContantsHomeMMS.firstRun);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return firstRun;
	}
	
	public static User loadJsonInfoRegister(String msg){
		User user = new User();
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				String id = jsonObj.isNull(ContantsHomeMMS.IDUserKey) ? null
						: jsonObj.getString(ContantsHomeMMS.IDUserKey);
				user.setId(id);
				user.setNameDisplay(
						jsonObj.isNull(ContantsHomeMMS.NameKey) ? null : jsonObj.getString(ContantsHomeMMS.NameKey));
				user.setPassword(
						jsonObj.isNull(ContantsHomeMMS.PassKey) ? null : jsonObj.getString(ContantsHomeMMS.PassKey));
				user.setAvatar(jsonObj.isNull(ContantsHomeMMS.AvatarKey) ? null
						: jsonObj.getString(ContantsHomeMMS.AvatarKey));
				user.setStatus(jsonObj.isNull(ContantsHomeMMS.StatusKey) ? null
						: jsonObj.getString(ContantsHomeMMS.StatusKey));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public static String loadJsonIDMessageAttach(String msg){
		String mID = null;
		try {
			JSONObject jsonObj = new JSONObject(msg);
			if (jsonObj != null) {
				mID = jsonObj.isNull(ContantsHomeMMS.mIDKey) ? null
						: jsonObj.getString(ContantsHomeMMS.mIDKey);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mID;
	}	
	
	public static String createJsonLoginSuccess(String userID){
		List<User> users = new ArrayList<User>();
		users = userModel.getAllUser();
		
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.loginKey, ContantsHomeMMS.loginSuccess);
			
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < users.size(); i++){
				 JSONObject user = new JSONObject();
				 user.put(ContantsHomeMMS.IDUserKey, users.get(i).getId());
                 user.put(ContantsHomeMMS.NameKey, users.get(i).getNameDisplay());
                 user.put(ContantsHomeMMS.AvatarKey, users.get(i).getAvatar());
                 user.put(ContantsHomeMMS.StatusKey, users.get(i).getStatus());
                 
                 jsonArray.put(user);
			}
			jsonObj.put(ContantsHomeMMS.userDatabase, jsonArray);
			
			jsonObj.put(ContantsHomeMMS.RoleKey, userModel.getRoleOfUser(userID));
			
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.LOGIN);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonLoginFail(){
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.loginKey, ContantsHomeMMS.loginFail);
			
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.LOGIN);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonChangeProfileSuccess(){
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.ResultEditProfile, ContantsHomeMMS.editSuccess);
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.PROFILEEDIT);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonChangeProfileFail(){
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.ResultEditProfile, ContantsHomeMMS.editFail);
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.PROFILEEDIT);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonRegisted(String userID){
		List<User> users = new ArrayList<User>();
		users = userModel.getAllUser();
		
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.registerKey, ContantsHomeMMS.registered);
			
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < users.size(); i++){
				 JSONObject user = new JSONObject();
				 user.put(ContantsHomeMMS.IDUserKey, users.get(i).getId());
                 user.put(ContantsHomeMMS.NameKey, users.get(i).getNameDisplay());
                 user.put(ContantsHomeMMS.AvatarKey, users.get(i).getAvatar());
                 user.put(ContantsHomeMMS.StatusKey, users.get(i).getStatus());
                 
                 jsonArray.put(user);
			}
			jsonObj.put(ContantsHomeMMS.userDatabase, jsonArray);
			
			jsonObj.put(ContantsHomeMMS.RoleKey, userModel.getRoleOfUser(userID));
			
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.HASREGISTER);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonNotRegisted(){
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.registerKey, ContantsHomeMMS.notRegistered);
			
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.HASREGISTER);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonRequestLogin(){
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.registerKey, ContantsHomeMMS.requestLoginKey);
			
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.HASREGISTER);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createJsonAskAcceptSendFileAttach(){
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(ContantsHomeMMS.cmdKey, Command.RECIEVEFILEATTACH);
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}

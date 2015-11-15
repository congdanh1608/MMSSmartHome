package Database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Model.User;

public class Utils {
	static UserModel userModel = new UserModel();

	public static List<User> getListUserFromReciver(String reciever) {
		List<User> users = new ArrayList<User>();
		if (reciever != null && !reciever.equals("")) {
			String[] macReciever = reciever.split("-");
			if (macReciever.length > 0) {
				for (String userID : macReciever) {
					users.add(userModel.getUser(userID));
				}
			}
		}

		return users;
	}

	public static User getUserFromSender(String userID) {
		User user = null;
		if (userID != null && !userID.equals("")) {
			user = userModel.getUser(userID);
		}
		return user;
	}
	
	public static String convertListUserToIDString(List<User> users){
		String reciever = "";
		for (int i = 0; i < users.size(); i++){
			reciever += users.get(i).getId();
			if (i+1 <  users.size()-1){
				reciever +="-";
			}
		}
		return reciever;
	}
	
	public String getCurrentTime() {
        String time = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        time = dateFormat.format(date); // 20151030_075959
        return time;
    }

    public static String convertStringToDate(String dateString) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        DateFormat dfExport = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        try {
            date = df.parse(dateString);
            return dfExport.format(date).toString();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

}

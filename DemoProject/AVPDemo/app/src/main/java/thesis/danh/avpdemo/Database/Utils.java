package thesis.danh.avpdemo.Database;

import java.util.ArrayList;
import java.util.List;

import thesis.danh.avpdemo.Model.User;

/**
 * Created by CongDanh on 06/11/2015.
 */
public class Utils {
    public static String convertListUserToString(List<User> users){
        String reciever = "";
        for (int i = 0; i < users.size(); i++){
            reciever += users.get(i).getId();
            if (i+1 <  users.size()-1){
                reciever +="-";
            }
        }
        return reciever;
    }
}


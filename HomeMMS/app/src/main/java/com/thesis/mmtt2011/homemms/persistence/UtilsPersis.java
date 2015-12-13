package com.thesis.mmtt2011.homemms.persistence;

import com.thesis.mmtt2011.homemms.model.User;

import java.util.List;

/**
 * Created by CongDanh on 06/11/2015.
 */
public class UtilsPersis {
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


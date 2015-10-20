package com.thesis.mmtt2011.homemms.model;

/**
 * Created by Xpie on 10/20/2015.
 */
public class User {

    String nameDisplay;
    String id; // save MAC address of user's device
    String password;
    String avatar; //link to file image

    public User(String _nameDisplay, String _id, String _password,String _avatar) {
        nameDisplay = _nameDisplay;
        id = _id;
        password = _password;
        avatar = _avatar;
    }

    public String getId() {
        return id;
    }

    public String getNameDisplay() {
        return nameDisplay;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }
}

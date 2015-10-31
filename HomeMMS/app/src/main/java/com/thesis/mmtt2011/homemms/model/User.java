package com.thesis.mmtt2011.homemms.model;

/**
 * Created by Xpie on 10/20/2015.
 */
public class User {

    String id; // save MAC address of user's device
    String nameDisplay;
    String password;
    String avatar; //link to file image
    String status; //online | offline

    public User(String _id, String _nameDisplay,  String _password, String _avatar, String _status) {
        id = _id;
        nameDisplay = _nameDisplay;
        password = _password;
        avatar = _avatar;
        status = _status;
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

    public String getStatus() {
        return status;
    }
}

package thesis.danh.avpdemo.Model;

public class User {

    String id; // save MAC address of user's device
    String nameDisplay;
    String password;
    String avatar; //link to file image
    String status; //online | offline

    public User(){
        id = null;
        nameDisplay = null;
        password = null;
        avatar = null;
        status = null;
    }

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

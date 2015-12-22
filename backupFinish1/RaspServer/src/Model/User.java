package Model;

public class User {
	private String id; // save MAC address of user's device
	private String nameDisplay;
	private String password;
	private String avatar; // link to file image
	private String status; // online | offline
	
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
    
    public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
    
    public void setId(String id) {
		this.id = id;
	}
    
    public void setNameDisplay(String nameDisplay) {
		this.nameDisplay = nameDisplay;
	}
    
    public void setPassword(String password) {
		this.password = password;
	}
    
    public void setStatus(String status) {
		this.status = status;
	}
}

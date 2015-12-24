package presistence;

public class ContantsHomeMMS {
	public static final String NameApp = "HOMEMMS", NameCacheApp = "Cache";
	public static final String AppFolder = "/home/pi/" + NameApp; 
	public static final String AppCacheFolder = AppFolder + "/" + NameCacheApp;

	// left
	public static final String cmdKey = "cmdKey", IDUserKey = "IDUSERKEY", NameKey = "NAMEKEY", PassKey = "PASSWORDKEY",
			AvatarKey = "AVATARKEY", StatusKey = "STATUSKEY", recieverNoteKey = "RECIEVERNOTEKEY", OldPassKey = "OLDPASSWORD";

	// left
	public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY",
			mIDKey = "MID", titleKey = "TITLENOTE", contentTextKey = "MSG", contentAudioKey = "ATTACHAKEY",
			contentVideoKey = "ATTACHVKEY", contentImageKey = "ATTACHPKEY";
	
	//Result change password
	public static final String ResultEditProfile = "EDITPROFILE", editSuccess = "EDITSUCCESS", editFail = "EDITFAIL";
	
	//Message is New
	public static final String isNewMsgKey = "NEWMSGKEY", isNewMsg =  "NEWMSG", isOldMsg = "OLDMSG";
	
	//Role of user
	public static final String RoleKey = "ROLE";

	// right
	public enum Command {
		LOGIN, HASREGISTER, INFOREGISTER, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT,
		RECIEVERNOTE, RECIEVEFILEATTACH, DELETEMSG, PROFILEEDIT, FORGETPW, RESTORENORMAL;
	}

	// Status of first load app
	public enum FirstStatus {
		REQUESTLOGIN, REGISTERED, NOTREGISTERED;
	}
	
	// Type of user
	public enum UserRole {
		user, admin;
	}

	public static final String loginKey = "LOGIN", loginSuccess = "LOGINSUCCESS", loginFail = "LOGINFAIL";

	// Register
	public static final String registerKey = "REGISTER", requestLoginKey = "ReqLOGIN", registered = "REGISTERED",
			notRegistered = "NOTREGISTERED", userDatabase = "USERDATABASE", firstRun = "FIRSTRUN";;

	// status of messages
	public static final String sending = "sending", sent = "sent", senderror = "senderror";

	public enum MessageStatus {
		sending, sent, errorsend;
	}

	// status of users.
	public enum UserStatus {
		online, offline, standing;
	}
	
	//Default username, password of Admin
	public static final String nameAdmin = "admin", userAdmin = "admin", passAdmin = "admin";
	
	// Preferences
}

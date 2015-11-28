package presistence;

public class ContantsHomeMMS {

	// left
	public static final String cmdKey = "cmdKey", IDUserKey = "IDUSERKEY", NameKey = "NAMEKEY", PassKey = "PASSWORDKEY",
			AvatarKey = "AVATARKEY", StatusKey = "STATUSKEY", recieverNoteKey = "RECIEVERNOTEKEY";

	// left
	public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY",
			mIDKey = "MID", titleKey = "TITLENOTE", contentTextKey = "MSG", contentAudioKey = "ATTACHAKEY",
			contentVideoKey = "ATTACHVKEY", contentImageKey = "ATTACHPKEY";

	// right
	public enum Command {
		LOGIN, HASREGISTER, INFOREGISTER, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
	}

	// Status of first load app
	public enum FirstStatus {
		REQUESTLOGIN, REGISTERED, NOTREGISTERED;
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
}

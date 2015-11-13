package presistence;

public class ContantsHomeMMS {

	//left
	public static final String cmdKey = "cmdKey", IDUserKey = "IDUSERKEY", NameKey = "NAMEKEY",
			PassKey = "PASSWORDKEY", AvatarKey = "AVATARKEY", StatusKey = "STATUSKEY",
			recieverNoteKey = "RECIEVERNOTEKEY";

	//left
	public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY",
			mIDKey = "MID", titleKey = "TITLENOTE", contentTextKey = "MSG", contentAudioKey = "ATTACHAKEY",
			contentVideoKey = "ATTACHVKEY", contentImageKey = "ATTACHPKEY";
	
	//right
	public enum Command {
		HASREGISTER, INFOREGISTER, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
	}
	
    public static final String registerKey="REGISTER" ,registeredKey="REGISTERED", 
    		notRegistered="NOTREGISTERED", userDatabase="USERDATABASE";
	
	//status of messages
	public static final String sending="sending", sent="sent", senderror="senderror";
	public enum MessageStatus{
		sending, sent, errorsend;
	}
	
	//status of users.
    public enum UserStatus{
        online, offline, standing;
    }
}

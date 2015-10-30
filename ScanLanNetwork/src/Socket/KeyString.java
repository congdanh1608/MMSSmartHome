package Socket;

public class KeyString {

	//left
	public static final String cmdKey = "cmdKey", IPKey = "IPKEY", NameKey = "NAMEKEY",
			MacKey = "MACKEY", titleKey = "TITLENOTE", msgKey = "MSG", ActtachAKey = "ATTACHAKEY",
			ActtachVKey = "ATTACHVKEY", ActtachPKey = "ATTACHPKEY", recieverNoteKey = "RECIEVERNOTEKEY";

	//left
	public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY";
	
	//right
	public enum Command {
		INFOKEY, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
	}
}

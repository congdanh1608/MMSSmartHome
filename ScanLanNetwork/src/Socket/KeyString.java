package Socket;

public class KeyString {

	public static final String IPKey = "IPKEY", NameKey = "NAMEKEY",
			MacKey = "MACKEY", cmdKey = "cmdKey", msgKey = "MSG", NameFileKey = "NAMEFILEKEY";

	public enum Command {
		INFOKEY, PUSHKEY, PULLKEY, MSGKEY;
	}
}

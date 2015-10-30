package thesis.danh.avpdemo.Socket;

/**
 * Created by CongDanh on 29/10/2015.
 */
public class KeyString {

    public static final String cmdKey = "cmdKey", IPKey = "IPKEY", NameKey = "NAMEKEY",
            MacKey = "MACKEY", titleKey = "TITLENOTE", msgKey = "MSG", ActtachAKey = "ATTACHAKEY",
            ActtachVKey = "ATTACHVKEY", ActtachPKey = "ATTACHPKEY", recieverNoteKey = "RECIEVERNOTEKEY";

    public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY";

    public enum Command {
        INFOKEY, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
    }

    public enum TypeFile{
        Audio, Video, Photo;
    }
}

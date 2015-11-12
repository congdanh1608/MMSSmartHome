package thesis.danh.avpdemo.Socket;

/**
 * Created by CongDanh on 29/10/2015.
 */
public class KeyString {

    public static final String cmdKey = "cmdKey", IDUserKey = "IDUSERKEY", NameKey = "NAMEKEY",
    PassKey = "PASSWORDKEY", AvatarKey = "AVATARKEY", StatusKey = "STATUSKEY",
    recieverNoteKey = "RECIEVERNOTEKEY";

    public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY",
            mIDKey = "MID", titleKey = "TITLENOTE", contentTextKey = "MSG", contentAudioKey = "ATTACHAKEY",
            contentVideoKey = "ATTACHVKEY", contentImageKey = "ATTACHPKEY";

    public enum Command {
        INFOKEY, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
    }

    public enum TypeFile{
        Audio, Video, Photo;
    }
}

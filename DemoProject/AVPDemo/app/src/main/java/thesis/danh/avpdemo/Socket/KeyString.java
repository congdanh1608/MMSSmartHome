package thesis.danh.avpdemo.Socket;

/**
 * Created by CongDanh on 29/10/2015.
 */
public class KeyString {

    public static final String  IPKey = "IPKEY", NameKey = "NAMEKEY",
            MacKey = "MACKEY", cmdKey = "cmdKey", msgKey = "MSG", NameFileKey = "NAMEFILEKEY";

    public static enum Command {
        INFOKEY, PUSHKEY, PULLKEY, MSGKEY;
    }
}

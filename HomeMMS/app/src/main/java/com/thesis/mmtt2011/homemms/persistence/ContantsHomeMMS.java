package com.thesis.mmtt2011.homemms.persistence;

import android.os.Environment;

import java.io.File;

/**
 * Created by CongDanh on 29/10/2015.
 */
public class ContantsHomeMMS {

    public static final File SD_Scard = Environment.getExternalStorageDirectory();
    public static final String AppName = "HOMEMMS";
    public static final String AppFolder = SD_Scard + "/" + AppName;
    public static final String nmblookupName = "nmblookup";

    //User - Pass default of Rasp Pi.
    public static final String userRaspPi = "pi";
    public static final String passRaspPi = "raspberry";

    public static final String cmdKey = "cmdKey", IDUserKey = "IDUSERKEY", NameKey = "NAMEKEY",
    PassKey = "PASSWORDKEY", AvatarKey = "AVATARKEY", StatusKey = "STATUSKEY",
    recieverNoteKey = "RECIEVERNOTEKEY";

    public static final String timeKey = "TIMEKEY", senderKey = "SENDERKEY", recieverKey = "RECIEVERKEY",
            mIDKey = "MID", titleKey = "TITLENOTE", contentTextKey = "MSG", contentAudioKey = "ATTACHAKEY",
            contentVideoKey = "ATTACHVKEY", contentImageKey = "ATTACHPKEY";

    //Message is New
    public static final String isNewMsgKey = "NEWMSGKEY", isNewMsg =  "NEWMSG", isOldMsg = "OLDMSG";

    public static final String registerKey="REGISTER" , requestloginKey ="ReqLOGIN", registered ="REGISTERED",
            notRegistered ="NOTREGISTERED", userDatabase="USERDATABASE", firstRun = "FIRSTRUN";

    public static final String loginKey = "LOGIN", loginSuccess = "LOGINSUCCESS", loginFail = "LOGINFAIL";

    public enum Command {
        LOGIN, HASREGISTER, INFOREGISTER, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
    }

    //Status of first load app
    public enum FirstStatus {
        REQUESTLOGIN, REGISTERED, NOTREGISTERED;
    }

    public enum TypeFile{
        Audio, Video, Photo;
    }

    public enum UserStatus{
        online, offline, standing;
    }

    public enum MessageStatus{
        draft, sent, received, read;
    }

    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1003;
    public static final int REQUEST_VIDEO_CAPTURE = 1002;
    public static final int REGISTER_REQUEST_CODE = 1000, LOGIN_REQUEST_CODE = 1001;

    //Share Preferences
    public static final String PREFERENCE_NAME = AppName + "_PREFERENCES";
    public static final String FIRST_RUN_PREFRENCES = "isFirstRun";
    public static final String FIRST_RUN_REQUEST_LOGIN = "isFirstRunReqestLogin";
    public static final String SERVER_IP = "ServerIP";
    public static final String SERVER_MAC = "ServerMAC";
    public static final String SERVER_NAME = "ServerNAME";
    public static final String USER_AVATAR = "UserAvatar";
}

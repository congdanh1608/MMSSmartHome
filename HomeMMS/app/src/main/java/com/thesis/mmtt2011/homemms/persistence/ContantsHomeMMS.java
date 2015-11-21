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

    public static final String registerKey="REGISTER" , registeredKey ="REGISTERED", notRegistered ="NOTREGISTERED", userDatabase="USERDATABASE";

    public enum Command {
        HASREGISTER, INFOREGISTER, RECIEVER, PUSHKEY, PULLKEY, MSGKEY, ENDNOTE, DISCONNECT, RECIEVERNOTE;
    }

    public enum TypeFile{
        Audio, Video, Photo;
    }

    public enum UserStatus{
        online, offline, standing;
    }

    public enum MessageStatus{
        draft, sent, received;
    }

    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1111;
    public static final int REQUEST_VIDEO_CAPTURE = 2222;
}

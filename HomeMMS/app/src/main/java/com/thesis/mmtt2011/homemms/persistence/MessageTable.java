package com.thesis.mmtt2011.homemms.persistence;

import android.provider.BaseColumns;

/**
 * Created by Xpie on 10/31/2015.
 */
public interface MessageTable {
    String NAME = "message";

    String COLUMN_ID = "_id"; //User sender MAC_hhmmss
    String COLUMN_SENDER = "sender";
    String COLUMN_RECEIVER = "receivers"; //list myUser receiver MACReceiver1_MACReceiver2_MACReceiver3
    String COLUMN_TITLE = "title";
    String COLUMN_CONTENT_TEXT = "text";
    String COLUMN_CONTENT_IMAGE = "image";
    String COLUMN_CONTENT_AUDIO = "audio";
    String COLUMN_CONTENT_VIDEO = "video";
    String COLUMN_STATUS = "status";
    String COLUMN_TIMESTAMP = "timestamp";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_SENDER, COLUMN_RECEIVER,
            COLUMN_TITLE, COLUMN_CONTENT_TEXT, COLUMN_CONTENT_IMAGE, COLUMN_CONTENT_AUDIO,
            COLUMN_CONTENT_VIDEO, COLUMN_STATUS, COLUMN_TIMESTAMP};

//    String CREATE = "CREATE TABLE " + NAME + " ("
//            + COLUMN_ID + " TEXT PRIMARY KEY, "
//            + FK_USER_SENDER + " REFERENCES "
//            + UserTable.NAME + "(" + UserTable.COLUMN_ID + "), "
//            + COLUMN_RECEIVER + " TEXT, "
//            + COLUMN_TITLE + " TEXT, "
//            + COLUMN_CONTENT_TEXT + " TEXT, "
//            + COLUMN_CONTENT_IMAGE + " TEXT, "
//            + COLUMN_CONTENT_AUDIO + " TEXT, "
//            + COLUMN_CONTENT_VIDEO + " TEXT, "
//            + COLUMN_STATUS + " TEXT, "
//            + COLUMN_TIMESTAMP + " TEXT);";

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_SENDER + " TEXT, "
            + COLUMN_RECEIVER + " TEXT, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_CONTENT_TEXT + " TEXT, "
            + COLUMN_CONTENT_IMAGE + " TEXT, "
            + COLUMN_CONTENT_AUDIO + " TEXT, "
            + COLUMN_CONTENT_VIDEO + " TEXT, "
            + COLUMN_STATUS + " TEXT, "
            + COLUMN_TIMESTAMP + " TEXT);";

    String DROP = "DROP TABLE IF EXISTS " + NAME;
}

package com.thesis.mmtt2011.homemms.persistence;

import android.provider.BaseColumns;

/**
 * Created by Xpie on 10/31/2015.
 */
public interface UserTable {
    String NAME = "user";

    String COLUMN_ID = "id";//BaseColumns._ID; //MAC address android device
    //String COLUMN_MAC = "mac";
    String COLUMN_NAME = "name";
    //String COLUMN_PASSWORD = "password";
    String COLUMN_AVATAR = "avatar";
    String COLUMN_STATUS = "status";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AVATAR, COLUMN_STATUS};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_AVATAR + " TEXT, "
            + COLUMN_STATUS + " TEXT);";

    String DROP = "DROP TABLE IF EXISTS " + NAME;
}

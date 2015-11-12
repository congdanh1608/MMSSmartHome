package com.thesis.mmtt2011.homemms.persistence;

import android.provider.BaseColumns;

/**
 * Created by Xpie on 10/31/2015.
 */
public interface UserTable {
    String NAME = "myUser";

    String COLUMN_ID = BaseColumns._ID; //MAC address android device
    //String COLUMN_MAC = "mac";
    String COLUMN_NAME = "name";
    //String COLUMN_PASSWORD = "password";
    String COLUMN_AVATAR = "avatar";
    String COLUMN_STATUS = "status";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AVATAR};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_AVATAR + " TEXT NOT NULL, "
            + COLUMN_STATUS + " TEXT);";
}

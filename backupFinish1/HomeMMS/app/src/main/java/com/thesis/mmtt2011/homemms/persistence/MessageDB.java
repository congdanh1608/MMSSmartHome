package com.thesis.mmtt2011.homemms.persistence;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.thesis.mmtt2011.homemms.model.Message;

import java.util.HashMap;

/**
 * Created by Xpie on 12/5/2015.
 */
public class MessageDB {

    private HomeMMSDatabaseHelper mmsDatabaseHelper;

    private HashMap<String, String> mAliasMap;

    public MessageDB(Context context) {
        mmsDatabaseHelper = new HomeMMSDatabaseHelper(context);
        //This HashMap is used to map table fields to Custom Suggestion fields
        mAliasMap = new HashMap<String, String>();
        //Unique id for the each Suggestions
        mAliasMap.put("_ID", MessageTable.COLUMN_ID + " as " + "_id");
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, MessageTable.COLUMN_TITLE + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, MessageTable.COLUMN_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    public Cursor getMessages(String[] selectionArgs) {
        String selection = MessageTable.COLUMN_TITLE + " like ? ";
        if (selectionArgs != null) {
            selectionArgs[0] = "%" + selectionArgs[0] + "%";
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setProjectionMap(mAliasMap);
        queryBuilder.setTables(MessageTable.NAME);

        Cursor cursor = queryBuilder.query(mmsDatabaseHelper.getReadableDatabase(),
                new String[] { "_ID",
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID } ,
                selection,
                selectionArgs,
                null,
                null,
                MessageTable.COLUMN_TITLE + " asc ", "10"
        );
        return cursor;
    }

    public Cursor getMessage(String id) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MessageTable.NAME);
        Cursor cursor = queryBuilder.query(mmsDatabaseHelper.getReadableDatabase(),
                MessageTable.PROJECTION,
                MessageTable.COLUMN_ID + " = ?",
                new String[] { id }, null, null, null, "1"
        );
        return cursor;
    }
}

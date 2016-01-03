package com.thesis.mmtt2011.homemms.persistence;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Xpie on 12/5/2015.
 */
public class MessageContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.thesis.mmtt2011.homemms.persistence.MessageContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/message" );

    MessageDB messageDB = null;
    private static final int SUGGESTIONS_MESSAGE = 1;
    private static final int SEARCH_MESSAGE = 2;
    private static final int GET_MESSAGE = 3;

    UriMatcher mUriMatcher = buildUriMatcher();

    private UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_MESSAGE);

        // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
        // Listview items of SearchResultActivity is provided by this uri
        // See android:searchSuggestIntentData="content://com.thesis.mmtt2011.homemms.provider/message" of searchable.xml
        uriMatcher.addURI(AUTHORITY, "message", SEARCH_MESSAGE);

        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        // Country details for MessageContentActivity is provided by this uri
        // See, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID in MessageDB.java
        uriMatcher.addURI(AUTHORITY, "message/*", GET_MESSAGE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        messageDB = new MessageDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case SUGGESTIONS_MESSAGE:
                cursor = messageDB.getMessages(selectionArgs);
                break;
            case SEARCH_MESSAGE:
                cursor = messageDB.getMessages(selectionArgs);
                break;
            case GET_MESSAGE:
                String id = uri.getLastPathSegment();
                cursor = messageDB.getMessage(id);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

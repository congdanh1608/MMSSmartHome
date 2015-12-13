package com.thesis.mmtt2011.homemms.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Telephony;

import com.thesis.mmtt2011.homemms.*;
import com.thesis.mmtt2011.homemms.model.JsonAttributes;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xpie on 10/31/2015.
 */
public class HomeMMSDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "HomeMMSDatabaseHelper";
    private static final String DB_NAME = "homemms";
    private static final String DB_SUFFIX = ".db";
    private static final int DB_VERSION = 1;
    private static List<Message> mMessages;
    private static List<User> mUsers;
    private static HomeMMSDatabaseHelper mInstance;

    public HomeMMSDatabaseHelper(Context context) {
        //prevents external instance creation
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
    }

    private static HomeMMSDatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new HomeMMSDatabaseHelper(context);
        }
        return mInstance;
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * create the myUser table first, as message table has foreign key
         * constraint on myUser id
         */
        db.execSQL(UserTable.CREATE);
        db.execSQL(MessageTable.CREATE);
        //preFillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* no-op */
        db.execSQL(UserTable.DROP);
        db.execSQL(MessageTable.DROP);

        //create new tables
        onCreate(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * CRUD USERS *
     */
    public static long createUser(Context context, User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_ID, user.getId());
        values.put(UserTable.COLUMN_NAME, user.getNameDisplay());
        values.put(UserTable.COLUMN_AVATAR, user.getAvatar());
        values.put(UserTable.COLUMN_STATUS, user.getStatus());

        //insert
        return getWritableDatabase(context).insert(UserTable.NAME, null, values);
    }

    public static User getUser(Context context, String userId) {
        String[] selectionArgs = {userId};
        Cursor data = getReadableDatabase(context)
                .query(UserTable.NAME, null, UserTable.COLUMN_ID + " = ?",
                        selectionArgs, null, null, null);
        if (data.moveToFirst()) {
            User user = new User();
            user.setId(data.getString(data.getColumnIndex(UserTable.COLUMN_ID)));
            user.setNameDisplay(data.getString(data.getColumnIndex(UserTable.COLUMN_NAME)));
            user.setAvatar(data.getString(data.getColumnIndex(UserTable.COLUMN_AVATAR)));
            user.setStatus(data.getString(data.getColumnIndex(UserTable.COLUMN_STATUS)));
            return user;
        } else {
            return null;
        }
    }

    public static List<User> getAllUserExceptMySeft(Context context, String userId) {
        List<User> users = new ArrayList<User>();
        String[] selectionArgs = {userId};
        Cursor data = getReadableDatabase(context)
                .query(UserTable.NAME, UserTable.PROJECTION, UserTable.COLUMN_ID + " <> ?", selectionArgs, null, null, null);
        if (data.moveToFirst()) {
            do {
                User user = new User();
                user.setId(data.getString(data.getColumnIndex(UserTable.COLUMN_ID)));
                user.setNameDisplay(data.getString(data.getColumnIndex(UserTable.COLUMN_NAME)));
                user.setAvatar(data.getString(data.getColumnIndex(UserTable.COLUMN_AVATAR)));
                user.setStatus(data.getString(data.getColumnIndex(UserTable.COLUMN_STATUS)));

                users.add(user);
            } while (data.moveToNext());
        }
        return users;
    }

    public static List<User> getAllUsers(Context context) {
        List<User> users = new ArrayList<User>();
        Cursor data = getReadableDatabase(context)
                .query(UserTable.NAME, UserTable.PROJECTION, null, null, null, null, null);
        if (data.moveToFirst()) {
            do {
                User user = new User();
                user.setId(data.getString(data.getColumnIndex(UserTable.COLUMN_ID)));
                user.setNameDisplay(data.getString(data.getColumnIndex(UserTable.COLUMN_NAME)));
                user.setAvatar(data.getString(data.getColumnIndex(UserTable.COLUMN_AVATAR)));
                user.setStatus(data.getString(data.getColumnIndex(UserTable.COLUMN_STATUS)));

                users.add(user);
            } while (data.moveToNext());
        }
        return users;
    }

    public static List<User> getAllUsersBy(Context context, String byColumn, String value) {
        List<User> users = new ArrayList<User>();
        String[] selectionArgs = {value};
        Cursor data = getReadableDatabase(context)
                .query(UserTable.NAME, UserTable.PROJECTION, byColumn + " = ?",
                        selectionArgs, null, null, null);
        if (data.moveToFirst()) {
            do {
                User user = new User();
                user.setId(data.getString(data.getColumnIndex(UserTable.COLUMN_ID)));
                user.setNameDisplay(data.getString(data.getColumnIndex(UserTable.COLUMN_NAME)));
                user.setNameDisplay(data.getString(data.getColumnIndex(UserTable.COLUMN_AVATAR)));
                user.setStatus(data.getString(data.getColumnIndex(UserTable.COLUMN_STATUS)));

                users.add(user);
            } while (data.moveToNext());
        }
        return users;
    }

    public int getUserCount() {
        Cursor data = getReadableDatabase()
                .query(UserTable.NAME, UserTable.PROJECTION, null, null, null, null, null);
        int count = data.getCount();
        return count;
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_NAME, user.getNameDisplay());
        values.put(UserTable.COLUMN_STATUS, user.getStatus());
        String[] selectionArgs = {user.getId()};
        return getWritableDatabase().update(UserTable.NAME, values, UserTable.COLUMN_ID + " = ?",
                selectionArgs);
    }

    public void deleteUser(String userId) {
        String[] selectionArgs = {userId};
        getWritableDatabase().delete(UserTable.NAME, UserTable.COLUMN_ID + " = ?",
                selectionArgs);
    }


    /*//Lay tat ca thong tin myUser dang json duoc server tra ve insert vao myUser table trong database
    public void fillUsers(SQLiteDatabase db, String usersJson) throws JSONException, IOException{

        ContentValues values = new ContentValues();
        JSONArray jsonArray = new JSONArray(usersJson);
        JSONObject user;
        for (int i = 0; i < jsonArray.length(); i++) {
            user = jsonArray.getJSONObject(i);
            final String userMac = user.getString(JsonAttributes.MAC);
            fillUser(db, values, user, userMac);
        }
    }

    //insert message from json into MessageTable in database
    private void fillUser(SQLiteDatabase db, ContentValues values, JSONObject user,
                             String userId) throws JSONException{
        values.clear();
        values.put(UserTable.COLUMN_ID, userId);
        values.put(UserTable.COLUMN_NAME, user.getString(JsonAttributes.NAME));
        values.put(UserTable.COLUMN_AVATAR, user.getString(JsonAttributes.AVATAR));
        values.put(UserTable.COLUMN_STATUS, user.getString(JsonAttributes.STATUS));
        db.insert(UserTable.NAME, null, values);
    }*/

    /*public void addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_ID, user.getId());
        values.put(UserTable.COLUMN_AVATAR, user.getAvatar());
        values.put(UserTable.COLUMN_NAME, user.getNameDisplay());
        values.put(UserTable.COLUMN_STATUS, user.getStatus());
        db.insert(UserTable.NAME, null, values);
    }

    public void updateUser(Context context, User user) {
        if (mUsers != null && mUsers.contains(user)) {
            final int location = mUsers.indexOf(user);
            mUsers.remove(location);
            mUsers.add(location, user);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        ContentValues userValues = createUserValueFor(user);
        writableDatabase.update(UserTable.NAME, userValues, UserTable.COLUMN_ID + "=?",
                new String[]{user.getId()});
    }

    public void UpdateUser_(User user) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_NAME, user.getNameDisplay());
        values.put(UserTable.COLUMN_AVATAR, user.getAvatar());
        values.put(UserTable.COLUMN_STATUS, user.getStatus());

        writableDatabase.update(UserTable.NAME, values, UserTable.COLUMN_ID + " =?",
                new String[]{String.valueOf(user.getId())});
        writableDatabase.close();
    }

    *//**
     * Gets all users wrapped in a {@link Cursor} positioned at it's first element.
     * @param context
     * @return All users stored in the database
     *//*
    private static Cursor getUserCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase
                .query(UserTable.NAME, UserTable.PROJECTION, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    *//**
     * Gets a myUser from the given position of the cursor provided
     *
     * @param cursor The cursor containing the data.
     * @return The found myUser
     *//*
    public static User getUser(Cursor cursor) {
        // "magic numbers" based on UserTable#PROJECTION
        final String id = cursor.getString(0);
        final String name = cursor.getString(1);
        final String avatar = cursor.getString(2);
        final String status = cursor.getString(3);

        return new User(id, name, null, avatar, status);
    }

    public static User getUserWith_(Context context, String userId){
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String sql = "SELECT * FROM " + UserTable.NAME + " where " + UserTable.COLUMN_ID + "='" + userId + "'";
        Cursor data = readableDatabase.rawQuery(sql, null);
        if (data==null || !data.moveToFirst()) return null;
        return getUser(data);
    }

    public static User getUserWith(Context context, String userId) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {userId};
        Cursor data = readableDatabase
                .query(UserTable.NAME, UserTable.PROJECTION, UserTable.COLUMN_ID + "?=",
                        selectionArgs, null, null, null);
        if (!data.moveToFirst()) return null;
        return getUser(data);
    }

    *//**
     * Gets all users
     *
     *//*

    public ContentValues createUserValueFor(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserTable.COLUMN_STATUS, user.getStatus());
        return contentValues;
    }

    public static List<User> getUsers(Context context, boolean fromDatabase) {
        if (null == mUsers || fromDatabase) {
            mUsers = loadUsers(context);
        }
        return mUsers;
    }

    public static List<User> loadUsers(Context context) {
        Cursor data = HomeMMSDatabaseHelper.getUserCursor(context);
        List<User> tmpUsers = new ArrayList<>(data.getCount());
        final SQLiteDatabase readableDatabase = HomeMMSDatabaseHelper.getReadableDatabase(context);
        do {
            final User user = getUser(data);
            tmpUsers.add(user);
        } while (data.moveToNext());
        return tmpUsers;
    }*/



    /**
     * CRUD MESSAGES *
     */

    public long createMessage(Context context, Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_ID, message.getId());
        values.put(MessageTable.COLUMN_SENDER, message.getSender().getId());
        values.put(MessageTable.COLUMN_RECEIVER, ConvertStringReceiver(message.getReceiver()));
        values.put(MessageTable.COLUMN_TITLE, message.getTitle());
        values.put(MessageTable.COLUMN_CONTENT_TEXT, message.getContentText());
        values.put(MessageTable.COLUMN_CONTENT_IMAGE, message.getContentImage()==null ? "null" : message.getContentImage());
        values.put(MessageTable.COLUMN_CONTENT_AUDIO, message.getContentAudio()==null ? "null" : message.getContentAudio());
        values.put(MessageTable.COLUMN_CONTENT_VIDEO, message.getContentVideo()==null ? "null" : message.getContentVideo());
        values.put(MessageTable.COLUMN_STATUS, message.getStatus());
        values.put(MessageTable.COLUMN_TIMESTAMP, message.getTimestamp());

        //insert
        return getWritableDatabase(context).insert(MessageTable.NAME, null, values);
    }

    /**
     * Gets list receivers (User) from String receivers
     * @param context
     * @return list myUser receivers
     */
    public static List<User> getListReceiver(Context context, String strReceivers) {
        ArrayList<User> receivers = null;
        if (strReceivers!=null) {
            String[] macReceivers = strReceivers.split("-");
            receivers = new ArrayList<>();
            for (String receiverMac : macReceivers) {
                receivers.add(getUser(context, receiverMac));
            }
        }
        return receivers;
    }

    public static Message getMessage(Context context, String messageId) {
        String[] selectionArgs = {messageId};
        Cursor data = getReadableDatabase(context)
                .query(MessageTable.NAME, null, MessageTable.COLUMN_ID + " = ?",
                        selectionArgs, null, null, null);
        if (data.moveToFirst()) {
            Message message = new Message();
            message.setMId(data.getString(data.getColumnIndex(MessageTable.COLUMN_ID)));
            User sender = getUser(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_SENDER)));
            message.setSender(sender);
            message.setListReceiverString(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_RECEIVER)));
            message.setTitle(data.getString(data.getColumnIndex(MessageTable.COLUMN_TITLE)));
            message.setContentText(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));
            message.setContentAudio(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_AUDIO)));
            message.setContentImage(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_IMAGE)));
            message.setContentVideo(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_VIDEO)));
            message.setTimeCondition(data.getString(data.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
            message.setStatus(data.getString(data.getColumnIndex(MessageTable.COLUMN_STATUS)));
            return message;
        } else {
            return null;
        }
    }

    public static List<Message> getAllMessages(Context context) {
        List<Message> messages = new ArrayList<Message>();
        Cursor data = getReadableDatabase(context)
                .query(MessageTable.NAME, MessageTable.PROJECTION, null, null, null, null, null);
        if (data.moveToFirst()) {
            do {
                Message message = new Message();
                message.setMId(data.getString(data.getColumnIndex(MessageTable.COLUMN_ID)));
                User sender = getUser(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_SENDER)));
                message.setSender(sender);
                message.setListReceiverString(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_RECEIVER)));
                message.setTitle(data.getString(data.getColumnIndex(MessageTable.COLUMN_TITLE)));
                message.setContentText(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));
                message.setContentAudio(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_AUDIO)));
                message.setContentImage(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_IMAGE)));
                message.setContentVideo(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_VIDEO)));
                message.setTimeCondition(data.getString(data.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
                message.setStatus(data.getString(data.getColumnIndex(MessageTable.COLUMN_STATUS)));

                messages.add(message);
            } while (data.moveToNext());
        }
        return messages;
    }

    //viet 1 ham lay cac tin nhan da nhan
    //like receiver
    public static List<Message> getAllMessagesByReceiver(Context context, String value) {
        List<Message> messages = new ArrayList<Message>();

        String sql = "SELECT * FROM " + MessageTable.NAME + " WHERE " + MessageTable.COLUMN_RECEIVER + " LIKE '%" + value + "%'";
        Cursor data = getReadableDatabase(context).rawQuery(sql, null);
        if (data.moveToFirst()) {
            do {
                Message message = new Message();
                message.setMId(data.getString(data.getColumnIndex(MessageTable.COLUMN_ID)));
                User sender = getUser(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_SENDER)));
                message.setSender(sender);
                message.setListReceiverString(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_RECEIVER)));
                message.setTitle(data.getString(data.getColumnIndex(MessageTable.COLUMN_TITLE)));
                message.setContentText(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));
                message.setContentAudio(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_AUDIO)));
                message.setContentImage(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_IMAGE)));
                message.setContentVideo(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_VIDEO)));
                message.setTimeCondition(data.getString(data.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
                message.setStatus(data.getString(data.getColumnIndex(MessageTable.COLUMN_STATUS)));

                messages.add(0, message);
            } while (data.moveToNext());
        }
        return messages;
    }
    //
    public static List<Message> getAllMessagesBy(Context context, String byColumn, String value) {
        List<Message> messages = new ArrayList<Message>();
        String[] selectionArgs = {value};
        Cursor data = getReadableDatabase(context)
                .query(MessageTable.NAME, MessageTable.PROJECTION, byColumn + " = ?",
                        selectionArgs, null, null, null);
        if (data.moveToFirst()) {
            do {
                Message message = new Message();
                message.setMId(data.getString(data.getColumnIndex(MessageTable.COLUMN_ID)));
                User sender = getUser(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_SENDER)));
                message.setSender(sender);
                message.setListReceiverString(context, data.getString(data.getColumnIndex(MessageTable.COLUMN_RECEIVER)));
                message.setTitle(data.getString(data.getColumnIndex(MessageTable.COLUMN_TITLE)));
                message.setContentText(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));
                message.setContentAudio(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_AUDIO)));
                message.setContentImage(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_IMAGE)));
                message.setContentVideo(data.getString(data.getColumnIndex(MessageTable.COLUMN_CONTENT_VIDEO)));
                message.setTimeCondition(data.getString(data.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
                message.setStatus(data.getString(data.getColumnIndex(MessageTable.COLUMN_STATUS)));

                messages.add(message);
            } while (data.moveToNext());
        }
        return messages;
    }

    public int getMessageCount() {
        Cursor data = getReadableDatabase()
                .query(MessageTable.NAME, MessageTable.PROJECTION, null, null, null, null, null);
        int count = data.getCount();
        return count;
    }

    private static String ConvertStringReceiver(List<User> receivers) {
        StringBuilder strReceivers = new StringBuilder();
        strReceivers.append(receivers.get(0).getId());
        for (int i = 1; i < receivers.size(); i++) {
            strReceivers.append("-" + receivers.get(i));
        }
        return strReceivers.toString();
    }

    public static int updateMessage(Context context, Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_TITLE, message.getTitle());
        values.put(MessageTable.COLUMN_SENDER, message.getSender().getId());
        values.put(MessageTable.COLUMN_RECEIVER, ConvertStringReceiver(message.getReceiver()));
        values.put(MessageTable.COLUMN_CONTENT_TEXT, message.getContentText());
        values.put(MessageTable.COLUMN_CONTENT_IMAGE, message.getContentImage());
        values.put(MessageTable.COLUMN_CONTENT_AUDIO, message.getContentAudio());
        values.put(MessageTable.COLUMN_CONTENT_VIDEO, message.getContentVideo());
        values.put(MessageTable.COLUMN_TIMESTAMP, message.getTimestamp());
        values.put(MessageTable.COLUMN_STATUS, message.getStatus());

        String[] selectionArgs = {message.getId()};
        return getWritableDatabase(context).update(MessageTable.NAME, values, MessageTable.COLUMN_ID + " = ?",
                selectionArgs);
    }

    public static void deleteMessage(Context context, String messageId) {
        String[] selectionArgs = {messageId};
        getWritableDatabase(context).delete(MessageTable.NAME, MessageTable.COLUMN_ID + " = ?",
                selectionArgs);
    }

    /**
     * Gets all messages
     *
     * @param context The context this is running in
     * @param fromDatabase <code>true</code> if a data refresh is needed, else <code>false</code>.
     * @return All messages stored in the database.
     */
    /*public static List<Message> getMessages(Context context, boolean fromDatabase) {
        if (null == mMessages || fromDatabase) {
            mMessages = loadMessages(context);
        }
        return  mMessages;
    }

    public static List<Message> loadMessages(Context context) {
        Cursor data = HomeMMSDatabaseHelper.getMessagesCursor(context);
        List<Message> tmpMessages = new ArrayList<>(data.getCount());
        final SQLiteDatabase readableDatabase = HomeMMSDatabaseHelper.getReadableDatabase(context);
        do {
            final Message message = getMessage(context, data, readableDatabase);
            tmpMessages.add(message);
        } while (data.moveToNext());
        return tmpMessages;
    }

    public static Message getMessage(Context context, Cursor cursor, SQLiteDatabase readableDatabase) {
        final String id = cursor.getString(0);
        User sender = getUser(context, cursor.getString(1));
        List<User> receivers = getListReceiver(context, cursor.getString(2));

        final String title = cursor.getString(3);
        final String text = cursor.getString(4);
        final String image = cursor.getString(5);
        final String audio = cursor.getString(6);
        final String video = cursor.getString(7);
        final String status = cursor.getString(8);
        final String timestamp = cursor.getString(9);
        //set default trang thai doc hay chua la true
        return new Message(id, sender, receivers, title, text, image, audio, video, status, timestamp, true);
    }

    public static Message getMessageWith(Context context, String messageId) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {messageId};
        Cursor data = readableDatabase
                .query(MessageTable.NAME, MessageTable.PROJECTION, MessageTable.COLUMN_ID + "=?",
                        selectionArgs, null, null, null);
        data.moveToFirst();
        return getMessage(context, data, readableDatabase);
    }

    *//**
     * Get all messages of sender sent
     * @param context
     * @param sender MAC address of sender present
     * @return All messages of sender
     *//*
    public static List<Message> getMessagesSender(Context context, String sender, String status) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {sender, status};
        Cursor data = readableDatabase
                .query(MessageTable.NAME, MessageTable.PROJECTION,
                        MessageTable.COLUMN_SENDER + "=? AND " + MessageTable.COLUMN_STATUS + "=?",
                        selectionArgs, null, null, null);
        List<Message> tmpMessages = new ArrayList<>(data.getCount());
        do {
            final Message message = getMessage(context, data, readableDatabase);
            tmpMessages.add(message);
        } while (data.moveToNext());
        return tmpMessages;
    }

    public static List<Message> getAllMessagesSent(Context context, String sender) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String sql = "SELECT * FROM " + MessageTable.NAME + " where " + MessageTable.COLUMN_SENDER + "='" + sender + "'";
        Cursor data = readableDatabase.rawQuery(sql, null);
        List<Message> tmpMessages = new ArrayList<>(data.getCount());
        do {
            final Message message = getMessage(context, data, readableDatabase);
            tmpMessages.add(message);
        } while (data.moveToNext());
        return tmpMessages;
    }

    public static List<Message> getAllMessagesReceived(Context context, String receiver) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String sql = "SELECT * FROM " + MessageTable.NAME + " where " + MessageTable.COLUMN_RECEIVER + " like '%" + receiver + "%'";
        Cursor data = readableDatabase.rawQuery(sql, null);
        List<Message> tmpMessages = new ArrayList<>(data.getCount());
        do {
            final Message message = getMessage(context, data, readableDatabase);
            tmpMessages.add(message);
        } while (data.moveToNext());
        return tmpMessages;
    }

    *//**
     * Get all messages have a status such as (draft, delivered)
     * @param context
     * @param status
     * @return
     *//*
    public static List<Message> getMessagesStatus(Context context, String status) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {status};
        Cursor data = readableDatabase
                .query(MessageTable.NAME, MessageTable.PROJECTION, MessageTable.COLUMN_STATUS + "=?",
                        selectionArgs, null, null, null);
        List<Message> tmpMessages = new ArrayList<>(data.getCount());
        do {
            final Message message = getMessage(context, data, readableDatabase);
            tmpMessages.add(message);
        } while (data.moveToNext());
        return tmpMessages;
    }
    *//**
     * Gets list receivers (User) from String receivers
     * @param context
     * @param strReceivers MAC addresses of receivers MACReceiver1-MACReceiver2
     * @return list myUser receivers
     *//*
    public static List<User> getListReceiver(Context context, String strReceivers) {
        ArrayList<User> receivers = null;
        if (strReceivers!=null) {
            String[] macReceivers = strReceivers.split("-");
            receivers = new ArrayList<>();
            for (String receiverMac : macReceivers) {
                receivers.add(getUser(context, receiverMac));
            }
        }
        return receivers;
    }

    *//**
     * Gets all messages wrapped in a {@link Cursor} positioned at it's first element.
     *
     * @param context The context this is running in
     * @return All messages stored in the database.
     *//*
    private static Cursor getMessagesCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase
                .query(MessageTable.NAME, MessageTable.PROJECTION, null, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    private void fillMessages(SQLiteDatabase db, String messagesJson) throws JSONException, IOException{
        ContentValues values = new ContentValues();
        JSONArray jsonArray = new JSONArray(messagesJson);
        JSONObject message;
        for (int i = 0; i < jsonArray.length(); i++) {
            message = jsonArray.getJSONObject(i);
            final String messageId = message.getString(JsonAttributes.ID);
            fillMessage(db, values, message, messageId);
        }
    }

    //insert message from json into MessageTable in database
    public void fillMessage(SQLiteDatabase db, ContentValues values, JSONObject message,
                             String messageId) throws JSONException{
        values.clear();
        values.put(MessageTable.COLUMN_ID, messageId);
        values.put(MessageTable.COLUMN_SENDER, message.getString(JsonAttributes.SENDER));
        values.put(MessageTable.COLUMN_RECEIVER, message.getString(JsonAttributes.RECEIVER));
        values.put(MessageTable.COLUMN_TITLE, message.getString(JsonAttributes.NAME));
        values.put(MessageTable.COLUMN_CONTENT_TEXT, message.getString(JsonAttributes.TEXT));;
        values.put(MessageTable.COLUMN_CONTENT_IMAGE, message.getString(JsonAttributes.IMAGE));
        values.put(MessageTable.COLUMN_CONTENT_AUDIO, message.getString(JsonAttributes.AUDIO));
        values.put(MessageTable.COLUMN_CONTENT_VIDEO, message.getString(JsonAttributes.VIDEO));
        values.put(MessageTable.COLUMN_STATUS, message.getString(JsonAttributes.STATUS));
        values.put(MessageTable.COLUMN_TIMESTAMP, message.getString(JsonAttributes.TIMESTAMP));
        db.insert(MessageTable.NAME, null, values);
    }

    public void addMessage(Message message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_ID, message.getId());
        values.put(MessageTable.COLUMN_SENDER, message.getSender().getId());
        values.put(MessageTable.COLUMN_RECEIVER, ConvertStringReceiver(message.getReceiver()));
        values.put(MessageTable.COLUMN_TITLE, message.getTitle());
        values.put(MessageTable.COLUMN_CONTENT_TEXT, message.getContentText());;
        values.put(MessageTable.COLUMN_CONTENT_IMAGE, message.getContentImage());
        values.put(MessageTable.COLUMN_CONTENT_AUDIO, message.getContentAudio());
        values.put(MessageTable.COLUMN_CONTENT_VIDEO, message.getContentVideo());
        values.put(MessageTable.COLUMN_STATUS, message.getStatus());
        values.put(MessageTable.COLUMN_TIMESTAMP, message.getTimestamp());
        db.insert(MessageTable.NAME, null, values);
    }

    public String ConvertStringReceiver(List<User> receivers) {
        StringBuilder strReceivers = new StringBuilder();
        strReceivers.append(receivers.get(0).getId());
        for (int i = 1; i < receivers.size(); i++) {
            strReceivers.append("-" + receivers.get(i));
        }
        return strReceivers.toString();
    }

    //update
    public  void updateMessage(Context context, Message message) {
        if (mMessages != null && mMessages.contains(message)) {
            final int location = mMessages.indexOf(message);
            mMessages.remove(location);
            mMessages.add(location, message);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        ContentValues messageValues = createMessageValueFor(message);
        writableDatabase.update(MessageTable.NAME, messageValues, MessageTable.COLUMN_ID + "=?",
                new String[]{message.getId()});
    }

    //update Status of Message.
    public void updateMessageStatus(Context context, String mID, String status) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_STATUS, status);

        writableDatabase.update(MessageTable.NAME, values, MessageTable.COLUMN_ID + " =?",
                new String[]{String.valueOf(mID)});
        writableDatabase.close();
    }

    *//**
     * Creates the content values to update a message in the database
     *  update status of message
     *  can update more values of message here
     *
     * @param message The message to update
     * @return ContentValues containing update data
     *//*
    public ContentValues createMessageValueFor(Message message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageTable.COLUMN_STATUS, message.getStatus());
        return contentValues;
    }

    //delete
    public void deleteMessage(String messageId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = {messageId};
        db.delete(MessageTable.NAME, MessageTable.COLUMN_ID + "=?", selectionArgs);
    }*/

    /*private String readMessagesFromResources() throws IOException {
        StringBuilder messagesJson = new StringBuilder();
        InputStream rawMessages = mResources.openRawResource(R.raw.messages);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawMessages));
        String line;

        while ((line = reader.readLine()) != null) {
            messagesJson.append(line);
        }
        return messagesJson.toString();
    }*/

    /*private void preFillDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                fillMessages(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "preFillDatabase", e);
        }
    }*/

    /**
     * Resets the contents of HomeMMS's database to it's initial state.
     *
     * @param context The context this is running in.
     */
    /*public static void reset(Context context) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        writableDatabase.delete(UserTable.NAME, null, null);
        writableDatabase.delete(MessageTable.NAME, null, null);
        getInstance(context).preFillDatabase(writableDatabase);
    }*/

}

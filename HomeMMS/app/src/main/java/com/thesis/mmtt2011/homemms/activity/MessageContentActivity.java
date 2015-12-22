package com.thesis.mmtt2011.homemms.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;
import com.thesis.mmtt2011.homemms.persistence.MessageTable;

public class MessageContentActivity extends MainActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "MessageContentActivity";

    private Uri mUri;

    private Message mMessage;

    private TextView mTitleView;
    private TextView mContentView;
    private TextView mTimestamp;

    public static Intent getStartIntent(Context context, Message message) {
        Intent starter = new Intent(context, MessageContentActivity.class);
        starter.putExtra(MessageContentActivity.TAG, message.getId());
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitleView = (TextView) findViewById(R.id.message_title);
        mContentView = (TextView) findViewById(R.id.message_content);
        mTimestamp = (TextView) findViewById(R.id.timestamp);
        // Get the message from the intent
        Intent intent = getIntent();
        if (intent.hasExtra(TAG)) {
            String messageId = intent.getStringExtra(TAG);
            mMessage = HomeMMSDatabaseHelper.getMessage(this, messageId);
            mTitleView.setText(mMessage.getTitle());
            mContentView.setText(mMessage.getContentText());
            mTimestamp.setText(mMessage.getTimestamp());
        } else {
            mUri = intent.getData();
            getSupportLoaderManager().initLoader(0, null, this);
        }

        //get sender to show here

        //get status of message here

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        if (MainActivity.isConnected) {
            if (checkMessageHasAttach(mMessage)) {
                client.SendRequestFileAttach(mMessage);
            }
        } else UtilsMain.showMessage(MessageContentActivity.this, "Server is offline");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mTitleView.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_TITLE)));
            mContentView.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));

            String timeStamp = UtilsMain.stringToDateCondition(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
            mTimestamp.setText(timeStamp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean checkMessageHasAttach(Message message) { //true - down, false - not down
        String myPath = ContantsHomeMMS.AppFolder + "/" + myUser.getId() + "/";
        if (message.getContentAudio() != null && !UtilsMain.checkFileIsExits(myPath + message.getContentAudio())) {
            return true;
        }
        if (message.getContentVideo() != null && !UtilsMain.checkFileIsExits(myPath + message.getContentVideo())) {
            return true;
        }
        if (message.getContentImage() != null && !UtilsMain.checkFileIsExits(myPath + message.getContentImage())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            //implement delete message here

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.thesis.mmtt2011.homemms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.adapter.MessageAdapter;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

public class MessageContentActivity extends AppCompatActivity {

    public static final String TAG = "MessageContentActivity";

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

        // Get the message from the intent
        Intent intent = getIntent();
        String messageId = intent.getStringExtra(TAG);
        mMessage = HomeMMSDatabaseHelper.getMessageWith(this, messageId);

        mTitleView = (TextView) findViewById(R.id.message_title);
        mTitleView.setText(mMessage.getTitle());

        mContentView = (TextView) findViewById(R.id.message_content);
        mContentView.setText(mMessage.getContentText());

        mTimestamp = (TextView) findViewById(R.id.timestamp);
        mTimestamp.setText(mMessage.getTimestamp());

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
    }
}

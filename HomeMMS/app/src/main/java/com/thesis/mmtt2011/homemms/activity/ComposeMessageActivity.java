package com.thesis.mmtt2011.homemms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;

public class ComposeMessageActivity extends AppCompatActivity {

    private EditText mMessageTitleView;
    private EditText mMessageContentView;
    private View mProgressView;
    private View mComposeMessageFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMessageTitleView = (EditText) findViewById(R.id.message_title);
        mMessageContentView = (EditText) findViewById(R.id.message_content);

        mProgressView = findViewById(R.id.compose_message_progress);
        mComposeMessageFormView = findViewById(R.id.compose_message_form);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Sending message...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_camera) {
            /*Intent intent = new Intent(this, ScanDevicesActivity.class);
            startActivity(intent);*/
            return true;
        }

        if (id == R.id.action_video) {
            //
            return true;
        }

        if (id == R.id.action_record) {
            //
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

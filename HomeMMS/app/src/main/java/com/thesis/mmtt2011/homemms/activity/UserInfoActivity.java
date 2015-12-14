package com.thesis.mmtt2011.homemms.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thesis.mmtt2011.homemms.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends MainActivity {

    EditText etNameDisplay;
    EditText etOldPass;
    EditText etNewPass;
    EditText etRetypePass;
    Button btChangePass;
    CircleImageView civAvatar;
    View formChangePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFormChangePass(false);
                //Save changes and send to server to update user info
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        civAvatar = (CircleImageView) findViewById(R.id.change_avatar);
        civAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select image to change avatar
            }
        });
        formChangePass = findViewById(R.id.change_password_form);
        etNameDisplay = (EditText) findViewById(R.id.name_display);
        etOldPass = (EditText) findViewById(R.id.old_password);
        etNewPass = (EditText) findViewById(R.id.new_password);
        etRetypePass = (EditText) findViewById(R.id.retype_password);

        btChangePass = (Button) findViewById(R.id.bt_change_password);
        btChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormChangePass(true);
            }
        });
    }

    private void showFormChangePass(final boolean show) {
        formChangePass.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}

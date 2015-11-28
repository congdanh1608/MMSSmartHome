package com.thesis.mmtt2011.homemms.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.Network.Discover;
import com.thesis.mmtt2011.homemms.Network.DiscoveryThread;
import com.thesis.mmtt2011.homemms.Network.Utils;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;

public class ConnectConfiguredServerActivity extends AppCompatActivity {

    private Button btAutoConnect;
    private Button btManualConnect;
    private Button btConnect;
    private EditText etIPServer;
    private View mProgressView;
    private View mServerConnectForm;

    private ServerConnectTask mConnectTask = null;
    private String ipAddressServer;
    private Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_configured_server);

        utils = new Utils(this);

        etIPServer = (EditText) findViewById(R.id.ipServer);
        etIPServer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptConnect();
                /*if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptConnect();
                    return true;
                }
                return false;*/
                return true;
            }
        });
        mProgressView = findViewById(R.id.connect_progress);

        btAutoConnect = (Button) findViewById(R.id.auto_connect);
        btAutoConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConnectConfiguredServerActivity.this,
                        "Finding server.Please wait...",
                        Toast.LENGTH_SHORT).show();
                //Scan network find and connect to configured server raspberry
                onAutoFindAndConnectToServer();
            }
        });

        btManualConnect = (Button) findViewById(R.id.manual_connect);
        btManualConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerConnectForm = findViewById(R.id.server_connect_form);
                mServerConnectForm.setVisibility(View.VISIBLE);

                btManualConnect.setVisibility(View.GONE);
            }
        });

        btConnect = (Button) findViewById(R.id.connect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Connect to server;
                Toast.makeText(ConnectConfiguredServerActivity.this,
                        "Connecting. Please wait...",
                        Toast.LENGTH_SHORT).show();

                attemptConnect();
            }
        });

    }

    private void onAutoFindAndConnectToServer(){
        //Find by broadcast.
        BroadcastFindServer();
        //Connect.

        //
        PreferencesHelper.writeToPreferences(this, false);
    }

    public void BroadcastFindServer(){
        Thread thread = new Thread(DiscoveryThread.getInstance(this));
        thread.start();
    }

    private void attemptConnect() {
        if (mConnectTask != null) {
            return;
        }

        //reset errors;
        etIPServer.setError(null);

        //Store values at the time of the login attempt;
        String ipServer = etIPServer.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the myUser entered one.
        if (!TextUtils.isEmpty(ipServer) && ! isIPServerValid(ipServer)) {
            etIPServer.setError(getString(R.string.error_incorrect_ip_address));
            focusView = etIPServer;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the myUser login attempt.
            showProgress(true);
            mConnectTask = new ServerConnectTask(ipServer);
            mConnectTask.execute((Void) null);
            PreferencesHelper.writeToPreferences(this, false);
        }
    }

    private boolean isIPServerValid(String ipServer) {
        //check ip server
        return true;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class ServerConnectTask extends AsyncTask<Void, Void, Boolean> {

        private final String ipServer;

        ServerConnectTask(String _ipServer) {
            ipServer = _ipServer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                MainActivity.rasp.setIPAddress(ipServer);
                MainActivity.rasp.setMacAddress(Discover.getMacFromArpCache(ipServer));
                MainActivity.rasp.setDeviceName(Discover.getHostNameNmblookup(ipServer, utils.getnmbLookupLocation()));
                //Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mConnectTask = null;
            showProgress(false);

            if (success) {
                //show activity register
//                Intent intent = new Intent(ConnectConfiguredServerActivity.this, RegisterActivity.class);
//                startActivity(intent);
                ConnectConfiguredServerActivity.this.finish();
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                etIPServer.setError(getString(R.string.error_incorrect_ip_address));
                etIPServer.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mConnectTask = null;
            showProgress(false);
        }
    }
}

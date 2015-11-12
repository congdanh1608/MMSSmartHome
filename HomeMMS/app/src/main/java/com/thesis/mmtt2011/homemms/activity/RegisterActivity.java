package com.thesis.mmtt2011.homemms.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
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

import com.thesis.mmtt2011.homemms.R;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mAuthTask = null;

    // UI references.
    private TextView mMacAddressView;
    private EditText mNameDisplayView;
    private EditText mPasswordView;
    private EditText mRetypePasswordView;
    private View mProgressView;
    private View mRegisterFormView;

    public String getMacAddress() {
        WifiManager wimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        return macAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Setup register form
        mMacAddressView = (TextView) findViewById(R.id.mac_address);
        mMacAddressView.setText(getMacAddress());

        mNameDisplayView = (EditText) findViewById(R.id.name_display);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.register || actionId == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        mRetypePasswordView = (EditText) findViewById(R.id.retype_password);
        mRetypePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.retype_pass || actionId == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }


    private void attemptRegister(){
        if (mAuthTask != null) {
            return;
        }

        //Reset errors.
        mNameDisplayView.setError(null);
        mPasswordView.setError(null);
        mRetypePasswordView.setError(null);

        //Store values at the time of the register attempt.
        String nameDisplay = mNameDisplayView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRetypePasswordView.getText().toString();
        String macAddress = mMacAddressView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the myUser entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isMatchPassword(password, rePassword)) {
            mRetypePasswordView.setError(getString(R.string.error_notmatch_password));
            focusView = mRetypePasswordView;
            cancel = true;
        }

        //Check for a valid name display
        if (TextUtils.isEmpty(nameDisplay)) {
            mNameDisplayView.setError(getString(R.string.error_field_required));
            focusView = mNameDisplayView;
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
            mAuthTask = new UserRegisterTask(macAddress, nameDisplay, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isMatchPassword(String password, String rePassword) {
        return TextUtils.equals(password, rePassword);
    }

    private void showProgress(boolean show) {
        //||disable view register form and show progress view
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= LoginActivity.MIN_LENGTH_PASSWORD;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the myUser
     */

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMACAddress;
        private final String mPassword;
        private final String mNameDisplay;

        UserRegisterTask(String macAddress, String nameDisplay, String password) {
            mMACAddress = macAddress;
            mPassword = password;
            mNameDisplay = nameDisplay;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e){
                return false;
            }
            //Create myUser and send register information to server
            //Check account exists
            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split("-");
                if (pieces[0].equals(mMACAddress)) {
                    //Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

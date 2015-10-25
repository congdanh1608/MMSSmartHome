package com.thesis.mmtt2011.homemms.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;

public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    public static final int MIN_LENGTH_PASSWORD = 3;
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "C4:42:02:5C:CB:83-hello", "00:00:10:10:10:11-world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mMacAddressView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mForgotPassword;

    //Group in class networkUils
    public String getMacAddress() {
        WifiManager wimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
//        if (macAddress == null) {
//            macAddress = "Device don't have mac address or wi-fi is disabled";
//        }
        return macAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Set up the login form
        mMacAddressView = (TextView) findViewById(R.id.mac_address);
        mMacAddressView.setText(getMacAddress());

        mForgotPassword = (TextView) findViewById(R.id.forgot_password);
        //Link to localhost to reset password
        mForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        //reset errors;
        mPasswordView.setError(null);

        //Store values at the time of the login attempt;
        String password = mPasswordView.getText().toString();
        String macAddress = getMacAddress();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && ! isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(macAddress, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= MIN_LENGTH_PASSWORD;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user
     */

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMACAddress;
        private final String mPassword;

        UserLoginTask(String macAddress, String password) {
            mMACAddress = macAddress;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e){
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split("-");
                if (pieces[0].equals(mMACAddress)) {
                    //Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
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

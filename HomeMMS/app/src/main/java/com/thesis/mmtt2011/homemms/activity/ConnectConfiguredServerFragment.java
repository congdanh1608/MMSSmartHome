package com.thesis.mmtt2011.homemms.activity;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.Network.Discover;
import com.thesis.mmtt2011.homemms.Network.DiscoveryThread;
import com.thesis.mmtt2011.homemms.Network.Utils;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

public class ConnectConfiguredServerFragment extends Fragment {

    private Button btAutoConnect;
    private Button btManualConnect;
    private Button btConnect;
    private EditText etIPServer;
    private View mProgressView;
    private View mServerConnectForm;

    private ServerManualConnectTask mManualConnectTask = null;
    private ServerAutoConnectTask mAutoConnectTask = null;
    private Utils utils;

    //
    public ConnectConfiguredServerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_connect_configured_server, container, false);

        //Create utils
        utils = new Utils(getActivity());

        etIPServer = (EditText) rootView.findViewById(R.id.ipServer);
        etIPServer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                attemptManualConnect();
                /*if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptManualConnect();
                    return true;
                }
                return false;*/
                return true;
            }
        });
        mProgressView = rootView.findViewById(R.id.connect_progress);

        btAutoConnect = (Button) rootView.findViewById(R.id.auto_connect);
        btAutoConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        "Finding server.Please wait...",
                        Toast.LENGTH_SHORT).show();
                //Scan network find and connect to configured server raspberry
                attemptAutoConnect();
            }
        });

        btManualConnect = (Button) rootView.findViewById(R.id.manual_connect);
        btManualConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServerConnectForm = getActivity().findViewById(R.id.server_connect_form);
                mServerConnectForm.setVisibility(View.VISIBLE);

                btManualConnect.setVisibility(View.GONE);
            }
        });

        btConnect = (Button) rootView.findViewById(R.id.connect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Connect to server;
                Toast.makeText(getActivity(),
                        "Connecting. Please wait...",
                        Toast.LENGTH_SHORT).show();

                attemptManualConnect();
            }
        });

        return rootView;
    }

    public void BroadcastFindServer() {
        Thread thread = new Thread(DiscoveryThread.getInstance(getActivity()));
        thread.start();
    }

    private void attemptAutoConnect() {
        if (mAutoConnectTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the myUser login attempt.
            showProgress(true);
            BroadcastFindServer();
            mAutoConnectTask = new ServerAutoConnectTask();
            mAutoConnectTask.execute((Void) null);
            PreferencesHelper.writeToPreferences(getActivity(), false);
        }
    }

    private void attemptManualConnect() {
        if (mManualConnectTask != null) {
            return;
        }

        //reset errors;
        etIPServer.setError(null);

        //Store values at the time of the login attempt;
        String ipServer = etIPServer.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the myUser entered one.
        if (!TextUtils.isEmpty(ipServer) && !isIPServerValid(ipServer)) {
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
            BroadcastFindServer();
            mManualConnectTask = new ServerManualConnectTask(ipServer);
            mManualConnectTask.execute((Void) null);
            PreferencesHelper.writeToPreferences(getActivity(), false);
        }
    }

    private boolean isIPServerValid(String ipServer) {
        //check ip server
        return true;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class ServerManualConnectTask extends AsyncTask<Void, Void, Boolean> {

        private final String ipServer;

        ServerManualConnectTask(String _ipServer) {
            ipServer = _ipServer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //Simulate network access.
                Thread.sleep(2000);
                //Test server answer broadcast
                if (MainActivity.rasp.getIPAddress().equals(ipServer)) {
                    MainActivity.rasp.setIPAddress(ipServer);
                    MainActivity.rasp.setMacAddress(Discover.getMacFromArpCache(ipServer));
                    MainActivity.rasp.setDeviceName(Discover.getHostNameNmblookup(ipServer, utils.getnmbLookupLocation()));

                    //Save to Preferences
                    PreferencesHelper.writeToPreferencesString(getActivity(), MainActivity.rasp.getIPAddress(), ContantsHomeMMS.SERVER_IP);
                    PreferencesHelper.writeToPreferencesString(getActivity(), MainActivity.rasp.getMacAddress(), ContantsHomeMMS.SERVER_MAC);
                    PreferencesHelper.writeToPreferencesString(getActivity(), MainActivity.rasp.getDeviceName(), ContantsHomeMMS.SERVER_NAME);
                }
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mManualConnectTask = null;
            showProgress(false);

            if (success) {
                //finish activity, go back to MainActivity
                getActivity().finish();
            } else {
                etIPServer.setError(getString(R.string.error_incorrect_ip_address));
                etIPServer.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mManualConnectTask = null;
            showProgress(false);
        }
    }

    public class ServerAutoConnectTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //Simulate network access.
                Thread.sleep(2000);

                String ipServer = MainActivity.rasp.getIPAddress();
                if (ipServer != null) {
                    MainActivity.rasp.setMacAddress(Discover.getMacFromArpCache(ipServer));
                    MainActivity.rasp.setDeviceName(Discover.getHostNameNmblookup(ipServer, utils.getnmbLookupLocation()));

                    //Save to Preferences
                    PreferencesHelper.writeToPreferencesString(getActivity(), MainActivity.rasp.getIPAddress(), ContantsHomeMMS.SERVER_IP);
                    PreferencesHelper.writeToPreferencesString(getActivity(), MainActivity.rasp.getMacAddress(), ContantsHomeMMS.SERVER_MAC);
                    PreferencesHelper.writeToPreferencesString(getActivity(), MainActivity.rasp.getDeviceName(), ContantsHomeMMS.SERVER_NAME);
                }
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mManualConnectTask = null;
            showProgress(false);

            if (success) {
                //finish activity, go back to MainActivity
                getActivity().finish();
            } else {
                etIPServer.setError(getString(R.string.error_incorrect_ip_address));
                etIPServer.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mManualConnectTask = null;
            showProgress(false);
        }
    }
}

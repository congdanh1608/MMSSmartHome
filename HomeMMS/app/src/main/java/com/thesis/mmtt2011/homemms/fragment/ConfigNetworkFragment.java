package com.thesis.mmtt2011.homemms.fragment;



import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.activity.ScanDevicesActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ConfigNetworkFragment extends Fragment {

    private static final String FRAGMENT_TAG = "ConfigNetworkFragment";
    private EditText etSSIDWifi;
    private EditText etPasswordWifi;
    FloatingActionButton nextFab;

    private View layout_config_form;
    private View mProgressView;

    public ConfigNetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_config_network, container, false);

        layout_config_form = rootView.findViewById(R.id.wifi_access_form);
        etSSIDWifi = (EditText) rootView.findViewById(R.id.wifi_ssid);

        etPasswordWifi = (EditText) rootView.findViewById(R.id.wifi_password);

        mProgressView = rootView.findViewById(R.id.auth_progress);

        nextFab = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_config_form.setVisibility(View.GONE);
                showProgress(true);
                //Authenticate wifi;

                //call ScanDevicesAsyncTask activity
                Intent intent = new Intent(getActivity(), ScanDevicesActivity.class);
                startActivity(intent);

            }
        });
        return rootView;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

package com.thesis.mmtt2011.homemms.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigNetworkFragment extends Fragment {

    private static final String FRAGMENT_TAG = "ConfigNetworkFragment";
    private EditText etSSIDWifi;
    private EditText etPasswordWifi;
    FloatingActionButton nextFab;

    public ConfigNetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_config_network, container, false);

        etSSIDWifi = (EditText) rootView.findViewById(R.id.wifi_ssid);

        etPasswordWifi = (EditText) rootView.findViewById(R.id.wifi_password);

        nextFab = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSSIDWifi.getText().toString().isEmpty()) {
                    Snackbar.make(rootView, "SSID of Wifi cannot null", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    ContantsHomeMMS.SSIDOfAP = etSSIDWifi.getText().toString();
                    if (!etPasswordWifi.getText().toString().isEmpty()){
                        ContantsHomeMMS.PassOfAP = etPasswordWifi.getText().toString();
                    }
                    //call ScanDevicesAsyncTask activity
                    Intent intent = new Intent(getActivity(), ScanDevicesActivity.class);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }
}

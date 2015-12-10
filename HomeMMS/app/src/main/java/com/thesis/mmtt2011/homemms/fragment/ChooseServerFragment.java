package com.thesis.mmtt2011.homemms.fragment;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.activity.ConnectConfiguredServerFragment;
import com.thesis.mmtt2011.homemms.activity.ScanDevicesActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseServerFragment extends Fragment {

    private static final String CONNECT_CONFIG_SERVER_FRAGMENT_TAG = "ConnectConfiguredServer";
    private static final String CONFIG_NETWORK_FRAGMENT_TAG = "ConfigNetwork";
    private Button btConfiguredServer;
    private Button btNewServer;

    public ChooseServerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_choose_server, container, false);
        btNewServer = (Button) rootView.findViewById(R.id.new_server);
        btNewServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call ConnectConfiguredServer
                ConfigNetworkFragment configNetworkFragment = new ConfigNetworkFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.choose_server_fragment, configNetworkFragment, CONFIG_NETWORK_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                /*//call ScanDevicesAsyncTask activity
                Intent intent = new Intent(getActivity(), ScanDevicesActivity.class);
                startActivity(intent);*/
            }
        });

        btConfiguredServer = (Button) rootView.findViewById(R.id.configured_server);
        btConfiguredServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call ConnectConfiguredServer
                ConnectConfiguredServerFragment connectConfiguredServerFragment = new ConnectConfiguredServerFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.choose_server_fragment, connectConfiguredServerFragment, CONNECT_CONFIG_SERVER_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                /*Intent intent = new Intent(getActivity(), ConnectConfiguredServerActivity.class);
                startActivity(intent);*/
            }
        });

        return rootView;
    }
}

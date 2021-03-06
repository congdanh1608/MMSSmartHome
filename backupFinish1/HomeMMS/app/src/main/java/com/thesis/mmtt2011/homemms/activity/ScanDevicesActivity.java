package com.thesis.mmtt2011.homemms.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thesis.mmtt2011.homemms.Network.ScanDevicesAsyncTask;
import com.thesis.mmtt2011.homemms.Network.UtilsNetwork;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.adapter.DeviceAdapter;
import com.thesis.mmtt2011.homemms.model.Device;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanDevicesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static List<Device> devices;

    private UtilsMain utilsMain;
    private UtilsNetwork utilsNetworkNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        utilsMain = new UtilsMain(this);
        utilsNetworkNetwork = new UtilsNetwork(this);

        //Copy nmblookup file to data.
        Installnmblookup();

        mRecyclerView = (RecyclerView) findViewById(R.id.device_recycler_view);
        // Scan devices
        initScan();
        mAdapter = new DeviceAdapter(devices, this);
        mRecyclerView.setAdapter(mAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getActivity());
        //use a grid layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSendMsg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initScan();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    private void initScan() {
        devices = new ArrayList<Device>();
        if (utilsNetworkNetwork.getMacAddress() != null) {
            new ScanDevicesAsyncTask(this).execute();
        } else utilsMain.ShowToast("Device don't connect network or wi-fi is disabled");
    }

    private void Installnmblookup() {
        String nmblookupPath = utilsNetworkNetwork.getnmbLookupLocation();

        //Check CPU x86?
        Boolean x86Cpu = UtilsMain.isX86Cpu();

        //Scan Network with nmblookup.
        if (!UtilsMain.ChecknmblookupAvalability(nmblookupPath)) {
            if (x86Cpu) {
                UtilsMain.copyAsset(getAssets(), "x86/" + ContantsHomeMMS.nmblookupName, nmblookupPath);
            } else {
                UtilsMain.copyAsset(getAssets(), ContantsHomeMMS.nmblookupName, nmblookupPath);
            }
            try {
                Process p = Runtime.getRuntime().exec("chmod 774 " + nmblookupPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        /*if (id == R.id.action_scan) {
            //Scan devices
            initScan();
            return true;
        }*/

        /*if (id == R.id.action_install_config) {
            //InstallRaspAsyncTask into device was choosen

            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}

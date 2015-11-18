package com.thesis.mmtt2011.homemms.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thesis.mmtt2011.homemms.Network.ScanDevicesAsyncTask;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.Utils;
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

    private com.thesis.mmtt2011.homemms.Network.Utils utilsNetwork;

    private void initScan(){
        devices = new ArrayList<Device>();
        new ScanDevicesAsyncTask(this).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        utilsNetwork = new com.thesis.mmtt2011.homemms.Network.Utils(this);

        //Copy nmblookup file to Android.


        mRecyclerView = (RecyclerView)findViewById(R.id.device_recycler_view);
        initScan();
        mAdapter = new DeviceAdapter(devices);
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        if (id == R.id.action_scan) {
            new ScanDevicesAsyncTask(this).execute();
            return true;
        }

        if (id == R.id.action_install_config) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Installnmblookup(){
        String nmblookupPath = utilsNetwork.getnmbLookupLocation();

        //Check CPU x86?
        Boolean x86Cpu = Utils.isX86Cpu();

//        Scan Network with nmblookup.
//        if (Utils.CreateFolder(ContantsHomeMMS.AppFolder) && !Utils.ChecknmblookupAvalability(nmblookupPath)) {
//            if (x86Cpu) {
//                Utils.copyAsset(getAssets(), "x86/" + ConstPath.nmblookupName, ConstPath.getnmbLookupLocation(this));
//            } else {
//                Utils.copyAsset(getAssets(), ConstPath.nmblookupName, ConstPath.getnmbLookupLocation(this));
//            }
//            try {
//                Process p = Runtime.getRuntime().exec("chmod 774 " + ConstPath.getnmbLookupLocation(this));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}

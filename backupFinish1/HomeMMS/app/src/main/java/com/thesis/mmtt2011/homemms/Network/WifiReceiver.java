package com.thesis.mmtt2011.homemms.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.thesis.mmtt2011.homemms.activity.MainActivity;

/**
 * Created by CongDanh on 03/12/2015.
 */
public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {
            //Wifi change states;
            MainActivity.createClient();
            Log.d("Wifi State", "Wifi change state.");
        }

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            //notify app is offline
            MainActivity.createClient();
            Log.d("Wifi State", "Wifi is off");
        }
    }
}

package thesis.danh.avpdemo.Network;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;

import thesis.danh.avpdemo.Model.Device;

/**
 * Created by CongDanh on 31/10/2015.
 */
public class Utils {
    private Activity activity;

    public Utils(Activity activity){
        this.activity = activity;
    }

    protected Device getInfoCurrentDevice(){
        String Mac = null, deviceName = null, IP = null;
        //get Mac
        Mac = getMacAddress();
        //get IP Address
        IP = getIpAddress();
        //get Device Name
        deviceName = getDeviceName();

        return new Device(deviceName, IP, Mac);
    }

    public String getMacAddress() {
        WifiManager wimanager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }

    public String getIpAddress(){
        WifiManager wimanager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wimanager.getConnectionInfo().getIpAddress());
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}

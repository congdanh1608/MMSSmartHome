package com.thesis.mmtt2011.homemms.Network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;

import com.thesis.mmtt2011.homemms.model.Device;

import ch.ethz.ssh2.Connection;


/**
 * Created by CongDanh on 31/10/2015.
 */
public class UtilsNetwork {
    private Activity activity;

    public UtilsNetwork(Activity activity){
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

    //Check Device is Connect wifi
    public boolean checkIsWifiConnect(){
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) return true;
        return false;
    }

    //Get Mac Address of Access point
    public String getMacAddressOfAP() {
        WifiManager wimanager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getBSSID();
        if (macAddress == null) {
            macAddress = null; //"Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
    }

    //Get SSID of aceess point.
    public String getSSIDOfAP() {
        WifiManager wimanager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        String ssid = wimanager.getConnectionInfo().getSSID();
        if (ssid == null) {
            ssid = null; //"Device don't have mac address or wi-fi is disabled";
        }
        return ssid;
    }

    //Get MAC Address of Device
    public String getMacAddress() {
        WifiManager wimanager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = null; //"Device don't have mac address or wi-fi is disabled";
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

    public static boolean CheckMacPi(String MacAddress){
        if (MacAddress.contains("b8:27:eb")){
            return true;
        };
        return false;
    }

    public static long ipToLong(String ipAddress) {
        long result = 0;
        String[] atoms = ipAddress.split("\\.");
        for (int i = 3; i >= 0; i--) {
            result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
        }
        return result & 0xFFFFFFFF;
    }

    public static String longToIp(long ip) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                sb.insert(0, '.');
            }
            ip >>= 8;
        }
        return sb.toString();
    }

    public static String fromBinaryToIp(String binary) {
        return longToIp(Long.parseLong(binary, 2));
    }

    public static String fromIpToHex(String ipAddress) {
        return Long.toHexString(ipToLong(ipAddress));
    }

    public static String fromHexToIp(String hex) {
        return longToIp(Long.parseLong(hex, 16));
    }

    public static String addTwoHexToString(String a, String b) {
        long number = Long.parseLong(a, 16) + Long.parseLong(b, 16);
        return Long.toHexString(number);
    }

    public static int countIP(String hexStartAddress, String hexEndAddress){
        int cout = 0;
        while (!hexStartAddress.equals(hexEndAddress)) {
            cout++;
            hexStartAddress = addTwoHexToString(hexStartAddress, "01");
        }
        return cout;
    }

    public String getnmbLookupLocation(){
        return "./data/data/" + activity.getPackageName() + "/" + "nmblookup";
    }

}

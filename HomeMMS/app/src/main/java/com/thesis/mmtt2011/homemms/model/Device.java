package com.thesis.mmtt2011.homemms.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Xpie on 10/22/2015.
 */
public class Device {
    private String deviceName;
    //private int iconIndex; //0 is rpi device, 1 PC device, 2 other
    private Drawable deviceIcon;
    private String IPAddress;
    private String MacAddress;
    protected String username;
    protected String password;
    protected int deviceType;   //0-Unknown 1-Rash 2-PC 3-Smart.

    public Device(String deviceName, Drawable deviceIcon, String IPAddress, String MacAddress) {
        this.deviceName = deviceName;
        this.deviceIcon = deviceIcon;
        this.IPAddress = IPAddress;
        this.MacAddress = MacAddress;
        username = null;
        password = null;
        deviceType = 0;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceName(){ return deviceName; }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public Drawable getDeviceIcon() {
        return deviceIcon;
    }

    public void setDeviceIcon(Drawable deviceIcon) {
        this.deviceIcon = deviceIcon;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    public String toString(){
        return IPAddress;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType() {
        return deviceType;
    }
}

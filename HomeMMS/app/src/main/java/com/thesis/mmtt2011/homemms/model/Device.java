package com.thesis.mmtt2011.homemms.model;
/**
 * Created by Y Pham on 4/20/2015.
 */
public class Device {
    private String deviceName;
    private String IPAddress;
    private String MacAddress;
    protected String username;
    protected String password;
    protected int deviceType;   //0-Unknown 1-Rash Pi 2-PC

    public Device(){
        deviceType = 0;
    }

    public Device(String deviceName, String IPAddress, String MacAddress){
        this.deviceName = deviceName;
        this.IPAddress = IPAddress;
        this.MacAddress = MacAddress;
        username = null;
        password = null;
        deviceType = 0;
    }

    public Device(String deviceName, String IPAddress, String MacAddress, int deviceType){
        this.deviceName = deviceName;
        this.IPAddress = IPAddress;
        this.MacAddress = MacAddress;
        username = null;
        password = null;
        this.deviceType = deviceType;
    }

    public Device(String deviceName, String IPAddress, String MacAddress, String username, String password, int deviceType){
        this.deviceName = deviceName;
        this.IPAddress = IPAddress;
        this.MacAddress = MacAddress;
        this.username = username;
        this.password = password;
        this.deviceType = deviceType;
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

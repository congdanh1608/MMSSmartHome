package com.thesis.mmtt2011.homemms.model;

import android.graphics.drawable.Drawable;

import java.net.Socket;

import ch.ethz.ssh2.Connection;

/**
 * Created by Y Pham on 4/21/2015.
 */
public class RaspberryPiClient extends Device {

    private boolean isConfigured;
    private Connection connection;
    private Socket socket;
    private boolean isChosenConfig;

    public RaspberryPiClient(String deviceName, String IPAddress, String MacAddress) {
        super(deviceName, IPAddress, MacAddress);
        isConfigured = false;
        isChosenConfig = true;
        connection = null;
        socket = null;
        this.username = "pi";
        this.password = "raspberry";
    }

    public RaspberryPiClient(String deviceName, String IPAddress, String MacAddress, Boolean isConfigured, Boolean isChosenConfig) {
        super(deviceName, IPAddress, MacAddress);
        this.isConfigured = isConfigured;
        this.isChosenConfig = isChosenConfig;
        connection = null;
        socket = null;
        this.username = "pi";
        this.password = "raspberry";
    }

    public RaspberryPiClient(String deviceName, Drawable deviceIcon, String IPAddress, String MacAddress) {
        super(deviceName, deviceIcon, IPAddress, MacAddress);
        isConfigured = false;
        isChosenConfig = true;
        socket = null;
        connection = null;
        this.username = "pi";
        this.password = "raspberry";
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    public void setIsConfigured(boolean isConfigured) {
        this.isConfigured = isConfigured;
    }

    public boolean getIsConfigured(){
        return isConfigured;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setIsChosenConfig(boolean isChosenConfig) {
        this.isChosenConfig = isChosenConfig;
    }

    public boolean getIsChosenConfig(){
        return isChosenConfig;
    }
}

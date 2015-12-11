package com.thesis.mmtt2011.homemms.model;

import java.net.Socket;

import ch.ethz.ssh2.Connection;

/**
 * Created by Y Pham on 4/21/2015.
 */
public class RaspberryPi extends Device {

    private boolean isConfigured;
    private Connection connection;
    private Socket socket;

    public RaspberryPi(){
        super();
        isConfigured = false;
        connection = null;
        socket = null;
        this.username = "pi";
        this.password = "raspberry";
    }

    public RaspberryPi(String deviceName, String IPAddress, String MacAddress) {
        super(deviceName, IPAddress, MacAddress);
        isConfigured = false;
        connection = null;
        socket = null;
        this.username = "pi";
        this.password = "raspberry";
    }

    public RaspberryPi(String deviceName, String IPAddress, String MacAddress, Boolean isConfigured) {
        super(deviceName, IPAddress, MacAddress);
        this.isConfigured = isConfigured;
        connection = null;
        socket = null;
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
}

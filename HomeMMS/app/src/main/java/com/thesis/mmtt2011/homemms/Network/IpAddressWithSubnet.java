package com.thesis.mmtt2011.homemms.Network;

/**
 * Created by CongDanh on 18/11/2015.
 */
public class IpAddressWithSubnet {
    private String ipAddress;
    private int subnet;

    public int getSubnet() {
        return subnet;
    }

    public void setSubnet(int subnet) {
        this.subnet = subnet;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}

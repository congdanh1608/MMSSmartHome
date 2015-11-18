package com.thesis.mmtt2011.homemms.Network;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by CongDanh on 18/11/2015.
 */
public class Interface {

    private static ArrayList<NetworkInterface> getNetworkInterface() {
        ArrayList<NetworkInterface> result = new ArrayList<NetworkInterface>();
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    result.add(networkInterface);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<IpAddressWithSubnet> GetInterfaceAddresses() {
        ArrayList<IpAddressWithSubnet> listOfIpAddressWithSubnet = new ArrayList<IpAddressWithSubnet>();
        ArrayList<NetworkInterface> listInter = getNetworkInterface();
        for (NetworkInterface networkInterface : listInter) {
            try {
                List<InterfaceAddress> listOfInterfaceAddresses =
                        networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : listOfInterfaceAddresses) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {
                        int subnet = interfaceAddress.getNetworkPrefixLength();
                        System.out.println("address "
                                + interfaceAddress.getAddress().getHostAddress()
                                + "/"
                                + subnet
                                + ", is siteLocal "
                                + interfaceAddress.getAddress().isSiteLocalAddress());
                        IpAddressWithSubnet ip = new IpAddressWithSubnet();
                        ip.setIpAddress(interfaceAddress.getAddress().getHostAddress());
                        ip.setSubnet(subnet);
                        if (subnet != 32)
                            listOfIpAddressWithSubnet.add(ip);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return listOfIpAddressWithSubnet;
    }

}

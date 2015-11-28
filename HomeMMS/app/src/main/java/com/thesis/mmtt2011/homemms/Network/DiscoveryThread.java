package com.thesis.mmtt2011.homemms.Network;

import android.app.Activity;

import com.thesis.mmtt2011.homemms.activity.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class DiscoveryThread implements Runnable {

    private static Activity mActivity;
    private static Utils utils;
    DatagramSocket datagramSocket;

    @Override
    public void run() {
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();
            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 3333);
                datagramSocket.send(sendPacket);
                System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }
            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 3333);
                        datagramSocket.send(sendPacket);
                    } catch (Exception e) {
                    }
                    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            datagramSocket.receive(receivePacket);
            //We have a response
            System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP
                String ipOfRasp = convertStringIP(receivePacket.getAddress().toString());
                MainActivity.rasp.setIPAddress(ipOfRasp);
                MainActivity.rasp.setMacAddress(Discover.getMacFromArpCache(ipOfRasp));
                MainActivity.rasp.setDeviceName(Discover.getHostNameNmblookup(ipOfRasp, utils.getnmbLookupLocation()));
                System.out.println(receivePacket.getAddress());
            }
            //Close the port!
            datagramSocket.close();
        } catch (IOException ex) {
        }
    }

    private String convertStringIP(String ip_){
        String ip = null;
        if (ip_.contains("/")){
            ip = ip_.substring(1);
        }
        else ip = ip_;
        return ip;
    }

    public static DiscoveryThread getInstance(Activity activity) {
        mActivity = activity;
        utils = new Utils(mActivity);
        return DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {
        private static final DiscoveryThread INSTANCE = new DiscoveryThread();

    }
}

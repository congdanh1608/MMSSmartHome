package com.thesis.mmtt2011.homemms.implement;

import com.thesis.mmtt2011.homemms.model.RaspberryPiClient;

import java.util.ArrayList;

/**
 * Created by CongDanh on 20/11/2015.
 */
public class LoadCommands {

    public static ArrayList<String> addCommandsUnzip(RaspberryPiClient raspberryPiClient) {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        listOfCommands.add("unzip -o /home/"
                + raspberryPiClient.getUsername()
                + "/configrasppi.zip && echo EndCommands");
        return listOfCommands;
    }

    public static ArrayList<String> addCommandsConfig(RaspberryPiClient raspberryPiClient, Boolean isPublicWifi, String WifiSSID, String WifiPassword) {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        //Config screen of Rasp Pi.
        listOfCommands.add("sudo cp inittab /etc/inittab && sudo cp xinitrc /boot/xinitrc && sudo cp rc.local /etc/rc.local && echo EndCommands");

        //Config wifi for Rasp Pi.
        listOfCommands.add("sudo cp wifi_config/interfaces /etc/network/ && sudo cp wifi_config/wpa_supplicant.conf /etc/wpa_supplicant/ && echo EndCommands");
        if (isPublicWifi) {
            //public wifi
            String networkPublic = String.format("printf 'network={\n" +
                    "        ssid=\"%s\"\n" +
                    "        key_mgmt=NONE\n" +
                    "}' | sudo tee -a /etc/wpa_supplicant/wpa_supplicant.conf && echo EndCommands", WifiSSID);
            listOfCommands.add(networkPublic);
        } else {
            //protected wifi
            //add both WPA1 & WPA2 for this SSID & pass
            String networkWPA = String.format("printf 'network={\n" +
                    "   ssid=\"%s\"\n" +
                    "   psk=\"%s\"\n" +
                    "   key_mgmt=WPA-PSK\n" +
                    "}' | sudo tee -a /etc/wpa_supplicant/wpa_supplicant.conf && echo EndCommands", WifiSSID, WifiPassword);
            listOfCommands.add(networkWPA);
        }
        //Remove hostap
        listOfCommands.add("sudo rm /usr/sbin/hostapd & sudo apt-get -y purge isc-dhcp-server && echo EndCommands");
        return listOfCommands;
    }

    public static ArrayList<String> addCommandsReboot() {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        listOfCommands.add("sudo reboot\n");
        return listOfCommands;
    }
}

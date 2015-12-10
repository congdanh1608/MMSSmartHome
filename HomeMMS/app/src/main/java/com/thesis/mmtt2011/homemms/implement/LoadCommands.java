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
/*
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
*/
        //Install mysql
        listOfCommands.add("echo \"mysql-server-5.5 mysql-server/root_password password 123456\" | debconf-set-selections\n" +
                "echo \"mysql-server-5.5 mysql-server/root_password_again password 123456\" | debconf-set-selections\n");
        //Config mysql
        listOfCommands.add("sudo chmod 644 mysql.sh && ./mysql.sh homemmsdb homemms 123456");
        return listOfCommands;
    }

    public static ArrayList<String> addCommandsReboot() {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        listOfCommands.add("sudo reboot\n");
        return listOfCommands;
    }

    public static String loadCommandsCreateFolder(String filePath, RaspberryPiClient raspberryPiClient) {
        String path = "/home/" + raspberryPiClient.getUsername() + filePath;
        return "sudo mkdir " + path + " && sudo chmod 777 " + path + " && echo EndCommands";
    }

    public static ArrayList<String> addCommandsRemoveInstall(RaspberryPiClient raspberryPiClient) {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        //Restore file backup + remove file.
        listOfCommands.add("sudo rm -rf /etc/inittab && sudo rm -rf /boot/xinitrc && sudo cp bak/etc/rc.local /etc/rc.local && sudo cp bak/etc/network/interfaces /etc/network/ && sudo cp bak/etc/wpa_supplicant/wpa_supplicant.conf /etc/wpa_supplicant/");
        listOfCommands.add("sudo rm -rf bak deb wifi_config inittab mysql.sh rc.local xinitrc");
//        listOfCommands.add("sudo chkconfig off Server && sudo");
        return listOfCommands;
    }
}

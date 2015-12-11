package com.thesis.mmtt2011.homemms.implement;

import com.thesis.mmtt2011.homemms.model.RaspberryPi;

import java.util.ArrayList;

/**
 * Created by CongDanh on 20/11/2015.
 */
public class LoadCommands {

    public static ArrayList<String> addCommandsUnzip(RaspberryPi raspberryPi) {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        listOfCommands.add("unzip -o /home/"
                + raspberryPi.getUsername()
                + "/configrasppi.zip && echo EndCommands");
        return listOfCommands;
    }

    public static ArrayList<String> addCommandsConfig(RaspberryPi raspberryPi, Boolean isPublicWifi, String WifiSSID, String WifiPassword) {
        ArrayList<String> listOfCommands = new ArrayList<String>();

        //Config screen of Rasp Pi not sleep & run Server.jar
        listOfCommands.add("sudo sed -i.bak \"s|exec /usr/bin/X -nolisten tcp \"$@\"|exec /usr/bin/X -s 0 dpms -nolisten tcp \"$@\"|\" /etc/X11/xinit/xserverrc && sudo echo -e \"#~\"" + raspberryPi.getUsername() + "\"/.xinitrc\\n java -jar /home/" + raspberryPi.getUsername() + "/Server.jar " + raspberryPi.getUsername() + "\" > /home/" + raspberryPi.getUsername() + "/.xinitrc && echo EndCommands");

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
        //Install mysql
        listOfCommands.add("sudo dpkg -i deb/dep_mysql/*.deb && sudo dpkg -i deb/\"mysql-client-5.5_5.5.46-0+deb8u1_armhf.deb\" && sudo echo \"mysql-server-5.5 mysql-server/root_password password 123456\" | sudo debconf-set-selections && sudo echo \"mysql-server-5.5 mysql-server/root_password_again password 123456\" | sudo debconf-set-selections && sudo dpkg -i deb/mysql-server-5.5_5.5.46-0+deb8u1_armhf.deb && echo EndCommands\n");
        //Config mysql & Remove hostap
        listOfCommands.add("sudo chmod 644 mysql.sh && ./mysql.sh homemmsdb homemms 123456 && sudo rm /usr/sbin/hostapd & sudo apt-get -y purge isc-dhcp-server && echo EndCommands");
        return listOfCommands;
    }

    public static ArrayList<String> addCommandsReboot() {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        listOfCommands.add("sudo reboot\n");
        return listOfCommands;
    }

    public static String loadCommandsCreateFolder(String filePath, RaspberryPi raspberryPi) {
        String path = "/home/" + raspberryPi.getUsername() + filePath;
        return "sudo mkdir " + path + " && sudo chmod 777 " + path + " && echo EndCommands";
    }

    public static ArrayList<String> addCommandsRemoveInstall(RaspberryPi raspberryPi) {
        ArrayList<String> listOfCommands = new ArrayList<String>();
        //Restore file backup + remove file.
        listOfCommands.add("sudo cp bak/interfaces /etc/network/ && sudo cp bak/wpa_supplicant.conf /etc/wpa_supplicant/");
        listOfCommands.add("sudo rm -rf bak deb wifi_config mysql.sh configrasppi.zip");
//        listOfCommands.add("sudo chkconfig off Server && sudo");
        return listOfCommands;
    }
}

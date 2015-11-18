package com.thesis.mmtt2011.homemms.Network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by CongDanh on 18/11/2015.
 */
public class Discover {

    //UDP port 137
    public static String getHostNameNmblookup(String IPAddress, String nmbLookupLocation){
        StringBuilder output = new StringBuilder();
        try {
            String cmd = nmbLookupLocation + " -A " + IPAddress;
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bReaderInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //Read output from command.
            while ((bReaderInput.readLine()) != null) {
                output.append(bReaderInput.readLine());
            }
            bReaderInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = output.toString();
        String Hostname = "Unknown";
        if (line.contains("ACTIVE") || line.contains("WORKGROUP")){
            String s = line.substring(0, line.indexOf(' ')).replace("\t","");
            if(s.contains("WORKGROUP")) Hostname = (line.split("<ACTIVE>")[1]).split("<")[0].replace("\t","");
            else Hostname = s;
        }
        return Hostname;
    }

    public static String getMacFromArpCache(String ip) {
        if (ip == null) return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        return mac;
                    } else {
                        return "Unknown";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Unknown";
    }
}

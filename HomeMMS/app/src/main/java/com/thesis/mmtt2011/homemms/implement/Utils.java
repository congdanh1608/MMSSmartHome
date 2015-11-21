package com.thesis.mmtt2011.homemms.implement;

import android.app.Activity;

import com.thesis.mmtt2011.homemms.model.RaspberryPiClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * Created by CongDanh on 20/11/2015.
 */
public class Utils {

    //limited Heap Memory less 64MB
    //Crash out of memory if file's size > 30MB
    public static byte[] LoadFileConfig(Activity activity) {
        byte[] appClientData = null;
        InputStream stream = null;
        stream = activity.getResources().openRawResource(
                activity.getResources().getIdentifier("configrasppi", "raw",
                        activity.getPackageName()));

        byte[] buffer = new byte[2048];
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        int len = 0;
        try {
            while ((len = stream.read(buffer)) > 0) {
                byteOut.write(buffer, 0, len);
            }
            appClientData = byteOut.toByteArray();
            stream.close();
            byteOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appClientData;
    }

    public static boolean pushFileConfigToPi(byte[] bytes, RaspberryPiClient rasp) {
        if (bytes == null) return false;
        try {
            SCPClient scpc = rasp.getConnection().createSCPClient();
            scpc.put(bytes, "configrasppi.zip", "/home/" + rasp.getUsername() + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getResponse(Session session) {
        BufferedReader br;
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            InputStream stdout = new StreamGobbler(session.getStdout());
            br = new BufferedReader(new InputStreamReader(stdout));
            String line = br.readLine();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void excCommand(RaspberryPiClient raspberryPiClient, ArrayList<String> listOfCommands) {
        Connection connection = raspberryPiClient.getConnection();
        try {
            while (listOfCommands.size() > 0) {
                String command = listOfCommands.remove(0);
                command += "\n";
                try {
                    Session session = connection.openSession();
                    session.execCommand(command);
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

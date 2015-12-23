package com.thesis.mmtt2011.homemms.SSH;

import com.thesis.mmtt2011.homemms.implement.UtilsImple;
import com.thesis.mmtt2011.homemms.model.Device;
import com.thesis.mmtt2011.homemms.model.RaspberryPi;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

/**
 * Created by CongDanh on 20/10/2015.
 */
public class UtilsSSH {
    public static boolean connectSSH(RaspberryPi raspberryPi) {
        Connection connection = new Connection(raspberryPi.getIPAddress());
        try {
            connection.connect(null, 3000, 3000);
            if (connection.authenticateWithPassword(raspberryPi.getUsername(), raspberryPi.getPassword())) {
                raspberryPi.setConnection(connection);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean disconnectSSH(RaspberryPi raspberryPi) {
        Connection connection = raspberryPi.getConnection();
        if (connection != null) {
            connection.close();
        }
        return true;
    }

    public static byte[] loadResourceFromPath(String path) {
        byte[] byteFileSource = null;
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            int len = 0;
            while ((len = fileInputStream.read(buffer)) > 0) {
                byteOut.write(buffer, 0, len);
            }
            byteFileSource = byteOut.toByteArray();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteFileSource;
    }

    public static int pushFile(byte[] byteFile, String fName, String pathTo, RaspberryPi raspberryPi) {
        if (byteFile != null) {
            if (raspberryPi.getConnection() != null) {
                try {
                    SCPClient scpc = raspberryPi.getConnection().createSCPClient();
                    scpc.put(byteFile, fName, "/home/" + raspberryPi.getUsername() + pathTo);
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;  //Error not know
                }
            } else return -2; //Error not connect PI
        } else return 0; //Error not data byte
        return 1;  //Error not know
    }

    public static boolean CheckIsRaspPiDefault(Device device) {
        Connection connection = new Connection(device.getIPAddress());
        try {
            connection.connect(null, 3000, 3000);
            if (connection.authenticateWithPassword(ContantsHomeMMS.userRaspPi, ContantsHomeMMS.passRaspPi)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int CheckRaspPiConfigured(RaspberryPi rasp) {
        Connection connection = new Connection(rasp.getIPAddress());
        try {
            connection.connect(null, 3000, 3000);
            if (connection.authenticateWithPassword(rasp.getUsername(), rasp.getPassword())) {
                String s = "ls /home/" + rasp.getUsername();
                String result = excCommandStringWithResult(connection, s);
                if (result != null && result.contains("RaspServer.jar")) {
                    return 1; // It is Rasp was configured.
                }
                return 0; // It is Rasp was not configured.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; //It is not Rasp
    }

    public static String excCommandStringWithResult(Connection connection, String string) {
        String result = null;
        if (string != null)
            string += "\n";
        try {
            Session session = connection.openSession();
            session.execCommand(string);
            result = UtilsImple.getResponse(session);
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

package thesis.danh.avpdemo.SSH;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import thesis.danh.avpdemo.Model.RaspberryPiClient;

/**
 * Created by CongDanh on 20/10/2015.
 */
public class Utils {
    private Activity activity;

    public Utils(Activity activity) {
        this.activity = activity;
    }

    public boolean connectSSH(RaspberryPiClient raspberryPiClient) {
        Connection connection = new Connection(raspberryPiClient.getIPAddress());
        try {
            connection.connect(null, 3000, 3000);
            if (connection.authenticateWithPassword(raspberryPiClient.getUsername(), raspberryPiClient.getPassword())) {
                raspberryPiClient.setConnection(connection);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disconnectSSH(RaspberryPiClient raspberryPiClient) {
        Connection connection = raspberryPiClient.getConnection();
        if (connection!=null) {
            connection.close();
        }
        return true;
    }

    public byte[] loadResource(String path) {
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

    public int pushFile(byte[] byteFile, String fName , RaspberryPiClient raspberryPiClient) {
        if (byteFile != null) {
            if (raspberryPiClient.getConnection()!=null) {
                try {
                    SCPClient scpc = raspberryPiClient.getConnection().createSCPClient();
                    scpc.put(byteFile, fName, "/home/" + raspberryPiClient.getUsername() + "/");
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;  //Error not know
                }
            }else return -2; //Error not connect PI
        }else return 0; //Error not data byte
        return 1;  //Error not know
    }

}

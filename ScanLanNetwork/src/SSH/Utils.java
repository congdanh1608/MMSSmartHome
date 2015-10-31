package SSH;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Model.RaspberryPiClient;
import ch.ethz.ssh2.SCPClient;

public class Utils {

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

//    public int pushFile(byte[] byteFile, String fName , RaspberryPiClient raspberryPiClient) {
//        if (byteFile != null) {
//            if (raspberryPiClient.getConnection()!=null) {
//                try {
//                    SCPClient scpc = raspberryPiClient.getConnection().createSCPClient();
//                    scpc.put(byteFile, fName, "/home/" + raspberryPiClient.getUsername() + "/");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return -1;  //Error not know
//                }
//            }else return -2; //Error not connect PI
//        }else return 0; //Error not data byte
//        return 1;  //Error not know
//    }
}

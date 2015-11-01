package thesis.danh.avpdemo.Socket;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import thesis.danh.avpdemo.MainActivity;

/**
 * Created by CongDanh on 31/10/2015.
 */
public class RecieveFile {

    public final static int SOCKET_PORT = 6666;
    public final static String SERVER = MainActivity.rasp.getIPAddress();
    public static String pathFile = null;
    public final static int FILE_SIZE = 20022386;

    public static void recieveFileFromServer(String fName) {
        pathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fName;
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        try {
            sock = new Socket(SERVER, SOCKET_PORT);
            System.out.println("Connecting...");

            //recieve file
            byte[] mybytearray = new byte[FILE_SIZE];
            InputStream is = sock.getInputStream();
            fos = new FileOutputStream(pathFile);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray, 0, mybytearray.length);
            current = bytesRead;
            do {
                bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            } while (bytesRead > -1);

            bos.write(mybytearray, 0, current);
            bos.flush();
            System.out.println("File " + pathFile + " downloaded (" + current + " bytes read)");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (bos != null) bos.close();
                if (sock != null) sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

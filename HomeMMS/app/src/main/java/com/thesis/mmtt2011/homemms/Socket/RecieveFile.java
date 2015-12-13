package com.thesis.mmtt2011.homemms.Socket;

import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.model.ObjectFile;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by CongDanh on 31/10/2015.
 */
public class RecieveFile {

    public final static int SOCKET_PORT = 6666;
    public final static String SERVER = MainActivity.rasp.getIPAddress();
    public static String pathFile = null;
    public final static int FILE_SIZE = 20022386;
    public static Socket socket;

    public static void recieveFileFromServer() {
        try {
            //Server low down, client delay 1s to Server open port listening.
            Thread.sleep(1000);
            socket = new Socket(SERVER, SOCKET_PORT);

            System.out.println("Client: Just connected to " + socket.getRemoteSocketAddress());

            OutputStream outToServer = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
//            out.writeUTF("Hello Server" + socket.getLocalSocketAddress());

            InputStream inFromServer = socket.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
//            System.out.println("Client gets the message from the server: "+in.readUTF());

            //Read Object
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<ObjectFile> objectFiles = (ArrayList<ObjectFile>) objectInputStream.readObject();

            //Read File Object
            readObjectFile(objectFiles);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }finally {
            try {
                if (socket!=null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readObjectFile(ArrayList<ObjectFile> objectFiles) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        for (ObjectFile objectFile : objectFiles) {
            String sender = objectFile.getSender();
            String nameFile = objectFile.getNameFile();
            byte[] contentInBytes = objectFile.getContentInBytes();

            try {
                String pathUserReceive = ContantsHomeMMS.AppFolder + "/" + sender;
                if (UtilsMain.CreateFolder(pathUserReceive)) {
                    String pathFileReceive = pathUserReceive + "/" + nameFile;
                    File fileReceive = new File(pathFileReceive);
                    if (fileReceive.exists()) {
                        fileReceive.createNewFile();
                    }
                    fos = new FileOutputStream(fileReceive);
                    bos = new BufferedOutputStream(fos);
                    bos.write(contentInBytes);
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) fos.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*public static void recieveFileFromServer(String fName) {
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
    }*/
}

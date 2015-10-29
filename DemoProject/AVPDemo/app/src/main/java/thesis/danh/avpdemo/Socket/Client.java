package thesis.danh.avpdemo.Socket;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import thesis.danh.avpdemo.RaspberryPiClient;
import thesis.danh.avpdemo.Socket.KeyString.Command;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class Client {
    private static Socket socketB;
    private static PrintWriter printWriter;
    private static String msgRecieve = "";
    private int port;
    private String ClientName = "Android";
    private SocketControl socketControl;
    private RaspberryPiClient rasp;
    private Handler handler;

    public Client(RaspberryPiClient rasp, int port) {
        this.rasp = rasp;
        this.port = port;

        handler = new Handler();
        //Demo info
        socketControl = new SocketControl(this, "192.168.0.106", "Android Danh", "as:de:sd:sd:Ds:Ad");
    }

    public void StartSocket() {
        new Thread(new ClientThread()).start();
    }

    public class ClientThread implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                socketB = new Socket(rasp.getIPAddress(), 2222);
                SendMessageInfoOfClient();

                BufferedReader input = new BufferedReader(new InputStreamReader(socketB.getInputStream()));
                final String box = input.readLine();
                handler.post(new Runnable() {
                    public void run() {
//
                    }
                });

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean CloseConnectSocket() {
        if (socketB != null && socketB.isConnected()) try {
            socketB.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //Send message contain info of client.
    public boolean SendMessageInfoOfClient() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createInfoClient());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send message from textview.
    public boolean SendMessages(String msg) {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createSendMessage(msg));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send message contain info of file was pushed to server PI.
    public boolean SendMessageInfoFilePush(String fileName) {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createInfoMessage(fileName, Command.PUSHKEY));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    public String getMsg() {
        return msgRecieve;
    }
}

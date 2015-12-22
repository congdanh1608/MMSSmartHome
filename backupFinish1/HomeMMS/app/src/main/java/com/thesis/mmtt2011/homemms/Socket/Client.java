package com.thesis.mmtt2011.homemms.Socket;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.RaspberryPi;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class Client {
    private static Socket socketB;
    private static PrintWriter printWriter;
    private static String msgRecieve = "";
    private int port;
    private SocketControl socketControl;
    private RaspberryPi rasp;
    private Handler handler;
    private Activity activity;
    private ClientThread clientThread;
    public static int loginSuccess = 0, changeprofile = 0;

    public Client(RaspberryPi rasp, int port, Activity activity) {
        this.rasp = rasp;
        this.port = port;
        this.activity = activity;

        handler = new Handler();
        socketControl = new SocketControl(this, MainActivity.myUser, activity);
    }

    public void StartSocket() {
//        new Thread(new ClientThread()).start();
        clientThread = new ClientThread();
        clientThread.start();
    }

    public void ReconnectSocket() {
        clientThread.close();
        clientThread.start();
    }

    public class ClientThread extends Thread {
        BufferedReader input;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
//                socketB = new Socket(rasp.getIPAddress(), port);
//                MainActivity.isConnected = true;
//                UtilsMain.showMessage(activity, "Connect to Server Successfully.");
//                SendFirstInfoOfClient();
                boolean firstshow = true;
                while (true) {
                    //try to connect to Server every 20s.
                    try {
                        if (!MainActivity.isConnected) {
                            socketB = new Socket(rasp.getIPAddress(), port);
                            MainActivity.isConnected = true;
                            UtilsMain.showMessage(activity, "Connect to Server Successfully.");
                            SendFirstInfoOfClient();
                        }
                    } catch (IOException ieo) {
                        if (firstshow) {
                            UtilsMain.showMessage(activity, "Cannot connect to Server. May be your Server has problem.");
                            firstshow = false;
                        }
                        //fail connect, wait 20s to try again.
                        Thread.sleep(10000);
                    }

                    if (socketB != null) {
                        input = new BufferedReader(new InputStreamReader(socketB.getInputStream()));
                        final String temp = input.readLine();
                        handler.post(new Runnable() {
                            public void run() {
                                //Server socket closed.
                                if (temp == null && MainActivity.isConnected) {
                                    MainActivity.isConnected = false;
                                    UtilsMain.showMessage(activity, "Was disconnected to Server. May be your network or Server has problem.");
                                }
                                //Receive message from Server.
                                if (temp != null && !temp.equals("")) {
                                    Log.d("Recieve", temp);
                                    socketControl.getCommand(temp);
                                }
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socketB != null) {
                try {
                    socketB.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean CloseSocket() {
        if (socketB != null) {
            try {
//                SendMessageDisconnnect();
                socketB.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (clientThread != null) {
            clientThread.close();
        }
        return true;
    }

    //Send message contain info reciver who client want send to.
    public boolean SendInfoMessage(Message message) {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createInfoMessage(message));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send message contain info of client.
    public boolean SendMessageInfoOfClient() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream(),  "UTF-8")), true);
                printWriter.println(socketControl.createInfoClient());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send message contain info of client.
   /* public boolean SendLoginInfoOfClient() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createLoginInfoClient());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }*/

    //Send message contain info of client.
    public boolean SendLoginInfoOfClient() {
        loginSuccess = 0;
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createLoginInfoClient());

                do {
                    Thread.sleep(1000);
                    if (loginSuccess==1) return true;
                    else if (loginSuccess==-1) return false;
                }while (loginSuccess!=0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //Send message contain info of client.
    public boolean SendFirstInfoOfClient() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createJsonFirstInfoClient());
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
    public boolean SendMessageInfoFilePush(String fileName, ContantsHomeMMS.TypeFile typeFile) {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createInfoMessage(fileName, typeFile, Command.PUSHKEY));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send mesage notice Client finish note.
    public boolean SendMessageEndNote() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createNoticeEndNote());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send mesage notice Client finish note.
    public boolean SendMessageDisconnnect() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createNoticeDisconnect());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send mesage request Server send file attach of Message.
    public boolean SendRequestFileAttach(Message message) {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createRequestFileAttach(message.getId()));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send mesage request Server delete the message.
    public boolean SendRequestDeleteMessage(Message message) {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createRequestDeleteMessage(message.getId()));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else return false;
        return false;
    }

    //Send mesage request change profile of user
    public boolean SendRequestChangeProfile(User user, String nameDisplay, String Avatar, String newPassword, String oldPassword) {
        changeprofile = 0;
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createRequestChangeProfile(user, nameDisplay, Avatar, newPassword, oldPassword));
                do {
                    Thread.sleep(1000);
                    if (changeprofile==1) return true;
                    else if (changeprofile==-1) return false;
                }while (changeprofile!=0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //Send mesage request Server restore to normal state/ Server will connect to AP.
    public boolean SendRequestServerToNormalState() {
        if (socketB.isConnected()) {
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream())), true);
                printWriter.println(socketControl.createRequestServerToNormailState());
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

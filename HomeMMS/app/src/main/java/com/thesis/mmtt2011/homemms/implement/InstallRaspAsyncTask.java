package com.thesis.mmtt2011.homemms.implement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.model.RaspberryPiClient;

import java.util.ArrayList;

import ch.ethz.ssh2.Session;

/**
 * Created by CongDanh on 20/11/2015.
 */
public class InstallRaspAsyncTask extends AsyncTask<Void, Integer, Void> {
    ProgressDialog pd;
    Activity activity;
    RaspberryPiClient rasp;
    boolean isPublicWifi = false;
    String WifiSSID, WifiPassword;

    public InstallRaspAsyncTask(Activity activity, RaspberryPiClient rasp, Boolean isPublicWifi, String WifiSSID, String WifiPassword) {
        this.activity = activity;
        this.rasp = rasp;
        this.isPublicWifi = isPublicWifi;
        this.WifiSSID = WifiSSID;
        this.WifiPassword = WifiPassword;

        pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Installing...");
        pd.setMessage("Prepaid install.");
        pd.setProgressStyle(pd.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        publishProgress(1, 10);
        com.thesis.mmtt2011.homemms.SSH.Utils.connectSSH(rasp); //Create connection for raspPi.
        publishProgress(2, 10);
        byte[] bytes = Utils.LoadFileConfig(activity);          //Load file config to byte[]
        if (Utils.pushFileConfigToPi(bytes, rasp)) {            //Push file to Rasp Pi
            try {
                publishProgress(3, 10);
                ArrayList<String> listOfCommands = LoadCommands.addCommandsUnzip(rasp);     //Unzip
                while (listOfCommands.size() > 0) {
                    String command = listOfCommands.remove(0);
                    command += "\n";
                    Session session = rasp.getConnection().openSession();
                    session.execCommand(command);
                    while (!Utils.getResponse(session).contains("EndCommands")) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    session.close();
                }

                //Config
                /*listOfCommands = LoadCommands.addCommandsConfig(rasp, isPublicWifi, WifiSSID, WifiPassword);
                while (listOfCommands.size() > 0) {
                    String command = listOfCommands.remove(0);
                    command += "\n";
                    Session session = rasp.getConnection().openSession();
                    session.execCommand(command);
                    while (!Utils.getResponse(session).contains("EndCommands")) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    session.close();
                    //Set Pi was configured
                    rasp.setIsConfigured(true);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values[0]);
        pd.setMax(values[1]);
        pd.setProgress(values[0]);
        switch (values[0]){
            case 1:
                pd.setMessage("Connect to Pi.");
                break;
            case 2:
                pd.setMessage("Load file and push to Pi.");
                break;
            case 3:
                pd.setMessage("Unziping file in Pi.");
                break;
            case 4:
                break;
            case 5:
                pd.setMessage("Install config for " + values[0] + ". Please Wait...");
                break;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pd.dismiss();
        createDialogReboot(activity).show();
    }

    public Dialog createDialogReboot(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Do you want reboot?");
        builder.setTitle("Reboot")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.excCommand(rasp, LoadCommands.addCommandsReboot());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}

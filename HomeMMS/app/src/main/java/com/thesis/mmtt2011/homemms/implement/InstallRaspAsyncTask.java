package com.thesis.mmtt2011.homemms.implement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.thesis.mmtt2011.homemms.Network.Discover;
import com.thesis.mmtt2011.homemms.Network.DiscoveryThread;
import com.thesis.mmtt2011.homemms.Network.UtilsNetwork;
import com.thesis.mmtt2011.homemms.SSH.UtilsSSH;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.model.RaspberryPi;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.util.ArrayList;

import ch.ethz.ssh2.Session;

/**
 * Created by CongDanh on 20/11/2015.
 */
public class InstallRaspAsyncTask extends AsyncTask<Void, Integer, Void> {
    private UtilsMain utilsMain;
    private UtilsNetwork utilsNetwork;
    ProgressDialog pd;
    Activity activity;
    RaspberryPi rasp;
    boolean isPublicWifi = false;
    String WifiSSID, WifiPassword;

    public InstallRaspAsyncTask(Activity activity, RaspberryPi rasp) {
        this.activity = activity;
        this.rasp = rasp;

        utilsMain = new UtilsMain(activity);
        utilsNetwork = new UtilsNetwork(activity);

        pd = new ProgressDialog(activity);

        //check info AP
        if (ContantsHomeMMS.PassOfAP == null) {
            isPublicWifi = true;
        } else {
            isPublicWifi = false;
            WifiSSID = ContantsHomeMMS.SSIDOfAP;
            WifiPassword = ContantsHomeMMS.PassOfAP;
        }
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
        publishProgress(1);
        if (rasp.getConnection() == null) {
            UtilsSSH.connectSSH(rasp); //Create connection for raspPi.
        }
        publishProgress(2);
        byte[] bytes = UtilsImple.LoadFileConfig(activity);          //Load file config to byte[]
        if (UtilsImple.pushFileConfigToPi(bytes, rasp)) {            //Push file to Rasp Pi
            try {
                publishProgress(3);
                ArrayList<String> listOfCommands = LoadCommands.addCommandsUnzip(rasp);     //Unzip
                while (listOfCommands.size() > 0) {
                    String command = listOfCommands.remove(0);
                    command += "\n";
                    Session session = rasp.getConnection().openSession();
                    session.execCommand(command);
                    Log.d("command", command + "");
                    while (!UtilsImple.getResponse(session).contains("EndCommands")) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    session.close();
                }

                //Config
                publishProgress(4);
                int c = 4;
                listOfCommands = LoadCommands.addCommandsConfig(rasp, isPublicWifi, WifiSSID, WifiPassword);
                while (listOfCommands.size() > 0) {
                    String command = listOfCommands.remove(0);
                    command += "\n";
                    Session session = rasp.getConnection().openSession();
                    session.execCommand(command);
                    Log.d("command", command + "");
                    c += 1;
                    publishProgress(c);
                    while (!UtilsImple.getResponse(session).contains("EndCommands")) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    session.close();
                    //Set Pi was configured
                    rasp.setIsConfigured(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values[0]);
        pd.setMax(10);
        pd.setProgress(values[0]);
        switch (values[0]) {
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
                pd.setMessage("Prepare install config for Pi.");
                break;
            case 5:
                pd.setMessage("Config start for Pi.");
                break;
            case 6:
                pd.setMessage("Config Wifi profile to Pi.");
                break;
            case 7:
                pd.setMessage("Install dependencies and mysql");
                break;
            case 8:
                pd.setMessage("Config mysql.");
                break;
            case 9:
                break;
            case 10:
                pd.setMessage("Finish.");
                break;
            default:
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
                        UtilsImple.excCommand(rasp, LoadCommands.addCommandsReboot());
                        new WaitServerReboot().execute();
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

    public class WaitServerReboot extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Server reboot. Please waiting...");
            progressDialog.setTitle("Server is rebooting.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (MainActivity.rasp.getIPAddress()==null){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Try broadcast find the server.
                BroadcastFindServer();
                Log.d("broadcast", "try find the server");

                String ipServer = MainActivity.rasp.getIPAddress();
                if (ipServer != null) {
                    MainActivity.rasp.setMacAddress(Discover.getMacFromArpCache(ipServer));
                    MainActivity.rasp.setDeviceName(Discover.getHostNameNmblookup(ipServer, utilsNetwork.getnmbLookupLocation()));

                    //Save to Preferences
                    GetAndSaveInfoToPrefer();
                    //Break the loop.
                    Log.d("broadcast", "Go to MainActivity");
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    private void BroadcastFindServer() {
        Thread thread = new Thread(DiscoveryThread.getInstance(activity));
        thread.start();
    }

    private void GetAndSaveInfoToPrefer(){
        //Save info of Server to Preferences
        PreferencesHelper.writeToPreferencesString(activity, MainActivity.rasp.getIPAddress(), ContantsHomeMMS.SERVER_IP);
        PreferencesHelper.writeToPreferencesString(activity, MainActivity.rasp.getMacAddress(), ContantsHomeMMS.SERVER_MAC);
        PreferencesHelper.writeToPreferencesString(activity, MainActivity.rasp.getDeviceName(), ContantsHomeMMS.SERVER_NAME);

        //Get Mac addrees and Name of access point, save in Preferences.
        String MacAddressOfAP = utilsNetwork.getMacAddressOfAP();
        String NameOfAP = utilsNetwork.getSSIDOfAP();
        if (MacAddressOfAP != null) {
            PreferencesHelper.writeToPreferencesString(activity, MacAddressOfAP, ContantsHomeMMS.AP_MACADDRESS);
        }
        if (NameOfAP != null) {
            PreferencesHelper.writeToPreferencesString(activity, NameOfAP, ContantsHomeMMS.AP_NAME);
        }
        //Save First run app.
        PreferencesHelper.writeToPreferences(activity, false);
    }
}

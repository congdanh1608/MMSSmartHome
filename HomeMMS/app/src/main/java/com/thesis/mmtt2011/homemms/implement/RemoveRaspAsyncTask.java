package com.thesis.mmtt2011.homemms.implement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.thesis.mmtt2011.homemms.model.RaspberryPi;

import java.util.ArrayList;

import ch.ethz.ssh2.Session;

/**
 * Created by CongDanh on 10/12/2015.
 */
public class RemoveRaspAsyncTask extends AsyncTask<Void, Void, Void>{
    ProgressDialog pd;
    Activity activity;
    RaspberryPi rasp;

    public RemoveRaspAsyncTask(Activity activity, RaspberryPi rasp) {
        this.activity = activity;
        this.rasp = rasp;
        pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Removing...");
        pd.setMessage("Wating remove install.");
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (rasp.getConnection()==null) {
            com.thesis.mmtt2011.homemms.SSH.Utils.connectSSH(rasp); //Create connection for raspPi.
        }
            try {
                //Remove config in Rasp Pi.
                ArrayList<String> listOfCommands = LoadCommands.addCommandsRemoveInstall(rasp);
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
                    rasp.setIsConfigured(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

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

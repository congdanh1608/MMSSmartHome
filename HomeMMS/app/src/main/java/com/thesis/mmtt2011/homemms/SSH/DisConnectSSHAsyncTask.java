package com.thesis.mmtt2011.homemms.SSH;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.thesis.mmtt2011.homemms.model.RaspberryPi;


public class DisConnectSSHAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Activity activity;
    RaspberryPi rasp;
    ProgressDialog pd;

    public DisConnectSSHAsyncTask(Activity activity, RaspberryPi rasp) {
        this.activity = activity;
        this.rasp = rasp;
        pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Disconnecting Pi...");
        pd.setMessage("Please Wait...");
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (Utils.disconnectSSH(rasp)) return true;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        pd.dismiss();
//        if (aBoolean) {
//            MainActivity.btnConnectSSHPI.setText("Connect");
//            MainActivity.tvConnectSSH.setText("Disconnected");
//            MainActivity.mStartConnectSSH = true;
//        } else MainActivity.tvConnectSSH.setText("Error");
    }
}

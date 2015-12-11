package com.thesis.mmtt2011.homemms.SSH;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.model.RaspberryPi;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

/**
 * Created by CongDanh on 21/10/2015.
 */
public class PushFileAsyncTask extends AsyncTask<Void, Void, Integer> {
    ProgressDialog pd;
    byte[] bytes;
    String fName;
    RaspberryPi rasp;
    Activity activity;

    public PushFileAsyncTask(byte[] bytes, String fName, RaspberryPi rasp, Activity activity){
        this.bytes = bytes;
        this.fName = fName;
        this.rasp = rasp;
        this.activity = activity;

        pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Pushing to Pi...");
        pd.setMessage("Please Wait...");
        pd.setProgressStyle(pd.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (rasp.getConnection() == null){
            Utils.connectSSH(rasp);
        }
        String pathFile = "/" + ContantsHomeMMS.AppName+ "/" + MainActivity.myUser.getId() + "/";
        return Utils.pushFile(bytes, fName, pathFile, rasp);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer){
            case 1:
//                MainActivity.client.SendMessageInfoFilePush(fName);
                Toast.makeText(activity, "Successful Push File", Toast.LENGTH_SHORT).show();
                break;
            case 0:
                Toast.makeText(activity, "Error not data byte", Toast.LENGTH_SHORT).show();
                break;
            case -2:
                Toast.makeText(activity, "Error not connect PI", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(activity, "Error not know", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
        pd.dismiss();
    }
}

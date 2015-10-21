package thesis.danh.avpdemo.SSH;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import thesis.danh.avpdemo.MainActivity;
import thesis.danh.avpdemo.RaspberryPiClient;

/**
 * Created by CongDanh on 21/10/2015.
 */
public class ConnectSSHAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Utils utils;
    Activity activity;
    RaspberryPiClient rasp;
    ProgressDialog pd;

    public ConnectSSHAsyncTask(Activity activity, RaspberryPiClient rasp) {
        this.activity = activity;
        this.rasp = rasp;
        utils = new Utils(activity);
        pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Connecting Pi...");
        pd.setMessage("Please Wait...");
        pd.setProgressStyle(pd.STYLE_HORIZONTAL);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (utils.connectSSH(rasp)) return true;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        pd.dismiss();
        if (aBoolean) {
            MainActivity.btnConnectPI.setText("Disconnect");
            MainActivity.tvConnect.setText("Connected");
            MainActivity.mStartConnect = false;
        } else MainActivity.tvConnect.setText("Error");
    }
}


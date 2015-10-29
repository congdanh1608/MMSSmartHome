package thesis.danh.avpdemo.SSH;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import thesis.danh.avpdemo.MainActivity;
import thesis.danh.avpdemo.RaspberryPiClient;
import thesis.danh.avpdemo.Socket.KeyString;

/**
 * Created by CongDanh on 21/10/2015.
 */
public class PushFileAsyncTask extends AsyncTask<Void, Void, Integer> {
    Utils utils;
    ProgressDialog pd;
    byte[] bytes;
    String fName;
    RaspberryPiClient rasp;
    Activity activity;

    public PushFileAsyncTask(byte[] bytes, String fName, RaspberryPiClient rasp, Activity activity){
        this.bytes = bytes;
        this.fName = fName;
        this.rasp = rasp;
        this.activity = activity;

        utils = new Utils(activity);
        pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle("Pushing to Pi...");
        pd.setMessage("Please Wait...");
        pd.setProgressStyle(pd.STYLE_HORIZONTAL);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return utils.pushFile(bytes, fName, rasp);
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

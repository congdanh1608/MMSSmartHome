package thesis.danh.avpdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class Utils {
    Activity activity;

    public Utils(Activity activity){
        this.activity = activity;
    }

    protected void showMesg(String msg){
        Toast.makeText( activity, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showDialogNewRecieve(){
        new AlertDialog.Builder(activity)
                .setTitle("Recieved")
                .setMessage("You have recieved a new note.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

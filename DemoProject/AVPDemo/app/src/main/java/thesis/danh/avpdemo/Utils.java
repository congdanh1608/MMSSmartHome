package thesis.danh.avpdemo;

import android.app.Activity;
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
}

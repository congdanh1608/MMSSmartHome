package com.thesis.mmtt2011.homemms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.Network.UtilsNetwork;
import com.thesis.mmtt2011.homemms.activity.ComposeMessageActivity;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by CongDanh on 22/10/2015.
 */
public class UtilsMain {
    public static long HOUR24_MILISECOND = 24 * 60 * 60 * 1000;
    private UtilsNetwork utilsNetworkNetwork;
    private Activity activity;
    private MediaRecorder mRecorderAudio = null;

    public UtilsMain(Activity activity) {
        this.activity = activity;
        utilsNetworkNetwork = new UtilsNetwork(activity);
    }

    public int checkIsConnectToWifiHome() {
        if (utilsNetworkNetwork.checkIsWifiConnect()) {
            if (!PreferencesHelper.getIsPreferenceBoolean(activity, ContantsHomeMMS.STATE_NORMAL)) {
                if (utilsNetworkNetwork.getSSIDOfAP().contains(ContantsHomeMMS.HP_NAME)) {
                    return 1;   //true wifi home
                }
            }
            else {
                String MACADDRESSOfAP = PreferencesHelper.getIsPreferenceString(activity, ContantsHomeMMS.AP_MACADDRESS);
                String SSIDOfAP = PreferencesHelper.getIsPreferenceString(activity, ContantsHomeMMS.AP_NAME);
                if (MACADDRESSOfAP == null || SSIDOfAP == null) {
                    return 0;   //unknow wifi
                }
                if (utilsNetworkNetwork.getMacAddressOfAP().equals(MACADDRESSOfAP) && utilsNetworkNetwork.getSSIDOfAP().equals(SSIDOfAP)) {
                    return 1;   //true wifi home
                }
            }
        }
        return -1; //not wifi home.
    }

    public static Boolean CreateFolder(String dir) {
        File folder = new File(dir);
        if (folder.exists())
            return true;
        else {
            try {
                folder.mkdirs();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean copyAsset(AssetManager assetManager,
                                    String fromAssetPath, String toPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);

            File file = new File(toPath);
            if (file.exists())
                file.delete();
            file = new File(toPath);
            boolean created = file.createNewFile();
            System.out.println("created file = " + created);
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resizeImage(String oldPath, String newPath, int weight, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);

        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, weight, height, true);
        File file = new File(newPath);
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecordingA(String mFileNameAudio) {
        mRecorderAudio = new MediaRecorder();
        mRecorderAudio.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorderAudio.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorderAudio.setOutputFile(mFileNameAudio);
        mRecorderAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorderAudio.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorderAudio.start();
    }

    public void stopRecordingA() {
        if (mRecorderAudio != null) {
            mRecorderAudio.release();
            mRecorderAudio = null;
        }
    }

    public void startRecordingAudio(String mFilePathAudio){
        Uri outputFileUriAudio = Uri.fromFile(new File(mFilePathAudio));
        Intent tankAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        tankAudioIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 300);     //Limit 300s
        tankAudioIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriAudio);
        if (tankAudioIntent.resolveActivity(activity.getPackageManager())!=null) {
            activity.startActivityForResult(tankAudioIntent, ContantsHomeMMS.REQUEST_AUDIO_CAPTURE);
        }
    }

    public void startRecordingV(String mFilePathVideo) {
        Uri outputFileUriVideo = Uri.fromFile(new File(mFilePathVideo));
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10983040L);       //Limit 10Mb    or
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);          //Limit 30s
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriVideo);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takeVideoIntent, ContantsHomeMMS.REQUEST_VIDEO_CAPTURE);
        }
    }

    public void openImageIntent(String mFilePathImage) {
//        final String fnameP = "img_" + System.currentTimeMillis() + ".jpg";
//        final File sdImageMainDirectory = new File(mDir, fnameP);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Uri outputFileUri = Uri.fromFile(new File(mFilePathImage));

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        //FileSystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        activity.startActivityForResult(chooserIntent, ContantsHomeMMS.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        if (contentUri!=null) {
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

    public static void showMessage(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void showDialogNewRecieve() {
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

    public static String createNameForFile(ContantsHomeMMS.TypeFile type){
        switch (type){
            case Audio:
                return getCurrentTimeHms() + ContantsHomeMMS.exAudio;

            case Video:
                return getCurrentTimeHms() + ContantsHomeMMS.exVideo;

            case Photo:
                return getCurrentTimeHms() + ContantsHomeMMS.exImage;

            default:
                break;
        }
        return "default";
    }

    public static String createNameForAvatar(String mac) {
        String name = null;
        if (mac != null && !mac.equals("")) {
            name = mac.substring(9, 10) + mac.substring(12, 13) + mac.substring(15, 16) + getCurrentTimeHms() +".png";
        }
        return name;
    }

    public String createMessageID(String mac) {
        String mID = null;
        if (mac != null && !mac.equals("")) {
            mID = mac.substring(9, 10) + mac.substring(12, 13) + mac.substring(15, 16) + getCurrentTimeHms();
        }
        return mID;
    }

    public static String getCurrentTimeMMMddHHmmss() {
        String time = null;
        DateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        time = dateFormat.format(date);
        return time;
    }

    public static String getCurrentTimeHms() {
        String time = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyymmddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        time = dateFormat.format(date); // 075959
        return time;
    }


    public String getCurrentTime() {
        String time = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date();
        time = dateFormat.format(date); // 20151030_075959
        return time;
    }

    public static String stringToDateCondition(String dateString) {
        Date now = new Date();
        try {
            Date date = convertStringToDate(dateString);
            long differenceTime = now.getTime() - date.getTime();
            if (differenceTime > HOUR24_MILISECOND) {
                DateFormat dateFormat = new SimpleDateFormat("MMM d");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                return dateFormat.format(date);
            } else {
                DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                return dateFormat.format(date);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return dateString;
    }

    public static Date convertStringToDate(String dateString) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            date = df.parse(dateString);
            return date;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public void showDialog() {
        final EditText input = new EditText(activity);
        input.setText("");
        new AlertDialog.Builder(activity)
                .setView(input)
                .setTitle("Choose Receiver")
                .setMessage("input receiver id (mac)!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ComposeMessageActivity.receiverID = input.getText().toString();
                        TextView contact_list = (TextView) activity.findViewById(R.id.contact_list);
                        contact_list.append(input.getText().toString());
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public static boolean isX86Cpu() {
        boolean bool;
        StringBuilder localStringBuilder;
        try {
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream()));
            char[] arrayOfChar = new char[4096];
            localStringBuilder = new StringBuilder();
            for (; ; ) {
                int i = localBufferedReader.read(arrayOfChar);
                if (i <= 0) {
                    break;
                }
                localStringBuilder.append(arrayOfChar, 0, i);
            }
            localBufferedReader.close();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            return false;
        }
        bool = localStringBuilder.toString().contains("x86");
        return bool;
    }

    public static Boolean ChecknmblookupAvalability(String dir) {
        File file = new File(dir);
        if (file.exists())
            return true;
        else {
            return false;
        }
    }

    public void ShowToast(String s) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkFileIsExits(String pathFile){
        if (pathFile==null) return false;
        File file = new File(pathFile);
        if (file.exists()) return true;
        else return false;
    }
}

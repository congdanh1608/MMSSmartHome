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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.activity.ComposeMessageActivity;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.BufferedReader;
import java.io.File;
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

/**
 * Created by CongDanh on 22/10/2015.
 */
public class Utils {
    private Activity activity;
    private MediaRecorder mRecorderAudio = null;

    public Utils(Activity activity) {
        this.activity = activity;
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
            CopyFile(in, out);
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

    public static void CopyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
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

    public void startRecordingV(String mFileNameVideo) {
        Uri outputFileUriVideo = Uri.fromFile(new File(mFileNameVideo));
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10983040L);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriVideo);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takeVideoIntent, ContantsHomeMMS.REQUEST_VIDEO_CAPTURE);
        }
    }

    public void openImageIntent(String mFileNameImage) {
//        final String fnameP = "img_" + System.currentTimeMillis() + ".jpg";
//        final File sdImageMainDirectory = new File(mDir, fnameP);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Uri outputFileUri = Uri.fromFile(new File(mFileNameImage));

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

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
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

    public static void showMessage(final Activity activity, final String msg){
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

    public String createMessageID(String mac) {
        String mID = null;
        if (mac != null && !mac.equals("")) {
            mID = mac.substring(9, 10) + mac.substring(12, 13) + mac.substring(15, 16) + getCurrentTimeHms();
        }
        return mID;
    }

    public String getCurrentTimeHms() {
        String time = null;
        DateFormat dateFormat = new SimpleDateFormat("HHmmss");
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

    public static Date convertStringToDate(String dateString) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
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

    public void ShowToast(String s){
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }
}

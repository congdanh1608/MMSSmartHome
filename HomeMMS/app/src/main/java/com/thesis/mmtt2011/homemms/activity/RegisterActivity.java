package com.thesis.mmtt2011.homemms.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.Encrypt;
import com.thesis.mmtt2011.homemms.Network.UtilsNetwork;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mAuthTask = null;

    // UI references.
    private TextView mMacAddressView;
    private EditText mNameDisplayView;
    private EditText mPasswordView;
    private EditText mRetypePasswordView;
    private View mProgressView;
    private View mRegisterFormView;
    private ImageView set_avatar;
    private String avatarFile = null, avatarCache = null, avatarName = null;
    private UtilsNetwork utilNetwork;

    public String getMacAddress() {
        WifiManager wimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        return macAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //create utilsMain
        utilNetwork = new UtilsNetwork(this);

        //Setup register form
        mMacAddressView = (TextView) findViewById(R.id.mac_address);
        mMacAddressView.setText(getMacAddress());

        mNameDisplayView = (EditText) findViewById(R.id.name_display);

        avatarCache = ContantsHomeMMS.AppCacheFolder + "/" + "Avatar.png";
        set_avatar = (ImageView) findViewById(R.id.set_avatar);
        set_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetAvatar();
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.register || actionId == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        mRetypePasswordView = (EditText) findViewById(R.id.retype_password);
        mRetypePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.retype_pass || actionId == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void onSetAvatar() {
        //Check folder app
        if (UtilsMain.CreateFolder(ContantsHomeMMS.AppFolder) && UtilsMain.CreateFolder(ContantsHomeMMS.AppCacheFolder)) {
            openImageIntent();
        }
    }

    public void openImageIntent() {
//        final String fnameP = "img_" + System.currentTimeMillis() + ".jpg";
//        final File sdImageMainDirectory = new File(mDir, fnameP);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Uri outputFileUri = Uri.fromFile(new File(avatarCache));

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
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
        startActivityForResult(chooserIntent, ContantsHomeMMS.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        //Reset errors.
        mNameDisplayView.setError(null);
        mPasswordView.setError(null);
        mRetypePasswordView.setError(null);

        //Store values at the time of the register attempt.
        String nameDisplay = mNameDisplayView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRetypePasswordView.getText().toString();
        String macAddress = mMacAddressView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid password, if the myUser entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isMatchPassword(password, rePassword)) {
            mRetypePasswordView.setError(getString(R.string.error_notmatch_password));
            focusView = mRetypePasswordView;
            cancel = true;
        }

        //Check for a valid name display
        if (TextUtils.isEmpty(nameDisplay)) {
            mNameDisplayView.setError(getString(R.string.error_field_required));
            focusView = mNameDisplayView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the myUser login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(macAddress, nameDisplay, Encrypt.md5(password));
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isMatchPassword(String password, String rePassword) {
        return TextUtils.equals(password, rePassword);
    }

    private void showProgress(boolean show) {
        //||disable view register form and show progress view
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= LoginActivity.MIN_LENGTH_PASSWORD;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the myUser
     */

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMACAddress;
        private final String mPassword;
        private final String mNameDisplay;

        UserRegisterTask(String macAddress, String nameDisplay, String password) {
            mMACAddress = macAddress;
            mPassword = password;
            mNameDisplay = nameDisplay;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (MainActivity.myUser == null){
                MainActivity.myUser = new User(utilNetwork.getMacAddress(), null, null, null,
                        ContantsHomeMMS.UserStatus.online.name());
            }
            try {
                //Get name for new avatar
                String mac = utilNetwork.getMacAddress();
                avatarName = UtilsMain.createNameForAvatar(mac);
                avatarFile = ContantsHomeMMS.myUserFolder + MainActivity.myUser.getId() + "/" + avatarName;
                //Resize avatar
                if (UtilsMain.checkFileIsExits(avatarCache)) {
                    UtilsMain.resizeImage(avatarCache, avatarFile, 64, 64);
                }else avatarFile=null;
                //Simulate network access.
                Log.d("package", getBaseContext().getPackageName());
                MainActivity.myUser.setId(mMACAddress);
                MainActivity.myUser.setPassword(mPassword);
                MainActivity.myUser.setNameDisplay(mNameDisplay);
                MainActivity.myUser.setAvatar(avatarFile!=null ? avatarName : null);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            //Create myUser and send register information to server
            //Check account exists
            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split("-");
                if (pieces[0].equals(mMACAddress)) {
                    //Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("result", requestCode + "");
        switch (requestCode) {
            case ContantsHomeMMS.CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    final boolean isCamera;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri selectedImageUri = null;
                    if (isCamera) {
//                        selectedImageUri = outputFileUri;
                        //Bitmap factory
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // downsizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 8;
//                        final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                        Bitmap bitmap = BitmapFactory.decodeFile(avatarCache, options);
                        //rotete image.
                        try {
                            ExifInterface ei = new ExifInterface(avatarCache);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            Matrix matrix;
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix = new Matrix();
                                    matrix.postRotate(90);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix = new Matrix();
                                    matrix.postRotate(180);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    break;
                                default:
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //set image to viewimage
                        set_avatar.setImageBitmap(bitmap);
                    } else {
                        selectedImageUri = data == null ? null : data.getData();
                        // /Bitmap factory
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // downsizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 8;
                        try {//Using Input Stream to get uri did the trick
                            InputStream input = getContentResolver().openInputStream(selectedImageUri);
                            final Bitmap bitmap = BitmapFactory.decodeStream(input);
                            //set image to viewimage
//                            imgPhoto.setImageBitmap(bitmap);
                            avatarCache = UtilsMain.getRealPathFromURI(this, selectedImageUri);
                            set_avatar.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // myUser cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed.", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}

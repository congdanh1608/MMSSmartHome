package com.thesis.mmtt2011.homemms.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.Encrypt;
import com.thesis.mmtt2011.homemms.Network.UtilsNetwork;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends MainActivity {

    EditText etNameDisplay;
    EditText etOldPass;
    EditText etNewPass;
    EditText etRetypePass;
    Button btChangePass;
    CircleImageView civAvatar;
    View formChangePass;
    CoordinatorLayout activity_user_info;

    private UtilsMain utilsMain;
    private UtilsNetwork utilNetwork;

    String oldName, newAvatar = null, mFilePathAvatar = null, avatarCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        utilsMain = new UtilsMain(this);
        utilNetwork = new UtilsNetwork(this);

        avatarCache = ContantsHomeMMS.AppCacheFolder + "/" + "Avatar.png";

        oldName = myUser.getNameDisplay();

        activity_user_info = (CoordinatorLayout) this.findViewById(R.id.activity_user_info);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFormChangePass(false);
                //Save changes and send to server to update user info
                if (!etNewPass.getText().toString().isEmpty() && etOldPass.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Old password cannot empty.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    if (etNewPass.getText().toString().isEmpty() && !etOldPass.getText().toString().isEmpty()) {
                        Snackbar.make(view, "New password cannot empty.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        if (etNewPass.getText().toString().isEmpty() || etNewPass.getText().toString().equals(etRetypePass.getText().toString())) {
                            if (!etNameDisplay.getText().toString().isEmpty() && !etNameDisplay.getText().toString().equals(oldName)) {
                                myUser.setNameDisplay(etNameDisplay.getText().toString());
                            }

                            //Resize image and send avatar
                            String mac = utilNetwork.getMacAddress();
                            newAvatar = utilsMain.createNameForAvatar(mac);
                            mFilePathAvatar = ContantsHomeMMS.AppFolder + "/" + myUser.getId() + "/" + newAvatar;
                            //Resize avatar
                            if (UtilsMain.checkFileIsExits(avatarCache)) {
                                UtilsMain.resizeImage(avatarCache, mFilePathAvatar, 64, 64);
                                myUser.setAvatar(newAvatar);
                                pushFileAttachToPi(newAvatar);
                            }else{
                                mFilePathAvatar = null;
                                newAvatar = null;
                            }

                            //Send request to Server
                            new UserChangeProfileTask(myUser, etNameDisplay.getText().toString(), newAvatar
                                    , etNewPass.getText().toString(), etOldPass.getText().toString()).execute();

                        } else {
                            Snackbar.make(view, "Try password is wrong.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
            }
        });
        civAvatar = (CircleImageView) findViewById(R.id.change_avatar);
        //Set image.
        Bitmap bmp = BitmapFactory.decodeFile(ContantsHomeMMS.AppFolder + "/" + myUser.getId() + "/" + myUser.getAvatar());
        civAvatar.setImageBitmap(bmp);
        civAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select image to change avatar
                utilsMain.openImageIntent(UserInfoActivity.this, avatarCache);
            }
        });
        formChangePass = findViewById(R.id.change_password_form);
        etNameDisplay = (EditText) findViewById(R.id.name_display);
        etOldPass = (EditText) findViewById(R.id.old_password);
        etNewPass = (EditText) findViewById(R.id.new_password);
        etRetypePass = (EditText) findViewById(R.id.retype_password);

        btChangePass = (Button) findViewById(R.id.bt_change_password);
        btChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormChangePass(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void showFormChangePass(final boolean show) {
        formChangePass.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("result", "onResult");
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
                        if (avatarCache != null) {
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
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        matrix = new Matrix();
                                        matrix.postRotate(270);
                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                        break;
                                    default:
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //set image to viewimage
                        civAvatar.setImageBitmap(bitmap);
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
                            civAvatar.setImageBitmap(bitmap);
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


    public class UserChangeProfileTask extends AsyncTask<Void, Void, Boolean> {
        private User user;
        private String nameDisplay, avatar, newPassword, oldPassword;

        UserChangeProfileTask(User user, String nameDisplay, String avatar, String newPassword, String oldPassword) {
            this.user = user;
            this.nameDisplay = nameDisplay;
            this.avatar = avatar;
            this.newPassword = newPassword;
            this.oldPassword = oldPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
//            try {
            //Simulate network access.
            //Send info to Server
            boolean b = client.SendRequestChangeProfile(user, nameDisplay, avatar,
                    newPassword.isEmpty() ? "" : Encrypt.md5(newPassword),
                    oldPassword.isEmpty() ? "" : Encrypt.md5(oldPassword));
            Log.d("changeprofileTask", b + "");
            //Update user in database
            HomeMMSDatabaseHelper.updateUser(getApplicationContext(), user);

//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                return false;
//            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split("-");
//                if (pieces[0].equals(mMACAddress)) {
//                    //Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }
            return b;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
//            mAuthTask = null;
//            showProgress(false);
            if (success) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                Snackbar.make(activity_user_info, "Change profile is successful", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            } else {
                Snackbar.make(activity_user_info, "Old Password is wrong!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
        }
    }

}

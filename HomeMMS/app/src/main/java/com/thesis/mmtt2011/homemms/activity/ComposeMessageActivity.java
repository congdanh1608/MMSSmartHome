package com.thesis.mmtt2011.homemms.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.SSH.ConnectSSHAsyncTask;
import com.thesis.mmtt2011.homemms.SSH.PushFileAsyncTask;
import com.thesis.mmtt2011.homemms.SSH.Utils;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ComposeMessageActivity extends MainActivity {

    private EditText mMessageTitleView;
    private EditText mMessageContentView;
    private View mProgressView;
    private View mComposeMessageFormView;
    private ImageButton add_contact;
    private TextView contact_list;

    public static String receiverID;      //ID of receiver.

    private HomeMMSDatabaseHelper homeMMSDatabaseHelper;

    private com.thesis.mmtt2011.homemms.Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeMMSDatabaseHelper = new HomeMMSDatabaseHelper(this);

        //create Utils.
        utils = new com.thesis.mmtt2011.homemms.Utils(this);

        mMessageTitleView = (EditText) findViewById(R.id.message_title);
        mMessageContentView = (EditText) findViewById(R.id.message_content);
        add_contact = (ImageButton) findViewById(R.id.add_contact);
        contact_list = (TextView) findViewById(R.id.contact_list);

        mProgressView = findViewById(R.id.compose_message_progress);
        mComposeMessageFormView = findViewById(R.id.compose_message_form);

        FloatingActionButton fabSendMsg = (FloatingActionButton) findViewById(R.id.fabSendMsg);
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.showDialog();
            }
        });
        fabSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Sending message...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                onSendMessage();
            }
        });
    }

    private void onSendMessage() {
        //Demo info reciever.
        User receiver = new User(receiverID, "", "", "", "");
        List<User> receivers = new ArrayList<User>();
        receivers.add(receiver);

        //Get Name file mms.
        if (mFileNameAudio!=null) {
            String[] separatedA = mFileNameAudio.split("/");
            mFileNameAudio = separatedA[separatedA.length - 1];
        }
        //Get Name file Photo
        if (mFileNameImage!=null) {
            String[] separatedP = mFileNameImage.split("/");
            mFileNameImage = separatedP[separatedP.length - 1];
        }
        //Get Name file Video
        if (mFileNameVideo!=null) {
            String[] separatedV = mFileNameVideo.split("/");
            mFileNameVideo = separatedV[separatedV.length - 1];
        }

        Message message = createAMessage(receivers);
        //Save message to database with status draft.
        homeMMSDatabaseHelper.addMessage(message);

        if (client != null) {
            client.SendInfoMessage(message);
        }
        if (mFileNameAudio!=null || mFileNameImage!=null || mFileNameVideo!=null) {
            //Connect SSH.
            new ConnectSSHAsyncTask(this, rasp).execute();
            //Push File by SSH
            pushFileAttachToPi(mFileNameAudio);
            pushFileAttachToPi(mFileNameImage);
            pushFileAttachToPi(mFileNameVideo);
        }

        //Notify server myUser finished note.
        if (client != null) {
            //After send message successful, update message in database status sent.
            if (client.SendMessageEndNote()) {
                homeMMSDatabaseHelper.updateMessageStatus(this, message.getId(), ContantsHomeMMS.MessageStatus.sent.name());
            }
        }
    }

    //Create a message.
    private Message createAMessage(List<User> receivers){
        Message message = new Message();
        message.setMId(utils.createMessageID(myUser.getId()));
        message.setSender(myUser);
        message.setReceiver(receivers);
        message.setTitle(mMessageTitleView.getText().toString());
        message.setContentText(mMessageContentView.getText().toString());
        message.setContentAudio(mFileNameAudio);
        message.setContentImage(mFileNameImage);
        message.setContentVideo(mFileNameVideo);
        message.setTimestamp(utils.getCurrentTime());
        message.setStatus(ContantsHomeMMS.MessageStatus.draft.name());
        return message;
    }

    private void pushFileAttachToPi(String mFileName) {
        if (mFileName != null) {
            byte[] bytes = Utils.loadResourceFromPath(mFileName);
            if (bytes != null) {
                PushFileAsyncTask async = new PushFileAsyncTask(bytes, mFileName, rasp, this);
                async.execute();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContantsHomeMMS.CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
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
//                selectedImageUri = outputFileUri;
                //Bitmap factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;
//                final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                Bitmap bitmap = BitmapFactory.decodeFile(mFileNameImage, options);
                //rotete image.
                try {
                    ExifInterface ei = new ExifInterface(mFileNameImage);
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
//                imgPhoto.setImageBitmap(bitmap);
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
//                    imgPhoto.setImageBitmap(bitmap);
                    mFileNameImage = utils.getRealPathFromURI(this, selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == ContantsHomeMMS.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            Uri videoUri = data.getData();
//            vViewV.setVideoURI(videoUri);
            //set video to viewimage
//            vViewV.setVideoURI(Uri.fromFile(new File(mFileNameVideo)));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_camera) {
            /*Intent intent = new Intent(this, ScanDevicesActivity.class);
            startActivity(intent);*/
            return true;
        }

        if (id == R.id.action_video) {
            //
            return true;
        }

        if (id == R.id.action_record) {
            //
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

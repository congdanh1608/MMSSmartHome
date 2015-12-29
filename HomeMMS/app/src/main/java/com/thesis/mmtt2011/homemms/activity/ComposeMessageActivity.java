package com.thesis.mmtt2011.homemms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.adapter.ContactAdapter;
import com.thesis.mmtt2011.homemms.adapter.ImageAdapter;
import com.thesis.mmtt2011.homemms.fragment.SentFragment;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;
import com.thesis.mmtt2011.homemms.persistence.UtilsPersis;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ComposeMessageActivity extends MainActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "ComposeMessageActivity";

    private EditText mMessageTitleView;
    private EditText mMessageContentView;
    private View mProgressView;
    private View mComposeMessageFormView;
    private Button add_contact;
    private TextView contact_list;
    private CoordinatorLayout coordinatorlayout;
    static View attatchFileView;
    //static VideoView mVideoView;
    static View imageView;
    static View playAudioView;
    static View videoView;
    ImageButton btnPlay;
    SeekBar audioProgressBar;
    TextView audioTotalDuration;
    TextView videoName;
    ImageButton imageCancel, audioCancel, videoCancel;
    //Button btnPlayVideo;
    //static View videoView;

    public static String receiverID;      //ID of receiver.

    private HomeMMSDatabaseHelper homeMMSDatabaseHelper;

    private UtilsMain utilsMain;

    private String mFileNameAudio = "null", mFileNameImage = "null", mFileNameVideo = "null";

    private boolean tabSentButton = false;

    private Message draftMessage;

    ContactAdapter mAdapter;
    ArrayList<User> contacts = new ArrayList<User>();
    ArrayList<User> selectedContacts = new ArrayList<>();

    static ArrayList<String> attachImages = new ArrayList<>();
    static RecyclerView mRecyclerView;
    static RecyclerView.Adapter mImageAdapter;
    static RecyclerView.LayoutManager mLayoutManager;

    MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    //dumpmy content audioName = "/storage/extSdCard/Music/Ryuukou-HoaTau-3089028.mp3"
    static String audioName = "";

    //get draftmessges id to load message draft
    public static Intent getStartIntent(Context context, Message message) {
        Intent starter = new Intent(context, ComposeMessageActivity.class);
        starter.putExtra(ComposeMessageActivity.TAG, message.getId());
        return starter;
    }

    // du lieu list contact
    public void initContacts() {
        contacts.addAll(homeMMSDatabaseHelper.getAllUserExceptMySeft(this, MainActivity.myUser.getId()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeMMSDatabaseHelper = new HomeMMSDatabaseHelper(this);

        //create UtilsMain.
        utilsMain = new UtilsMain(this);

        coordinatorlayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        mMessageTitleView = (EditText) findViewById(R.id.message_title);
        mMessageContentView = (EditText) findViewById(R.id.message_content);
        add_contact = (Button) findViewById(R.id.add_contact);
        contact_list = (TextView) findViewById(R.id.contact_list);

        mProgressView = findViewById(R.id.compose_message_progress);
        mComposeMessageFormView = findViewById(R.id.compose_message_form);

        attatchFileView = findViewById(R.id.attach_content_view);
        // get images devices
        imageView = findViewById(R.id.image_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.image_recycler_view);
        imageCancel = (ImageButton) findViewById(R.id.cancel_image);
        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetImageAttach();
            }
        });
        //dummy content
        //attachImages.add(0, "/storage/emulated/0/Snapseed/snapseed-14.jpeg");
        //attachImages.add(0, "/storage/emulated/0/HOMEMMS/34:4d:f7:55:50:18/20154523174531.jpg");

        //load list image attach path were downloaded on android
        videoName = (TextView) findViewById(R.id.video_name);

        audioProgressBar = (SeekBar) findViewById(R.id.seekbar);
        audioProgressBar.setOnSeekBarChangeListener(this);
        audioTotalDuration = (TextView) findViewById(R.id.tv_total_duration);
        btnPlay = (ImageButton) findViewById(R.id.play_button);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                            btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
                        }
                    } else if (mediaPlayer != null) {
                        mediaPlayer.start();
                        btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_36dp);
                    }
                } else {
                    //audio
                    mediaPlayer = new MediaPlayer();
                    playAudio(audioName);
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_36dp);
                }
            }
        });
        playAudioView = findViewById(R.id.play_audio_view);
        audioCancel = (ImageButton) findViewById(R.id.cancel_audio);
        audioCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAudioAttach();
            }
        });
        //load video
        /*videoView = findViewById(R.id.video_view);
        btnPlayVideo = (Button) findViewById(R.id.bt_play_video);
        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
                }
                //mVideoView.setVideoPath("/storage/extSdCard/Videos/minion.mp4");
                mVideoView.setMediaController(new MediaController(ComposeMessageActivity.this));
                mVideoView.start();
                mVideoView.setVisibility(View.VISIBLE);
            }
        });*/
        videoView = findViewById(R.id.video_view);
        videoCancel = (ImageButton) findViewById(R.id.cancel_video);
        videoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetVideoAttach();
            }
        });
        hideAllAttachView();
        mImageAdapter = new ImageAdapter(this, attachImages);
        mRecyclerView.setAdapter(mImageAdapter);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        attachImages.clear();
        checkPathFileAttach();
        Intent intent = getIntent();
        loadMessageDraft(intent);

        FloatingActionButton fabSendMsg = (FloatingActionButton) findViewById(R.id.fabSendMsg);

        initContacts();
        mAdapter = new ContactAdapter(ComposeMessageActivity.this, contacts, selectedContacts);
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //utilsMain.showDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(ComposeMessageActivity.this);
                builder.setAdapter(mAdapter, null);
                builder.setTitle(R.string.select_contact);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do something with selectedContact
                                mAdapter.notifyDataSetChanged();
                                contact_list.setText(showListSelectedContact());
                            }
                        });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog contactDialog = builder.create();
                ListView contactList = contactDialog.getListView();
                contactList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        LinearLayout linearLayout = (LinearLayout) view;
                        View v = linearLayout.getChildAt(2);
                        CheckedTextView checkedTextView = (CheckedTextView) v;
                        checkedTextView.setChecked(!checkedTextView.isChecked());
                        if (checkedTextView.isChecked()) {
                            selectedContacts.add(0, contacts.get(position));
                        } else {
                            for(User selectedContact : selectedContacts) {
                                if (selectedContact.getId().equals(contacts.get(position).getId())) {
                                    selectedContacts.remove(selectedContact);
                                }
                            }
                        }
                    }
                });
                contactDialog.show();
            }
        });

        fabSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMessageContentView.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Content is not empty.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (mMessageTitleView.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Title is not empty.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (contact_list.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Select contact to send message.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Sending message...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    onSendMessage();
                    tabSentButton = true;
                }
            }
        });
    }
    //check file attach when click in main and draft message
    private void checkPathFileAttach() {
        if(UtilsMain.checkFileIsExits(mFilePathImage)) {
            attachImages.add(0, mFilePathImage);
            mImageAdapter.notifyDataSetChanged();
            imageView.setVisibility(View.VISIBLE);
        }
        if(UtilsMain.checkFileIsExits(mFilePathAudio)) {
            audioName = mFilePathAudio;
            playAudioView.setVisibility(View.VISIBLE);
        }
        if(UtilsMain.checkFileIsExits(mFilePathVideo)) {
            videoName.setText(mFilePathVideo);
            videoView.setVisibility(View.VISIBLE);
        }
    }

    private void onSendMessage() {
        //info reciever.
        List<User> receivers = selectedContacts;

        //Get Name file mms.
        if (mFilePathAudio != null) {
            String[] separatedA = mFilePathAudio.split("/");
            mFileNameAudio = separatedA[separatedA.length - 1];
        }
        //Get Name file Photo
        if (mFilePathImage != null) {
            String[] separatedP = mFilePathImage.split("/");
            mFileNameImage = separatedP[separatedP.length - 1];
        }
        //Get Name file Video
        if (mFilePathVideo != null) {
            String[] separatedV = mFilePathVideo.split("/");
            mFileNameVideo = separatedV[separatedV.length - 1];
        }

        Message message = createAMessageDraft(receivers);
        //Save message to database with status draft.
        homeMMSDatabaseHelper.createMessage(getApplicationContext(), message);

        //Send message to Server.
        if (client != null && MainActivity.isConnected) {
            client.SendInfoMessage(message);
            if (utilsMain.checkFileIsExits(mFilePathAudio)) pushFileAttachToPi(mFileNameAudio);
            if (utilsMain.checkFileIsExits(mFilePathImage)) pushFileAttachToPi(mFileNameImage);
            if (utilsMain.checkFileIsExits(mFilePathVideo)) pushFileAttachToPi(mFileNameVideo);

            //Connect SSH.
//                new ConnectSSHAsyncTask(this, rasp).execute();
            //Push File by SSH
//                pushFileAttachToPi(mFileNameAudio);
//                pushFileAttachToPi(mFileNameImage);
//                pushFileAttachToPi(mFileNameVideo);
            //close SSH.
//                new DisConnectSSHAsyncTask(this, rasp).execute();

            //Notify server myUser finished note.
            //After send message successful,
            if (client.SendMessageEndNote()) {
                //update message in database status sent.
                message.setStatus(ContantsHomeMMS.MessageStatus.sent.name());
                homeMMSDatabaseHelper.updateMessage(this, message);
                //update message in Sent Fragment.
                SentFragment.UpdateNewMessageSent(message.getId());
            }
        } else {      //Save message with status wait_send
            message.setStatus(ContantsHomeMMS.MessageStatus.wait_send.name());
            homeMMSDatabaseHelper.updateMessage(this, message);
            utilsMain.ShowToast("Message will send to Server when online.");
            //update message in Sent Fragment.
            SentFragment.UpdateNewMessageSent(message.getId());
        }

        //Delete old message draft if has after send success
        if (draftMessage !=null){
            homeMMSDatabaseHelper.deleteMessage(this, draftMessage.getId());
        }

        Snackbar.make(coordinatorlayout, "Send successful", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        //Finish activity
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        finish();
    }

    private void saveMessageDraft() {
        if (!selectedContacts.isEmpty() && !tabSentButton && (!mMessageTitleView.getText().toString().isEmpty() || mMessageContentView.getText().toString().isEmpty())) {
            //info reciever.
            List<User> receivers = selectedContacts;

            //Get Name file mms.
            if (mFilePathAudio != null) {
                String[] separatedA = mFilePathAudio.split("/");
                mFileNameAudio = separatedA[separatedA.length - 1];
            }
            //Get Name file Photo
            if (mFilePathImage != null) {
                String[] separatedP = mFilePathImage.split("/");
                mFileNameImage = separatedP[separatedP.length - 1];
            }
            //Get Name file Video
            if (mFilePathVideo != null) {
                String[] separatedV = mFilePathVideo.split("/");
                mFileNameVideo = separatedV[separatedV.length - 1];
            }

            if (draftMessage == null) {
                Message message = createAMessageDraft(receivers);
                //Save message to database with status draft.
                homeMMSDatabaseHelper.createMessage(getApplicationContext(), message);
            }else{
                Message message = createAMessageOldDraft(receivers, draftMessage.getId());
                homeMMSDatabaseHelper.updateMessage(getApplicationContext(), message);
            }
        }
    }

    private void loadMessageDraft(Intent intent) {
        if (intent.hasExtra(TAG)) {
            String messageId = intent.getStringExtra(TAG);
            draftMessage = new Message();
            draftMessage = HomeMMSDatabaseHelper.getMessage(this, messageId);
            mMessageTitleView.setText(draftMessage.getTitle());
            mMessageContentView.setText(draftMessage.getContentText());
            selectedContacts.addAll(draftMessage.getReceiver());
            contact_list.setText(showListSelectedContact());
            //get attach files
            String PATH = ContantsHomeMMS.AppFolder + "/" + draftMessage.getSender().getId() + "/";
            attachImages.clear();
            if(!draftMessage.getContentImage().equals("null")){
                attachImages.add(0, PATH + draftMessage.getContentImage());
                mImageAdapter.notifyDataSetChanged();
                imageView.setVisibility(View.VISIBLE);
            }
            if(!draftMessage.getContentVideo().equals("null")) {
                videoName.setText(PATH + draftMessage.getContentVideo());
                videoView.setVisibility(View.VISIBLE);
            }
            if(!draftMessage.getContentAudio().equals("null")) {
                audioName = PATH + draftMessage.getContentAudio();
                playAudioView.setVisibility(View.VISIBLE);
            }
        }
    }

    //Create a message.
    private Message createAMessageDraft(List<User> receivers) {
        Message message = new Message();
        message.setMId(utilsMain.createMessageID(myUser.getId()));
        message.setSender(myUser);
        message.setReceiver(receivers);
        message.setTitle(mMessageTitleView.getText().toString());
        message.setContentText(mMessageContentView.getText().toString());
        message.setContentAudio(mFileNameAudio);
        message.setContentImage(mFileNameImage);
        message.setContentVideo(mFileNameVideo);
        message.setTimestamp(utilsMain.getCurrentTime());
        message.setStatus(ContantsHomeMMS.MessageStatus.draft.name());
        return message;
    }

    //Create a message.
    private Message createAMessageOldDraft(List<User> receivers, String mID) {
        Message message = new Message();
        message.setMId(mID);
        message.setSender(myUser);
        message.setReceiver(receivers);
        message.setTitle(mMessageTitleView.getText().toString());
        message.setContentText(mMessageContentView.getText().toString());
        message.setContentAudio(mFileNameAudio);
        message.setContentImage(mFileNameImage);
        message.setContentVideo(mFileNameVideo);
        message.setTimestamp(utilsMain.getCurrentTime());
        message.setStatus(ContantsHomeMMS.MessageStatus.draft.name());
        return message;
    }

    private String showListSelectedContact() {
        StringBuilder contactList = new StringBuilder();
        for (int i = 0; i < selectedContacts.size(); i++) {
            User user = selectedContacts.get(i);
            contactList.append(user.getNameDisplay());
            if (i < selectedContacts.size() - 1) {
                contactList.append(", ");
            }
        }
        return contactList.toString();
    }

    private void resetImageAttach() {
        imageView.setVisibility(View.GONE);
        attachImages.clear();
        mFilePathImage = "null";
    }

    private void resetAudioAttach() {
        playAudioView.setVisibility(View.GONE);
        audioName = "";
        mFilePathAudio = "null";
    }

    private void resetVideoAttach() {
        videoView.setVisibility(View.GONE);
        mFilePathVideo = "null";
    }

    private void hideAllAttachView() {
        imageView.setVisibility(View.GONE);
        playAudioView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        if (mFilePathImage == null) {
                            mFilePathImage = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.ImagePref);
                        }
                        /*Bitmap bitmap = BitmapFactory.decodeFile(mFilePathImage, options);
                        //rotete image.
                        if (mFilePathImage != null) {
                            try {
                                ExifInterface ei = new ExifInterface(mFilePathImage);
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
                        }*/
                        //set image to viewimage
//                        imgPhoto.setImageBitmap(bitmap);
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
                            String tempPath = utilsMain.getRealPathFromURI(this, selectedImageUri);
                            utilsMain.copyFile(new File(tempPath), new File(mFilePathImage));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    //show image mFilePathImage
                    if(UtilsMain.checkFileIsExits(mFilePathImage)) {
                        attachImages.clear();
                        attachImages.add(0, mFilePathImage);
                        mImageAdapter.notifyDataSetChanged();
                        imageView.setVisibility(View.VISIBLE);
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
            case ContantsHomeMMS.REQUEST_VIDEO_CAPTURE:
                if (resultCode == RESULT_OK) {
//                    Uri videoUri = data.getData();
//                    vViewV.setVideoURI(videoUri);
                    //set video to viewimage
//                    vViewV.setVideoURI(Uri.fromFile(new File(mFileNameVideo)));
                    videoName.setText(mFilePathVideo);
                    videoView.setVisibility(View.VISIBLE);

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

            case ContantsHomeMMS.REQUEST_AUDIO_CAPTURE:
                if (resultCode == RESULT_OK) {
                    String tempPath = utilsMain.getRealPathFromURI(this, data.getData());
                    utilsMain.copyFile(new File(tempPath), new File(mFilePathAudio));
                    //check exists mFilePathAudio
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
                if(UtilsMain.checkFileIsExits(mFilePathAudio)) {
                    audioName = mFilePathAudio;
                    playAudioView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //if (ContantsHomeMMS.ROLEOFMYUSER != null && ContantsHomeMMS.ROLEOFMYUSER.equals(ContantsHomeMMS.UserRole.admin.name())) {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        //}
        return true;
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
            mFilePathImage = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/"
                    + utilsMain.createNameForFile(ContantsHomeMMS.TypeFile.Photo);
            PreferencesHelper.writeToPreferencesString(getBaseContext(), mFilePathImage, ContantsHomeMMS.ImagePref);
            utilsMain.openImageIntent(ComposeMessageActivity.this, mFilePathImage);
            return true;
        }

        if (id == R.id.action_video) {
            mFilePathVideo = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/"
                    + utilsMain.createNameForFile(ContantsHomeMMS.TypeFile.Video);
            utilsMain.startRecordingV(ComposeMessageActivity.this, mFilePathVideo);
            return true;
        }

        if (id == R.id.action_record) {
            mFilePathAudio = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/"
                    + utilsMain.createNameForFile(ContantsHomeMMS.TypeFile.Audio);
            utilsMain.startRecordingAudio(ComposeMessageActivity.this, mFilePathAudio);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            //mediaPlayer.release();
        }
        //check and save message draft.
        saveMessageDraft();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //Audio
    @Override
    public void onCompletion(MediaPlayer mp) {
        btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = UtilsPersis.progressToTimer(seekBar.getProgress(), totalDuration);

        //forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);
        //update timer progress again
        updateProgressBar();
    }

    public void playAudio(String audioName) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            audioProgressBar.setProgress(0);
            audioProgressBar.setMax(100);
            updateProgressBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //update timer on seekbar
    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    //Background Runnable thread
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
            int progress = (int) UtilsPersis.getProgressPercentage(currentDuration, totalDuration);

            // Displaying Total Duration time
            audioTotalDuration.setText("" + UtilsPersis.milliSecondsToTimer(totalDuration));
            audioProgressBar.setProgress(progress);
            mHandler.postDelayed(this, 100);
        }
    };
}

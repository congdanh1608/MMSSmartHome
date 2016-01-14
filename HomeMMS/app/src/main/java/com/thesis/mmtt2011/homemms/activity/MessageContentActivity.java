package com.thesis.mmtt2011.homemms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.Socket.RecieveFile;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.adapter.ImageAdapter;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.ObjectFile;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;
import com.thesis.mmtt2011.homemms.persistence.MessageTable;
import com.thesis.mmtt2011.homemms.persistence.UtilsPersis;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageContentActivity extends MainActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    static final String PATH_FOLDER_USER = ContantsHomeMMS.myUserFolder;

    public static final String TAG = "MessageContentActivity";

    private Uri mUri;

    private static Message mMessage;

    static ArrayList<String> attachImages = new ArrayList<>();

    static RecyclerView mRecyclerView;
    static RecyclerView.Adapter mImageAdapter;
    static RecyclerView.LayoutManager mLayoutManager;

    //static View attatchFileView;
    static View imageView;
    private CircleImageView contact_avatar;
    private TextView username;
    private TextView mTitleView;
    private TextView mContentView;
    private TextView mTimestamp;
    static VideoView mVideoView;
    static View playAudioView;
    //implement audio
    ImageButton btnPlay;
    SeekBar audioProgressBar;
    TextView audioTotalDuration;
    Button btnPlayVideo;
    static View videoView;

    MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    //dumpmy content audioName = "/storage/extSdCard/Music/Ryuukou-HoaTau-3089028.mp3"
    static String audioName = "";

    public static Intent getStartIntent(Context context, Message message) {
        Intent starter = new Intent(context, MessageContentActivity.class);
        starter.putExtra(MessageContentActivity.TAG, message.getId());
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitleView = (TextView) findViewById(R.id.message_title);
        mContentView = (TextView) findViewById(R.id.message_content);
        mTimestamp = (TextView) findViewById(R.id.timestamp);
        contact_avatar = (CircleImageView) findViewById(R.id.contact_avatar);
        username = (TextView) findViewById(R.id.username);

        //attatchFileView = findViewById(R.id.attach_content_view);
        imageView = findViewById(R.id.image_view);
        // get images devices
        mRecyclerView = (RecyclerView) findViewById(R.id.image_recycler_view);
        //dummy content
        /*attachImages.add(0, "/storage/emulated/0/Snapseed/snapseed-14.jpeg");
        attachImages.add(0, "/storage/emulated/0/HOMEMMS/34:4d:f7:55:50:18/20154523174531.jpg");*/

        mImageAdapter = new ImageAdapter(this, attachImages);
        mRecyclerView.setAdapter(mImageAdapter);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //load list image attach path were downloaded on android

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
        //load video
        videoView = findViewById(R.id.video_view);
        btnPlayVideo = (Button) findViewById(R.id.bt_play_video);
        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
                }
                //mVideoView.setVideoPath("/storage/extSdCard/Videos/minion.mp4");
                mVideoView.setMediaController(new MediaController(MessageContentActivity.this));
                mVideoView.start();
                mVideoView.setVisibility(View.VISIBLE);
            }
        });
        mVideoView = (VideoView) findViewById(R.id.video_content);
        //dummy content load video content
        //String videoURL = "http://vredir.nixcdn.com/b27ff3dfe95c3fa6dfddf9775c06006e/567b8697/PreNCT10/Daddy-PSYCL2NE1-4230218_360.mp4";
        /*try {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mediaController);
            Uri uriMedia = Uri.parse(videoURL);
            mVideoView.setVideoURI(uriMedia);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });*/
        //
        // Get the message from the intent
        Intent intent = getIntent();
        if (intent.hasExtra(TAG)) {
            String messageId = intent.getStringExtra(TAG);
            mMessage = HomeMMSDatabaseHelper.getMessage(this, messageId);
            mTitleView.setText(mMessage.getTitle());
            mContentView.setText(mMessage.getContentText());
            mTimestamp.setText(UtilsMain.stringToDateCondition(mMessage.getTimestamp()));
            //Set Avatar
            String avatar = ContantsHomeMMS.AppFolder + "/" + mMessage.getSender().getId() + "/" + mMessage.getSender().getAvatar();
            if (UtilsMain.checkFileIsExits(avatar)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(ContantsHomeMMS.AppFolder + "/" + mMessage.getSender().getId() + "/" + mMessage.getSender().getAvatar(), options);
                contact_avatar.setImageBitmap(bitmap);
            }
            //SetName
            username.setText(mMessage.getSender().getNameDisplay());
            if (MainActivity.isConnected) {
                //check need download attach file.
                if (checkMessageHasAttach(mMessage)) {
                    client.SendRequestFileAttach(mMessage);
                } else {
                    loadAttachFiles();
                }
            } else {
//                UtilsMain.showMessage(MessageContentActivity.this, "Server is offline");
                if (!checkMessageHasAttach(mMessage)){
                    loadAttachFiles();
                }
            }
        } else {
            mUri = intent.getData();
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        try {
            if (cursor.moveToFirst()) {
                String msgId = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_ID));
                mMessage = HomeMMSDatabaseHelper.getMessage(this, msgId);
                mTitleView.setText(mMessage.getTitle());
                mContentView.setText(mMessage.getContentText());
                mTimestamp.setText(UtilsMain.stringToDateCondition(mMessage.getTimestamp()));
                //Set Avatar
                String avatar = ContantsHomeMMS.AppFolder + "/" + mMessage.getSender().getId() + "/" + mMessage.getSender().getAvatar();
                if (UtilsMain.checkFileIsExits(avatar)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bitmap = BitmapFactory.decodeFile(ContantsHomeMMS.AppFolder + "/" + mMessage.getSender().getId() + "/" + mMessage.getSender().getAvatar(), options);
                    contact_avatar.setImageBitmap(bitmap);
                }
                //SetName
                username.setText(mMessage.getSender().getNameDisplay());
                if (MainActivity.isConnected) {
                    //check need download attach file.
                    if (checkMessageHasAttach(mMessage)) {
                        client.SendRequestFileAttach(mMessage);
                    } else {
                        loadAttachFiles();
                    }
                } else {
//                    UtilsMain.showMessage(MessageContentActivity.this, "Server is offline");
                    if (!checkMessageHasAttach(mMessage)){
                        loadAttachFiles();
                    }
                }
            /*mTitleView.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_TITLE)));
            mContentView.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));

            String timeStamp = UtilsMain.stringToDateCondition(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
            mTimestamp.setText(timeStamp);*/
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean checkMessageHasAttach(Message message) { //true - down, false - not down
        if (!message.getContentImage().equals("null") || !message.getContentVideo().equals("null") || !message.getContentAudio().equals("null")) {
            String myPath = ContantsHomeMMS.AppFolder + "/" + message.getSender().getId() + "/";
            if (!message.getContentAudio().equals("null") && !UtilsMain.checkFileIsExits(myPath + message.getContentAudio())) {
                return true;
            }
            if (!message.getContentVideo().equals("null") && !UtilsMain.checkFileIsExits(myPath + message.getContentVideo())) {
                return true;
            }
            if (!message.getContentImage().equals("null") && !UtilsMain.checkFileIsExits(myPath + message.getContentImage())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_message_content, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            //implement delete message here
            MainActivity.client.SendRequestDeleteMessage(HomeMMSDatabaseHelper.getMessage(this, mMessage.getId()));
            HomeMMSDatabaseHelper.deleteMessage(this, mMessage.getId());
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    public static class ReceiveFile extends AsyncTask<Void, Void, Void> {
        public final static int SOCKET_PORT = 6666;
        public final static String SERVER = MainActivity.rasp.getIPAddress();
        public static Socket socket;
        public ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mActivity);
            pd.setTitle("Downloading");
            pd.setMessage("Downloading attach file");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Server low down, client delay 1s to Server open port listening.
                Thread.sleep(1000);
                socket = new Socket(SERVER, SOCKET_PORT);

                System.out.println("Client: Just connected to " + socket.getRemoteSocketAddress());

                InputStream inFromServer = socket.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);

                //Read Object
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ArrayList<ObjectFile> objectFiles = (ArrayList<ObjectFile>) objectInputStream.readObject();

                //Read File Object
                RecieveFile.readObjectFile(objectFiles);

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //load attach file
            loadAttachFiles();
            pd.dismiss();
            super.onPostExecute(aVoid);
        }
    }

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

    public static void loadAttachFiles() {
        if (!mMessage.getContentImage().equals("null")) {
            attachImages.clear();
            attachImages.add(0, PATH_FOLDER_USER + "/" + mMessage.getSender().getId() + "/" + mMessage.getContentImage());
            mImageAdapter.notifyDataSetChanged();
            imageView.setVisibility(View.VISIBLE);
        }
        if (!mMessage.getContentAudio().equals("null")) {
            audioName = PATH_FOLDER_USER + "/" + mMessage.getSender().getId() + "/" + mMessage.getContentAudio();
            playAudioView.setVisibility(View.VISIBLE);
        }

        if (!mMessage.getContentVideo().equals("null")) {
            mVideoView.setVideoPath(PATH_FOLDER_USER + "/" + mMessage.getSender().getId() + "/" + mMessage.getContentVideo());
            videoView.setVisibility(View.VISIBLE);
        }
        //attatchFileView.setVisibility(View.VISIBLE);
    }

    private void resetAllAttachView() {
        imageView.setVisibility(View.GONE);
        playAudioView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        attachImages.clear();
        audioName = "";

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.stop();
        resetAllAttachView();
    }
}

package com.thesis.mmtt2011.homemms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ImageButton;
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
import java.util.List;


public class MessageContentActivity extends MainActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    static final String PATH_FOLDER_USER = ContantsHomeMMS.myUserFolder + MainActivity.myUser.getId() + "/";

    public static final String TAG = "MessageContentActivity";

    private Uri mUri;

    private Message mMessage;

    private List<String> attachImages;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mTitleView;
    private TextView mContentView;
    private TextView mTimestamp;
    private VideoView mVideoView;

    //implement audio
    ImageButton btnPlay;
    SeekBar audioProgressBar;
    TextView audioTotalDuration;

    MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    //dumpmy content
    String audioName = "/storage/extSdCard/Music/Ryuukou-HoaTau-3089028.mp3";

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
        // Get the message from the intent
        Intent intent = getIntent();
        if (intent.hasExtra(TAG)) {
            String messageId = intent.getStringExtra(TAG);
            mMessage = HomeMMSDatabaseHelper.getMessage(this, messageId);
            mTitleView.setText(mMessage.getTitle());
            mContentView.setText(mMessage.getContentText());
            mTimestamp.setText(mMessage.getTimestamp());
        } else {
            mUri = intent.getData();
            getSupportLoaderManager().initLoader(0, null, this);
        }

        // get images devices
        mRecyclerView = (RecyclerView) findViewById(R.id.device_recycler_view);
        //load list image attach path were downloaded on android
        getListImageAttach();
        mAdapter = new ImageAdapter(this, attachImages);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //audio
        audioProgressBar = (SeekBar) findViewById(R.id.seekbar);
        audioProgressBar.setOnSeekBarChangeListener(this);
        audioTotalDuration = (TextView) findViewById(R.id.tv_total_duration);
        btnPlay = (ImageButton) findViewById(R.id.play_button);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
                    } else {
                        playAudio(audioName);
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_36dp);
                    }
                }
            }
        });

        //load video
        mVideoView = (VideoView) findViewById(R.id.video_content);
        //load video content

        //
        if (MainActivity.isConnected) {
            if (checkMessageHasAttach(mMessage)) {
                client.SendRequestFileAttach(mMessage);
            }
        } else UtilsMain.showMessage(MessageContentActivity.this, "Server is offline");
    }

    public void getListImageAttach() {
        //get image attach AsyncTask here
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mTitleView.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_TITLE)));
            mContentView.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_CONTENT_TEXT)));

            String timeStamp = UtilsMain.stringToDateCondition(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_TIMESTAMP)));
            mTimestamp.setText(timeStamp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean checkMessageHasAttach(Message message) { //true - down, false - not down
        String myPath = ContantsHomeMMS.AppFolder + "/" + myUser.getId() + "/";
        if (message.getContentAudio() != null && !UtilsMain.checkFileIsExits(myPath + message.getContentAudio())) {
            return true;
        }
        if (message.getContentVideo() != null && !UtilsMain.checkFileIsExits(myPath + message.getContentVideo())) {
            return true;
        }
        if (message.getContentImage() != null && !UtilsMain.checkFileIsExits(myPath + message.getContentImage())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            //implement delete message here

            return true;
        }

        return super.onOptionsItemSelected(item);
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
            pd.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}

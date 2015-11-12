package thesis.danh.avpdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import thesis.danh.avpdemo.Model.Message;
import thesis.danh.avpdemo.Model.User;
import thesis.danh.avpdemo.Model.RaspberryPiClient;
import thesis.danh.avpdemo.SSH.ConnectSSHAsyncTask;
import thesis.danh.avpdemo.SSH.DisConnectSSHAsyncTask;
import thesis.danh.avpdemo.SSH.PushFileAsyncTask;
import thesis.danh.avpdemo.SSH.Utils;
import thesis.danh.avpdemo.Socket.Client;
import thesis.danh.avpdemo.Socket.KeyString;

public class MainActivity extends AppCompatActivity {
    public static TextView tvConnectSSH, tvConnectS, tvSendMessage,
            tvSenderRecieve, tvMessageRecieve, tvAudioRecieve, tvVideoRecieve, tvPhotoRecieve, tvTime;
    EditText edIPPI, edReciever;
    Button btnRecordA, btnPlayRecordA, btnRecordV, btnPlayV, btnPhoto, btnPushA, btnPushP, btnPushV,
            btnSendMessage, btnFileInfoPush, btnAudioRecieve, btnPhotoRecieve, btnVideoRecieve;
    public static Button btnConnectSSHPI, btnConnectSPI;
    ImageView imgPhoto, imgvPhotoRecieve;
    VideoView vViewV;
    public static LinearLayout lnRecive, lnVideoRecieve, lnPhotoRecieve, lnAudioRecieve;

    private static final String LOG_TAG_A = "AudioRecordTest";
    private static String mDir = null, mFileNameA = null, mFileNameP = null, mFileNameV = null;

    private MediaRecorder mRecorderA = null;
    private MediaPlayer mPlayerA = null;
    private boolean mStartRecordingA = true, mStartPlayingA = true;
    private boolean mStartPlayingV = true;
    private Uri outputFileUri = null, outputFileUriV = null;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1111;
    private static final int REQUEST_VIDEO_CAPTURE = 2222;

    private Utils utilsSSH;
    private static thesis.danh.avpdemo.Utils utils;
    private thesis.danh.avpdemo.Network.Utils utilsNetwork;

    public static Client client;
    private static final int port = 2222;

    public static boolean mStartConnectSSH = true;
    public static boolean mStartConnectS = true;

    public static User user;

    //Demo info
    public static RaspberryPiClient rasp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        utilsSSH = new Utils(this);
        utils = new thesis.danh.avpdemo.Utils(this);
        utilsNetwork = new thesis.danh.avpdemo.Network.Utils(this);

        //get my myDevice info
        user = new User(utilsNetwork.getMacAddress(), "Danh", "123", "avatar.jpg", "online");

        mDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileNameA = mDir + "/audiorecord.mp3";
//        mFileNameP = mDir + "/imagecapture.jpg";
//        mFileNameV = mDir + "/videorecord.mp4";

        btnRecordA = (Button) findViewById(R.id.btnRecordA);
        btnPlayRecordA = (Button) findViewById(R.id.btnPlayRecordA);
        btnRecordV = (Button) findViewById(R.id.btnRecordV);
        btnPlayV = (Button) findViewById(R.id.btnPlayV);
        btnPhoto = (Button) findViewById(R.id.btnChoosePhoto);
        btnPushA = (Button) findViewById(R.id.btnPushA);
        btnPushP = (Button) findViewById(R.id.btnPushP);
        btnPushV = (Button) findViewById(R.id.btnPushV);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        btnConnectSSHPI = (Button) findViewById(R.id.btnConnectSSHPI);
        btnConnectSPI = (Button) findViewById(R.id.btnConnectSPI);
        btnFileInfoPush = (Button) findViewById(R.id.btnFileInfoPush);
        tvConnectSSH = (TextView) findViewById(R.id.tvConnectSSH);
        tvConnectS = (TextView) findViewById(R.id.tvConnectS);
        tvSendMessage = (TextView) findViewById(R.id.edSendMessage);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        vViewV = (VideoView) findViewById(R.id.vViewV);
        edIPPI = (EditText) findViewById(R.id.edIPPI);
        edReciever = (EditText) findViewById(R.id.edReciever);
        lnRecive = (LinearLayout) findViewById(R.id.lnRecive);
        tvSenderRecieve = (TextView) findViewById(R.id.tvSenderRecieve);
        tvMessageRecieve = (TextView) findViewById(R.id.tvMessageRecieve);
        tvAudioRecieve = (TextView) findViewById(R.id.tvAudioRecieve);
        tvVideoRecieve = (TextView) findViewById(R.id.tvVideoRecieve);
        tvPhotoRecieve = (TextView) findViewById(R.id.tvPhotoRecieve);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnAudioRecieve = (Button) findViewById(R.id.btnAudioRecieve);
        btnPhotoRecieve = (Button) findViewById(R.id.btnPhotoRecieve);
        btnVideoRecieve = (Button) findViewById(R.id.btnVideoRecieve);
        imgvPhotoRecieve = (ImageView) findViewById(R.id.imgvPhotoRecieve);
        lnAudioRecieve = (LinearLayout) findViewById(R.id.lnAudioRecieve);
        lnVideoRecieve = (LinearLayout) findViewById(R.id.lnVideoRecieve);
        lnPhotoRecieve = (LinearLayout) findViewById(R.id.lnPhotoRecieve);

        btnConnectSPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Demo info
                rasp = new RaspberryPiClient("Pi", edIPPI.getText().toString(), "B8:27:EB:57:07:1C");
                onConnectSocket(mStartConnectS);
            }
        });

        btnConnectSSHPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onConnectSSH(mStartConnectSSH);
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendMessage(tvSendMessage.getText().toString());
//
//                //Temp send info reciever.
//                String reciever = edReciever.getText().toString();
//                client.SendInfoMessage(reciever);
            }
        });

        btnRecordA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileNameA = mDir + "/audiorecord.mp3";
                onRecordA(mStartRecordingA);
                if (mStartRecordingA) {
                    btnRecordA.setText("Stop recording");
                } else {
                    btnRecordA.setText("Start recording");
                }
                mStartRecordingA = !mStartRecordingA;
            }
        });

        btnPlayRecordA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayA(mStartPlayingA);
                if (mStartPlayingA) {
                    btnPlayRecordA.setText("Stop playing");
                } else {
                    btnPlayRecordA.setText("Start playing");
                }
                mStartPlayingA = !mStartPlayingA;
            }
        });

        btnRecordV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileNameV = mDir + "/videorecord.mp4";
                onRecordV();
            }
        });

        btnPlayV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayV(mStartPlayingV);
                if (mStartPlayingV) {
                    btnPlayV.setText("Pause playing");
                } else {
                    btnPlayV.setText("Resume playing");
                }
                mStartPlayingV = !mStartPlayingV;
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileNameP = mDir + "/imagecapture.jpg";
                openImageIntent();
            }
        });

        btnPushA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pushAudio();
            }
        });

        btnPushP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pushPhoto();
            }
        });

        btnPushV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pushVideo();
            }
        });

        btnFileInfoPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send info reciever.
                //Demo info
                String receiverID = edReciever.getText().toString();
                User receiver = new User(receiverID, "", "", "", "");
                List<User> receivers = new ArrayList<User>();
                receivers.add(receiver);

                String fNameAudio = null, fNamePhoto = null, fNameVideo = null;
                //Get Name File Audio
                if (mFileNameA!=null) {
                    String[] separatedA = mFileNameA.split("/");
                    fNameAudio = separatedA[separatedA.length - 1];
                }
                //Get Name file Photo
                if (mFileNameP!=null) {
                    String[] separatedP = mFileNameP.split("/");
                    fNamePhoto = separatedP[separatedP.length - 1];
                }
                //Get Name file Video
                if (mFileNameV!=null) {
                    String[] separatedV = mFileNameV.split("/");
                    fNameVideo = separatedV[separatedV.length - 1];
                }
                Message message = new Message();
                message.setmId(utils.createMessageID(user.getId()));
                message.setSender(user);
                message.setReceiver(receivers);
                message.setTitle("Title demo");
                message.setContentText(tvSendMessage.getText().toString());
                message.setContentAudio(fNameAudio);
                message.setContentImage(fNamePhoto);
                message.setContentVideo(fNameVideo);
                message.setTimestamp(utils.getCurrentTime());

                if (client != null) {
                    client.SendInfoMessage(message);
                } else utils.showMesg("You must connect Socket!");

                //Send messages
//                sendMessage(tvSendMessage.getText().toString());

                //Connect SSH
                if (fNameAudio!=null || fNamePhoto!=null || fNameVideo!=null) {
                    onConnectSSH(mStartConnectSSH);

                    //Push File
                    pushAudio(fNameAudio);
                    pushPhoto(fNamePhoto);
                    pushVideo(fNameVideo);
                }
                //Send File Info
//                sendFileInfoPush();

                //End note.
                if (client != null) {
                    client.SendMessageEndNote();
                } else utils.showMesg("Dont have info client.");
            }
        });

        btnAudioRecieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAudioRecieve(tvAudioRecieve.getText().toString());
            }
        });

        btnVideoRecieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewVideoRecieve(tvVideoRecieve.getText().toString());
            }
        });

        btnPhotoRecieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPhotoRecieve(tvPhotoRecieve.getText().toString());
            }
        });
    }


    private void viewAudioRecieve(String fName) {
        mPlayerA = new MediaPlayer();
        try {
            mPlayerA.setDataSource(mDir + "/" + fName);
            mPlayerA.prepare();
            mPlayerA.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            mPlayerA.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayRecordA.setText("Play Audio");
                    mStartPlayingA = !mStartPlayingA;
                }
            });
            mPlayerA.start();
        } catch (IOException e) {
            Log.e(LOG_TAG_A, "prepare() failed");
        }
    }

    private void viewVideoRecieve(String fName) {
        String path = mDir + "/" + fName;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intent.setDataAndType(Uri.parse(path), "video/mp4");
        startActivity(intent);
    }

    private void viewPhotoRecieve(String fName) {
        File imgFile = new  File(mDir + "/" + fName);
        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgvPhotoRecieve.setImageBitmap(myBitmap);
        }
    }

    private void pushAudio(String fNameAudio) {
        if (mFileNameA != null) {
            byte[] bytes = utilsSSH.loadResource(mFileNameA);
            if (bytes != null) {
                PushFileAsyncTask async = new PushFileAsyncTask(bytes, fNameAudio, rasp, MainActivity.this);
                async.execute();
            }
        }
    }

    private void pushPhoto(String fNamePhoto) {
        if (mFileNameP != null) {
            byte[] bytes = utilsSSH.loadResource(mFileNameP);
            if (bytes != null) {
                PushFileAsyncTask async = new PushFileAsyncTask(bytes, fNamePhoto, rasp, MainActivity.this);
                async.execute();
            }
        }
    }

    private void pushVideo(String fNameVideo) {
        if (mFileNameV != null) {
            byte[] bytes = utilsSSH.loadResource(mFileNameV);
            if (bytes != null) {
                PushFileAsyncTask async = new PushFileAsyncTask(bytes, fNameVideo, rasp, MainActivity.this);
                async.execute();
            }
        }
    }

    private void onConnectSocket(boolean connect) {
        client = new Client(rasp, port);
        if (connect) {
            client.StartSocket();
            mStartConnectS = false;
            btnConnectSPI.setText("Disconnect");
            tvConnectS.setText("Connected");
        } else {
            if (client.CloseConnectSocket()) {
                mStartConnectS = true;
                btnConnectSPI.setText("Connect");
                tvConnectS.setText("Disconnected");
            }
        }
    }

    private void sendMessage(String msg) {
        if (msg != null && !msg.equals("")) {
            client.SendMessages(msg);
        }
    }

    private void onConnectSSH(boolean connect) {
        if (connect) {
            new ConnectSSHAsyncTask(this, rasp).execute();
        } else {
            new DisConnectSSHAsyncTask(this, rasp).execute();
        }
    }

    private void onRecordA(boolean start) {
        if (start) {
            startRecordingA();
        } else {
            stopRecordingA();
        }
    }

    private void onPlayA(boolean start) {
        if (start) {
            startPlayingA();
        } else {
            stopPlayingA();
        }
    }

    private void startPlayingA() {
        mPlayerA = new MediaPlayer();
        try {
            mPlayerA.setDataSource(mFileNameA);
            mPlayerA.prepare();
            mPlayerA.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            mPlayerA.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayRecordA.setText("Start playing");
                    mStartPlayingA = !mStartPlayingA;
                }
            });
            mPlayerA.start();
        } catch (IOException e) {
            Log.e(LOG_TAG_A, "prepare() failed");
        }
    }

    private void stopPlayingA() {
        if (mPlayerA != null) {
            mPlayerA.release();
            mPlayerA = null;
        }
    }

    private void startRecordingA() {
//        mFileNameA = "audio_" + System.currentTimeMillis() + ".3gp";

        mRecorderA = new MediaRecorder();
        mRecorderA.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorderA.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorderA.setOutputFile(mFileNameA);
        mRecorderA.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorderA.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG_A, "prepare() failed");
        }

        mRecorderA.start();
    }

    private void stopRecordingA() {
        if (mRecorderA != null) {
            mRecorderA.release();
            mRecorderA = null;
        }
    }

    private void onRecordV() {
        startRecordingV();
    }

    private void onPlayV(boolean start) {
        if (start) {
            startPlayingV();
        } else {
            pausePlayingV();
        }
    }

    private void startRecordingV() {
        outputFileUriV = Uri.fromFile(new File(mFileNameV));
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10983040L);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriV);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void startPlayingV() {
        if (vViewV != null) {
            if (vViewV.isPlaying()) {
                vViewV.resume();
            } else vViewV.start();

            vViewV.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            vViewV.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayV.setText("Start playing");
                    mStartPlayingV = !mStartPlayingV;
                }
            });
        }
    }

    private void pausePlayingV() {
        if (vViewV != null) {
            vViewV.pause();
        }
    }

    private void openImageIntent() {
//        final String fnameP = "img_" + System.currentTimeMillis() + ".jpg";
//        final File sdImageMainDirectory = new File(mDir, fnameP);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        outputFileUri = Uri.fromFile(new File(mFileNameP));

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
        startActivityForResult(chooserIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    private void sendFileInfoPush() {
        if (mFileNameP != null) {
            String photo = mFileNameP.substring(mFileNameP.lastIndexOf("/") + 1);
            client.SendMessageInfoFilePush(photo, KeyString.TypeFile.Photo);
        }
        if (mFileNameA != null) {
            String audio = mFileNameA.substring(mFileNameA.lastIndexOf("/") + 1);
            client.SendMessageInfoFilePush(audio, KeyString.TypeFile.Audio);
        }
        if (mFileNameV != null) {
            String video = mFileNameV.substring(mFileNameV.lastIndexOf("/") + 1);
            client.SendMessageInfoFilePush(video, KeyString.TypeFile.Video);
        }
    }

    public static void updateRecieveInfo(Message message) {
        //Chờ khi có database tren client -> dùng hàm get User from userid.
//        tvSenderRecieve.setText(message.getSender().getId());
        lnRecive.setVisibility(View.VISIBLE);
        tvMessageRecieve.setText(message.getContentText());
        if (!message.getContentAudio().equals("")) {
            tvAudioRecieve.setText(message.getContentAudio());
            lnAudioRecieve.setVisibility(View.VISIBLE);
        }
        if (!message.getContentVideo().equals("")) {
            tvVideoRecieve.setText(message.getContentVideo());
            lnVideoRecieve.setVisibility(View.VISIBLE);
        }
        if (!message.getContentImage().equals("")) {
            tvPhotoRecieve.setText(message.getContentImage());
            lnPhotoRecieve.setVisibility(View.VISIBLE);
        }
        tvTime.setText(convertDate(message.getTimestamp()).toString());
    }

    public static Date convertDate(String dateString) {
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

    public static void updateRecieverNote(String msg) {
//        tvReciverNote.setText(msg);

        //Call show dialog notice
        utils.showDialogNewRecieve();
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

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorderA != null) {
            mRecorderA.release();
            mRecorderA = null;
        }

        if (mPlayerA != null) {
            mPlayerA.release();
            mPlayerA = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
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
                Bitmap bitmap = BitmapFactory.decodeFile(mFileNameP, options);
                //rotete image.
                try {
                    ExifInterface ei = new ExifInterface(mFileNameP);
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
                imgPhoto.setImageBitmap(bitmap);
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
                    imgPhoto.setImageBitmap(bitmap);
                    mFileNameP = getRealPathFromURI(this, selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            Uri videoUri = data.getData();
//            vViewV.setVideoURI(videoUri);
            vViewV.setVideoURI(Uri.fromFile(new File(mFileNameV)));
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

package thesis.danh.avpdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import thesis.danh.avpdemo.SSH.ConnectSSHAsyncTask;
import thesis.danh.avpdemo.SSH.DisConnectSSHAsyncTask;
import thesis.danh.avpdemo.SSH.PushFileAsyncTask;
import thesis.danh.avpdemo.SSH.Utils;
import thesis.danh.avpdemo.Socket.Client;

public class MainActivity extends AppCompatActivity {
    public static TextView tvConnectSSH, tvConnectS, tvSendMessage;
    EditText edIPPI;
    Button btnRecordA, btnPlayRecordA, btnRecordV, btnPlayV, btnPhoto, btnPushA, btnPushP, btnPushV, btnSendMessage, btnFileInfoPush;
    public static Button btnConnectSSHPI, btnConnectSPI;
    ImageView imgPhoto;
    VideoView vViewV;

    private static final String LOG_TAG_A = "AudioRecordTest";
    private static String mDir = null, mFileNameA = null, mFileNameP = null, mFileNameV = null;

    private MediaRecorder mRecorderA = null;
    private MediaPlayer mPlayerA = null;
    private boolean mStartRecordingA = true, mStartPlayingA = true;
    private boolean mStartPlayingV = true;
    private Uri outputFileUri = null, outputFileUriV = null;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1111;
    private static final int REQUEST_VIDEO_CAPTURE = 2222;

    private Utils utils;

    public static Client client;
    private static final int port = 2222;

    public static boolean mStartConnectSSH = true;
    public static boolean mStartConnectS = true;


    //Demo info
    RaspberryPiClient rasp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        utils = new Utils(this);

        mDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileNameA = mDir + "/audiorecord.3gp";
        mFileNameP = mDir + "/imagecapture.jpg";
        mFileNameV = mDir + "/videorecord.3gp";

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

        //Demo info
        rasp = new RaspberryPiClient("Pi", edIPPI.getText().toString(), "B8:27:EB:57:07:1C");

        btnConnectSPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectSocket(mStartConnectS);
            }
        });

        btnConnectSSHPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectSSH(mStartConnectSSH);
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(tvSendMessage.getText().toString());
            }
        });

        btnConnectSPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectSocket(mStartConnectS);
            }
        });

        btnRecordA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        btnRecordV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                openImageIntent();
            }
        });

        btnPushA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileNameA != null) {
                    String[] separated = mFileNameA.split("/");
                    //Get Name File
                    String fName = separated[separated.length - 1];
                    byte[] bytes = utils.loadResource(mFileNameA);
                    if (bytes != null) {
                        PushFileAsyncTask async = new PushFileAsyncTask(bytes, fName, rasp, MainActivity.this);
                        async.execute();
                    }
                }
            }
        });

        btnPushP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileNameP != null) {
                    String[] separated = mFileNameP.split("/");
                    //Get Name File
                    String fName = separated[separated.length - 1];
                    byte[] bytes = utils.loadResource(mFileNameP);
                    if (bytes != null) {
                        PushFileAsyncTask async = new PushFileAsyncTask(bytes, fName, rasp, MainActivity.this);
                        async.execute();
                    }
                }
            }
        });

        btnPushV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileNameV != null) {
                    String[] separated = mFileNameV.split("/");
                    //Get Name File
                    String fName = separated[separated.length - 1];
                    byte[] bytes = utils.loadResource(mFileNameV);
                    if (bytes != null) {
                        PushFileAsyncTask async = new PushFileAsyncTask(bytes, fName, rasp, MainActivity.this);
                        async.execute();
                    }
                }
            }
        });

        btnFileInfoPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFileInfoPush();
            }
        });
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

    private void sendMessage(String msg){
        if (msg!=null && !msg.equals("")){
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
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriV);
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
        final String fnameP = "img_" + System.currentTimeMillis() + ".jpg";
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

    private void sendFileInfoPush(){
        client.SendMessageInfoFilePush(mFileNameA);
        client.SendMessageInfoFilePush(mFileNameP);
        client.SendMessageInfoFilePush(mFileNameV);
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

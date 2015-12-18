package com.thesis.mmtt2011.homemms.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.thesis.mmtt2011.homemms.Network.UtilsNetwork;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.SSH.PushFileAsyncTask;
import com.thesis.mmtt2011.homemms.SSH.UtilsSSH;
import com.thesis.mmtt2011.homemms.SlidingTabLayout;
import com.thesis.mmtt2011.homemms.Socket.Client;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.adapter.ViewPagerAdapter;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.RaspberryPi;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Message", "Sent"};
    int[] tabIcons = {
            R.drawable.ic_inbox_white_48dp,
            R.drawable.ic_send_white_48dp,
    };
    final int NumbOfTabs = 2;
    SearchView searchView;

    public static Client client;
    private static final int port = 2222;
    public static User myUser;
    private UtilsNetwork utilsNetworkNetwork;
    private static UtilsMain utilsMain;
    //Demo info
    public static RaspberryPi rasp;

    public static String mFilePathAudio = null, mFilePathImage = null, mFilePathVideo = null;

    private static Activity mActivity;
    private static HomeMMSDatabaseHelper homeMMSDatabaseHelper;

    public static boolean isConnected = false;        //Client is connecting to Server?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //create UtilsMain.
        utilsNetworkNetwork = new UtilsNetwork(this);
        utilsMain = new UtilsMain(this);

        mActivity = this;
        homeMMSDatabaseHelper = new HomeMMSDatabaseHelper(this);

        //get my User info Vì chưa biết user đã có đăng ký hay chưa?
        //Nếu đã dk rồi thì sẽ có full info myUser.
        //Nếu chưa sẽ chỉ có ID (Mac) trong info myUser.;
        User user = HomeMMSDatabaseHelper.getUser(getBaseContext(), utilsNetworkNetwork.getMacAddress());
        if (user != null) {
            myUser = user;
        } else {
            myUser = new User(utilsNetworkNetwork.getMacAddress(), null, null, null
                    , ContantsHomeMMS.UserStatus.online.name());
//            long longid = HomeMMSDatabaseHelper.createUser(getBaseContext(), myUser);
//            Log.i("HomeMMS", String.valueOf(longid));
        }

        //Info server Pi
        String ServerIP = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.SERVER_IP);
        String ServerMAC = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.SERVER_MAC);
        String ServerNAME = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.SERVER_NAME);
        rasp = new RaspberryPi(ServerNAME, ServerIP, ServerMAC);
//        rasp = new RaspberryPiClient("Pi", "192.168.1.240", "B8:27:EB:57:07:1C");

        //Create folder app if not exits. Run after myUser has UserID.
        createFolderApp();

        //Load before getIsFirstRun()
        //Load connect to Pi and listen receive message from Pi.
        createClient();

        if (PreferencesHelper.getIsFirstRun(this)) {
            Intent intent = new Intent(this, AppIntroSetup.class);
            startActivity(intent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabNewMessage = (FloatingActionButton) findViewById(R.id.fab_new_message);
        fabNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hit new message", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, ComposeMessageActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabPhoto = (FloatingActionButton) findViewById(R.id.fab_photo);
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hit new message photo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                /*Implement compose photo message;*/

                //Test
                PreferencesHelper.writeToPreferencesBoolean(mActivity, false, ContantsHomeMMS.STATE_NORMAL);

//                mFilePathImage = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/"
//                        + utilsMain.createNameForFile(ContantsHomeMMS.TypeFile.Photo);
//                PreferencesHelper.writeToPreferencesString(getBaseContext(), mFilePathImage, ContantsHomeMMS.ImagePref);
//                utilsMain.openImageIntent(mFilePathImage);
            }
        });

        FloatingActionButton fabRecord = (FloatingActionButton) findViewById(R.id.fab_record);
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hit new message record", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //Test
                client.SendRequestServerToNormalState();

//                mFilePathAudio = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/"
//                        + utilsMain.createNameForFile(ContantsHomeMMS.TypeFile.Audio);
//                utilsMain.startRecordingAudio(mFilePathAudio);
            }
        });

        FloatingActionButton fabVideo = (FloatingActionButton) findViewById(R.id.fab_video);
        fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hit new message video", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 /*Implement compose video message;*/
                mFilePathVideo = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/"
                        + utilsMain.createNameForFile(ContantsHomeMMS.TypeFile.Video);
                utilsMain.startRecordingV(mFilePathVideo);
            }
        });


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumbOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (client == null) {
            createClient();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (client != null) {
//            client.CloseSocket();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        ComponentName componentName = new ComponentName(this, SearchResultsActivity.class);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconifiedByDefault(false);
        //searchView.setOnQueryTextListener(queryTextListener);
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
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_scan_devices) {
            Intent intent = new Intent(this, ScanDevicesActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_user_info) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            //Toast.makeText(getActivity(),"Searching " + , Toast.LENGTH_SHORT).show();
            return false;
        }
    };*/

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
                        if (mFilePathImage == null) {
                            mFilePathImage = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.ImagePref);
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(mFilePathImage, options);
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
                                    default:
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
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

                    gotoComposeMessageActivity();

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
                    gotoComposeMessageActivity();
//                    Uri videoUri = data.getData();
//                    vViewV.setVideoURI(videoUri);
                    //set video to viewimage
//                    vViewV.setVideoURI(Uri.fromFile(new File(mFileNameVideo)));
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
                    gotoComposeMessageActivity();
                    String tempPath = utilsMain.getRealPathFromURI(this, data.getData());
                    utilsMain.copyFile(new File(tempPath), new File(mFilePathAudio));
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

            case ContantsHomeMMS.LOGIN_REQUEST_CODE:
//                Log.d("result", "LOGIN_REQUEST_CODE");
//                Log.d("result", resultCode + "");
//                if (resultCode == RESULT_OK) {
//                    boolean b = client.SendLoginInfoOfClient();
//                    Log.d("login", b + "");
//                }
                break;
            case ContantsHomeMMS.REGISTER_REQUEST_CODE:
                Log.d("result", "REGISTER_REQUEST_CODE");
                if (resultCode == RESULT_OK) {
                    client.SendMessageInfoOfClient();
                    String avatarFile = ContantsHomeMMS.myUserFolder + myUser.getId() + "/" + myUser.getAvatar();
                    if (utilsMain.checkFileIsExits(avatarFile)) {
                        //Push File by SSH
                        pushFileAttachToPi(myUser.getAvatar());
                    }
                }
                break;
            default:
                Log.d("result", "default");
                break;
        }
    }

    public static void createClient() {
        //check state Server is Router --> set IP Address rasp again.
        if (!PreferencesHelper.getIsPreferenceBoolean(mActivity, ContantsHomeMMS.STATE_NORMAL)){
            rasp.setIPAddress(ContantsHomeMMS.HP_SERVER_IP);
        }

        //connect socket.
        int testWifi = utilsMain.checkIsConnectToWifiHome();
        if (testWifi == 1) {
            if (rasp != null && !PreferencesHelper.getIsFirstRun(mActivity)) {
                client = new Client(rasp, port, mActivity);
                client.StartSocket();
            }
        } else if (testWifi == -1) {
            utilsMain.ShowToast("App is offline");
        } else if (testWifi == 0) {
            utilsMain.ShowToast("Dont config Server.");
        }
    }

    public static void ShowLoginAcitivty() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivityForResult(intent, ContantsHomeMMS.LOGIN_REQUEST_CODE);
    }

    public static void ShowRegisterActivity() {
        Intent intent = new Intent(mActivity, RegisterActivity.class);
        mActivity.startActivityForResult(intent, ContantsHomeMMS.REGISTER_REQUEST_CODE);
    }

    //Send message to Server.
    public static void SendMessageWaitSend(Message message) {
        if (client != null && isConnected) {
            client.SendInfoMessage(message);

            if (message.getContentAudio() != null) pushFileAttachToPi(message.getContentAudio());
            if (message.getContentImage() != null) pushFileAttachToPi(message.getContentImage());
            if (message.getContentVideo() != null) pushFileAttachToPi(message.getContentVideo());

//            if (message.getContentAudio() != null || message.getContentImage() != null || message.getContentAudio() != null) {
//                //Connect SSH.
//                new ConnectSSHAsyncTask(mActivity, rasp).execute();
//                //Push File by SSH
//                pushFileAttachToPi(message.getContentAudio());
//                pushFileAttachToPi(message.getContentImage());
//                pushFileAttachToPi(message.getContentVideo());
//            }

            //Notify server myUser finished note.
            //After send message successful,
            if (client.SendMessageEndNote()) {
                //update message in database status sent.
                message.setStatus(ContantsHomeMMS.MessageStatus.sent.name());
                homeMMSDatabaseHelper.updateMessage(mActivity, message);
            }
        }
    }

    public static void pushFileAttachToPi(String mFileName) {
        if (mFileName != null) {
            String pathFile = ContantsHomeMMS.AppFolder + "/" + MainActivity.myUser.getId() + "/" + mFileName;
            if (utilsMain.checkFileIsExits(pathFile)) {
                byte[] bytes = UtilsSSH.loadResourceFromPath(pathFile);
                if (bytes != null) {
                    PushFileAsyncTask async = new PushFileAsyncTask(bytes, mFileName, rasp, mActivity);
                    async.execute();
                }
            }
        }
    }

    private void createFolderApp() {
        if (utilsMain.CreateFolder(ContantsHomeMMS.AppFolder)) {
            if (utilsMain.CreateFolder(ContantsHomeMMS.AppCacheFolder)) {
                utilsMain.CreateFolder(ContantsHomeMMS.myUserFolder + myUser.getId());
            }
        }

    }

    private void gotoComposeMessageActivity() {
        Intent intent = new Intent(MainActivity.this, ComposeMessageActivity.class);
        startActivity(intent);
    }
}

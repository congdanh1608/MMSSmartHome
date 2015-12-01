package com.thesis.mmtt2011.homemms.activity;

import android.app.Activity;
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
import com.thesis.mmtt2011.homemms.Network.Utils;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.SlidingTabLayout;
import com.thesis.mmtt2011.homemms.Socket.Client;
import com.thesis.mmtt2011.homemms.adapter.ViewPagerAdapter;
import com.thesis.mmtt2011.homemms.helper.PreferencesHelper;
import com.thesis.mmtt2011.homemms.model.RaspberryPiClient;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Message", "Sent"};
    final int NumbOfTabs = 2;
    SearchView searchView;

    public static Client client;
    private static final int port = 2222;
    public static User myUser;
    private Utils utilsNetwork;
    private com.thesis.mmtt2011.homemms.Utils utils;
    //Demo info
    public static RaspberryPiClient rasp;
    public static String mDir = null, mFileNameAudio = null, mFileNameImage = null, mFileNameVideo = null;

    private static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //create Utils.
        utilsNetwork = new Utils(this);
        utils = new com.thesis.mmtt2011.homemms.Utils(this);

        mActivity = this;

        //Info server Pi
        String ServerIP = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.SERVER_IP);
        String ServerMAC = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.SERVER_MAC);
        String ServerNAME = PreferencesHelper.getIsPreferenceString(this, ContantsHomeMMS.SERVER_NAME);
        rasp = new RaspberryPiClient(ServerNAME, ServerIP, ServerMAC);
//        rasp = new RaspberryPiClient("Pi", "192.168.1.240", "B8:27:EB:57:07:1C");

        //get my User info Vì chưa biết user đã có đăng ký hay chưa?
        //Nếu đã dk rồi thì sẽ có full info myUser.
        //Nếu chưa sẽ chỉ có ID (Mac) trong info myUser.;
        User user = HomeMMSDatabaseHelper.getUser(getBaseContext(), utilsNetwork.getMacAddress());
        if (user != null) {
            myUser = user;
        } else {
               myUser = new User(utilsNetwork.getMacAddress(), null, null, null
                    , ContantsHomeMMS.UserStatus.online.name());
            long longid = HomeMMSDatabaseHelper.createUser(getBaseContext(), myUser);
            Log.i("HomeMMS", String.valueOf(longid));
        }

        //Load before getIsFirstRun()
        //Load connect to Pi and listen receive message from Pi.
        init();

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
            }
        });

        FloatingActionButton fabRecord = (FloatingActionButton) findViewById(R.id.fab_record);
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hit new message record", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                /*Intent intent = new Intent(MainActivity.this, ComposeMessageActivity.class);
                startActivity(intent);*/
            }
        });

        FloatingActionButton fabVideo = (FloatingActionButton) findViewById(R.id.fab_video);
        fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hit new message video", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 /*Implement compose video message;*/
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
    protected void onStart() {
        super.onStart();
        if (client == null) {
            init();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(queryTextListener);
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

        /*if (id == R.id.action_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
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
    };

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
                            mFileNameImage = utils.getRealPathFromURI(this, selectedImageUri);
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
            case ContantsHomeMMS.REQUEST_VIDEO_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri videoUri = data.getData();
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
            case ContantsHomeMMS.LOGIN_REQUEST_CODE:
                Log.d("result", "LOGIN_REQUEST_CODE");
                Log.d("result", resultCode + "");
                if (resultCode == RESULT_OK) {
                    client.SendLoginInfoOfClient();
                }
                break;
            case ContantsHomeMMS.REGISTER_REQUEST_CODE:
                Log.d("result", "REGISTER_REQUEST_CODE");
                if (resultCode == RESULT_OK) {
                    client.SendMessageInfoOfClient();
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        //connect socket.
        if (rasp != null && !PreferencesHelper.getIsFirstRun(this)) {
            client = new Client(rasp, port, this);
            client.StartSocket();
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

}

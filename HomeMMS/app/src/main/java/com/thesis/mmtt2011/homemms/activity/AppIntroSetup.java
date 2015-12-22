package com.thesis.mmtt2011.homemms.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.fragment.ChooseServerFragment;

public class AppIntroSetup extends AppCompatActivity {

    private ChooseServerFragment chooseServerFragment;
    private static final String FRAGMENT_TAG = "ChooseServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro_setup);

        FloatingActionButton fabNext = (FloatingActionButton) findViewById(R.id.fab_next);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Hit new message", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, ComposeMessageActivity.class);
                startActivity(intent);*/
                chooseServerFragment = new ChooseServerFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.welcome_fragment_container, chooseServerFragment, FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(FRAGMENT_TAG);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            //background process and show home screen not main
            this.moveTaskToBack(true);
            //super.onBackPressed();
        }
    }
}

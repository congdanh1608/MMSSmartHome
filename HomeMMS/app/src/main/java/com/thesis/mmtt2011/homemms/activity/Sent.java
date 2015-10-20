package com.thesis.mmtt2011.homemms.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesis.mmtt2011.homemms.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Sent extends Fragment {


    public Sent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sent, container, false);
    }


}

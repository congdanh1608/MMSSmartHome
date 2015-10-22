package com.thesis.mmtt2011.homemms.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.adapter.MessageAdapter;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Sent extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Message> sentMessages;

    //Khoi tao danh sach cach message mau
    public void initListMessage() {
        sentMessages = new ArrayList<>();
        List<User> receivers = new ArrayList<>();
        User receiver = new User("Name Display", "00:00:00:00:00:00", "password", "link avatar");
        receivers.add(receiver);
        for(int i = 0; i < 15; i++) {
            User user = new User("Name Display", String.valueOf(i), "password", "link avatar");
            sentMessages.add(new Message("Title " + i, "Content " + i, "Timestamp " + i, "Status " + i, user, receivers));
        }
    }

    public Sent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.inbox_recycler_view);
        initListMessage();
        mAdapter = new MessageAdapter(sentMessages);
        mRecyclerView.setAdapter(mAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getActivity());
        //use a grid layout manager
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }


}

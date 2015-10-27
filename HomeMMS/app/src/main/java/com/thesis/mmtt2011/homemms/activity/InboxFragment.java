package com.thesis.mmtt2011.homemms.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
public class InboxFragment extends Fragment {

    public static final int COLUMN_GRID = 2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Message> messages;

    //Khoi tao danh sach cach message mau
    public void initListMessage() {
        messages = new ArrayList<>();
        List<User> receivers = new ArrayList<>();
        User receiver = new User("Name Display", "00:00:00:00:00:00", "password", "link avatar");
        receivers.add(receiver);
        String content[] = new String[3];
        content[0] = "Lorem ipsum dolor sit amet, vis invenire interesset ut. Ad duo tale appareat contentiones, id per nisl tantas percipitur. Tollit mediocrem repudiandae ad vim, ad pri habeo dolorem disputando. ";
        content[1] = "Lorem ipsum dolor sit amet, vis invenire interesset ut. Ad duo tale appareat contentiones, id per nisl tantas percipitur";
        content[2] = "Lorem ipsum dolor sit amet, vis invenire interesset ut.";
        for(int i = 0; i < 15; i++) {
            User user = new User("Pham Xuan Y", String.valueOf(i), "password", "link avatar");
            messages.add(new Message("Title " + i, content[i%3], "Oct 2" + i, "Status " + i, user, receivers));
        }
    }

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.inbox_recycler_view);
        initListMessage();
        mAdapter = new MessageAdapter(messages);
        mRecyclerView.setAdapter(mAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getActivity());
        //use a grid layout manager
        mLayoutManager = new GridLayoutManager(getActivity(),COLUMN_GRID);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

}

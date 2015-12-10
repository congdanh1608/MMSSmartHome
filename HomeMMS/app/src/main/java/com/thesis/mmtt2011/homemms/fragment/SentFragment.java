package com.thesis.mmtt2011.homemms.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.activity.MessageContentActivity;
import com.thesis.mmtt2011.homemms.adapter.MessageAdapter;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;
import com.thesis.mmtt2011.homemms.persistence.MessageTable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentFragment extends Fragment implements MessageAdapter.MessageViewHolder.ClickListener {

    private RecyclerView mRecyclerView;
    private static MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static Activity mActivity;

    static List<Message> sentMessages;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    //Khoi tao danh sach cach message mau
    public void initListMessage() {
        sentMessages = new ArrayList<>();
        sentMessages = HomeMMSDatabaseHelper.getAllMessagesBy(getActivity(), MessageTable.COLUMN_SENDER, MainActivity.myUser.getId());
        /*List<User> receivers = new ArrayList<>();
        User receiver = new User("00:00:00:00:00:00", "Name Display", "password", "link avatar", "offline");
        receivers.add(receiver);
        for (int i = 0; i < 15; i++) {
            User user = new User("00:00:00:00:00:00", "Name Display " + String.valueOf(i), "password", "link avatar", "online");
            sentMessages.add(new Message("00:00:00:00:00:01", user, receivers, "Title " + i, "Content" + i, null, null, null, "Oct 1" + i, "Status " + i));
        }*/
    }

    public SentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sent, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sent_recycler_view);

        mActivity = getActivity();

        initListMessage();
        mAdapter = new MessageAdapter(sentMessages, this);
        mRecyclerView.setAdapter(mAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getActivity());
        //use a grid layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }

    public static void UpdateNewMessageSent(String mID) {
        Message message = HomeMMSDatabaseHelper.getMessage(mActivity, mID);
        sentMessages.add(0, message);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            Intent intent = MessageContentActivity.getStartIntent(getActivity(), sentMessages.get(position));
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
            Toast.makeText(getActivity(), "Long click to this message" + position, Toast.LENGTH_SHORT).show();
        }

        toggleSelection(position);

        return true;
    }

    /**
     * Toggle the selection state of an item.
     * <p/>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
//                    MainActivity.client.SendRequestDeleteMessage(HomeMMSDatabaseHelper.getMessage(getActivity(), ));
//                    HomeMMSDatabaseHelper.deleteMessage(getActivity(), );
                    mAdapter.removeMessages(mAdapter.getSelectedItems());
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }
    }
}

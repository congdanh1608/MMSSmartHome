package com.thesis.mmtt2011.homemms.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.adapter.MessageAdapter;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment implements MessageAdapter.MessageViewHolder.ClickListener {

    public static final int COLUMN_GRID = 2;

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    List<Message> messages;

    //Khoi tao danh sach cach message mau
    public void initListMessage() {
        //Get messages from database
        //messages = HomeMMSDatabaseHelper.getMessages(getActivity(), true);
        messages = new ArrayList<>();
        List<User> receivers = new ArrayList<>();
        User receiver = new User("00:00:00:00:00:00", "Name Display", "password", "link avatar", "offline");
        receivers.add(receiver);
        String content[] = new String[3];
        content[0] = "Lorem ipsum dolor sit amet, vis invenire interesset ut. Ad duo tale appareat contentiones, id per nisl tantas percipitur. Tollit mediocrem repudiandae ad vim, ad pri habeo dolorem disputando. ";
        content[1] = "Lorem ipsum dolor sit amet, vis invenire interesset ut. Ad duo tale appareat contentiones, id per nisl tantas percipitur";
        content[2] = "Lorem ipsum dolor sit amet, vis invenire interesset ut.";
        for(int i = 0; i < 15; i++) {
            User user = new User(String.valueOf(i), "Pham Xuan Y", "password", "link avatar", "offline");
            messages.add(new Message("00:00:00:00:00:01", user, receivers, "Title " + i, content[i%3], null, null, null, "Oct 2" + i, "Status " + i, (i % 2)==1));
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
        mAdapter = new MessageAdapter(messages, this);
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

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            mAdapter.removeMessage(position);
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
     *
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
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
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

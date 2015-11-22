package com.thesis.mmtt2011.homemms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Xpie on 11/22/2015.
 */
public class ContactAdapter extends ArrayAdapter{

    Context context;
    List<User> contacts;
    LayoutInflater inflater;

    public ContactAdapter(Context _context, ArrayList<User> _contacts) {
        super(_context, R.layout.contact_view_item, _contacts);
        context = _context;
        contacts = _contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_view_item, null);
        }

        CheckedTextView tvUserName = (CheckedTextView)convertView.findViewById(R.id.username);
        CircleImageView avatar = (CircleImageView)convertView.findViewById(R.id.avatar_circle);
        tvUserName.setText(contacts.get(position).getNameDisplay());
        return convertView;
    }

}

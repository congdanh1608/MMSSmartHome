package com.thesis.mmtt2011.homemms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.model.User;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Xpie on 11/22/2015.
 */
public class ContactAdapter extends ArrayAdapter{

    Context context;
    List<User> contacts;
    List<User> selectedContacts;
    LayoutInflater inflater;

    public ContactAdapter(Context _context, ArrayList<User> _contacts, ArrayList<User> _selectedContacts) {
        super(_context, R.layout.contact_view_item, _contacts);
        context = _context;
        contacts = _contacts;
        selectedContacts = _selectedContacts;
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
        ImageView imStatus = (ImageView)convertView.findViewById(R.id.iv_status);

        tvUserName.setChecked(false);
        CircleImageView avatar = (CircleImageView)convertView.findViewById(R.id.avatar_circle);
        User contact = contacts.get(position);
        if(contact.getStatus().equals(ContantsHomeMMS.UserStatus.online.name())) {
//            imStatus.setColorFilter(R.color.colorAccent);
            imStatus.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        }
        tvUserName.setText(contact.getNameDisplay());
        if(selectedContacts.contains(contact)) {
            tvUserName.setChecked(true);
        }
        return convertView;
    }

}

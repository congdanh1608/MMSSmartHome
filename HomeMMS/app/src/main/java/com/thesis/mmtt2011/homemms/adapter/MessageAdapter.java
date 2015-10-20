package com.thesis.mmtt2011.homemms.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.model.Message;

import java.util.List;

/**
 * Created by Xpie on 10/20/2015.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(List<Message> _messages) {
        messages = _messages;
    }
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card_view, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(v);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvUsername.setText(message.getSender().getNameDisplay());
        holder.tvTitle.setText(message.getTitle());
        holder.tvContent.setText(message.getContent());
        holder.tvTimeStamp.setText(message.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvUsername;
        protected TextView tvContent;
        protected TextView tvTitle;
        protected TextView tvTimeStamp;

        public MessageViewHolder(View messageView) {
            super(messageView);
            tvUsername = (TextView)messageView.findViewById(R.id.username);
            tvTitle = (TextView)messageView.findViewById(R.id.title);
            tvContent = (TextView)messageView.findViewById(R.id.content);
            tvTimeStamp = (TextView)messageView.findViewById(R.id.timestamp);
        }
    }
}

package com.thesis.mmtt2011.homemms.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.activity.MainActivity;
import com.thesis.mmtt2011.homemms.activity.MessageContentActivity;
import com.thesis.mmtt2011.homemms.model.Message;
import com.thesis.mmtt2011.homemms.persistence.ContantsHomeMMS;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Xpie on 10/20/2015.
 */
public class MessageAdapter extends SelectableAdapter<MessageAdapter.MessageViewHolder> {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    private static final int TYPE_UNREAD = 0;
    private static final int TYPE_READ = 1;
    private List<Message> messages = new ArrayList<>();

    private MessageViewHolder.ClickListener clickListener;
    protected static Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(Context context, List<Message> _messages, MessageViewHolder.ClickListener _clickListener) {
        super();
        messages = _messages;
        clickListener = _clickListener;
        this.context = context;
    }

    public void removeMessage(int position) {
        if (!messages.get(position).getStatus().equals(ContantsHomeMMS.MessageStatus.draft.name())) {
            MainActivity.client.SendRequestDeleteMessage(HomeMMSDatabaseHelper.getMessage(context, messages.get(position).getId()));
        }
        HomeMMSDatabaseHelper.deleteMessage(context, messages.get(position).getId());
        messages.remove(position);
        notifyItemRemoved(position);
    }

    public void removeMessages(List<Integer> positions) {
        //Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeMessage(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeMessage(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }
                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            messages.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create a new view
        int layout = ((viewType == TYPE_UNREAD) ? R.layout.message_card_view : R.layout.message_card_view_read);

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(v, clickListener);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.bindMessage(message);

        //Highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message != null) {
            String status = message.getStatus();
            return status.equals(ContantsHomeMMS.MessageStatus.read.name()) ? TYPE_READ : TYPE_UNREAD;
        }
        return TYPE_READ;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private static final String TAG = MessageViewHolder.class.getSimpleName();
        private CircleImageView avatar_circle;
        private final TextView tvUsername;
        private final TextView tvContent;
        private final TextView tvTitle;
        private final TextView tvTimeStamp;
        private final ImageView ivImage;
        private final ImageView ivAudio;
        private final ImageView ivVideo;
        private View selectedOverlay;

        //private Message mMessage;

        private ClickListener listener;

        public MessageViewHolder(View messageView, ClickListener _listener) {
            super(messageView);
            tvUsername = (TextView) messageView.findViewById(R.id.username);
            tvTitle = (TextView) messageView.findViewById(R.id.title);
            tvContent = (TextView) messageView.findViewById(R.id.content);
            tvTimeStamp = (TextView) messageView.findViewById(R.id.timestamp);
            selectedOverlay = messageView.findViewById(R.id.selected_overlay);
            avatar_circle = (CircleImageView) messageView.findViewById(R.id.avatar_circle);

            ivImage =(ImageView)messageView.findViewById(R.id.image_attach);
            ivAudio =(ImageView)messageView.findViewById(R.id.audio_attach);
            ivVideo =(ImageView)messageView.findViewById(R.id.video_attach);

            this.listener = _listener;
            //Select message when click on message card view
            messageView.setOnClickListener(this);
            messageView.setOnLongClickListener(this);

        }

        //bind message properties to interface
        public void bindMessage(Message message) {
            //mMessage = message;
            if(message!=null) {
                String avatar = ContantsHomeMMS.AppFolder + "/" + message.getSender().getId() + "/" + message.getSender().getAvatar();
                if (UtilsMain.checkFileIsExits(avatar)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bitmap = BitmapFactory.decodeFile(ContantsHomeMMS.AppFolder + "/" + message.getSender().getId() + "/" + message.getSender().getAvatar(), options);
                    avatar_circle.setImageBitmap(bitmap);
                }else avatar_circle.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_homemms));

                tvUsername.setText(message.getSender().getNameDisplay());

                tvTitle.setText(message.getTitleTrim());
                tvContent.setText(message.getContentTextTrim());
                tvTimeStamp.setText(message.getTimestamp());

                if(message.getContentImage()!=null && !message.getContentImage().equals("null")) {
                    ivImage.setVisibility(View.VISIBLE);
                }
                if(message.getContentAudio()!=null && !message.getContentAudio().equals("null")) {
                    ivAudio.setVisibility(View.VISIBLE);
                }
                if (message.getContentVideo()!=null && !message.getContentVideo().equals("null")) {
                    ivVideo.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }

            return false;
        }

        public interface ClickListener {
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
        }
    }
}

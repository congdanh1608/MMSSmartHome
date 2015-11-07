package com.thesis.mmtt2011.homemms.model;

import java.util.List;
import java.util.UUID;

/**
 * Created by Xpie on 10/20/2015.
 */
public class Message {
    String mId;
    String title;
    User sender;
    List<User> receiver;
    String contentText;
    String contentImage;
    String contentAudio;
    String contentVideo;
    String timestamp; //(id)
    String status;
    private boolean read;

    public Message(String _mId, User _sender, List<User> _receiver, String _title, String _contentText,
                   String _contentImage, String _contentAudio, String _contentVideo, String _status,
                   String _timestamp, boolean _read) {

        mId = _mId;
        sender = _sender;
        receiver = _receiver;
        title = _title;
        contentText = _contentText;
        contentImage = _contentImage;
        contentAudio = _contentAudio;
        contentVideo = _contentVideo;
        timestamp = _timestamp;
        status = _status;
        read = _read;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return title;
    }

    public String getContentText() {
        return contentText;
    }

    public String getContentImage() {
        return contentImage;
    }

    public String getContentAudio() {
        return contentAudio;
    }

    public String getContentVideo() {
        return contentVideo;
    }

    public User getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public List<User> getReceiver() {
        return receiver;
    }

    public boolean isRead() {
        return read;
    }
}

package com.thesis.mmtt2011.homemms.model;

import android.content.Context;

import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.persistence.HomeMMSDatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Xpie on 10/20/2015.
 */
public class Message {
    private static final int MAX_TEXT_LENGTH = 16;
    private static final int MAX_CONTENT_TEXT_LENGTH = 100;
    public static long HOUR24_MILISECOND = 24 * 60 * 60 * 1000;

    String mId;
    String title;
    User sender;
    List<User> receivers;
    String contentText;
    String contentImage;
    String contentAudio;
    String contentVideo;
    String timestamp; //(id)
    String status;
    //private boolean read;

    public Message(){
    }

    public Message(String _mId, User _sender, List<User> _receiver, String _title, String _contentText,
                   String _contentImage, String _contentAudio, String _contentVideo, String _status,
                   String _timestamp) {

        mId = _mId;
        sender = _sender;
        receivers = _receiver;
        title = _title;
        contentText = _contentText;
        contentImage = _contentImage;
        contentAudio = _contentAudio;
        contentVideo = _contentVideo;
        timestamp = _timestamp;
        status = _status;
    }

    public String getTitleTrim() {
        if(title.length() > MAX_TEXT_LENGTH) {
            return title.substring(0, MAX_TEXT_LENGTH) + "...";
        }else{
            return title;
        }
    }

    public String getContentTextTrim() {
        if(contentText.length() > MAX_CONTENT_TEXT_LENGTH) {
            return contentText.substring(0, MAX_CONTENT_TEXT_LENGTH) + "...";
        }else{
            return contentText;
        }
    }

    public void setTimeCondition(String dateString) {
        Date now = new Date();
        try {
            Date date = UtilsMain.convertStringToDate(dateString);
            long differenceTime = now.getTime() - date.getTime();
            if (differenceTime > HOUR24_MILISECOND) {
                DateFormat dateFormat = new SimpleDateFormat("MMM d");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                this.timestamp = dateFormat.format(date);
                return;
            } else {
                DateFormat dateFormat = new SimpleDateFormat("h:mm a");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                this.timestamp = dateFormat.format(date);
                return;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        this.timestamp = dateString;
    }

    public String getListReceiverString() {
        StringBuilder strReceivers = new StringBuilder();
        strReceivers.append(this.receivers.get(0).getId());
        for (int i = 1; i < this.receivers.size(); i++) {
            strReceivers.append("-" + this.receivers.get(i));
        }
        return strReceivers.toString();
    }

    public void setListReceiverString(Context context, String strReceivers) {
        ArrayList<User> receivers = null;
        if (strReceivers!=null) {
            String[] macReceivers = strReceivers.split("-");
            receivers = new ArrayList<>();
            for (String receiverMac : macReceivers) {
                receivers.add(HomeMMSDatabaseHelper.getUser(context, receiverMac));
            }
        }
        this.receivers = receivers;
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
        return receivers;
    }

    public void setContentAudio(String contentAudio) {
        this.contentAudio = contentAudio;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setContentVideo(String contentVideo) {
        this.contentVideo = contentVideo;
    }

    public void setMId(String mId) {
        this.mId = mId;
    }

    public void setReceiver(List<User> receiver) {
        this.receivers = receiver;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

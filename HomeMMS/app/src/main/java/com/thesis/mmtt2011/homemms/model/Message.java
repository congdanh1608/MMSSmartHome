package com.thesis.mmtt2011.homemms.model;

import java.util.List;

/**
 * Created by Xpie on 10/20/2015.
 */
public class Message {
    String title;
    String content;
    String timestamp;
    String status;
    List<User> receiver;
    User sender;

    public Message(String _title, String _content, String _timestamp,
                   String _status, User _sender, List<User> _receiver) {
        title = _title;
        content = _content;
        timestamp = _timestamp;
        status = _status;
        sender = _sender;
        receiver = _receiver;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
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
}

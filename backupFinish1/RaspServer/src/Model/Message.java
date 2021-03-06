package Model;

import java.util.List;

public class Message {
	private String mId;
	private User sender;
	private List<User> receiver;
	private String title;
	private String contentText;
	private String contentAudio;
	private String contentVideo;
	private String contentImage;
	private String timestamp;
	private String status;

	public Message() {
		mId = null;
		sender = null;
		receiver = null;
		title = null;
		contentText = null;
		contentImage = null;
		contentAudio = null;
		contentVideo = null;
		timestamp = null;
		status = null;
	}

	public Message(String _mId, User _sender, List<User> _receiver, String _title, String _contentText,
			String _contentImage, String _contentAudio, String _contentVideo, String _status, String _timestamp) {

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
	}

	public String getmId() {
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
	
	public void setmId(String mId) {
		this.mId = mId;
	}
	
	public void setReceiver(List<User> receiver) {
		this.receiver = receiver;
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

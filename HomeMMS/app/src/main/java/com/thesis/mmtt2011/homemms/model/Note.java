package com.thesis.mmtt2011.homemms.model;

public class Note {
	private String title;
	private String message;
	private String fileAttachA;
	private String fileAttachV;
	private String fileAttachP;
	private String time;
	private String reciever;
	private String sender;
	
	public Note(){
		title = null;
		message = null;
		fileAttachA = null;
		fileAttachV = null;
		fileAttachP = null;
		time = null;
		reciever = null;
		sender = null;
	}
	
	public Note(String title, String message, String fileAttachA, String fileAttachV, String fileAttachP, String time, String reciever, String sender){
		this.title = title;
		this.message = message;
		this.fileAttachA = fileAttachA;
		this.fileAttachV = fileAttachV;
		this.fileAttachP = fileAttachP;
		this.time = time;
		this.reciever = reciever;
		this.sender = sender;
	}
	
	public Note(String title, String message, String fileAttachA, String fileAttachV, String fileAttachP, String reciever){
		this.title =title;
		this.message = message;
		this.fileAttachA = fileAttachA;
		this.fileAttachV = fileAttachV;
		this.fileAttachP = fileAttachP;
		this.reciever = reciever;
		this.time = null;
		this.sender = "Anonymous";
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getFileAttachA() {
		return fileAttachA;
	}
	
	public void setFileAttachA(String fileAttachA) {
		this.fileAttachA = fileAttachA;
	}
	
	public String getFileAttachV() {
		return fileAttachV;
	}
	
	public void setFileAttachV(String fileAttachV) {
		this.fileAttachV = fileAttachV;
	}
	
	public String getFileAttachP() {
		return fileAttachP;
	}
	
	public void setFileAttachP(String fileAttachP) {
		this.fileAttachP = fileAttachP;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getReciever() {
		return reciever;
	}
	
	public void setReciever(String reciever) {
		this.reciever = reciever;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
}

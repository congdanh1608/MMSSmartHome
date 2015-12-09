package com.thesis.mmtt2011.homemms.model;

import java.io.Serializable;

/**
 * Created by CongDanh on 08/12/2015.
 */
public class ObjectFile implements Serializable {
    private String sender;
    private String nameFile;
    private byte[] contentInBytes;
    private int fileSize;

    public ObjectFile(){
        sender = null;
        nameFile = null;
        contentInBytes = null;
        fileSize = 0;
    }

    public ObjectFile(String sender, String nameFile, byte[] contentInByte){
        this.sender = sender;
        this.nameFile = nameFile;
        this.contentInBytes = contentInByte;
        fileSize = 0;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContentInBytes(byte[] contentInBytes) {
        this.contentInBytes = contentInBytes;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public byte[] getContentInBytes() {
        return contentInBytes;
    }

    public String getNameFile() {
        return nameFile;
    }

    public String getSender() {
        return sender;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}

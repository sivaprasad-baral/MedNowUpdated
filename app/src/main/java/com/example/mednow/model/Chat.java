package com.example.mednow.model;

public class Chat {

    private String senderId,receiverId,message;

    public Chat() {
    }

    public Chat(String senderId, String receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }
}

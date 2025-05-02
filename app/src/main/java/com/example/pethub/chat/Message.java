package com.example.pethub.chat;

public class Message {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String content;
    private String timestamp;

    // Add constructor
    public Message(int messageId, int senderId, int receiverId, String content, String timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public boolean isFromUser(int userId) {
        return senderId == userId;
    }

    // Update getters
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getMessageId() { return messageId; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }


}
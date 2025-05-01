package com.example.pethub.chat;

public class Message {
    private int messageId;
    private int userId;
    private int sitterId;
    private String content;
    private boolean isUser;
    private String timestamp;

    public Message(int messageId, int userId, int sitterId, String content, boolean isUser, String timestamp) {
        this.messageId = messageId;
        this.userId = userId;
        this.sitterId = sitterId;
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    public String getContent() { return content; }
    public boolean isUser() { return isUser; }
    public String getTimestamp() { return timestamp; }
    public int getMessageId() { return messageId; }
    public int getUserId() { return userId; }
    public int getSitterId() { return sitterId; }
}
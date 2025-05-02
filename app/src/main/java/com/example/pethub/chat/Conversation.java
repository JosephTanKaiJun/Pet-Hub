// Conversation.java
package com.example.pethub.chat;

public class Conversation {
    private int sitterId;
    private String sitterName;
    private String lastMessage;
    private String timestamp;

    public Conversation(int sitterId, String sitterName, String lastMessage, String timestamp) {
        this.sitterId = sitterId;
        this.sitterName = sitterName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // Getters
    public int getSitterId() { return sitterId; }
    public String getSitterName() { return sitterName; }
    public String getLastMessage() { return lastMessage; }
    public String getTimestamp() { return timestamp; }
}
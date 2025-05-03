// Conversation.java
package com.example.pethub.chat;

public class Conversation {
    private int sitterId;
    private String sitterName;
    private String lastMessage;
    private String timestamp;
    private String photoUri;


    public Conversation(int sitterId, String sitterName, String lastMessage, String timestamp, String photoUri) {
        this.sitterId = sitterId;
        this.sitterName = sitterName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.photoUri = photoUri;

    }

    // Getters
    public int getSitterId() { return sitterId; }
    public String getSitterName() { return sitterName; }
    public String getLastMessage() { return lastMessage; }
    public String getTimestamp() { return timestamp; }
    public String getPhotoUri() { return photoUri; }

}
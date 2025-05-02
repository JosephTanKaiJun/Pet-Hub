package com.example.pethub.chat;

public class Booking {
    private int requestId;
    private int userId;
    private int sitterId;
    private String sitterName;
    private String date;
    private String petType;
    private String species;
    private String remarks;
    private String status;
    private String timestamp;
    //private int photoResId;
    private String photoUri;
    public Booking(int requestId, int userId, int sitterId, String sitterName, String date, String petType, String species, String remarks, String status, String timestamp, String photoUri) {
        this.requestId = requestId;
        this.userId = userId;
        this.sitterId = sitterId;
        this.sitterName = sitterName;
        this.date = date;
        this.petType = petType;
        this.species = species;
        this.remarks = remarks;
        this.status = status;
        this.timestamp = timestamp;
        //this.photoResId = photoResId;
        this.photoUri = photoUri;
    }

    public int getRequestId() { return requestId; }
    public int getUserId() { return userId; }
    public int getSitterId() { return sitterId; }
    public String getSitterName() { return sitterName; }
    public String getDate() { return date; }
    public String getPetType() { return petType; }
    public String getSpecies() { return species; }
    public String getRemarks() { return remarks; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    //public int getPhotoResId() { return photoResId; }
    public String getPhotoUri() {
        return photoUri;
    }

}
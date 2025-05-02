package com.example.pethub.chat;

public class Sitter {
    private int id;
    private String name;
    private String bio;
    private String skills;
    private String email;
    //private int photoResId;
    private String photoUri;

    public Sitter(int id, String name, String bio, String skills, String email, String photoUri) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.bio = bio != null ? bio : "";
        this.skills = skills != null ? skills : "";
        this.email = email != null ? email : "";
        //this.photoResId = photoResId;
        this.photoUri = photoUri;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getSkills() { return skills; }
    public String getEmail() { return email; }
    //    public int getPhotoResId() {
    //        return photoResId;
    //    }
    public String getPhotoUri() {
        return photoUri;
    }
}
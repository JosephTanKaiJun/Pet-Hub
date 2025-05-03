package com.example.pethub.api;

import com.google.gson.annotations.SerializedName;

public class NewsArticle {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("url")
    private String url;

    @SerializedName("urlToImage")
    private String imageUrl;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getImageUrl() { return imageUrl; }
}
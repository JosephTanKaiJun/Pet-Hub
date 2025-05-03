package com.example.pethub.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponse {
    @SerializedName("articles")
    private List<NewsArticle> articles;

    public List<NewsArticle> getArticles() {
        return articles;
    }
}

package com.example.pethub.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("everything")
    Call<NewsResponse> getNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}

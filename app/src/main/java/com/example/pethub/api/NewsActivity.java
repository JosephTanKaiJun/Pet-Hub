package com.example.pethub.api;
import com.example.pethub.R;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsArticle> newsList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_news);

        // Initialize views
        recyclerView = findViewById(R.id.newsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(newsList, this);
        recyclerView.setAdapter(adapter);

        // Setup SwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchNews();
        });

        // Set the refresh colors (optional)
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent
        );

        // Initial data load
        fetchNews();
    }

    private void fetchNews() {
        swipeRefreshLayout.setRefreshing(true); // Show loading indicator

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);

        String query = "pets OR pet care OR dogs OR cats OR " +
                "plants OR gardening OR houseplants OR " +
                "animal welfare OR veterinary OR " +
                "plant care OR indoor plants OR " +
                "pet health OR organic gardening";

        Call<NewsResponse> call = apiService.getNews(
                query,
                "f2b0080b8ce14313a8df8b26fd43bea7"
        );

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                swipeRefreshLayout.setRefreshing(false); // Hide loading indicator

                if (response.isSuccessful() && response.body() != null) {
                    newsList.clear();
                    newsList.addAll(response.body().getArticles());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(NewsActivity.this, "Failed to load news", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false); // Hide loading indicator
                t.printStackTrace();
                Toast.makeText(NewsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
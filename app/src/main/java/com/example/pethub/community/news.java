package com.example.pethub.community;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class news extends AppCompatActivity {

    TextView txtNewsContent, txtKeywordUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setTitle("Pet News");

        txtNewsContent = findViewById(R.id.txtNewsContent);
        txtNewsContent = findViewById(R.id.txtNewsContent);
        txtKeywordUsed = findViewById(R.id.txtKeywordUsed);

        fetchLatestNews();
    }

    private void fetchLatestNews() {
        OkHttpClient client = new OkHttpClient();

        String apiKey = "8ceafd9d9e0e4e8b9a6c08bc9ee28a87";

        String[] keywords = {"animals", "wildlife", "pets", "zoo", "nature", "birds", "insects"};
        Random rand = new Random();
        String query = keywords[rand.nextInt(keywords.length)]; // pick a random keyword
        txtKeywordUsed.setText("Searching for: " + query);
        String url = "https://newsapi.org/v2/everything?q=" + query +
                "&language=en&sortBy=publishedAt&page=1&apiKey=" + apiKey;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> txtNewsContent.setText("Failed to load news 😿"));
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONArray articles = json.getJSONArray("articles");
                        StringBuilder newsBuilder = new StringBuilder();

                        for (int i = 0; i < Math.min(3, articles.length()); i++) {
                            JSONObject article = articles.getJSONObject(i);
                            String title = article.getString("title");
                            newsBuilder.append("• ").append(title).append("\n\n");
                        }

                        String finalNews = newsBuilder.toString().trim();

                        runOnUiThread(() -> txtNewsContent.setText(finalNews.isEmpty() ? "No news found." : finalNews));

                    } catch (Exception e) {
                        runOnUiThread(() -> txtNewsContent.setText("Error parsing news data 😢"));
                    }
                } else {
                    runOnUiThread(() -> txtNewsContent.setText("API error: " + response.code()));
                }
            }
        });
    }
}

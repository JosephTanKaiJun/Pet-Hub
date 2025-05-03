package com.example.pethub.community;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.R;
import com.example.pethub.community.rating;
import com.example.pethub.community.pod; // Add this import

public class community extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        setTitle("Welcome to Community");

        Button btnGoToRating = findViewById(R.id.btnGoToRating);
        btnGoToRating.setOnClickListener(v -> {
            Intent intent = new Intent(community.this, rating.class);
            startActivity(intent);
        });

        Button btnViewRatings = findViewById(R.id.btnViewRatings);
        btnViewRatings.setOnClickListener(v -> {
            Intent intent = new Intent(community.this, ViewRatingsActivity.class);
            startActivity(intent);
        });

        // New button for Post and Discussion
        Button btnGoToPod = findViewById(R.id.btnGoToPod);
        btnGoToPod.setOnClickListener(v -> {
            Intent intent = new Intent(community.this, pod.class);
            startActivity(intent);
        });

        Button btnGoToNews = findViewById(R.id.btnGoToNews);
        btnGoToNews.setOnClickListener(v -> {
            Intent intent = new Intent(community.this, news.class);
            startActivity(intent);
        });

    }
}

package com.example.pethub.community;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;

import java.util.List;

public class rating extends AppCompatActivity {

    private EditText inputUsername, inputSitterName, inputDetails;
    private RatingBar ratingBar;
    private Button btnSubmit;
    private ListView listRatings;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        setTitle("Rate the Community");

        dbHelper = new DatabaseHelper(this);

        inputUsername = findViewById(R.id.input_username);
        inputSitterName = findViewById(R.id.input_sitter_name);
        inputDetails = findViewById(R.id.input_details);
        ratingBar = findViewById(R.id.rating_bar);
        btnSubmit = findViewById(R.id.btn_submit_rating);
        listRatings = findViewById(R.id.list_rating_posts);

        loadRatings();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim();
                String sitterName = inputSitterName.getText().toString().trim();
                String details = inputDetails.getText().toString().trim();
                int stars = (int) ratingBar.getRating();

                if (username.isEmpty() || sitterName.isEmpty() || details.isEmpty() || stars == 0) {
                    Toast.makeText(rating.this, "Please fill all fields and rate at least 1 star", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.addOrUpdateRating(username, sitterName, details, stars);
                if (success) {
                    Toast.makeText(rating.this, "Rating saved ✅", Toast.LENGTH_SHORT).show();
                    inputDetails.setText("");
                    inputSitterName.setText("");
                    ratingBar.setRating(0);
                    loadRatings();
                } else {
                    Toast.makeText(rating.this, "Error saving rating", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadRatings() {
        List<String> ratingPosts = dbHelper.getAllRatingsWithAverageAndDetails();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ratingPosts);
        listRatings.setAdapter(adapter);
    }
}

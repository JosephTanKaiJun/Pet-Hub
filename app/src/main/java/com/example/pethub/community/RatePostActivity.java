package com.example.pethub.community;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;

public class RatePostActivity extends AppCompatActivity {

    private TextView sitterText;
    private EditText usernameInput, detailInput;
    private RatingBar ratingBar;
    private Button btnSubmit;

    private String sitterName;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_post);

        sitterName = getIntent().getStringExtra("sitterName");
        db = new DatabaseHelper(this);

        sitterText = findViewById(R.id.textSitterName);
        usernameInput = findViewById(R.id.editUsername);
        detailInput = findViewById(R.id.editDetails);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit = findViewById(R.id.btnRateSubmit);

        sitterText.setText("Rating for: " + sitterName);

        btnSubmit.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String details = detailInput.getText().toString().trim();
            int stars = (int) ratingBar.getRating();

            if (username.isEmpty() || details.isEmpty() || stars == 0) {
                Toast.makeText(this, "Please fill in all fields and rate at least 1 star", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.addOrUpdateRating(username, sitterName, details, stars);
            if (success) {
                Toast.makeText(this, "Your rating was added! ⭐", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Rating update failed ❗", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

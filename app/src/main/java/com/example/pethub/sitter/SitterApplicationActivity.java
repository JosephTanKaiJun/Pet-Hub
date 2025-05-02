package com.example.pethub.sitter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivitySitterApplicationBinding;
import com.example.pethub.R;
import java.text.DecimalFormat;

public class SitterApplicationActivity extends AppCompatActivity {

    private ActivitySitterApplicationBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Get user ID from Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if user is already a sitter
        if (dbHelper.isUserSitter(userId)) {
            Intent intent = new Intent(this, SitterMainActivity.class);
            intent.putExtra("USER_ID", userId); // Pass the userId to SitterMainActivity
            startActivity(intent);
            finish();
            return;
        }

        // Submit button listener
        binding.buttonSubmit.setOnClickListener(v -> submitApplication());
    }

    private void submitApplication() {
        // Validate inputs
        String skills = "General pet care"; // Add UI element for skills if needed
        boolean carePets = binding.checkboxPets.isChecked();
        boolean carePlants = binding.checkboxPlants.isChecked();
        String petRateStr = binding.textInputPetRate.getEditText().getText().toString().trim();
        String plantRateStr = binding.textInputPlantRate.getEditText().getText().toString().trim();
        String bio = binding.textInputBio.getEditText().getText().toString().trim();

        if (!carePets && !carePlants) {
            Toast.makeText(SitterApplicationActivity.this, "Please select at least one care option", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((carePets && petRateStr.isEmpty()) || (carePlants && plantRateStr.isEmpty())) {
            Toast.makeText(SitterApplicationActivity.this, "Please enter rates for selected care options", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bio.isEmpty()) {
            Toast.makeText(SitterApplicationActivity.this, "Please enter a bio", Toast.LENGTH_SHORT).show();
            return;
        }

        double petRate = carePets ? parseRate(petRateStr) : 0;
        double plantRate = carePlants ? parseRate(plantRateStr) : 0;

        if ((carePets && petRate < 0) || (carePlants && plantRate < 0)) {
            Toast.makeText(SitterApplicationActivity.this, "Rates cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format rates to 2 decimal places
        petRate = Double.parseDouble(decimalFormat.format(petRate));
        plantRate = Double.parseDouble(decimalFormat.format(plantRate));

        // Save to database
        try {
            dbHelper.addSitter(userId, bio, carePets, carePlants, petRate, plantRate, skills);
            dbHelper.setUserAsSitter(userId);
            Toast.makeText(SitterApplicationActivity.this, "Application submitted successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SitterMainActivity.class);
            intent.putExtra("USER_ID", userId); // Pass the userId to SitterMainActivity
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(SitterApplicationActivity.this, "Error submitting application", Toast.LENGTH_SHORT).show();
        }
    }

    private double parseRate(String rateStr) {
        try {
            return Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
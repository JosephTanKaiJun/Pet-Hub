package com.example.pethub.sitter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivitySitterEditBinding;
import java.text.DecimalFormat;

public class SitterEditActivity extends AppCompatActivity {

    private ActivitySitterEditBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterEditBinding.inflate(getLayoutInflater());
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

        // Load sitter details into editable fields
        loadSitterDetails();

        // Save button listener
        binding.buttonSave.setOnClickListener(v -> saveChanges());
    }

    private void loadSitterDetails() {
        Cursor cursor = dbHelper.getSitterDetails(userId);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("username");
            int studentIdIndex = cursor.getColumnIndex("student_id");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone_number");
            int bioIndex = cursor.getColumnIndex("bio");
            int carePetsIndex = cursor.getColumnIndex("care_pets");
            int carePlantsIndex = cursor.getColumnIndex("care_plants");
            int petRateIndex = cursor.getColumnIndex("pet_rate");
            int plantRateIndex = cursor.getColumnIndex("plant_rate");

            if (nameIndex < 0 || studentIdIndex < 0 || emailIndex < 0 || phoneIndex < 0 || bioIndex < 0 ||
                    carePetsIndex < 0 || carePlantsIndex < 0 || petRateIndex < 0 || plantRateIndex < 0) {
                Toast.makeText(this, "Error: Missing columns in query result", Toast.LENGTH_SHORT).show();
                cursor.close();
                finish();
                return;
            }

            String username = cursor.getString(nameIndex);
            String studentId = cursor.getString(studentIdIndex);
            String email = cursor.getString(emailIndex);
            String phone = cursor.getString(phoneIndex);
            String bio = cursor.getString(bioIndex);
            boolean carePets = cursor.getInt(carePetsIndex) == 1;
            boolean carePlants = cursor.getInt(carePlantsIndex) == 1;
            double petRate = cursor.getDouble(petRateIndex);
            double plantRate = cursor.getDouble(plantRateIndex);

            // Set editable fields
            binding.edittextName.setText(username);
            binding.edittextStudentId.setText(studentId);
            binding.edittextEmail.setText(email);
            binding.edittextPhone.setText(phone);
            binding.edittextBio.setText(bio);
            binding.editCheckboxPets.setChecked(carePets);
            binding.editCheckboxPlants.setChecked(carePlants);
            binding.edittextPetRate.setText(String.valueOf(petRate));
            binding.edittextPlantRate.setText(String.valueOf(plantRate));
        } else {
            Toast.makeText(this, "Sitter details not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.close();
    }

    private void saveChanges() {
        // Get values from editable fields
        String username = binding.edittextName.getText().toString().trim();
        String studentId = binding.edittextStudentId.getText().toString().trim();
        String email = binding.edittextEmail.getText().toString().trim();
        String phone = binding.edittextPhone.getText().toString().trim();
        String bio = binding.edittextBio.getText().toString().trim();
        boolean carePets = binding.editCheckboxPets.isChecked();
        boolean carePlants = binding.editCheckboxPlants.isChecked();
        String petRateStr = binding.edittextPetRate.getText().toString().trim();
        String plantRateStr = binding.edittextPlantRate.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty()) {
            binding.edittextName.setError("Name is required");
            return;
        }
        if (studentId.isEmpty()) {
            binding.edittextStudentId.setError("Student ID is required");
            return;
        }
        if (email.isEmpty()) {
            binding.edittextEmail.setError("Email is required");
            return;
        }
        if (phone.isEmpty()) {
            binding.edittextPhone.setError("Phone number is required");
            return;
        }
        if (bio.isEmpty()) {
            binding.edittextBio.setError("Bio is required");
            return;
        }

        if (!carePets && !carePlants) {
            Toast.makeText(this, "Please select at least one care option", Toast.LENGTH_SHORT).show();
            return;
        }

        double petRate;
        try {
            petRate = carePets ? Double.parseDouble(petRateStr) : 0;
            if (petRate < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.edittextPetRate.setError("Invalid pet care rate");
            return;
        }

        double plantRate;
        try {
            plantRate = carePlants ? Double.parseDouble(plantRateStr) : 0;
            if (plantRate < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.edittextPlantRate.setError("Invalid plant care rate");
            return;
        }

        // Format rates
        petRate = Double.parseDouble(decimalFormat.format(petRate));
        plantRate = Double.parseDouble(decimalFormat.format(plantRate));
        int photoResId = 0; // Get from UI or existing data
        String skills = "General pet care"; // Get from UI
        // Update database
        try {
            dbHelper.updateSitterDetails(
                    userId,
                    username,
                    studentId,
                    email,
                    phone,
                    photoResId, // Add photoResId
                    bio,
                    carePets,
                    carePlants,
                    petRate,
                    plantRate,
                    skills  // Add skills parameter
            );
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }
}
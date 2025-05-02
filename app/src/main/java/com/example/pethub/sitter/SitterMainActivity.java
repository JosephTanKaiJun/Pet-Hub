package com.example.pethub.sitter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivitySitterMainBinding;
import java.text.DecimalFormat;

public class SitterMainActivity extends AppCompatActivity {

    private static final String TAG = "SitterMainActivity";
    private static final int REQUEST_CODE_EDIT = 1;
    private ActivitySitterMainBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterMainBinding.inflate(getLayoutInflater());
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

        // Load sitter details
        loadSitterDetails();

        // Edit button listener
        binding.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(SitterMainActivity.this, SitterEditActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        });
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
            int incomeIndex = cursor.getColumnIndex("income");

            if (nameIndex < 0 || studentIdIndex < 0 || emailIndex < 0 || phoneIndex < 0 || bioIndex < 0 ||
                    carePetsIndex < 0 || carePlantsIndex < 0 || petRateIndex < 0 || plantRateIndex < 0 || incomeIndex < 0) {
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
            double income = cursor.getDouble(incomeIndex);

            // Set views
            binding.textviewName.setText(username);
            binding.textviewStudentId.setText(studentId);
            binding.textviewEmail.setText(email);
            binding.textviewPhone.setText(phone);
            binding.textviewSitterBio.setText(bio);
            binding.checkboxPets.setChecked(carePets);
            binding.checkboxPlants.setChecked(carePlants);
            binding.textviewPetRate.setText(decimalFormat.format(carePets ? petRate : 0) + " RM/day");
            binding.textviewPlantRate.setText(decimalFormat.format(carePlants ? plantRate : 0) + " RM/day");
            binding.textviewIncomeValue.setText("RM " + decimalFormat.format(income));

            Log.d(TAG, "Loaded Name: " + username);
            Log.d(TAG, "Loaded Student ID: " + studentId);
            Log.d(TAG, "Loaded Email: " + email);
            Log.d(TAG, "Loaded Phone: " + phone);
            Log.d(TAG, "Loaded Bio: " + bio);
            Log.d(TAG, "Loaded Care Pets: " + carePets);
            Log.d(TAG, "Loaded Care Plants: " + carePlants);
            Log.d(TAG, "Loaded Pet Rate: " + petRate);
            Log.d(TAG, "Loaded Plant Rate: " + plantRate);
            Log.d(TAG, "Loaded Income: " + income);
        } else {
            Toast.makeText(this, "Sitter details not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            // Reload sitter details after editing
            loadSitterDetails();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
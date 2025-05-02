package com.example.pethub.sitter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivityOwnerMainBinding;
import com.example.pethub.sitter.OwnerEditActivity;

public class OwnerMainActivity extends AppCompatActivity {

    private static final String TAG = "OwnerMainActivity";
    private static final int REQUEST_CODE_EDIT = 1;
    private ActivityOwnerMainBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOwnerDetails();

        binding.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerMainActivity.this, OwnerEditActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        });
    }

    private void loadOwnerDetails() {
        Cursor cursor = dbHelper.getOwnerDetails(userId);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int studentIdIndex = cursor.getColumnIndex("student_id");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone_number");
            int bioIndex = cursor.getColumnIndex("bio");
            int ownsPetsIndex = cursor.getColumnIndex("own_pets");
            int ownsPlantsIndex = cursor.getColumnIndex("own_plants");

            if (nameIndex < 0 || studentIdIndex < 0 || emailIndex < 0 || phoneIndex < 0 || bioIndex < 0 ||
                    ownsPetsIndex < 0 || ownsPlantsIndex < 0) {
                Toast.makeText(this, "Error: Missing columns in query result", Toast.LENGTH_SHORT).show();
                cursor.close();
                finish();
                return;
            }

            String name = cursor.getString(nameIndex);
            String studentId = cursor.getString(studentIdIndex);
            String email = cursor.getString(emailIndex);
            String phone = cursor.getString(phoneIndex);
            String bio = cursor.getString(bioIndex);
            boolean ownPets = cursor.getInt(ownsPetsIndex) == 1;
            boolean ownPlants = cursor.getInt(ownsPlantsIndex) == 1;

            binding.textviewName.setText(name);
            binding.textviewStudentId.setText(studentId);
            binding.textviewEmail.setText(email);
            binding.textviewPhone.setText(phone);
            binding.textviewBio.setText(bio);
            binding.checkboxPets.setChecked(ownPets);
            binding.checkboxPlants.setChecked(ownPlants);

            Log.d(TAG, "Loaded Name: " + name);
            Log.d(TAG, "Loaded Student ID: " + studentId);
            Log.d(TAG, "Loaded Email: " + email);
            Log.d(TAG, "Loaded Phone: " + phone);
            Log.d(TAG, "Loaded Bio: " + bio);
            Log.d(TAG, "Owns Pets: " + ownPets);
            Log.d(TAG, "Owns Plants: " + ownPlants);
        } else {
            Toast.makeText(this, "Owner details not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            loadOwnerDetails();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}

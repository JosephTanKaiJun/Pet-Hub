package com.example.pethub.sitter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivitySitterEditBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class SitterEditActivity extends AppCompatActivity {

    private ActivitySitterEditBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Fixed pattern for leading zero
    private static final int PICK_IMAGE_REQUEST = 100;
    private String profilePictureFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSitterDetails();
        binding.buttonUploadProfile.setOnClickListener(v -> openImagePicker());
        binding.buttonSave.setOnClickListener(v -> saveChanges());
    }

    private void loadSitterDetails() {
        Cursor cursor = dbHelper.getSitterDetails(userId);
        if (cursor.moveToFirst()) {
            binding.edittextName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
            binding.edittextStudentId.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID)));
            binding.edittextEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            binding.edittextPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE_NUMBER)));
            profilePictureFilename = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO_URI));
            if (profilePictureFilename != null) {
                File imageFile = new File(new File(getFilesDir(), "profilepic"), profilePictureFilename);
                if (imageFile.exists()) {
                    binding.imageviewProfilePreview.setImageURI(Uri.fromFile(imageFile));
                }
            }
            binding.edittextBio.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BIO)));
            binding.editCheckboxPets.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARE_PETS)) == 1);
            binding.editCheckboxPlants.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARE_PLANTS)) == 1);
            double petRate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PET_RATE));
            double plantRate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLANT_RATE));
            binding.edittextPetRate.setText(petRate > 0 ? decimalFormat.format(petRate) : "");
            binding.edittextPlantRate.setText(plantRate > 0 ? decimalFormat.format(plantRate) : "");
        }
        cursor.close();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.imageviewProfilePreview.setImageBitmap(bitmap);
                profilePictureFilename = saveImageToInternalStorage(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        String filename = "user_" + userId + "_profile.jpg";
        File directory = new File(getFilesDir(), "profilepic");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return filename;
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void saveChanges() {
        String username = binding.edittextName.getText().toString().trim();
        String studentId = binding.edittextStudentId.getText().toString().trim();
        String email = binding.edittextEmail.getText().toString().trim();
        String phoneNumber = binding.edittextPhone.getText().toString().trim();
        String bio = binding.edittextBio.getText().toString().trim();
        boolean carePets = binding.editCheckboxPets.isChecked();
        boolean carePlants = binding.editCheckboxPlants.isChecked();
        String petRateStr = binding.edittextPetRate.getText().toString().trim();
        String plantRateStr = binding.edittextPlantRate.getText().toString().trim();
        String skills = "General pet care"; // Default skills as per previous implementation

        if (username.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all basic details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!carePets && !carePlants) {
            Toast.makeText(this, "Please select at least one care option", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((carePets && petRateStr.isEmpty()) || (carePlants && plantRateStr.isEmpty())) {
            Toast.makeText(this, "Please enter rates for selected care options", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bio.isEmpty()) {
            Toast.makeText(this, "Please enter a bio", Toast.LENGTH_SHORT).show();
            return;
        }

        double petRate = carePets ? parseRate(petRateStr) : 0;
        double plantRate = carePlants ? parseRate(plantRateStr) : 0;

        if ((carePets && petRate < 0) || (carePlants && plantRate < 0)) {
            Toast.makeText(this, "Rates cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        petRate = Double.parseDouble(decimalFormat.format(petRate));
        plantRate = Double.parseDouble(decimalFormat.format(plantRate));

        try {
            dbHelper.updateSitterDetails(
                    userId, username, studentId, email, phoneNumber, profilePictureFilename,
                    bio, carePets, carePlants, petRate, plantRate, skills
            );
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SitterMainActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
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
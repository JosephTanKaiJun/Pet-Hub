package com.example.pethub.sitter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivitySitterApplicationBinding;
import com.example.pethub.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class SitterApplicationActivity extends AppCompatActivity {

    private ActivitySitterApplicationBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private DecimalFormat decimalFormat = new DecimalFormat("#.00");
    private static final int PICK_IMAGE_REQUEST = 100;
    private String profilePictureFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
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
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
            return;
        }

        // Upload profile picture button listener
        binding.buttonUploadProfile.setOnClickListener(v -> openImagePicker());

        // Submit button listener
        binding.buttonSubmit.setOnClickListener(v -> submitApplication());
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
                // Resize bitmap to match ImageView dimensions (120dp)
                int targetSize = (int) (120 * getResources().getDisplayMetrics().density); // Convert dp to pixels
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, true);
                // Crop to circle
                Bitmap circularBitmap = getCircularBitmap(resizedBitmap);
                binding.imageviewProfilePreview.setImageBitmap(circularBitmap);
                profilePictureFilename = saveImageToInternalStorage(circularBitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFFFFFF);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // In SitterApplicationActivity and SitterEditActivity
    private String saveImageToInternalStorage(Bitmap bitmap) {
        // Add timestamp to filename
        String filename = "user_" + userId + "_profile_" + System.currentTimeMillis() + ".jpg";
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

    private void submitApplication() {
        String skills = "General pet care";
        boolean carePets = binding.checkboxPets.isChecked();
        boolean carePlants = binding.checkboxPlants.isChecked();
        String petRateStr = binding.textInputPetRate.getEditText().getText().toString().trim();
        String plantRateStr = binding.textInputPlantRate.getEditText().getText().toString().trim();
        String bio = binding.textInputBio.getEditText().getText().toString().trim();

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
        if (profilePictureFilename == null) {
            Toast.makeText(this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
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
            dbHelper.addSitter(userId, bio, carePets, carePlants, petRate, plantRate, skills);
            dbHelper.setUserAsSitter(userId);
            // Update user table with profile picture filename
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseHelper.COLUMN_PHOTO_URI, profilePictureFilename);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.update(DatabaseHelper.TABLE_USERS, userValues, DatabaseHelper.COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
            db.close();

            Toast.makeText(this, "Application submitted successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SitterMainActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error submitting application", Toast.LENGTH_SHORT).show();
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
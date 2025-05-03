package com.example.pethub.sitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivityOwnerEditBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OwnerEditActivity extends AppCompatActivity {

    private ActivityOwnerEditBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private static final int PICK_IMAGE_REQUEST = 100;
    private String profilePictureFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerEditBinding.inflate(getLayoutInflater());
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

        loadOwnerDetails();
        binding.buttonUploadProfile.setOnClickListener(v -> openImagePicker());
        binding.buttonSave.setOnClickListener(v -> saveChanges());
    }

    private void loadOwnerDetails() {
        Cursor cursor = dbHelper.getOwnerDetails(userId);
        if (cursor.moveToFirst()) {
            try {
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

                binding.edittextBio.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_BIO)));
                binding.editCheckboxPets.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_CARE_PETS)) == 1);
                binding.editCheckboxPlants.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_CARE_PLANTS)) == 1);
            } catch (Exception e) {
                Log.e("OwnerEditActivity", "Error loading owner details", e);
                Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to load owner details", Toast.LENGTH_SHORT).show();
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
                int targetSize = (int) (120 * getResources().getDisplayMetrics().density);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, true);
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

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateOwnerDetails(
                userId, username, studentId, email, phoneNumber,
                profilePictureFilename, bio, carePets, carePlants
        );

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.pethub.sitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivityOwnerMainBinding;

import java.io.File;

public class OwnerMainActivity extends AppCompatActivity {

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
            startActivity(intent);
        });
    }

    private void loadOwnerDetails() {
        Cursor cursor = dbHelper.getOwnerDetails(userId);
        if (cursor.moveToFirst()) {
            try {
                // Basic user info
                binding.textviewName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
                binding.textviewStudentId.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID)));
                binding.textviewEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
                binding.textviewPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE_NUMBER)));

                // Profile picture
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO_URI));
                if (photoUri != null) {
                    File imageFile = new File(new File(getFilesDir(), "profilepic"), photoUri);
                    if (imageFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        if (bitmap != null) {
                            int targetSize = (int) (120 * getResources().getDisplayMetrics().density);
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, true);
                            Bitmap circularBitmap = getCircularBitmap(resizedBitmap);
                            binding.imageviewProfile.setImageBitmap(circularBitmap);
                        }
                    }
                }

                // Owner-specific info
                String bio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_BIO));
                binding.textviewBio.setText(bio != null ? bio : "No bio provided");

                binding.checkboxPets.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_CARE_PETS)) == 1);
                binding.checkboxPlants.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OWNER_CARE_PLANTS)) == 1);
            } catch (Exception e) {
                Log.e("OwnerMainActivity", "Error loading owner details", e);
                Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to load owner details", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
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

    @Override
    protected void onResume() {
        super.onResume();
        loadOwnerDetails();
    }
}
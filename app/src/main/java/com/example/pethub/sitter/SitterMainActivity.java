package com.example.pethub.sitter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivitySitterMainBinding;
import java.io.File;
import java.text.DecimalFormat;

public class SitterMainActivity extends AppCompatActivity {

    private ActivitySitterMainBinding binding;
    private DatabaseHelper dbHelper;
    private int userId;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Fixed pattern for leading zero

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySitterMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!dbHelper.isUserSitter(userId)) {
            Toast.makeText(this, "User is not a sitter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSitterDetails();
        binding.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, SitterEditActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    private void loadSitterDetails() {
        Cursor cursor = dbHelper.getSitterDetails(userId);
        if (cursor.moveToFirst()) {
            binding.textviewName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
            binding.textviewStudentId.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID)));
            binding.textviewEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            binding.textviewPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE_NUMBER)));
            String photoFilename = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO_URI));
            if (photoFilename != null) {
                File imageFile = new File(new File(getFilesDir(), "profilepic"), photoFilename);
                if (imageFile.exists()) {
                    binding.imageviewProfile.setImageURI(Uri.fromFile(imageFile));
                }
            }
            binding.textviewSitterBio.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BIO)));
            binding.checkboxPets.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARE_PETS)) == 1);
            binding.checkboxPlants.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARE_PLANTS)) == 1);
            double petRate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PET_RATE));
            double plantRate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLANT_RATE));
            double income = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME));
            binding.textviewPetRate.setText(decimalFormat.format(petRate) + " RM/day");
            binding.textviewPlantRate.setText(decimalFormat.format(plantRate) + " RM/day");
            binding.textviewIncomeValue.setText("RM " + decimalFormat.format(income));
        } else {
            Toast.makeText(this, "Failed to load sitter details", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}
package com.example.pethub;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivityMainBinding;
import com.example.pethub.sitter.SitterApplicationActivity;
import com.example.pethub.sitter.SitterMainActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    private int userId = 1; // Hardcoded for testing; replace with actual user authentication logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Button to navigate to sitter-related activity
        binding.sitterSignupBtn.setOnClickListener(v -> {
            if (dbHelper.isUserSitter(userId)) {
                Intent intent = new Intent(this, SitterMainActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, SitterApplicationActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
            }
        });
    }
}
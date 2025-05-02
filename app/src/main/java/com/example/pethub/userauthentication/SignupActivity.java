package com.example.pethub.userauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.databinding.ActivitySignupBinding;
import com.example.pethub.database.DatabaseHelper;
public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private DatabaseHelper databaseHelper; // Changed from AuthDatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.signupButton.setOnClickListener(v -> {
            String username = binding.signupUsername.getText().toString();
            String email = binding.signupEmail.getText().toString();
            String password = binding.signupPassword.getText().toString();
            String confirmPassword = binding.signupConfirm.getText().toString();

            if (!password.equals(confirmPassword)) {
                showToast("Passwords don't match");
            } else if (databaseHelper.checkUser(username)) { // Check username existence
                showToast("Username already exists");
            } else if (databaseHelper.insertUser(username, email, password)) {
                showToast("Signup successful");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                showToast("Signup failed");
            }
        });

        binding.loginRedirectText.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
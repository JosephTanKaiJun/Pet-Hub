package com.example.pethub.userauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.databinding.ActivitySignupBinding;
import com.example.pethub.database.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private DatabaseHelper databaseHelper;

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

            // Check for empty fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("All fields are required");
                return;
            }

            // Check password match
            if (!password.equals(confirmPassword)) {
                showToast("Passwords don't match");
                return;
            }

            // Validate email domain
            if (!isValidEmail(email)) {
                showToast("Email must be from @example.com or @1utar.my");
                return;
            }

            // Validate password complexity
            if (!isValidPassword(password)) {
                showToast("Password must contain both letters and numbers");
                return;
            }

            // Check if username exists
            if (databaseHelper.checkUser(username)) {
                showToast("Username already exists");
                return;
            }

            // Proceed with signup
            if (databaseHelper.insertUser(username, email, password)) {
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

    // Validate email format and domain
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(example\\.com|1utar\\.my)$";
        return email.matches(emailRegex);
    }

    // Validate password contains letters and numbers
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).+$";
        return password.matches(passwordRegex);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
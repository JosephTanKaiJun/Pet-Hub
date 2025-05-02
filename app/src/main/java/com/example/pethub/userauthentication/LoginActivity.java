package com.example.pethub.userauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.chat.SelectionActivity;
import com.example.pethub.main.MainActivity;
import com.example.pethub.databinding.ActivityLoginBinding;
import com.example.pethub.database.DatabaseHelper;
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        binding.signupRedirectText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        binding.loginButton.setOnClickListener(v -> {
            String username = binding.loginUsername.getText().toString(); // Changed from email
            String password = binding.loginPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                showToast("All fields required");
            } else if (databaseHelper.checkCredentials(username, password)) { // Use username
                int userId = databaseHelper.getUserId(username); // Use username
                startMainActivity(userId);
            } else {
                showToast("Invalid credentials");
            }
        });


    }

    private void startMainActivity(int userId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
package com.example.pethub.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.chat.SelectionActivity;
import com.example.pethub.chat.BookingActivity;
import com.example.pethub.R;
import com.example.pethub.userauthentication.AuthDatabaseHelper;
import com.example.pethub.userauthentication.LoginActivity;
import com.example.pethub.database.DatabaseHelper;
import com.example.pethub.databinding.ActivityMainBinding;
import com.example.pethub.sitter.SitterApplicationActivity;
import com.example.pethub.sitter.SitterMainActivity;
import com.example.pethub.database.DatabaseHelper;
public class MainActivity extends AppCompatActivity {
    private ImageButton searchSitterBtn, sitterSignupBtn, navChat;
    private int userId = 1; // Hardcoded for testing; replace with actual user authentication logic;
    private TextView txtUsername;
    private AuthDatabaseHelper authDbHelper;
    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUsername = findViewById(R.id.txtUsername); // <-- Add this line
        dbHelper = new DatabaseHelper(this);
        authDbHelper = new AuthDatabaseHelper(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) finish();

        String username = authDbHelper.getUsername(userId);
        txtUsername.setText(username != null ? username : "User");

        // Dropdown menu setup
        txtUsername.setOnClickListener(v -> showDropdownMenu());
        searchSitterBtn = findViewById(R.id.searchSitterBtn);
        sitterSignupBtn = findViewById(R.id.sitterSignupBtn);
        navChat = findViewById(R.id.navChat);

        searchSitterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectionActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        sitterSignupBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Sitter Sign Up clicked", Toast.LENGTH_SHORT).show();
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



        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }
    private void showDropdownMenu() {
        PopupMenu popup = new PopupMenu(this, txtUsername);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_signout) {
                // Clear user session and redirect
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
        popup.show();
    }
}
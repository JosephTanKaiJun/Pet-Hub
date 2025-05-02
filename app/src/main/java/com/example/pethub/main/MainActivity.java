package com.example.pethub.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.chat.SelectionActivity;
import com.example.pethub.chat.BookingActivity;
import com.example.pethub.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton searchSitterBtn, sitterSignupBtn, navChat;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) finish();

        searchSitterBtn = findViewById(R.id.searchSitterBtn);
        sitterSignupBtn = findViewById(R.id.sitterSignupBtn);
        navChat = findViewById(R.id.navChat);

        searchSitterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectionActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        sitterSignupBtn.setOnClickListener(v ->
                Toast.makeText(this, "Sitter Sign Up clicked", Toast.LENGTH_SHORT).show());

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }
}
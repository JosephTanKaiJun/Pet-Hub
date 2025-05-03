package com.example.pethub.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pethub.sitter.SitterMainActivity;
import com.example.pethub.sitter.SitterApplicationActivity;
import com.example.pethub.chat.ConversationListActivity;
import com.example.pethub.chat.SelectionActivity;
import com.example.pethub.chat.BookingActivity;
import com.example.pethub.R;
import com.example.pethub.sitter.OwnerMainActivity;
import com.example.pethub.userauthentication.AuthDatabaseHelper;
import com.example.pethub.userauthentication.LoginActivity;
import com.example.pethub.database.DatabaseHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView searchSitterBtn, sitterSignupBtn;
    private FloatingActionButton fabChat,fabProfile,fabSignOut,fabNews;


    private ConstraintLayout navHome;
    private int userId = 1;
    private TextView txtUsername;
    private AuthDatabaseHelper authDbHelper;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        txtUsername = findViewById(R.id.txtUsername);
        dbHelper = new DatabaseHelper(this);
        authDbHelper = new AuthDatabaseHelper(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) finish();

        String username = authDbHelper.getUsername(userId);
        txtUsername.setText(username != null ? username : "User");

        // Initialize UI components
        searchSitterBtn = findViewById(R.id.searchSitterBtn);
        sitterSignupBtn = findViewById(R.id.sitterSignupBtn);
        fabChat = findViewById(R.id.fabChat);
        navHome = findViewById(R.id.bookingHistoryCard);
        fabProfile = findViewById(R.id.fabProfile);
        fabSignOut = findViewById(R.id.fabSignOut);
        // Set click listeners
        txtUsername.setOnClickListener(v -> showDropdownMenu());

        fabProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OwnerMainActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        fabSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        searchSitterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectionActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        sitterSignupBtn.setOnClickListener(v -> {
            if (dbHelper.isUserSitter(userId)) {
                Toast.makeText(this, "You are already a Sitter", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(this, SitterMainActivity.class);
//                intent.putExtra("USER_ID", userId);
//                startActivity(intent);
//                finish();
            } else {
                Intent intent = new Intent(this, SitterApplicationActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
            }
        });

        navHome.setOnClickListener(v -> {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            if (dbHelper.isUserSitter(userId)) {
                Intent intent = new Intent(this, SitterMainActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, OwnerMainActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConversationListActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        ConstraintLayout communityCard = findViewById(R.id.communityCard);
        communityCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.pethub.community.community.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });


    }

    private void showDropdownMenu() {
        PopupMenu popup = new PopupMenu(this, txtUsername);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_signout) {
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
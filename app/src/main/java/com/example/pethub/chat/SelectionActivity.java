package com.example.pethub.chat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pethub.main.MainActivity;
import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity implements SitterAdapter.OnSitterClickListener {
    private DatabaseHelper dbHelper;
    private RecyclerView sitterRecyclerView;
    private SitterAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if(currentUserId == -1) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
        }

        dbHelper = new DatabaseHelper(this);
        initializeUI();
        loadSitters();
    }

    private void initializeUI() {
        sitterRecyclerView = findViewById(R.id.sitterRecyclerView);
        sitterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadSitters() {
        List<Sitter> sitters = new ArrayList<>();
        Cursor cursor = dbHelper.getAllSitters();

        if(cursor.moveToFirst()) {
            do {
                sitters.add(new Sitter(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SKILLS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO_URI))
                ));
            } while(cursor.moveToNext());
        }
        cursor.close();

        adapter = new SitterAdapter(sitters, this);
        sitterRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSitterClick(Sitter sitter) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("SITTER_ID", sitter.getId());
        intent.putExtra("SITTER_NAME", sitter.getName());
        startActivity(intent);
    }

    private void addTestUser(String name, String studentId, String email, String photoUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, name);
        values.put(DatabaseHelper.COLUMN_STUDENT_ID, studentId);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PHOTO_URI, photoUri);
        values.put(DatabaseHelper.COLUMN_IS_SITTER, 1);
        db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();
    }

    private void addTestSitter(int userId, String bio, boolean carePets, boolean carePlants,
                               double petRate, double plantRate, String skills) {
        dbHelper.addSitter(userId, bio, carePets, carePlants, petRate, plantRate, skills);
    }
}
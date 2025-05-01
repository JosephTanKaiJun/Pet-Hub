package com.example.pethub.chat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
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

        Button btnAddTestData = findViewById(R.id.btnAddTestData);
        btnAddTestData.setOnClickListener(v -> addTestData());
    }

    private void loadSitters() {
        List<Sitter> sitters = new ArrayList<>();
        Cursor cursor = dbHelper.getAllSitters();

        if(cursor.moveToFirst()) {
            do {
                sitters.add(new Sitter(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
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

    private void addTestData() {
        dbHelper.addSitter("Ava Lively", "Cat specialist", "Pets", "ava@example.com");
        dbHelper.addSitter("John Green", "Plant expert", "Plants", "john@example.com");
        loadSitters();
    }
}
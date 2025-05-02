package com.example.pethub.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText etMessage;
    private ImageButton btnSend;
    private DatabaseHelper dbHelper;
    private int userId, sitterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getIntExtra("USER_ID", -1);
        sitterId = getIntent().getIntExtra("SITTER_ID", -1);
        String sitterName = getIntent().getStringExtra("SITTER_NAME");

        if (userId == -1 || sitterId == -1) {
            Toast.makeText(this, "Error loading chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnHire = findViewById(R.id.btnHire);
        btnHire.setOnClickListener(v -> {
            Intent intent = new Intent(this, HireActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SITTER_ID", sitterId);
            startActivity(intent);
        });

        dbHelper = new DatabaseHelper(this);
        initializeUI(sitterName);
        loadMessages();
    }

    private void initializeUI(String sitterName) {
        TextView tvSitterName = findViewById(R.id.tvSitterName);
        tvSitterName.setText(sitterName);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            dbHelper.addMessage(userId, sitterId, message, true);
            etMessage.setText("");
            loadMessages();
        }
    }

    private void loadMessages() {
        List<Message> messages = dbHelper.getMessages(userId, sitterId);
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setAdapter(chatAdapter);
        if (!messages.isEmpty()) {
            chatRecyclerView.scrollToPosition(messages.size() - 1);
        }
    }
}
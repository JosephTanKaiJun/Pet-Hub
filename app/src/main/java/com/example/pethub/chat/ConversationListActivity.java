// ConversationListActivity.java
package com.example.pethub.chat;
import com.example.pethub.chat.Conversation;
import com.example.pethub.chat.ConversationAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity implements ConversationAdapter.OnConversationClickListener {
    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) finish();
        // In your ConversationListActivity's onCreate
        int targetUserId = getIntent().getIntExtra("TARGET_USER_ID", -1);
        if(targetUserId != -1) {
            // Directly open chat with this user
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("USER_ID", currentUserId);
            chatIntent.putExtra("RECIPIENT_ID", targetUserId);
            startActivity(chatIntent);
            finish();
        }
        dbHelper = new DatabaseHelper(this);
        initializeUI();
        loadConversations();
    }

    private void initializeUI() {
        recyclerView = findViewById(R.id.conversationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadConversations() {
        List<Conversation> conversations = dbHelper.getConversations(currentUserId);
        adapter = new ConversationAdapter(conversations, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("SITTER_ID", conversation.getSitterId());
        intent.putExtra("SITTER_NAME", conversation.getSitterName());
        startActivity(intent);
    }
}
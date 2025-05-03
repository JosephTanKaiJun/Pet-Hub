package com.example.pethub.community;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;

import java.util.List;

public class pod extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listComments;
    private EditText editUsername, editComment;
    private Button btnPost;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);
        setTitle("Post & Discussion");

        dbHelper = new DatabaseHelper(this);
        listComments = findViewById(R.id.list_comments);
        editUsername = findViewById(R.id.edit_username);
        editComment = findViewById(R.id.edit_comment);
        btnPost = findViewById(R.id.btn_post);

        loadComments();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString().trim();
                String comment = editComment.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(pod.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (comment.isEmpty()) {
                    Toast.makeText(pod.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.addCommunityComment(username, comment);
                editComment.setText("");
                loadComments(); // Refresh list
            }
        });
    }

    private void loadComments() {
        List<String> comments = dbHelper.getAllCommunityComments();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);
        listComments.setAdapter(adapter);
    }
}

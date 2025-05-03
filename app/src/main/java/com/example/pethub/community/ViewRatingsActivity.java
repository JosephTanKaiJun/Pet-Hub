package com.example.pethub.community;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;

import java.util.List;

public class ViewRatingsActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ratings);

        setTitle("View Sitter Ratings");

        listView = findViewById(R.id.ratingListView);
        db = new DatabaseHelper(this);

        List<String> allRatings = db.getAllRatingsWithAverageAndDetails();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allRatings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = allRatings.get(position);
            String sitterName = item.split(":")[0].trim(); // assume format: "SitterName: ⭐3.5"

            Intent intent = new Intent(ViewRatingsActivity.this, RatePostActivity.class);
            intent.putExtra("sitterName", sitterName);
            startActivity(intent);
        });
    }
}

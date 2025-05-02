package com.example.pethub.chat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;
import java.util.List;

public class BookingActivity extends AppCompatActivity {
    private RecyclerView bookingRecyclerView;
    private BookingAdapter adapter;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        bookingRecyclerView = findViewById(R.id.bookingRecyclerView);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadBookings();
    }

    private void loadBookings() {
        List<Booking> bookings = dbHelper.getBookingsForUser(userId);
        adapter = new BookingAdapter(bookings);
        bookingRecyclerView.setAdapter(adapter);
    }
}
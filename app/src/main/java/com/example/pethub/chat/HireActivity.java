package com.example.pethub.chat;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pethub.R;
import com.example.pethub.database.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HireActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int userId, sitterId;
    private EditText etDate, etRemarks;
    private RadioGroup rgPetType;
    private LinearLayout speciesLayout;
    private Spinner spinnerSpecies;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        userId = getIntent().getIntExtra("USER_ID", -1);
        sitterId = getIntent().getIntExtra("SITTER_ID", -1);
        if (userId == -1 || sitterId == -1) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        Sitter sitter = dbHelper.getSitterById(sitterId);
        if (sitter == null) {
            Toast.makeText(this, "Sitter not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate profile
        ImageView ivSitterPhoto = findViewById(R.id.ivSitterPhoto);
        TextView tvSitterName = findViewById(R.id.tvSitterName);
        TextView tvSitterBio = findViewById(R.id.tvSitterBio);
        TextView tvSitterSkills = findViewById(R.id.tvSitterSkills);
        TextView tvSitterEmail = findViewById(R.id.tvSitterEmail);

        Glide.with(this).load(sitter.getPhotoResId()+1).placeholder(R.drawable.default_avatar).into(ivSitterPhoto);
        tvSitterName.setText(sitter.getName());
        tvSitterBio.setText(sitter.getBio());
        tvSitterSkills.setText(sitter.getSkills());
        tvSitterEmail.setText(sitter.getEmail());

        // Set up form
        etDate = findViewById(R.id.etDate);
        etRemarks = findViewById(R.id.etRemarks);
        etDate.setOnClickListener(v -> showDatePickerDialog());

        rgPetType = findViewById(R.id.rgPetType);
        speciesLayout = findViewById(R.id.speciesLayout);
        rgPetType.setOnCheckedChangeListener((group, checkedId) -> {
            speciesLayout.setVisibility(checkedId == R.id.rbAnimal ? View.VISIBLE : View.GONE);
        });

        // Set up species Spinner
        spinnerSpecies = findViewById(R.id.spinnerSpecies);
        String[] species = {"Dog", "Cat", "Bird", "Fish", "Reptile", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, species);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecies.setAdapter(adapter);

        // Set up submit button
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void submitRequest() {
        String date = etDate.getText().toString().trim();
        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        String petType = rgPetType.getCheckedRadioButtonId() == R.id.rbPlant ? "Plant" :
                rgPetType.getCheckedRadioButtonId() == R.id.rbAnimal ? "Animal" : "";
        if (petType.isEmpty()) {
            Toast.makeText(this, "Please select pet or plant", Toast.LENGTH_SHORT).show();
            return;
        }

        String species = petType.equals("Animal") ? spinnerSpecies.getSelectedItem().toString() : "";
        String remarks = etRemarks.getText().toString().trim();

        if (dbHelper.hasBookingOnDate(userId, date)) {
            Toast.makeText(this, "You already have a booking on this date", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addHiringRequest(userId, sitterId, date, petType, species, remarks);
            Toast.makeText(this, "Request submitted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
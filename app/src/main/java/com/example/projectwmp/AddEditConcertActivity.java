package com.example.projectwmp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEditConcertActivity extends AppCompatActivity {

    private EditText etArtistName, etVenue, etDate, etTime, etPrice;
    private Spinner spinnerStatus;
    private Button btnSave, btnDelete;
    private DatabaseReference mDatabase;

    private String mode; // "add" or "edit"
    private String concertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_concert);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        etArtistName = findViewById(R.id.et_artist_name);
        etVenue = findViewById(R.id.et_venue);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etPrice = findViewById(R.id.et_price);
        spinnerStatus = findViewById(R.id.spinner_status);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);

        // Setup Spinner
        String[] statuses = {"Upcoming", "Sold Out", "Cancelled"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Get mode from Intent
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        if ("edit".equals(mode)) {
            // Load existing concert data
            concertId = intent.getStringExtra("concertId");
            etArtistName.setText(intent.getStringExtra("artistName"));
            etVenue.setText(intent.getStringExtra("venue"));
            etDate.setText(intent.getStringExtra("date"));
            etTime.setText(intent.getStringExtra("time"));
            etPrice.setText(String.valueOf(intent.getDoubleExtra("price", 0)));

            String status = intent.getStringExtra("status");
            int statusPosition = adapter.getPosition(status);
            spinnerStatus.setSelection(statusPosition);

            btnDelete.setVisibility(View.VISIBLE);
            setTitle("Edit Concert");
        } else {
            btnDelete.setVisibility(View.GONE);
            setTitle("Add Concert");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConcert();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation();
            }
        });
    }

    private void saveConcert() {
        String artistName = etArtistName.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(artistName)) {
            etArtistName.setError("Artist name is required");
            return;
        }

        if (TextUtils.isEmpty(venue)) {
            etVenue.setError("Venue is required");
            return;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required");
            return;
        }

        if (TextUtils.isEmpty(time)) {
            etTime.setError("Time is required");
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            return;
        }

        double price = Double.parseDouble(priceStr);

        if ("add".equals(mode)) {
            // Generate new concert ID
            concertId = mDatabase.child("concerts").push().getKey();
        }

        // Create Concert object
        Concert concert = new Concert(concertId, artistName, venue, date, time, price, status, "");

        // Save to Firebase
        mDatabase.child("concerts").child(concertId).setValue(concert)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddEditConcertActivity.this, "Concert saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditConcertActivity.this, "Failed to save concert", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Concert")
                .setMessage("Are you sure you want to delete this concert?")
                .setPositiveButton("Delete", (dialog, which) -> deleteConcert())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteConcert() {
        mDatabase.child("concerts").child(concertId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddEditConcertActivity.this, "Concert deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditConcertActivity.this, "Failed to delete concert", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.example.projectwmp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Concert;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class AddEditConcertActivity extends AppCompatActivity {

    private EditText etArtistName, etVenue, etDate, etTime, etPrice, etImageUrl;
    private Spinner spinnerStatus, spinnerGenre;
    private Button btnSave, btnDelete, btnPreviewImage;
    private ImageView ivConcertPoster;

    private DatabaseReference mDatabase;
    private Concert concert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_concert);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        etArtistName = findViewById(R.id.et_artist_name);
        etVenue = findViewById(R.id.et_venue);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etPrice = findViewById(R.id.et_price);
        etImageUrl = findViewById(R.id.et_image_url);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerGenre = findViewById(R.id.spinner_genre);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnPreviewImage = findViewById(R.id.btn_preview_image);
        ivConcertPoster = findViewById(R.id.iv_concert_poster);

        // Setup Status Spinner
        String[] statuses = {"Upcoming", "Sold Out", "Cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Setup Genre Spinner
        String[] genres = {"Pop", "Rock", "Jazz", "EDM", "Hip-Hop", "K-Pop", "Indie", "Classical"};
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genres);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        // Check if editing existing concert
        concert = (Concert) getIntent().getSerializableExtra("concert");

        if (concert != null) {
            // Edit mode
            setTitle("Edit Concert");
            loadConcertData();
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            // Add mode
            setTitle("Add Concert");
            btnDelete.setVisibility(View.GONE);
        }

        btnPreviewImage.setOnClickListener(v -> previewImage());
        btnSave.setOnClickListener(v -> saveConcert());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadConcertData() {
        etArtistName.setText(concert.getArtist());
        etVenue.setText(concert.getVenue());
        etDate.setText(concert.getDate());
        etTime.setText(concert.getTime());
        etPrice.setText(String.valueOf(concert.getPrice()));
        etImageUrl.setText(concert.getImageUrl());

        // Set status
        String status = concert.getStatus();
        ArrayAdapter<String> statusAdapter = (ArrayAdapter<String>) spinnerStatus.getAdapter();
        int statusPosition = statusAdapter.getPosition(status);
        spinnerStatus.setSelection(statusPosition);

        // Set genre
        String genre = concert.getGenre();
        if (genre != null) {
            ArrayAdapter<String> genreAdapter = (ArrayAdapter<String>) spinnerGenre.getAdapter();
            int genrePosition = genreAdapter.getPosition(genre);
            if (genrePosition >= 0) {
                spinnerGenre.setSelection(genrePosition);
            }
        }

        // Load existing image
        if (!TextUtils.isEmpty(concert.getImageUrl())) {
            loadImageFromUrl(concert.getImageUrl());
        }
    }

    private void previewImage() {
        String imageUrl = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, "Please enter image URL first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert Google Drive URL if needed
        String convertedUrl = convertGoogleDriveUrl(imageUrl);
        etImageUrl.setText(convertedUrl);

        loadImageFromUrl(convertedUrl);
    }

    private void loadImageFromUrl(String url) {
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_placeholder_concert)
                .error(R.drawable.ic_placeholder_concert)
                .into(ivConcertPoster);
    }

    private String convertGoogleDriveUrl(String url) {
        if (url.contains("drive.google.com")) {
            String fileId = null;

            if (url.contains("/file/d/")) {
                String[] parts = url.split("/file/d/");
                if (parts.length > 1) {
                    fileId = parts[1].split("/")[0];
                }
            } else if (url.contains("id=")) {
                String[] parts = url.split("id=");
                if (parts.length > 1) {
                    fileId = parts[1].split("&")[0];
                }
            }

            if (fileId != null && !fileId.isEmpty()) {
                return "https://drive.google.com/uc?export=view&id=" + fileId;
            }
        }
        return url;
    }

    private void saveConcert() {
        String title = etArtistName.getText().toString().trim();
        String artistName = etArtistName.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String genre = spinnerGenre.getSelectedItem().toString();

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
        String finalImageUrl = convertGoogleDriveUrl(imageUrl);

        // Generate concert ID if new
        String concertId = (concert != null) ? concert.getConcertId() :
                mDatabase.child("concerts").push().getKey();

        // ✅ FIXED: Constructor dengan urutan yang BENAR (11 parameter)
        // Concert(concertId, title, artistName, venue, imageUrl, description, date, time, price, status, genre)
        Concert newConcert = new Concert(
                concertId,          // 1. concertId
                title,              // 2. title
                artistName,         // 3. artistName
                venue,              // 4. venue
                finalImageUrl,      // 5. imageUrl
                "",                 // 6. description (kosong)
                date,               // 7. date
                time,               // 8. time
                price,              // 9. price
                status,             // 10. status
                genre               // 11. genre
        );

        // Save to database
        btnSave.setEnabled(false);
        mDatabase.child("concerts").child(concertId).setValue(newConcert)
                .addOnCompleteListener(task -> {
                    btnSave.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Concert saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save concert", Toast.LENGTH_SHORT).show();
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
        if (concert == null) return;

        btnDelete.setEnabled(false);
        mDatabase.child("concerts").child(concert.getConcertId()).removeValue()
                .addOnCompleteListener(task -> {
                    btnDelete.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Concert deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete concert", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
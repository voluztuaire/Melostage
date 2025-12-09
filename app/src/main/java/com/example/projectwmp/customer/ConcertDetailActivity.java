package com.example.projectwmp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Concert;

public class ConcertDetailActivity extends AppCompatActivity {

    private static final String TAG = "ConcertDetailActivity";

    private ImageView ivConcertImage;
    private TextView tvArtistName, tvVenue, tvDate, tvTime, tvPrice, tvDescription, tvGenre;
    private Button btnBookNow;
    private Concert concert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Concert Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initViews();

        // Get Concert object (Parcelable)
        concert = getIntent().getParcelableExtra("selected_concert");

        if (concert != null) {
            Log.d(TAG, "✅ Received concert: " + concert.getTitle());
            displayConcertData();
        } else {
            Toast.makeText(this, "❌ Error loading concert data", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Concert object is null!");
            finish();
        }
    }

    private void initViews() {
        ivConcertImage = findViewById(R.id.iv_concert_image);
        tvArtistName = findViewById(R.id.tv_artist_name);
        tvVenue = findViewById(R.id.tv_venue);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_description);
        tvGenre = findViewById(R.id.tv_genre);
        btnBookNow = findViewById(R.id.btn_book_now);
    }

    private void displayConcertData() {
        // Artist name
        tvArtistName.setText(concert.getArtist());

        // Venue
        tvVenue.setText(concert.getVenue());

        // Date
        tvDate.setText(concert.getDate());

        // Time
        tvTime.setText(concert.getTime());

        // Price
        tvPrice.setText("Rp " + String.format("%,.0f", concert.getPrice()));

        // Description
        if (tvDescription != null) {
            String desc = concert.getDescription();
            if (desc != null && !desc.isEmpty()) {
                tvDescription.setText(desc);
            } else {
                tvDescription.setText("No description available");
            }
        }

        // Genre
        if (tvGenre != null) {
            String genre = concert.getGenre();
            if (genre != null && !genre.isEmpty()) {
                tvGenre.setText(genre);
                tvGenre.setVisibility(View.VISIBLE);
            } else {
                tvGenre.setVisibility(View.GONE);
            }
        }

        // Load image
        if (ivConcertImage != null) {
            String imageUrl = concert.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_concert)
                        .error(R.drawable.ic_placeholder_concert)
                        .centerCrop()
                        .into(ivConcertImage);
            } else {
                ivConcertImage.setImageResource(R.drawable.ic_placeholder_concert);
            }
        }

        // Book Now button
        btnBookNow.setOnClickListener(v -> {
            Log.d(TAG, "Book Now clicked for: " + concert.getTitle());

            Intent bookingIntent = new Intent(ConcertDetailActivity.this, BookingActivity.class);
            bookingIntent.putExtra("selected_concert", concert);
            startActivity(bookingIntent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
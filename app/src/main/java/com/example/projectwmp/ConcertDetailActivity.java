package com.example.projectwmp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConcertDetailActivity extends AppCompatActivity {

    private TextView tvArtistName, tvVenue, tvDate, tvTime, tvPrice;
    private Button btnBookNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_detail);

        // Inisialisasi View
        tvArtistName = findViewById(R.id.tv_artist_name);
        tvVenue = findViewById(R.id.tv_venue);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvPrice = findViewById(R.id.tv_price);
        btnBookNow = findViewById(R.id.btn_book_now);

        // Ambil objek Concert dari Intent
        Concert concert = (Concert) getIntent().getSerializableExtra("selected_concert");

        if (concert != null) {
            tvArtistName.setText(concert.getArtistName());
            tvVenue.setText(concert.getVenue());
            tvDate.setText(concert.getDate());
            tvTime.setText(concert.getTime());
            tvPrice.setText("IDR " + String.format("%.0f", concert.getPrice()));

            btnBookNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ConcertDetailActivity.this, BookingActivity.class);
                    intent.putExtra("selected_concert", concert);
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "Gagal memuat detail konser.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
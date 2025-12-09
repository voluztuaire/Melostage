package com.example.projectwmp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class TicketDetailActivity extends AppCompatActivity {

    private TextView tvSuccess, tvArtistName, tvVenue, tvDate, tvTicketClass, tvQuantity, tvTotalPrice;
    private ImageView ivQRCode;
    private Button btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        // Initialize views
        tvSuccess = findViewById(R.id.tv_success);
        tvArtistName = findViewById(R.id.tv_artist_name);
        tvVenue = findViewById(R.id.tv_venue);
        tvDate = findViewById(R.id.tv_date);
        tvTicketClass = findViewById(R.id.tv_ticket_class);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        ivQRCode = findViewById(R.id.iv_qr_code);
        btnBackHome = findViewById(R.id.btn_back_home);

        // Get data from Intent
        Intent intent = getIntent();
        String bookingId = intent.getStringExtra("bookingId");
        String artistName = intent.getStringExtra("artistName");
        String venue = intent.getStringExtra("venue");
        String date = intent.getStringExtra("date");
        String ticketClass = intent.getStringExtra("ticketClass");
        int quantity = intent.getIntExtra("quantity", 0);
        double totalPrice = intent.getDoubleExtra("totalPrice", 0);

        // Display data
        tvSuccess.setText("🎉 Booking Successful!");
        tvArtistName.setText("Artist: " + artistName);
        tvVenue.setText("Venue: " + venue);
        tvDate.setText("Date: " + date);
        tvTicketClass.setText("Ticket Class: " + ticketClass);
        tvQuantity.setText("Quantity: " + quantity);
        tvTotalPrice.setText("Total Paid: Rp " + String.format("%.0f", totalPrice));

        // Generate QR Code
        try {
            Bitmap qrBitmap = generateQRCode(bookingId);
            ivQRCode.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TicketDetailActivity.this, UserHomeActivity.class));
                finish();
            }
        });
    }

    private Bitmap generateQRCode(String text) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bitmap;
    }
}
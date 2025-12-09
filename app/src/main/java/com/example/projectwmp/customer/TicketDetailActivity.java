package com.example.projectwmp.customer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Booking;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.text.NumberFormat;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    private ImageView ivQrCode;
    private TextView tvBookingId, tvConcertTitle, tvArtist, tvDate, tvTime;
    private TextView tvVenue, tvTicketClass, tvQuantity, tvTotalPrice;
    private TextView tvBookingDate, tvStatus;
    private Button btnDownloadTicket, btnBackToHome;

    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Your Ticket");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ✅ PERBAIKAN SUDAH DITERIMA DI SINI. Jika BHA sudah benar, ini akan berhasil.
        booking = getIntent().getParcelableExtra("booking");

        if (booking == null) {
            // ✅ Menerjemahkan dan menggunakan string resource (asumsi Anda menambahkan ini ke strings.xml)
            Toast.makeText(this, getString(R.string.error_failed_to_load_ticket), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        populateTicketData();
        generateQRCode();
    }

    private void initViews() {
        ivQrCode = findViewById(R.id.iv_qr_code);
        tvBookingId = findViewById(R.id.tv_booking_id);
        tvConcertTitle = findViewById(R.id.tv_concert_title);
        tvArtist = findViewById(R.id.tv_artist);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvVenue = findViewById(R.id.tv_venue);
        tvTicketClass = findViewById(R.id.tv_ticket_class);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvBookingDate = findViewById(R.id.tv_booking_date);
        tvStatus = findViewById(R.id.tv_status);
        btnDownloadTicket = findViewById(R.id.btn_download_ticket);
        btnBackToHome = findViewById(R.id.btn_back_to_home);

        btnDownloadTicket.setOnClickListener(v -> downloadTicket());
        btnBackToHome.setOnClickListener(v -> finish());
    }

    private void populateTicketData() {
        // ... (Logika pemanggilan data Booking sudah benar)
        tvBookingId.setText(booking.getBookingId());
        tvConcertTitle.setText(booking.getConcertTitle());
        tvArtist.setText(booking.getArtistName());
        tvDate.setText(booking.getDate());
        tvTime.setText(booking.getTime());
        tvVenue.setText(booking.getVenue());

        // Memastikan tampilannya konsisten dengan format di XML
        tvTicketClass.setText(booking.getTicketClass()); // Dibuat terpisah dari quantity di XML
        tvQuantity.setText(String.valueOf(booking.getQuantity())); // Quantity di field terpisah

        // Format price - Use existing helper method
        tvTotalPrice.setText(booking.getFormattedPrice());

        // Format booking date - Use existing helper method
        tvBookingDate.setText(booking.getFormattedTimestamp());

        //tvStatus.setText(booking.getStatus());

        // Set status color
        //String status = booking.getStatus();
        //if ("Confirmed".equalsIgnoreCase(status) || "Paid".equalsIgnoreCase(status)) {
            //tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        //} else if ("Pending".equalsIgnoreCase(status)) {
            //tvStatus.setTextColor(Color.parseColor("#FF9800"));
        //} else {
            //tvStatus.setTextColor(Color.parseColor("#F44336"));
        //}
    }

    private void generateQRCode() {
        // ... (Logika QR Code sudah benar)
        String qrContent = "BOOKING:" + booking.getBookingId() +
                "|USER:" + booking.getUserId() +
                "|CONCERT:" + booking.getConcertId() +
                "|CLASS:" + booking.getTicketClass() +
                "|QTY:" + booking.getQuantity() +
                "|STATUS:" + booking.getPaymentStatus();

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ivQrCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_failed_to_generate_qr), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadTicket() {
        // TODO: Implement screenshot or PDF generation
        Toast.makeText(this, getString(R.string.toast_ticket_saved), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
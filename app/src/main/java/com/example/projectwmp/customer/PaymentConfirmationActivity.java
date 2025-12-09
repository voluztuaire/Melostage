package com.example.projectwmp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Booking;
import com.example.projectwmp.models.Concert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "PaymentConfirm";

    private TextView tvTitle, tvArtist, tvTicketClass, tvQuantity, tvTotalPrice;
    private TextView tvPaymentMethod, tvInstruction;
    private Button btnViewTicket, btnBackToHome;

    private Booking currentBooking;
    private Concert selectedConcert;
    private String concertId;
    private String concertTitle;
    private String artistName;
    private String venue;
    private String date;
    private String time;
    private String ticketClass;
    private int quantity;
    private double totalPrice;
    private String paymentMethod;
    private String bookingId;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment Confirmation");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get data from intent
        getIntentData();

        initViews();
        displayBookingDetails();
        displayPaymentInstruction();

        // Fetch booking from Firebase instead of creating new one
        fetchBookingFromFirebase();
    }

    private void getIntentData() {
        Intent intent = getIntent();

        // Get booking ID (saved by BookingActivity)
        bookingId = intent.getStringExtra("bookingId");

        // Try to get Concert object (if passed)
        selectedConcert = intent.getParcelableExtra("selected_concert");

        if (selectedConcert != null) {
            concertId = selectedConcert.getConcertId();
            concertTitle = selectedConcert.getTitle();
            artistName = selectedConcert.getArtist();
            venue = selectedConcert.getVenue();
            date = selectedConcert.getDate();
            time = selectedConcert.getTime();
            Log.d(TAG, "✅ Concert object received");
        } else {
            // Fallback: Get individual fields from intent
            concertId = intent.getStringExtra("concertId");
            concertTitle = intent.getStringExtra("concertTitle");
            artistName = intent.getStringExtra("artistName");
            venue = intent.getStringExtra("venue");
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            Log.d(TAG, "⚠️ Using individual fields");
        }

        // Get booking details
        ticketClass = intent.getStringExtra("ticketClass");
        quantity = intent.getIntExtra("quantity", 1);
        totalPrice = intent.getDoubleExtra("totalPrice", 0);
        paymentMethod = intent.getStringExtra("paymentMethod");

        Log.d(TAG, "Booking ID: " + bookingId);
        Log.d(TAG, "Artist: " + artistName);
        Log.d(TAG, "Ticket Class: " + ticketClass);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        tvTicketClass = findViewById(R.id.tv_ticket_class);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvInstruction = findViewById(R.id.tv_instruction);
        btnViewTicket = findViewById(R.id.btn_view_ticket);
        btnBackToHome = findViewById(R.id.btn_back_to_home);

        btnViewTicket.setOnClickListener(v -> {
            if (currentBooking != null) {
                navigateToTicketDetail(currentBooking);
            } else {
                Toast.makeText(this, "Please wait, loading booking...", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayBookingDetails() {
        tvTitle.setText(concertTitle != null ? concertTitle : artistName);
        tvArtist.setText("Artist: " + (artistName != null ? artistName : "Unknown"));
        tvTicketClass.setText("Ticket Class: " + (ticketClass != null ? ticketClass : "Unknown"));
        tvQuantity.setText("Quantity: " + quantity);
        tvTotalPrice.setText(String.format("Total: Rp %,.0f", totalPrice));
    }

    private void displayPaymentInstruction() {
        String instruction = getPaymentInstruction(paymentMethod, totalPrice);
        tvPaymentMethod.setText("Payment Method: " + (paymentMethod != null ? paymentMethod : "Unknown"));
        tvInstruction.setText(instruction);
    }

    private String getPaymentInstruction(String method, double total) {
        if (method == null) return "Please make payment according to selected method.";

        if (method.contains("BCA")) {
            return "💳 Transfer to:\n\n" +
                    "Payment Method: Bank Transfer (BCA)\n" +
                    "Bank: BCA\n" +
                    "Account No: 1234567890\n" +
                    "Account Name: Concert Ticket App\n\n" +
                    "Amount: Rp " + String.format("%,.0f", total) + "\n\n";
        } else if (method.contains("ShopeePay")) {
            return "💰 Transfer to:\n\n" +
                    "ShopeePay: 081234567890\n" +
                    "Account Name: Concert Ticket App\n\n" +
                    "Amount: Rp " + String.format("%,.0f", total) + "\n\n";
        } else if (method.contains("GoPay")) {
            return "💰 Transfer to:\n\n" +
                    "GoPay: 081234567890\n" +
                    "Account Name: Concert Ticket App\n\n" +
                    "Amount: Rp " + String.format("%,.0f", total) + "\n\n";
        }

        return "Please make payment according to selected method.";
    }

    private void fetchBookingFromFirebase() {
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Error: Booking ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("bookings").child(bookingId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        currentBooking = snapshot.getValue(Booking.class);
                        if (currentBooking != null) {
                            Log.d(TAG, "✅ Booking loaded: " + currentBooking.getBookingId());
                            Toast.makeText(this, "Booking loaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "❌ Booking not found in database");
                        Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to load booking", e);
                    Toast.makeText(this, "Failed to load booking: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToTicketDetail(Booking booking) {
        Intent intent = new Intent(PaymentConfirmationActivity.this, TicketDetailActivity.class);
        intent.putExtra("booking", booking);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
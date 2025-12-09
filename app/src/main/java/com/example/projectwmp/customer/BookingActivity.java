package com.example.projectwmp.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Concert;
import com.example.projectwmp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView tvArtistName, tvVenue, tvDate, tvQuantity, tvTotal;
    private Spinner spinnerTicketClass, spinnerPaymentMethod;
    private Button btnMinus, btnPlus, btnConfirmPurchase;
    private CheckBox checkboxTerms;

    private Concert concert;
    private int quantity = 1;
    private double basePrice = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userName, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Book Ticket");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        tvArtistName = findViewById(R.id.tv_artist_name);
        tvVenue = findViewById(R.id.tv_venue);
        tvDate = findViewById(R.id.tv_date);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvTotal = findViewById(R.id.tv_total);
        spinnerTicketClass = findViewById(R.id.spinner_ticket_class);
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_method);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        btnConfirmPurchase = findViewById(R.id.btn_confirm_purchase);

        // Get Concert object from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selected_concert")) {
            concert = intent.getParcelableExtra("selected_concert");

            if (concert != null) {
                basePrice = concert.getPrice();

                // Display concert info
                tvArtistName.setText(concert.getArtist());
                tvVenue.setText(concert.getVenue());
                tvDate.setText(concert.getDate());
            } else {
                Toast.makeText(this, "Error: Concert data not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "Error: No concert data received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup Ticket Class Spinner
        String[] ticketClasses = {"Regular (1x)", "VIP (2x)", "Festival (1.5x)"};
        ArrayAdapter<String> ticketAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ticketClasses);
        ticketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTicketClass.setAdapter(ticketAdapter);

        // Setup Payment Method Spinner
        String[] paymentMethods = {"Bank Transfer (BCA)", "Bank Transfer (Mandiri)", "GoPay", "OVO", "DANA", "ShopeePay"};
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(paymentAdapter);

        // Load user data
        loadUserData();

        // Calculate initial price
        calculateTotalPrice();

        // Quantity controls
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                calculateTotalPrice();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (quantity < 10) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                calculateTotalPrice();
            }
        });

        // Recalculate when ticket class changes
        spinnerTicketClass.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                calculateTotalPrice();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnConfirmPurchase.setOnClickListener(v -> confirmPurchase());
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userName = user.getFullName();
                        userPhone = user.getPhoneNumber();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalPrice() {
        double multiplier = 1.0;

        int selectedPosition = spinnerTicketClass.getSelectedItemPosition();
        if (selectedPosition == 1) {
            multiplier = 2.0; // VIP
        } else if (selectedPosition == 2) {
            multiplier = 1.5; // Festival
        }

        double totalPrice = basePrice * multiplier * quantity;
        tvTotal.setText("Rp " + String.format("%.0f", totalPrice));
    }

    private void confirmPurchase() {
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected values
        String ticketClass = spinnerTicketClass.getSelectedItem().toString();
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

        // Calculate total
        double multiplier = 1.0;
        int selectedPosition = spinnerTicketClass.getSelectedItemPosition();
        if (selectedPosition == 1) multiplier = 2.0;
        else if (selectedPosition == 2) multiplier = 1.5;

        double totalPrice = basePrice * multiplier * quantity;

        // Save booking to Firebase
        String userId = mAuth.getCurrentUser().getUid();
        String bookingId = mDatabase.child("bookings").push().getKey();

        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingId", bookingId);
        booking.put("userId", userId);
        booking.put("userName", userName != null ? userName : "Unknown");
        booking.put("userPhone", userPhone != null ? userPhone : "");
        booking.put("concertId", concert.getConcertId());
        booking.put("concertTitle", concert.getTitle()); // ADDED
        booking.put("artistName", concert.getArtist());
        booking.put("venue", concert.getVenue());
        booking.put("date", concert.getDate());
        booking.put("time", concert.getTime());
        booking.put("ticketClass", ticketClass);
        booking.put("quantity", quantity);
        booking.put("totalPrice", totalPrice);
        booking.put("timestamp", System.currentTimeMillis());
        booking.put("paymentStatus", "Pending");
        booking.put("status", "Pending"); // ADDED
        booking.put("paymentMethod", paymentMethod);

        mDatabase.child("bookings").child(bookingId).setValue(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Send WhatsApp notification
                        sendWhatsAppNotification(userName, concert.getArtist(), ticketClass, quantity, totalPrice, paymentMethod);

                        // FIXED: Navigate to Payment Confirmation with proper data
                        Intent intent = new Intent(BookingActivity.this, PaymentConfirmationActivity.class);

                        // Pass Concert object (recommended)
                        intent.putExtra("selected_concert", concert);

                        // Pass booking details
                        intent.putExtra("bookingId", bookingId);
                        intent.putExtra("ticketClass", ticketClass);
                        intent.putExtra("quantity", quantity);
                        intent.putExtra("totalPrice", totalPrice);
                        intent.putExtra("paymentMethod", paymentMethod);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendWhatsAppNotification(String name, String artist, String ticketClass, int qty, double total, String payment) {
        if (userPhone == null || userPhone.isEmpty()) {
            return;
        }

        String phoneNumber = userPhone.startsWith("0") ? "62" + userPhone.substring(1) : userPhone;

        String message = "🎫 *Booking Confirmation*\n\n" +
                "Hello " + name + "!\n\n" +
                "Your booking is successful!\n\n" +
                "📋 *Booking Details:*\n" +
                "🎤 Artist: " + artist + "\n" +
                "🎟️ Ticket Class: " + ticketClass + "\n" +
                "📊 Quantity: " + qty + "\n" +
                "💰 Total: Rp " + String.format("%.0f", total) + "\n" +
                "💳 Payment: " + payment + "\n\n" +
                "⚠️ Status: *Pending Payment*\n\n" +
                "Please make payment according to selected method.\n\n" +
                "Thank you! 🎉";

        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            whatsappIntent.setData(Uri.parse("https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message)));
            startActivity(whatsappIntent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
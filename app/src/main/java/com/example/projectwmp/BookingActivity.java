package com.example.projectwmp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView tvEventName, tvTotalPrice;
    private RadioGroup radioGroupTicketClass;
    private RadioButton rbVIP, rbRegular, rbFestival;
    private EditText etQuantity;
    private CheckBox cbTerms;
    private Button btnConfirmPurchase;

    private Concert concert;
    private double basePrice;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        tvEventName = findViewById(R.id.tv_full_name);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        radioGroupTicketClass = findViewById(R.id.radio_group_ticket_class);
        rbVIP = findViewById(R.id.rb_vip);
        rbRegular = findViewById(R.id.rb_regular);
        rbFestival = findViewById(R.id.rb_festival);
        etQuantity = findViewById(R.id.et_quantity);
        cbTerms = findViewById(R.id.cb_terms);
        btnConfirmPurchase = findViewById(R.id.btn_confirm_purchase);

        // Get Concert object from Intent
        concert = (Concert) getIntent().getSerializableExtra("selected_concert");

        if (concert != null) {
            basePrice = concert.getPrice();
            tvEventName.setText("Booking: " + concert.getArtistName());
        } else {
            Toast.makeText(this, "Error loading concert data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set default quantity
        etQuantity.setText("2");

        // Calculate price when ticket class changes
        radioGroupTicketClass.setOnCheckedChangeListener((group, checkedId) -> calculateTotalPrice());

        // Calculate price when quantity changes (real-time)
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Initial calculation
        calculateTotalPrice();

        btnConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPurchase();
            }
        });
    }

    private void calculateTotalPrice() {
        String quantityStr = etQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantityStr)) {
            tvTotalPrice.setText("Total: Rp 0");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            tvTotalPrice.setText("Total: Rp 0");
            return;
        }

        double multiplier = 1.0;

        int selectedId = radioGroupTicketClass.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_vip) {
            multiplier = 2.0; // VIP is 2x base price
        } else if (selectedId == R.id.rb_regular) {
            multiplier = 1.0; // Regular is base price
        } else if (selectedId == R.id.rb_festival) {
            multiplier = 1.5; // Festival is 1.5x base price
        }

        double totalPrice = basePrice * multiplier * quantity;
        tvTotalPrice.setText("Total: Rp " + String.format("%,.0f", totalPrice));
    }

    private void confirmPurchase() {
        String quantityStr = etQuantity.getText().toString().trim();

        if (radioGroupTicketClass.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a ticket class", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("Quantity is required");
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            return;
        }

        // Get selected ticket class
        int selectedId = radioGroupTicketClass.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String ticketClass = selectedRadioButton.getText().toString();

        // Calculate total
        double multiplier = 1.0;
        if (selectedId == R.id.rb_vip) multiplier = 2.0;
        else if (selectedId == R.id.rb_festival) multiplier = 1.5;

        double totalPrice = basePrice * multiplier * quantity;

        // Save booking to Firebase
        String userId = mAuth.getCurrentUser().getUid();
        String bookingId = mDatabase.child("bookings").push().getKey();

        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingId", bookingId);
        booking.put("userId", userId);
        booking.put("concertId", concert.getConcertId());
        booking.put("artistName", concert.getArtistName());
        booking.put("venue", concert.getVenue());
        booking.put("date", concert.getDate());
        booking.put("time", concert.getTime());
        booking.put("ticketClass", ticketClass);
        booking.put("quantity", quantity);
        booking.put("totalPrice", totalPrice);
        booking.put("timestamp", System.currentTimeMillis());

        mDatabase.child("bookings").child(bookingId).setValue(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Navigate to Ticket Detail
                        Intent intent = new Intent(BookingActivity.this, TicketDetailActivity.class);
                        intent.putExtra("bookingId", bookingId);
                        intent.putExtra("artistName", concert.getArtistName());
                        intent.putExtra("venue", concert.getVenue());
                        intent.putExtra("date", concert.getDate());
                        intent.putExtra("ticketClass", ticketClass);
                        intent.putExtra("quantity", quantity);
                        intent.putExtra("totalPrice", totalPrice);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.example.projectwmp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private List<Booking> allBookings;
    private List<Booking> filteredBookings;
    private Button btnPending, btnPaid, btnAll;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Booking History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recycler_booking_history);
        btnAll = findViewById(R.id.btn_filter_all);
        btnPending = findViewById(R.id.btn_filter_pending);
        btnPaid = findViewById(R.id.btn_filter_paid);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allBookings = new ArrayList<>();
        filteredBookings = new ArrayList<>();

        adapter = new BookingHistoryAdapter(this, filteredBookings, new BookingHistoryAdapter.OnBookingActionListener() {
            @Override
            public void onPayNow(Booking booking) {
                // Navigasi ke PaymentConfirmationActivity
                Intent intent = new Intent(BookingHistoryActivity.this, PaymentConfirmationActivity.class);
                intent.putExtra("bookingId", booking.getBookingId());
                intent.putExtra("artistName", booking.getArtistName());
                intent.putExtra("ticketClass", booking.getTicketClass());
                intent.putExtra("quantity", booking.getQuantity());
                intent.putExtra("totalPrice", booking.getTotalPrice());
                intent.putExtra("paymentMethod", booking.getPaymentMethod());
                startActivity(intent);
            }

            @Override
            public void onViewTicket(Booking booking) {
                // 🚨 PERBAIKAN KRITIS: Kirim seluruh objek Booking (Parcelable/Serializable)
                Intent intent = new Intent(BookingHistoryActivity.this, TicketDetailActivity.class);
                intent.putExtra("booking", booking); // Menggunakan kunci "booking"
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        btnAll.setOnClickListener(v -> filterBookings("all"));
        btnPending.setOnClickListener(v -> filterBookings("pending"));
        btnPaid.setOnClickListener(v -> filterBookings("paid"));

        loadBookingHistory();
    }

    private void loadBookingHistory() {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("bookings")
                .orderByChild("userId")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allBookings.clear();
                        for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                            Booking booking = bookingSnapshot.getValue(Booking.class);
                            if (booking != null) {
                                allBookings.add(booking);
                            }
                        }

                        // Sort berdasarkan timestamp terbaru
                        Collections.sort(allBookings, (b1, b2) ->
                                Long.compare(b2.getTimestamp(), b1.getTimestamp())
                        );

                        filterBookings(currentFilter);

                        if (allBookings.isEmpty()) {
                            // ✅ Menerjemahkan Toast
                            Toast.makeText(BookingHistoryActivity.this,
                                    "No booking history found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // ✅ Menerjemahkan Toast
                        Toast.makeText(BookingHistoryActivity.this,
                                "Failed to load booking history", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterBookings(String filter) {
        currentFilter = filter;
        filteredBookings.clear();

        // Reset warna tombol (asumsi R.color.colorPrimary sudah ada)
        btnAll.setBackgroundTintList(null);
        btnPending.setBackgroundTintList(null);
        btnPaid.setBackgroundTintList(null);

        switch (filter) {
            case "all":
                filteredBookings.addAll(allBookings);
                btnAll.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
                break;
            case "pending":
                for (Booking booking : allBookings) {
                    if ("pending".equals(booking.getPaymentStatus())) {
                        filteredBookings.add(booking);
                    }
                }
                btnPending.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
                break;
            case "paid":
                for (Booking booking : allBookings) {
                    if ("paid".equals(booking.getPaymentStatus())) {
                        filteredBookings.add(booking);
                    }
                }
                btnPaid.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
                break;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
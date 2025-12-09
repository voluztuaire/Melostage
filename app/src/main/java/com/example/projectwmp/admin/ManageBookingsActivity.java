package com.example.projectwmp.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Booking;
import com.example.projectwmp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminBookingAdapter adapter;
    private List<Booking> allBookings;
    private List<Booking> filteredBookings;
    private Button btnAll, btnPending, btnPaid;
    private DatabaseReference mDatabase;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Bookings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recycler_bookings);
        btnAll = findViewById(R.id.btn_filter_all);
        btnPending = findViewById(R.id.btn_filter_pending);
        btnPaid = findViewById(R.id.btn_filter_paid);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allBookings = new ArrayList<>();
        filteredBookings = new ArrayList<>();

        adapter = new AdminBookingAdapter(this, filteredBookings, new AdminBookingAdapter.OnBookingActionListener() {
            @Override
            public void onUpdatePaymentStatus(Booking booking) {
                showUpdatePaymentDialog(booking);
            }

            @Override
            public void onContactCustomer(Booking booking) {
                contactCustomerViaWhatsApp(booking);
            }

            @Override
            public void onViewDetails(Booking booking) {
                showBookingDetailsDialog(booking);
            }
        });

        recyclerView.setAdapter(adapter);

        btnAll.setOnClickListener(v -> filterBookings("all"));
        btnPending.setOnClickListener(v -> filterBookings("pending"));
        btnPaid.setOnClickListener(v -> filterBookings("paid"));

        loadAllBookings();
    }

    private void loadAllBookings() {
        mDatabase.child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allBookings.clear();
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    Booking booking = bookingSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        // Load user info
                        loadUserInfo(booking);
                        allBookings.add(booking);
                    }
                }

                Collections.sort(allBookings, (b1, b2) ->
                        Long.compare(b2.getTimestamp(), b1.getTimestamp())
                );

                filterBookings(currentFilter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageBookingsActivity.this,
                        "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserInfo(Booking booking) {
        mDatabase.child("users").child(booking.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            booking.setUserName(user.getFullName());
                            booking.setUserPhone(user.getPhoneNumber());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void filterBookings(String filter) {
        currentFilter = filter;
        filteredBookings.clear();

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
                    if ("pending".equalsIgnoreCase(booking.getPaymentStatus())) {
                        filteredBookings.add(booking);
                    }
                }
                btnPending.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
                break;
            case "paid":
                for (Booking booking : allBookings) {
                    if ("paid".equalsIgnoreCase(booking.getPaymentStatus())) {
                        filteredBookings.add(booking);
                    }
                }
                btnPaid.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
                break;
        }

        adapter.notifyDataSetChanged();
    }

    private void showUpdatePaymentDialog(Booking booking) {
        String[] options = {"Pending", "Paid"};
        int currentSelection = "paid".equalsIgnoreCase(booking.getPaymentStatus()) ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Update Payment Status")
                .setSingleChoiceItems(options, currentSelection, null)
                .setPositiveButton("Update", (dialog, which) -> {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    int selectedPosition = alertDialog.getListView().getCheckedItemPosition();
                    String newStatus = selectedPosition == 1 ? "paid" : "pending";
                    updatePaymentStatus(booking, newStatus);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePaymentStatus(Booking booking, String newStatus) {
        mDatabase.child("bookings").child(booking.getBookingId())
                .child("paymentStatus").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Payment status updated to: " + newStatus,
                            Toast.LENGTH_SHORT).show();

                    // Send WhatsApp notification if paid
                    if ("paid".equalsIgnoreCase(newStatus)) {
                        sendPaymentConfirmationWhatsApp(booking);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void contactCustomerViaWhatsApp(Booking booking) {
        if (booking.getUserPhone() == null || booking.getUserPhone().isEmpty()) {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = booking.getUserPhone();
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+62" + phoneNumber.replaceFirst("^0+", "");
        }

        String message = "Halo " + booking.getUserName() + "! 👋\n\n" +
                "Terima kasih sudah booking tiket konser " + booking.getArtistName() + "!\n\n" +
                "📋 Booking ID: " + booking.getBookingId() + "\n" +
                "🎤 Artist: " + booking.getArtistName() + "\n" +
                "📍 Venue: " + booking.getVenue() + "\n" +
                "📅 Date: " + booking.getDate() + "\n" +
                "💰 Total: Rp " + String.format("%,.0f", booking.getTotalPrice());

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message)));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPaymentConfirmationWhatsApp(Booking booking) {
        if (booking.getUserPhone() == null || booking.getUserPhone().isEmpty()) {
            return;
        }

        String phoneNumber = booking.getUserPhone();
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+62" + phoneNumber.replaceFirst("^0+", "");
        }

        String message = "✅ PAYMENT CONFIRMED! ✅\n\n" +
                "Halo " + booking.getUserName() + "!\n\n" +
                "Pembayaran Anda untuk tiket konser " + booking.getArtistName() + " sudah dikonfirmasi! 🎉\n\n" +
                "📋 Booking ID: " + booking.getBookingId() + "\n" +
                "🎤 Artist: " + booking.getArtistName() + "\n" +
                "📍 Venue: " + booking.getVenue() + "\n" +
                "📅 Date: " + booking.getDate() + "\n" +
                "🕐 Time: " + booking.getTime() + "\n" +
                "🎫 Class: " + booking.getTicketClass() + "\n" +
                "💰 Total: Rp " + String.format("%,.0f", booking.getTotalPrice()) + "\n\n" +
                "Tiket Anda sudah siap! Silakan cek aplikasi untuk melihat QR code tiket Anda.\n\n" +
                "Sampai jumpa di konser! 🎵";

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message)));
            startActivity(intent);
        } catch (Exception e) {
            // WhatsApp not installed, skip notification
        }
    }

    private void showBookingDetailsDialog(Booking booking) {
        String details = "📋 Booking Details\n\n" +
                "Booking ID: " + booking.getBookingId() + "\n" +
                "Customer: " + booking.getUserName() + "\n" +
                "Phone: " + booking.getUserPhone() + "\n\n" +
                "🎤 Concert Info:\n" +
                "Artist: " + booking.getArtistName() + "\n" +
                "Venue: " + booking.getVenue() + "\n" +
                "Date: " + booking.getDate() + "\n" +
                "Time: " + booking.getTime() + "\n\n" +
                "🎫 Ticket Info:\n" +
                "Class: " + booking.getTicketClass() + "\n" +
                "Quantity: " + booking.getQuantity() + "\n" +
                "Total Price: Rp " + String.format("%,.0f", booking.getTotalPrice()) + "\n\n" +
                "💳 Payment:\n" +
                "Method: " + booking.getPaymentMethod() + "\n" +
                "Status: " + booking.getPaymentStatus().toUpperCase();

        new AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(details)
                .setPositiveButton("Close", null)
                .setNeutralButton("Contact", (dialog, which) -> contactCustomerViaWhatsApp(booking))
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
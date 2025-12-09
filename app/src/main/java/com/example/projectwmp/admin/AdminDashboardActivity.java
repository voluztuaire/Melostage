package com.example.projectwmp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.auth.LoginActivity;
import com.example.projectwmp.models.Booking;
import com.example.projectwmp.models.Concert;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminConcertAdapter concertAdapter;
    private List<Concert> concertList;
    private FloatingActionButton fabAddConcert;
    private CardView cardManageBookings, cardManageBanners;
    private TextView tvTotalConcerts, tvTotalBookings, tvTotalRevenue, tvPendingPayments;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        recyclerView = findViewById(R.id.recycler_concerts_admin);
        fabAddConcert = findViewById(R.id.fab_add_concert);
        cardManageBookings = findViewById(R.id.card_manage_bookings);
        cardManageBanners = findViewById(R.id.card_manage_banners);

        tvTotalConcerts = findViewById(R.id.tv_total_concerts);
        tvTotalBookings = findViewById(R.id.tv_total_bookings);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvPendingPayments = findViewById(R.id.tv_pending_payments);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        concertList = new ArrayList<>();
        concertAdapter = new AdminConcertAdapter(this, concertList, new AdminConcertAdapter.OnConcertClickListener() {
            @Override
            public void onConcertClick(Concert concert) {
                // Navigate to Edit Concert
                Intent intent = new Intent(AdminDashboardActivity.this, AddEditConcertActivity.class);
                intent.putExtra("concert", (Parcelable) concert);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(concertAdapter);

        // FAB untuk ADD concert (JANGAN kirim concert object)
        fabAddConcert.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddEditConcertActivity.class);
            startActivity(intent);
        });

        // Manage Bookings Card
        cardManageBookings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageBookingsActivity.class);
            startActivity(intent);
        });

        // Manage Banners Card
        cardManageBanners.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageBannersActivity.class);
            startActivity(intent);
        });

        loadStatistics();
        loadAllConcerts();
    }

    private void loadStatistics() {
        // Load total concerts
        mDatabase.child("concerts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalConcerts = snapshot.getChildrenCount();
                tvTotalConcerts.setText(String.valueOf(totalConcerts));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Load bookings statistics
        mDatabase.child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalBookings = 0;
                double totalRevenue = 0;
                long pendingPayments = 0;

                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    Booking booking = bookingSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        totalBookings++;
                        if ("paid".equalsIgnoreCase(booking.getPaymentStatus())) {
                            totalRevenue += booking.getTotalPrice();
                        } else {
                            pendingPayments++;
                        }
                    }
                }

                tvTotalBookings.setText(String.valueOf(totalBookings));
                tvTotalRevenue.setText(String.format("Rp %,.0f", totalRevenue));
                tvPendingPayments.setText(String.valueOf(pendingPayments));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadAllConcerts() {
        mDatabase.child("concerts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                concertList.clear();
                for (DataSnapshot concertSnapshot : snapshot.getChildren()) {
                    Concert concert = concertSnapshot.getValue(Concert.class);
                    if (concert != null) {
                        concertList.add(concert);
                    }
                }
                concertAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this,
                        "Failed to load concerts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
        loadAllConcerts();
    }
}
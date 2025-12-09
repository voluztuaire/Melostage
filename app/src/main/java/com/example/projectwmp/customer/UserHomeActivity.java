package com.example.projectwmp.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.projectwmp.R;
import com.example.projectwmp.auth.LoginActivity;
import com.example.projectwmp.models.Banner;
import com.example.projectwmp.models.Concert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {

    private static final String TAG = "UserHomeActivity";

    // Banner Section
    private ViewPager2 viewPagerBanner;
    private BannerPagerAdapter bannerAdapter;
    private List<Banner> bannerList;

    // Featured Events (Horizontal)
    private RecyclerView recyclerFeatured;
    private ConcertAdapter featuredAdapter;
    private List<Concert> featuredList;

    // All Upcoming Concerts (Vertical)
    private RecyclerView recyclerAllConcerts;
    private ConcertAdapter allConcertsAdapter;
    private List<Concert> allConcertsList;

    private TextView tvEmptyFeatured, tvEmptyAllConcerts;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        initViews();

        // Load data
        loadBanners();
        loadAllConcerts(); // Load semua concert sekaligus
    }

    private void initViews() {
        // ===== BANNER SECTION =====
        viewPagerBanner = findViewById(R.id.viewpager_banner);
        bannerList = new ArrayList<>();

        bannerAdapter = new BannerPagerAdapter(this, bannerList, banner -> {
            Log.d(TAG, "Banner clicked: " + banner.getTitle());

            if (banner.getConcertId() != null && !banner.getConcertId().isEmpty()) {
                loadConcertAndNavigate(banner.getConcertId());
            } else {
                Toast.makeText(UserHomeActivity.this,
                        "This banner is not linked to any concert",
                        Toast.LENGTH_SHORT).show();
            }
        });

        viewPagerBanner.setAdapter(bannerAdapter);

        // ===== FEATURED EVENTS (HORIZONTAL) =====
        recyclerFeatured = findViewById(R.id.recycler_featured);
        tvEmptyFeatured = findViewById(R.id.tv_empty_featured);

        // Set HORIZONTAL layout manager
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerFeatured.setLayoutManager(horizontalLayout);

        featuredList = new ArrayList<>();
        featuredAdapter = new ConcertAdapter(this, featuredList, concert -> {
            Log.d(TAG, "Featured concert clicked: " + concert.getTitle());
            navigateToConcertDetail(concert);
        });
        recyclerFeatured.setAdapter(featuredAdapter);

        // ===== ALL CONCERTS (VERTICAL) =====
        recyclerAllConcerts = findViewById(R.id.recycler_all_concerts);
        tvEmptyAllConcerts = findViewById(R.id.tv_empty_all_concerts);

        recyclerAllConcerts.setLayoutManager(new LinearLayoutManager(this));

        allConcertsList = new ArrayList<>();
        allConcertsAdapter = new ConcertAdapter(this, allConcertsList, concert -> {
            Log.d(TAG, "Concert clicked: " + concert.getTitle());
            navigateToConcertDetail(concert);
        });
        recyclerAllConcerts.setAdapter(allConcertsAdapter);
    }

    private void loadBanners() {
        mDatabase.child("banners")
                .orderByChild("active")
                .equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bannerList.clear();
                        for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                            Banner banner = bannerSnapshot.getValue(Banner.class);
                            if (banner != null && banner.isActive()) {
                                bannerList.add(banner);
                            }
                        }

                        // Sort by priority
                        if (!bannerList.isEmpty()) {
                            Collections.sort(bannerList, (b1, b2) ->
                                    Integer.compare(b1.getPriority(), b2.getPriority()));
                        }

                        bannerAdapter.notifyDataSetChanged();
                        Log.d(TAG, "✅ Loaded " + bannerList.size() + " banners");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "❌ Failed to load banners: " + error.getMessage());
                    }
                });
    }

    private void loadAllConcerts() {
        mDatabase.child("concerts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featuredList.clear();
                allConcertsList.clear();

                for (DataSnapshot concertSnapshot : snapshot.getChildren()) {
                    Concert concert = concertSnapshot.getValue(Concert.class);

                    if (concert != null && "Upcoming".equalsIgnoreCase(concert.getStatus())) {
                        // Add to all concerts list
                        allConcertsList.add(concert);

                        // Add to featured if date is soon (next 30 days) or marked as featured
                        if (concert.isFeatured() || isUpcomingSoon(concert.getDate())) {
                            featuredList.add(concert);
                        }
                    }
                }

                // Sort featured by date (nearest first)
                Collections.sort(featuredList, (c1, c2) ->
                        compareDates(c1.getDate(), c2.getDate()));

                // Limit featured to 5 items
                if (featuredList.size() > 5) {
                    featuredList = new ArrayList<>(featuredList.subList(0, 5));
                }

                // Sort all concerts by date
                Collections.sort(allConcertsList, (c1, c2) ->
                        compareDates(c1.getDate(), c2.getDate()));

                // Update adapters
                featuredAdapter.notifyDataSetChanged();
                allConcertsAdapter.notifyDataSetChanged();

                // Show/hide empty states
                updateEmptyStates();

                Log.d(TAG, "✅ Featured: " + featuredList.size() + ", All: " + allConcertsList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "❌ Failed to load concerts: " + error.getMessage());
                Toast.makeText(UserHomeActivity.this,
                        "Failed to load concerts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyStates() {
        // Featured empty state
        if (featuredList.isEmpty()) {
            recyclerFeatured.setVisibility(View.GONE);
            tvEmptyFeatured.setVisibility(View.VISIBLE);
        } else {
            recyclerFeatured.setVisibility(View.VISIBLE);
            tvEmptyFeatured.setVisibility(View.GONE);
        }

        // All concerts empty state
        if (allConcertsList.isEmpty()) {
            recyclerAllConcerts.setVisibility(View.GONE);
            tvEmptyAllConcerts.setVisibility(View.VISIBLE);
        } else {
            recyclerAllConcerts.setVisibility(View.VISIBLE);
            tvEmptyAllConcerts.setVisibility(View.GONE);
        }
    }

    private boolean isUpcomingSoon(String dateStr) {
        // Simple check: if date contains current year or next month
        // You can improve this with proper date parsing
        return true; // For now, include all upcoming
    }

    private int compareDates(String date1, String date2) {
        // Simple string comparison (works if format is YYYY-MM-DD)
        // For better sorting, use SimpleDateFormat
        if (date1 == null) return 1;
        if (date2 == null) return -1;
        return date1.compareTo(date2);
    }

    private void loadConcertAndNavigate(String concertId) {
        mDatabase.child("concerts").child(concertId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Concert concert = snapshot.getValue(Concert.class);
                        if (concert != null) {
                            navigateToConcertDetail(concert);
                        } else {
                            Toast.makeText(UserHomeActivity.this,
                                    "Concert not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to load concert: " + error.getMessage());
                    }
                });
    }

    private void navigateToConcertDetail(Concert concert) {
        Intent intent = new Intent(UserHomeActivity.this, ConcertDetailActivity.class);
        intent.putExtra("selected_concert", concert);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        } else if (id == R.id.menu_booking_history) {
            startActivity(new Intent(this, BookingHistoryActivity.class));
            return true;
        } else if (id == R.id.menu_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
package com.example.projectwmp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.admin.BannerAdapter; // HARUS SESUAI PACKAGE
import com.example.projectwmp.models.Banner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Implementasi interface WAJIB
public class ManageBannersActivity extends AppCompatActivity implements BannerAdapter.BannerClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddBanner;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private FirebaseDatabase mDatabase;
    private List<Banner> bannerList;
    private BannerAdapter bannerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_banners);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Banners");
        }

        mDatabase = FirebaseDatabase.getInstance();

        // Inisialisasi Views (ID sesuai activity_manage_banners.xml)
        recyclerView = findViewById(R.id.recycler_banners);
        fabAddBanner = findViewById(R.id.fab_add_banner);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bannerList = new ArrayList<>();
        // Constructor Adapter
        bannerAdapter = new BannerAdapter(this, bannerList, this);
        recyclerView.setAdapter(bannerAdapter);

        fabAddBanner.setOnClickListener(v -> {
            Intent intent = new Intent(ManageBannersActivity.this, AddEditBannerActivity.class);
            startActivity(intent);
        });

        loadBanners();
    }

    private void loadBanners() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        mDatabase.getReference().child("banners").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                bannerList.clear();
                for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                    Banner banner = bannerSnapshot.getValue(Banner.class);
                    if (banner != null) {
                        banner.setBannerId(bannerSnapshot.getKey());
                        bannerList.add(banner);
                    }
                }

                Collections.sort(bannerList, (b1, b2) -> Integer.compare(b1.getPriority(), b2.getPriority()));

                if (bannerList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                bannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
                Toast.makeText(ManageBannersActivity.this, "Failed to load banners: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Implementasi method interface
    @Override
    public void onBannerClick(Banner banner) {
        Intent intent = new Intent(ManageBannersActivity.this, AddEditBannerActivity.class);
        intent.putExtra("banner", banner);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
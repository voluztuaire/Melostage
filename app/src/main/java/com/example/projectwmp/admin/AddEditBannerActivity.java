package com.example.projectwmp.admin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Banner;
import com.example.projectwmp.models.Concert;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddEditBannerActivity extends AppCompatActivity {

    private static final String TAG = "AddEditBannerActivity";

    private ImageView imgPreview;
    private EditText etBannerTitle, etDescription, etImageUrl, etPriority;
    private Spinner spinnerConcert;
    private CheckBox cbActive;
    private Button btnSave, btnPreviewImage;

    private DatabaseReference bannersRef, concertsRef;
    private ArrayList<Concert> concertList;
    private ArrayList<String> concertTitles;
    private ArrayAdapter<String> concertAdapter;
    private Concert selectedConcert;
    private String bannerId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_banner);

        // Initialize Firebase
        bannersRef = FirebaseDatabase.getInstance().getReference("banners");
        concertsRef = FirebaseDatabase.getInstance().getReference("concerts");

        // Initialize views
        initViews();

        // Check if edit mode
        checkEditMode();

        // Load concerts
        loadConcerts();

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        imgPreview = findViewById(R.id.imgPreview);
        etBannerTitle = findViewById(R.id.etBannerTitle);
        etDescription = findViewById(R.id.etDescription);
        etImageUrl = findViewById(R.id.etImageUrl);
        etPriority = findViewById(R.id.etPriority);
        spinnerConcert = findViewById(R.id.spinnerConcert);
        cbActive = findViewById(R.id.cbActive);
        btnSave = findViewById(R.id.btnSave);
        btnPreviewImage = findViewById(R.id.btnPreviewImage);

        // Set default priority
        etPriority.setText("1");
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("banner_id")) {
            isEditMode = true;
            bannerId = intent.getStringExtra("banner_id");
            setTitle("Edit Banner");
            loadBannerData();
        } else {
            setTitle("Add New Banner");
        }
    }

    private void loadConcerts() {
        concertList = new ArrayList<>();
        concertTitles = new ArrayList<>();

        concertTitles.add("No Concert Link");
        concertList.add(null);

        concertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Concert defaultOption = concertList.get(0);
                String defaultTitle = concertTitles.get(0);

                concertList.clear();
                concertTitles.clear();

                concertList.add(defaultOption);
                concertTitles.add(defaultTitle);

                for (DataSnapshot data : snapshot.getChildren()) {
                    Concert concert = data.getValue(Concert.class);
                    if (concert != null) {
                        concert.setId(data.getKey());
                        String displayTitle = concert.getTitle();

                        if (displayTitle != null && !displayTitle.isEmpty()
                                && !displayTitle.equals("Untitled Concert")) {
                            concertList.add(concert);
                            concertTitles.add(displayTitle);
                        }
                    }
                }

                setupSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditBannerActivity.this,
                        "Failed to load concerts: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                setupSpinner();
            }
        });
    }

    private void setupSpinner() {
        for (int i = concertTitles.size() - 1; i >= 0; i--) {
            if (concertTitles.get(i) == null) {
                concertTitles.remove(i);
                concertList.remove(i);
            }
        }

        concertAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                concertTitles
        );
        concertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConcert.setAdapter(concertAdapter);

        spinnerConcert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedConcert = null;
                } else {
                    selectedConcert = concertList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedConcert = null;
            }
        });
    }

    private void setupListeners() {
        // ✅ BUTTON PREVIEW IMAGE - MANUAL
        btnPreviewImage.setOnClickListener(v -> {
            String url = etImageUrl.getText().toString().trim();

            if (TextUtils.isEmpty(url)) {
                Toast.makeText(this, "Please enter image URL first", Toast.LENGTH_SHORT).show();
                etImageUrl.requestFocus();
                return;
            }

            // Convert & preview
            String convertedUrl = convertGoogleDriveUrl(url);
            Log.d(TAG, "Original URL: " + url);
            Log.d(TAG, "Converted URL: " + convertedUrl);

            etImageUrl.setText(convertedUrl);
            loadImagePreview(convertedUrl);
        });

        // Auto preview on blur
        etImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String url = etImageUrl.getText().toString().trim();
                if (!TextUtils.isEmpty(url)) {
                    String convertedUrl = convertGoogleDriveUrl(url);
                    etImageUrl.setText(convertedUrl);
                    loadImagePreview(convertedUrl);
                }
            }
        });

        // Save button
        btnSave.setOnClickListener(v -> saveBanner());
    }

    // ✅ IMPROVED: Converter dengan logging
    private String convertGoogleDriveUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        // Remove whitespace
        url = url.trim();

        if (url.contains("drive.google.com")) {
            String fileId = null;

            // Format: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
            if (url.contains("/file/d/")) {
                String[] parts = url.split("/file/d/");
                if (parts.length > 1) {
                    String afterFileD = parts[1];
                    if (afterFileD.contains("/")) {
                        fileId = afterFileD.split("/")[0];
                    } else {
                        fileId = afterFileD;
                    }
                }
            }
            // Format: https://drive.google.com/open?id=FILE_ID
            else if (url.contains("id=")) {
                String[] parts = url.split("id=");
                if (parts.length > 1) {
                    String afterId = parts[1];
                    if (afterId.contains("&")) {
                        fileId = afterId.split("&")[0];
                    } else {
                        fileId = afterId;
                    }
                }
            }

            if (fileId != null && !fileId.isEmpty()) {
                String convertedUrl = "https://drive.google.com/uc?export=view&id=" + fileId;
                Log.d(TAG, "Extracted File ID: " + fileId);
                Log.d(TAG, "Final URL: " + convertedUrl);
                return convertedUrl;
            } else {
                Log.e(TAG, "Failed to extract File ID from: " + url);
            }
        }

        return url;
    }

    // ✅ IMPROVED: Load image dengan better error handling
    private void loadImagePreview(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, "Image URL is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading image from: " + imageUrl);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Failed to load image: " + (e != null ? e.getMessage() : "Unknown error"));
                        Toast.makeText(AddEditBannerActivity.this,
                                "❌ Failed to load image. Check:\n1. Link is valid\n2. File is set to 'Anyone with link'",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully!");
                        Toast.makeText(AddEditBannerActivity.this,
                                "✅ Image loaded successfully!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .into(imgPreview);
    }

    private void loadBannerData() {
        bannersRef.child(bannerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Banner banner = snapshot.getValue(Banner.class);
                if (banner != null) {
                    etBannerTitle.setText(banner.getTitle());
                    etDescription.setText(banner.getDescription());
                    etImageUrl.setText(banner.getImageUrl());
                    etPriority.setText(String.valueOf(banner.getPriority()));
                    cbActive.setChecked(banner.isActive());

                    loadImagePreview(banner.getImageUrl());

                    if (banner.getConcertId() != null) {
                        setSpinnerSelection(banner.getConcertId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditBannerActivity.this,
                        "Failed to load banner: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection(String concertId) {
        for (int i = 0; i < concertList.size(); i++) {
            Concert concert = concertList.get(i);
            if (concert != null && concert.getId() != null && concert.getId().equals(concertId)) {
                spinnerConcert.setSelection(i);
                break;
            }
        }
    }

    private void saveBanner() {
        String title = etBannerTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String priorityStr = etPriority.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etBannerTitle.setError("Title is required");
            etBannerTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            etImageUrl.setError("Image URL is required");
            etImageUrl.requestFocus();
            return;
        }

        int priority = 1;
        if (!TextUtils.isEmpty(priorityStr)) {
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException e) {
                etPriority.setError("Invalid priority number");
                etPriority.requestFocus();
                return;
            }
        }

        String id = isEditMode ? bannerId : bannersRef.push().getKey();
        String concertId = selectedConcert != null ? selectedConcert.getId() : null;
        String concertTitle = selectedConcert != null ? selectedConcert.getTitle() : null;

        Banner banner = new Banner(
                id,
                title,
                description,
                imageUrl,
                concertId,
                concertTitle,
                priority,
                cbActive.isChecked(),
                System.currentTimeMillis()
        );

        if (id != null) {
            bannersRef.child(id).setValue(banner)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddEditBannerActivity.this,
                                isEditMode ? "Banner updated successfully" : "Banner added successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddEditBannerActivity.this,
                                "Failed to save banner: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
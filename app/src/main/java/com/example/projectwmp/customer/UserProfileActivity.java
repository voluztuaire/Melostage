package com.example.projectwmp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;
import com.example.projectwmp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhoneNumber, tvPreferredGenre;
    private Button btnEditProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvFullName = findViewById(R.id.tv_full_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvPreferredGenre = findViewById(R.id.tv_preferred_genre);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
            }
        });

        loadUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(); // Reload profile when returning from EditProfileActivity
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            tvFullName.setText("Name: " + user.getFullName());
                            tvEmail.setText("Email: " + user.getEmail());
                            tvPhoneNumber.setText("Phone: " + user.getPhoneNumber());
                            tvPreferredGenre.setText("Preferred Genre: " + user.getPreferredGenre());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
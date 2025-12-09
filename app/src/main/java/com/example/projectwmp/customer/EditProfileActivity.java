package com.example.projectwmp.customer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;
import com.example.projectwmp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPhoneNumber;
    private Spinner spinnerGenre;
    private Button btnSaveProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etFullName = findViewById(R.id.et_full_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        spinnerGenre = findViewById(R.id.spinner_genre);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Setup Spinner
        String[] genres = {"Pop", "Rock", "Jazz", "EDM", "Hip-Hop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);

        loadUserProfile();

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        etFullName.setText(user.getFullName());
                        etPhoneNumber.setText(user.getPhoneNumber());

                        // Set spinner selection
                        String[] genres = {"Pop", "Rock", "Jazz", "EDM", "Hip-Hop"};
                        for (int i = 0; i < genres.length; i++) {
                            if (genres[i].equals(user.getPreferredGenre())) {
                                spinnerGenre.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String preferredGenre = spinnerGenre.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone number is required");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Update only specific fields
        mDatabase.child("users").child(userId).child("fullName").setValue(fullName);
        mDatabase.child("users").child(userId).child("phoneNumber").setValue(phoneNumber);
        mDatabase.child("users").child(userId).child("preferredGenre").setValue(preferredGenre)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
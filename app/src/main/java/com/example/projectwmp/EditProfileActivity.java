package com.example.projectwmp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectwmp.R;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etEmail, etPhone, etPassword;
    private ImageView imgProfile;
    private Button btnChangePicture, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etEditName);
        etEmail = findViewById(R.id.etEditEmail);
        etPhone = findViewById(R.id.etEditPhone);
        etPassword = findViewById(R.id.etEditPassword);
        imgProfile = findViewById(R.id.imgEditProfile);
        btnChangePicture = findViewById(R.id.btnChangePicture);
        btnSave = findViewById(R.id.btnSaveChanges);

        // Pre-fill with mock data
        etName.setText("Hikaru Tanaka");
        etEmail.setText("hikaru.tanaka@example.com");
        etPhone.setText("+62 812 3456 7890");

        btnChangePicture.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle Image Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imgProfile.setImageURI(imageUri); // Show the selected image
        }
    }

    private void saveProfile() {
        // Validation Simulation
        String newName = etName.getText().toString();
        if (newName.isEmpty()) {
            etName.setError("Name cannot be empty");
            return;
        }

        // Logic: Since we don't have a database, we just simulate success.
        Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();

        // Return to previous screen
        finish();
    }
}
package com.example.projectwmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConcertAdapter concertAdapter;
    private List<Concert> concertList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_concerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        concertList = new ArrayList<>();
        concertAdapter = new ConcertAdapter(this, concertList, new ConcertAdapter.OnConcertClickListener() {
            @Override
            public void onConcertClick(Concert concert) {
                // Navigate to Concert Detail - KIRIM OBJEK CONCERT LANGSUNG
                Intent intent = new Intent(UserHomeActivity.this, ConcertDetailActivity.class);
                intent.putExtra("selected_concert", concert);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(concertAdapter);

        loadConcerts();
    }

    private void loadConcerts() {
        mDatabase.child("concerts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                concertList.clear();
                for (DataSnapshot concertSnapshot : snapshot.getChildren()) {
                    Concert concert = concertSnapshot.getValue(Concert.class);
                    if (concert != null && "Upcoming".equals(concert.getStatus())) {
                        concertList.add(concert);
                    }
                }
                concertAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserHomeActivity.this, "Failed to load concerts", Toast.LENGTH_SHORT).show();
            }
        });
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
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
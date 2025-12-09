package com.example.projectwmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private ConcertAdapter concertAdapter;
    private List<Concert> concertList;
    private FloatingActionButton fabAddConcert;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        recyclerView = findViewById(R.id.recycler_concerts_admin);
        fabAddConcert = findViewById(R.id.fab_add_concert);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        concertList = new ArrayList<>();
        concertAdapter = new ConcertAdapter(this, concertList, new ConcertAdapter.OnConcertClickListener() {
            @Override
            public void onConcertClick(Concert concert) {
                // Navigate to Edit Concert
                Intent intent = new Intent(AdminDashboardActivity.this, AddEditConcertActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("concertId", concert.getConcertId());
                intent.putExtra("artistName", concert.getArtistName());
                intent.putExtra("venue", concert.getVenue());
                intent.putExtra("date", concert.getDate());
                intent.putExtra("time", concert.getTime());
                intent.putExtra("price", concert.getPrice());
                intent.putExtra("status", concert.getStatus());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(concertAdapter);

        fabAddConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AddEditConcertActivity.class);
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });

        loadAllConcerts();
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
                Toast.makeText(AdminDashboardActivity.this, "Failed to load concerts", Toast.LENGTH_SHORT).show();
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
        loadAllConcerts();
    }
}
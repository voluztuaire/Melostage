package com.example.projectwmp.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Concert;
import java.util.List;

public class AdminConcertAdapter extends RecyclerView.Adapter<AdminConcertAdapter.AdminConcertViewHolder> {

    private final Context context;
    private final List<Concert> concertList;
    private final OnConcertClickListener listener;

    public interface OnConcertClickListener {
        void onConcertClick(Concert concert);
    }

    public AdminConcertAdapter(Context context, List<Concert> concertList, OnConcertClickListener listener) {
        this.context = context;
        this.concertList = concertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_concert, parent, false);
        return new AdminConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminConcertViewHolder holder, int position) {
        Concert concert = concertList.get(position);

        holder.tvArtistName.setText(concert.getArtist());
        holder.tvVenue.setText("Venue: " + concert.getVenue());
        holder.tvDate.setText("Date: " + concert.getDate());
        holder.tvPrice.setText("Rp " + String.format("%.0f", concert.getPrice()));
        holder.tvStatus.setText("Status: " + concert.getStatus());

        // Set status color
        int statusColor;
        switch (concert.getStatus()) {
            case "Upcoming":
                statusColor = 0xFF4CAF50; // Green
                break;
            case "Sold Out":
                statusColor = 0xFFFF9800; // Orange
                break;
            case "Cancelled":
                statusColor = 0xFFF44336; // Red
                break;
            default:
                statusColor = 0xFF9E9E9E; // Gray
                break;
        }
        holder.tvStatus.setTextColor(statusColor);

        // Click on card to view/edit
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConcertClick(concert);
            }
        });
    }

    @Override
    public int getItemCount() {
        return concertList.size();
    }

    public static class AdminConcertViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName, tvVenue, tvDate, tvPrice, tvStatus;
        CardView cardView;

        public AdminConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_concert);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvVenue = itemView.findViewById(R.id.tv_venue);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
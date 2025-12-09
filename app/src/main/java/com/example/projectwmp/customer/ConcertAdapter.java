package com.example.projectwmp.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Concert;
import java.util.List;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {

    private final Context context;
    private final List<Concert> concertList;
    private final OnConcertClickListener listener;

    public interface OnConcertClickListener {
        void onConcertClick(Concert concert);
    }

    public ConcertAdapter(Context context, List<Concert> concertList, OnConcertClickListener listener) {
        this.context = context;
        this.concertList = concertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_concert, parent, false);
        return new ConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, int position) {
        Concert concert = concertList.get(position);

        // Set text data
        holder.tvArtistName.setText(concert.getArtist());
        holder.tvVenue.setText(concert.getVenue());
        holder.tvDate.setText(concert.getDate());
        holder.tvPrice.setText("Rp " + String.format("%,.0f", concert.getPrice()));

        // Load concert image (jika ada ImageView di layout)
        if (holder.ivConcertImage != null) {
            String imageUrl = concert.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_concert)
                        .error(R.drawable.ic_placeholder_concert)
                        .centerCrop()
                        .into(holder.ivConcertImage);
            } else {
                holder.ivConcertImage.setImageResource(R.drawable.ic_placeholder_concert);
            }
        }

        // Click listener on entire card
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConcertClick(concert);
            }
        });

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConcertClick(concert);
            }
        });
    }

    @Override
    public int getItemCount() {
        return concertList != null ? concertList.size() : 0;
    }

    public static class ConcertViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivConcertImage;
        TextView tvArtistName, tvVenue, tvDate, tvPrice;

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_concert);
            ivConcertImage = itemView.findViewById(R.id.iv_concert_image); // Jika ada
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvVenue = itemView.findViewById(R.id.tv_venue);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
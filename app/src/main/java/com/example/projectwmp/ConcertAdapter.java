package com.example.projectwmp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {

    private Context context;
    private List<Concert> concertList;
    private OnConcertClickListener listener;

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

        holder.tvArtistName.setText(concert.getArtistName());
        holder.tvVenue.setText(concert.getVenue());
        holder.tvDate.setText(concert.getDate());
        holder.tvPrice.setText("Rp " + String.format("%.0f", concert.getPrice()));

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

    public static class ConcertViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName, tvVenue, tvDate, tvPrice;
        CardView cardView;

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_concert);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvVenue = itemView.findViewById(R.id.tv_venue);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
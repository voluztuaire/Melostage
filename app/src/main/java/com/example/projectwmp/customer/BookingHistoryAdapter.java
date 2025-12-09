package com.example.projectwmp.customer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat; // Import tambahan
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Booking;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onPayNow(Booking booking);
        void onViewTicket(Booking booking);
    }

    public BookingHistoryAdapter(Context context, List<Booking> bookingList, OnBookingActionListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan item_booking_history.xml yang sudah diperbarui
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Menggunakan string resources atau hardcoded string yang diterjemahkan
        holder.tvArtistName.setText(booking.getArtistName());
        holder.tvVenue.setText(String.format("Venue: %s", booking.getVenue()));
        holder.tvDate.setText(String.format("Date: %s", booking.getDate()));
        holder.tvTicketClass.setText(String.format("Class: %s", booking.getTicketClass()));
        holder.tvQuantity.setText(String.format("Qty: %d", booking.getQuantity()));

        // PENTING: Gunakan Locale Indonesia (ID) untuk format Rupiah
        Locale idLocale = new Locale("in", "ID");
        holder.tvTotalPrice.setText(String.format(idLocale, "Total: Rp %,.0f", booking.getTotalPrice()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String bookingDate = sdf.format(new Date(booking.getTimestamp()));
        // PENTING: Menerjemahkan teks 'Booked on'
        holder.tvBookingDate.setText(String.format("Booked on: %s", bookingDate));

        String status = booking.getPaymentStatus();

        // 🚨 PERBAIKAN: Menerjemahkan dan mengatur tampilan status
        if ("paid".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("✓ PAID"); // Diterjemahkan
            // Menggunakan ContextCompat untuk warna yang lebih aman (seharusnya menggunakan R.color.green atau sejenisnya)
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_paid));
            if (holder.btnPayNow != null) holder.btnPayNow.setVisibility(View.GONE);
            if (holder.btnViewTicket != null) holder.btnViewTicket.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("⏳ PENDING"); // Diterjemahkan
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orange_pending));
            if (holder.btnPayNow != null) holder.btnPayNow.setVisibility(View.VISIBLE);
            if (holder.btnViewTicket != null) holder.btnViewTicket.setVisibility(View.GONE);
        }

        // PENTING: Menangani potensi error klik pada CardView
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                if ("paid".equalsIgnoreCase(status)) {
                    listener.onViewTicket(booking);
                } else {
                    listener.onPayNow(booking);
                }
            }
        });

        // Event listener untuk tombol Pay Now (sekarang sudah View Ticket)
        if (holder.btnPayNow != null) {
            holder.btnPayNow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPayNow(booking);
                }
            });
        }

        // Event listener untuk tombol View Ticket (sekarang sudah Lihat Tiket)
        if (holder.btnViewTicket != null) {
            holder.btnViewTicket.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewTicket(booking);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName, tvVenue, tvDate, tvTicketClass, tvQuantity, tvTotalPrice, tvBookingDate, tvStatus;
        Button btnPayNow, btnViewTicket;
        CardView cardView;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_booking);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvVenue = itemView.findViewById(R.id.tv_venue);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTicketClass = itemView.findViewById(R.id.tv_ticket_class);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnPayNow = itemView.findViewById(R.id.btn_pay_now);
            btnViewTicket = itemView.findViewById(R.id.btn_view_ticket);
        }
    }
}
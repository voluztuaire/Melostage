package com.example.projectwmp.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Booking;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onUpdatePaymentStatus(Booking booking);
        void onContactCustomer(Booking booking);
        void onViewDetails(Booking booking);
    }

    public AdminBookingAdapter(Context context, List<Booking> bookingList, OnBookingActionListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Customer info
        holder.tvCustomerName.setText(booking.getUserName() != null ? booking.getUserName() : "Loading...");
        holder.tvCustomerPhone.setText(booking.getUserPhone() != null ? booking.getUserPhone() : "");

        // Concert info
        holder.tvArtistName.setText(booking.getArtistName());
        holder.tvVenue.setText("📍 " + booking.getVenue());
        holder.tvDate.setText("📅 " + booking.getDate());

        // Booking info
        holder.tvBookingId.setText("ID: " + booking.getBookingId());
        holder.tvTicketClass.setText("🎫 " + booking.getTicketClass() + " × " + booking.getQuantity());
        holder.tvTotalPrice.setText("💰 Rp " + String.format(Locale.getDefault(), "%,.0f", booking.getTotalPrice()));
        holder.tvPaymentMethod.setText("💳 " + booking.getPaymentMethod());

        // Timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String bookingTime = sdf.format(new Date(booking.getTimestamp()));
        holder.tvTimestamp.setText("⏰ " + bookingTime);

        // Payment status (Menggunakan getPaymentStatus() yang sekarang adalah satu-satunya status)
        String status = booking.getPaymentStatus();
        if ("paid".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("✓ PAID");
            holder.tvStatus.setTextColor(0xFF4CAF50);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid);
            holder.btnUpdateStatus.setText("Mark as Pending");
        } else {
            holder.tvStatus.setText("⏳ PENDING");
            holder.tvStatus.setTextColor(0xFFFF9800);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            holder.btnUpdateStatus.setText("Mark as Paid");
        }

        // Button listeners
        holder.btnUpdateStatus.setOnClickListener(v -> {
            if (listener != null) {
                // Ini akan memicu update paymentStatus di Admin Activity
                listener.onUpdatePaymentStatus(booking);
            }
        });

        holder.btnContact.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactCustomer(booking);
            }
        });

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCustomerName, tvCustomerPhone, tvArtistName, tvVenue, tvDate;
        TextView tvBookingId, tvTicketClass, tvTotalPrice, tvPaymentMethod, tvTimestamp, tvStatus;
        Button btnUpdateStatus, btnContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_booking);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerPhone = itemView.findViewById(R.id.tv_customer_phone);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvVenue = itemView.findViewById(R.id.tv_venue);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvBookingId = itemView.findViewById(R.id.tv_booking_id);
            tvTicketClass = itemView.findViewById(R.id.tv_ticket_class);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnUpdateStatus = itemView.findViewById(R.id.btn_update_status);
            btnContact = itemView.findViewById(R.id.btn_contact);
        }
    }
}
package com.example.projectwmp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Booking implements Parcelable {
    private String bookingId;
    private String userId;
    private String userName;
    private String userPhone;
    private String concertId;
    private String concertTitle;
    private String artistName;
    private String venue;
    private String date;
    private String time;
    private String ticketClass;
    private int quantity;
    private double totalPrice;
    private String paymentMethod;
    private String paymentStatus; // STATUS UTAMA YANG DIGUNAKAN
    private long timestamp;

    // Default constructor required for Firebase
    public Booking() {
    }

    // Full constructor (field 'status' dihilangkan)
    public Booking(String bookingId, String userId, String userName, String userPhone,
                   String concertId, String artistName, String venue, String date, String time,
                   String ticketClass, int quantity, double totalPrice, String paymentMethod,
                   String paymentStatus, long timestamp) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.concertId = concertId;
        this.artistName = artistName;
        this.venue = venue;
        this.date = date;
        this.time = time;
        this.ticketClass = ticketClass;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.timestamp = timestamp;

        this.concertTitle = artistName; // Default
    }

    // Getters
    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName != null ? userName : "";
    }

    public String getUserPhone() {
        return userPhone != null ? userPhone : "";
    }

    public String getConcertId() {
        return concertId;
    }

    public String getConcertTitle() {
        if (concertTitle != null && !concertTitle.isEmpty()) {
            return concertTitle;
        }
        return artistName != null ? artistName : "Unknown Concert";
    }

    public String getArtistName() {
        return artistName != null ? artistName : "";
    }

    public String getVenue() {
        return venue != null ? venue : "";
    }

    public String getDate() {
        return date != null ? date : "";
    }

    public String getTime() {
        return time != null ? time : "";
    }

    public String getTicketClass() {
        return ticketClass != null ? ticketClass : "";
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod != null ? paymentMethod : "";
    }

    public String getPaymentStatus() {
        return paymentStatus != null ? paymentStatus : "Pending";
    }

    // 🚨 Method getStatus() yang redundant DIHAPUS. Gunakan getPaymentStatus() di semua tempat.

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setConcertId(String concertId) {
        this.concertId = concertId;
    }

    public void setConcertTitle(String concertTitle) {
        this.concertTitle = concertTitle;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTicketClass(String ticketClass) {
        this.ticketClass = ticketClass;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // 🚨 Method setStatus() yang redundant DIHAPUS.

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("Rp %,.0f", totalPrice);
    }

    public String getFormattedTimestamp() {
        return new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm",
                java.util.Locale.getDefault()).format(new java.util.Date(timestamp));
    }

    // ========== PARCELABLE IMPLEMENTATION ==========

    protected Booking(Parcel in) {
        bookingId = in.readString();
        userId = in.readString();
        userName = in.readString();
        userPhone = in.readString();
        concertId = in.readString();
        concertTitle = in.readString();
        artistName = in.readString();
        venue = in.readString();
        date = in.readString();
        time = in.readString();
        ticketClass = in.readString();
        quantity = in.readInt();
        totalPrice = in.readDouble();
        paymentMethod = in.readString();
        paymentStatus = in.readString();
        // 🚨 Field status DIHAPUS dari parcelable
        timestamp = in.readLong();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userPhone);
        dest.writeString(concertId);
        dest.writeString(concertTitle);
        dest.writeString(artistName);
        dest.writeString(venue);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(ticketClass);
        dest.writeInt(quantity);
        dest.writeDouble(totalPrice);
        dest.writeString(paymentMethod);
        dest.writeString(paymentStatus);
        // 🚨 Field status DIHAPUS dari parcelable
        dest.writeLong(timestamp);
    }
}
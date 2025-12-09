package com.example.projectwmp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Concert implements Parcelable {
    private String concertId;
    private String title;
    private String artistName;
    private String venue;
    private String imageUrl;
    private String description;
    private String date;
    private String time;
    private double price;
    private String status;
    private String genre;
    private int availableTickets;
    private boolean featured;
    private long createdAt;

    // Empty constructor for Firebase
    public Concert() {
    }

    // Constructor (11 parameters)
    public Concert(String concertId, String title, String artistName, String venue,
                   String imageUrl, String description, String date, String time,
                   double price, String status, String genre) {
        this.concertId = concertId;
        this.title = title;
        this.artistName = artistName;
        this.venue = venue;
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
        this.time = time;
        this.price = price;
        this.status = status;
        this.genre = genre;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getConcertId() { return concertId; }

    public String getTitle() {
        if (title != null && !title.isEmpty() && !title.equals("Untitled Concert")) {
            return title;
        }
        return artistName != null && !artistName.isEmpty() ? artistName : "Untitled Concert";
    }

    public String getArtistName() { return artistName != null ? artistName : ""; }
    public String getArtist() { return getArtistName(); }
    public String getVenue() { return venue != null ? venue : ""; }
    public String getImageUrl() { return imageUrl != null ? imageUrl : ""; }
    public String getDescription() { return description != null ? description : ""; }
    public String getDate() { return date != null ? date : ""; }
    public String getTime() { return time != null ? time : ""; }
    public double getPrice() { return price; }
    public String getStatus() { return status != null ? status : "Upcoming"; }
    public String getGenre() { return genre != null ? genre : ""; }
    public int getAvailableTickets() { return availableTickets; }
    public boolean isFeatured() { return featured; }
    public long getCreatedAt() { return createdAt; }
    public String getId() { return getConcertId(); }

    // Setters
    public void setConcertId(String concertId) { this.concertId = concertId; }
    public void setId(String id) { this.concertId = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Helper methods
    @Override
    public String toString() { return getTitle(); }

    public String getFormattedPrice() { return String.format("Rp %,.0f", price); }
    public boolean hasAvailableTickets() { return availableTickets > 0; }
    public String getDateTime() { return date + " at " + time; }

    // ========== PARCELABLE IMPLEMENTATION ==========

    protected Concert(Parcel in) {
        concertId = in.readString();
        title = in.readString();
        artistName = in.readString();
        venue = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        date = in.readString();
        time = in.readString();
        price = in.readDouble();
        status = in.readString();
        genre = in.readString();
        availableTickets = in.readInt();
        featured = in.readByte() != 0;
        createdAt = in.readLong();
    }

    public static final Creator<Concert> CREATOR = new Creator<Concert>() {
        @Override
        public Concert createFromParcel(Parcel in) {
            return new Concert(in);
        }

        @Override
        public Concert[] newArray(int size) {
            return new Concert[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(concertId);
        dest.writeString(title);
        dest.writeString(artistName);
        dest.writeString(venue);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeDouble(price);
        dest.writeString(status);
        dest.writeString(genre);
        dest.writeInt(availableTickets);
        dest.writeByte((byte) (featured ? 1 : 0));
        dest.writeLong(createdAt);
    }
}
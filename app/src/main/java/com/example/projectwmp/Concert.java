package com.example.projectwmp;
import java.io.Serializable;
public class Concert implements Serializable{
    private String concertId;
    private String artistName;
    private String venue;
    private String date;
    private String time;
    private double price;
    private String status; // Upcoming, Sold Out, Cancelled
    private String imageUrl;

    public Concert() {
        // Default constructor required for Firebase
    }

    public Concert(String concertId, String artistName, String venue, String date, String time, double price, String status, String imageUrl) {
        this.concertId = concertId;
        this.artistName = artistName;
        this.venue = venue;
        this.date = date;
        this.time = time;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getConcertId() {
        return concertId;
    }

    public void setConcertId(String concertId) {
        this.concertId = concertId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
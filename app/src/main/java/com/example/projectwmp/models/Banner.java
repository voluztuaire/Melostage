package com.example.projectwmp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Banner implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String concertId;
    private String concertTitle;
    private int priority;
    private boolean active;
    private long createdAt;

    // Empty constructor for Firebase
    public Banner() {}

    // Constructor dengan 9 parameter (sesuai AddEditBannerActivity)
    public Banner(String id, String title, String description, String imageUrl,
                  String concertId, String concertTitle, int priority,
                  boolean active, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.concertId = concertId;
        this.concertTitle = concertTitle;
        this.priority = priority;
        this.active = active;
        this.createdAt = createdAt;
    }

    // GETTERS
    public String getId() {
        return id;
    }

    public String getBannerId() {
        return id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    // ✅ Method yang missing - getFullImageUrl()
    public String getFullImageUrl() {
        return getImageUrl();
    }

    public String getConcertId() {
        return concertId;
    }

    public String getConcertTitle() {
        return concertTitle;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isActive() {
        return active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // SETTERS (Wajib untuk Firebase)
    public void setId(String id) {
        this.id = id;
    }

    public void setBannerId(String bannerId) {
        this.id = bannerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setConcertId(String concertId) {
        this.concertId = concertId;
    }

    public void setConcertTitle(String concertTitle) {
        this.concertTitle = concertTitle;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // --- Implementasi Parcelable ---
    protected Banner(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        concertId = in.readString();
        concertTitle = in.readString();
        priority = in.readInt();
        active = in.readByte() != 0;
        createdAt = in.readLong();
    }

    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(concertId);
        dest.writeString(concertTitle);
        dest.writeInt(priority);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeLong(createdAt);
    }
}
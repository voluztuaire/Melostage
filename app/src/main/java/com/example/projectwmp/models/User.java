package com.example.projectwmp.models;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String preferredGenre;
    private boolean admin;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String uid, String fullName, String email, String phoneNumber, String preferredGenre, boolean admin) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.preferredGenre = preferredGenre;
        this.admin = admin;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPreferredGenre() {
        return preferredGenre;
    }

    public void setPreferredGenre(String preferredGenre) {
        this.preferredGenre = preferredGenre;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
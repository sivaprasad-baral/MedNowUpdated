package com.example.mednow.model;

public class User {

    private String email,phone,name,userId,profileImg;
    private Double latitude,longitude;
    private int walletBalance;

    public User() {
    }

    public User(String email, String name, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public User(String email, String phone, String name, String userId, int walletBalance) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.userId = userId;
        this.walletBalance = walletBalance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(int walletBalance) {
        this.walletBalance = walletBalance;
    }
}
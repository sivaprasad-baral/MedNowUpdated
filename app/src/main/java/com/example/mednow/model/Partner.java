package com.example.mednow.model;

public class Partner {

    private String email,name,phone,userId,profileImg;
    private String licenseNo,pharmacyName,pharmacyImg;
    private Double latitude,longitude,rating;
    private boolean available;

    public Partner() {
    }

    public Partner(String email, String name, String phone, String userId, String profileImg, String licenseNo, String pharmacyName, String pharmacyImg, Double latitude, Double longitude, Double rating, boolean available) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.userId = userId;
        this.profileImg = profileImg;
        this.licenseNo = licenseNo;
        this.pharmacyName = pharmacyName;
        this.pharmacyImg = pharmacyImg;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.available = available;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public String getPharmacyImg() {
        return pharmacyImg;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getRating() {
        return rating;
    }

    public boolean isAvailable() {
        return available;
    }
}

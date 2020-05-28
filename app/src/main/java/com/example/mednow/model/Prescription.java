package com.example.mednow.model;

public class Prescription {

    private String userId,partnerId,prescriptionImg,prescriptionId;

    public Prescription() {
    }

    public Prescription(String userId, String partnerId, String prescriptionImg, String prescriptionId) {
        this.userId = userId;
        this.partnerId = partnerId;
        this.prescriptionImg = prescriptionImg;
        this.prescriptionId = prescriptionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getPrescriptionImg() {
        return prescriptionImg;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }
}

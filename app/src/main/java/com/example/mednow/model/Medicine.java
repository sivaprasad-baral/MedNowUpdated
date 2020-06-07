package com.example.mednow.model;

public class Medicine {

    private String medName,medPrice,medId;
    private int medQty;

    public Medicine() {
    }

    public Medicine(String medName, String medPrice, String medId, int medQty) {
        this.medName = medName;
        this.medPrice = medPrice;
        this.medId = medId;
        this.medQty = medQty;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getMedPrice() {
        return medPrice;
    }

    public void setMedPrice(String medPrice) {
        this.medPrice = medPrice;
    }

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public int getMedQty() {
        return medQty;
    }

    public void setMedQty(int medQty) {
        this.medQty = medQty;
    }
}

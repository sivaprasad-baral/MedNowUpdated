package com.example.mednow.model;

import java.util.Map;

public class Order {

    private String partnerId,customerId,orderId,prescriptionImg;
    private long orderTimeStamp,prescriptionTimeStamp;
    private double cost;
    private Map<String,Medicine> medicines;
    private int orderStatus;
    private boolean paid;
    private String otpDelivery;

    public static final int ORDER_SENT = 0;
    public static final int ORDER_TIMED_OUT = 1;
    public static final int ORDER_PROCESSING = 2;
    public static final int ORDER_REJECTED_BY_PARTNER = 3;
    public static final int ORDER_REJECTED_BY_CUSTOMER = 4;
    public static final int ORDER_SENT_FOR_CONFIRMATION = 5;
    public static final int ORDER_CONFIRMED_BY_CUSTOMER = 6;
    public static final int ORDER_OUT_FOR_DELIVERY = 7;
    public static final int ORDER_DELIVERED = 8;
    public static final int ORDER_CANCELLED = 9;
    public static final int ORDER_RETURNED = 10;

    public Order() {
    }

    public Order(String partnerId, String customerId, String orderId, String prescriptionImg, long prescriptionTimeStamp) {
        this.partnerId = partnerId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.prescriptionImg = prescriptionImg;
        this.prescriptionTimeStamp = prescriptionTimeStamp;
        this.orderStatus = ORDER_SENT;
        this.paid = false;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPrescriptionImg() {
        return prescriptionImg;
    }

    public long getOrderTimeStamp() {
        return orderTimeStamp;
    }

    public long getPrescriptionTimeStamp() {
        return prescriptionTimeStamp;
    }

    public double getCost() {
        return cost;
    }

    public Map<String, Medicine> getMedicines() {
        return medicines;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getOtpDelivery() {
        return otpDelivery;
    }
}

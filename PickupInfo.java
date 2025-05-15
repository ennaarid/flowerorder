package com.example.flowermanagementsystem;

import java.time.LocalDate;

public class PickupInfo {
    private String customerName;
    private String phoneNumber;
    private String email;
    private LocalDate pickupDate;

    public PickupInfo(String customerName, String phoneNumber, String email, LocalDate pickupDate) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pickupDate = pickupDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }


    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }
}

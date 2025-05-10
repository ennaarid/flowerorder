package com.example.flowermanagementsystem;

import java.time.LocalDate;

/**
 * Represents pickup information for an order.
 */
public class PickupInfo {
    private String customerName;
    private String phoneNumber;
    private String email;
    private LocalDate pickupDate;

    /**
     * Creates a new PickupInfo instance.
     *
     * @param customerName The customer's name
     * @param phoneNumber The customer's phone number
     * @param email The customer's email address
     * @param pickupDate The estimated date of pickup
     */
    public PickupInfo(String customerName, String phoneNumber, String email, LocalDate pickupDate) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pickupDate = pickupDate;
    }

    /**
     * Gets the customer's name.
     *
     * @return The customer's name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Gets the customer's phone number.
     *
     * @return The customer's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the customer's email address.
     *
     * @return The customer's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the estimated date of pickup.
     *
     * @return The estimated date of pickup
     */
    public LocalDate getPickupDate() {
        return pickupDate;
    }

    /**
     * Sets the customer's name.
     *
     * @param customerName The customer's name
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Sets the customer's phone number.
     *
     * @param phoneNumber The customer's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email The customer's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the estimated date of pickup.
     *
     * @param pickupDate The estimated date of pickup
     */
    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }
}
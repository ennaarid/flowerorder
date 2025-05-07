package com.example.final_flowerorderingsystem;

public class OrderData {
    private String receiptId;
    private String user;
    private int numberOfItems;
    private double amount;
    private String status;
    private String dateOrdered;
    private String pickupDate;

    public OrderData(String receiptId, String user, int numberOfItems, double amount, 
                    String status, String dateOrdered, String pickupDate) {
        this.receiptId = receiptId;
        this.user = user;
        this.numberOfItems = numberOfItems;
        this.amount = amount;
        this.status = status;
        this.dateOrdered = dateOrdered;
        this.pickupDate = pickupDate;
    }

    // Getters
    public String getReceiptId() {
        return receiptId;
    }

    public String getUser() {
        return user;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    // Setters
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }
} 
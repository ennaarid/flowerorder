package com.example.flowermanagementsystem;

import java.time.LocalDate;

public class InventoryData {
    private String productID;
    private String productName;
    private String season;
    private double price;
    private String status;
    private int stock;
    private String date;
    private String description;
    private String imagePath;

    public InventoryData(String productID, String productName, String season, double price, String status, int stock, String date, String description, String imagePath) {
        this.productID = productID;
        this.productName = productName;
        this.season = season;
        this.price = price;
        this.status = status;
        this.stock = stock;
        this.date = date;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Getters
    public String getProductID() { return productID; }
    public String getProductName() { return productName; }
    public String getSeason() { return season; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public int getStock() { return stock; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }

    // Setters
    public void setProductID(String productID) { this.productID = productID; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setSeason(String season) { this.season = season; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setStock(int stock) { this.stock = stock; }
    public void setDate(String date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
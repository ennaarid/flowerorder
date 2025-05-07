package com.flowerorder;

import java.time.LocalDate;

public class Inventory {
    private int productId;
    private String productName;
    private String season;
    private double price;
    private String status;
    private int stock;
    private LocalDate date;
    private String description;
    private String imagePath;

    public Inventory(int productId, String productName, String season, double price, 
                    String status, int stock, LocalDate date, String description, String imagePath) {
        this.productId = productId;
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
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSeason() { return season; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public int getStock() { return stock; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }

    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setSeason(String season) { this.season = season; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setStock(int stock) { this.stock = stock; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
} 
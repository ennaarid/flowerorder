package com.example.flowermanagementsystem;

public class CartItem {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private String imagePath;

    public CartItem(String productId, String productName, double price, int quantity, String imagePath) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getTotal() {
        return price * quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
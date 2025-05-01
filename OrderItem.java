package com.example.cart;

public class OrderItem {
    private Flower flower;
    private int quantity;

    public OrderItem(Flower flower, int quantity) {
        this.flower = flower;
        this.quantity = quantity;
    }

    public Flower getFlower() {
        return flower;
    }

    public int getQuantity() {
        return quantity;
    }
}
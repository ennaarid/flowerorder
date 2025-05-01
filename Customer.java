package com.example.cart;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private List<OrderItem> cart = new ArrayList<>();
    private String username; // Added a basic attribute

    public Customer(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public List<OrderItem> getCart() {
        return cart;
    }

    public void addToCart(Flower flower, int quantity) {
        OrderItem orderItem = new OrderItem(flower, quantity);
        cart.add(orderItem);
        System.out.println(quantity + " " + flower.getName() + "(s) added to your cart.");
    }

    public void removeFromCart(Flower flower) {
        cart.removeIf(item -> item.getFlower().equals(flower));
        System.out.println(flower.getName() + " removed from your cart.");
    }

    public Order checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty. Cannot checkout.");
            return null;
        }
        // For now, just create a dummy Order
        Order order = new Order("TEMP_ORDER_ID", this, new ArrayList<>(cart));
        cart.clear();
        System.out.println("Checkout successful (placeholder). Order ID: " + order.getOrderId());
        return order;
    }
}
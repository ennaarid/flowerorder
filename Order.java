package com.example.cart;

import java.util.List;

public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> items;

    public Order(String orderId, Customer customer, List<OrderItem> items) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Customer getCustomer() {
        return customer;
    }
}
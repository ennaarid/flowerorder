package com.example.flowermanagementsystem;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> items;
    private LocalDateTime orderDate;
    private String status;
    private String paymentMethod;
    private ShippingInfo shippingInfo;
    private PickupInfo pickupInfo;
    private boolean isPickup;

    public Order(String orderId, Customer customer, List<OrderItem> items) {
        this(orderId, customer, items, LocalDateTime.now(), "Pending", "Cash on Delivery", (ShippingInfo) null);
    }

    public Order(String orderId, Customer customer, List<OrderItem> items, 
                 LocalDateTime orderDate, String status, String paymentMethod, 
                 ShippingInfo shippingInfo) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.shippingInfo = shippingInfo;
        this.isPickup = false;
    }

    public Order(String orderId, Customer customer, List<OrderItem> items, 
                 LocalDateTime orderDate, String status, String paymentMethod, 
                 PickupInfo pickupInfo) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.pickupInfo = pickupInfo;
        this.isPickup = true;
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public PickupInfo getPickupInfo() {
        return pickupInfo;
    }

    public boolean isPickup() {
        return isPickup;
    }

    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getFlower().getPrice() * item.getQuantity();
        }
        return total;
    }
}

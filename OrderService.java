package com.example.flowermanagementsystem;

import java.util.List;

public interface OrderService {

    List<OrderData> loadOrders();
    boolean updateOrderStatus(String orderId, String status);

    boolean deleteOrder(String orderId);
    String createOrder(String customerId, int itemCount, double totalAmount, String status, String orderDate);
}
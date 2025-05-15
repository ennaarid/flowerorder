package com.example.flowermanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the OrderService interface.
 */
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderServiceImpl.class.getName());
    
    // Singleton instance
    private static OrderServiceImpl instance;
    
    /**
     * Gets the singleton instance of OrderServiceImpl.
     * 
     * @return The singleton instance
     */
    public static synchronized OrderServiceImpl getInstance() {
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
    }
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private OrderServiceImpl() {
        // Initialize if needed
    }

    @Override
    public List<OrderData> loadOrders() {
        List<OrderData> orderList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while loading orders");
                return orderList;
            }
            
            String query = "SELECT * FROM `order`";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String receiptId = rs.getString("order_id");
                String user = rs.getString("customer_id");
                int numberOfItems = rs.getInt("item_count");
                double amount = rs.getDouble("total_amount");
                String status = rs.getString("status");
                String dateOrdered = rs.getString("order_date");
                String pickupDate = "Not scheduled"; // Default value since pickup_date is not in the database
                
                OrderData order = new OrderData(receiptId, user, numberOfItems, amount, status, dateOrdered, pickupDate);
                orderList.add(order);
            }
            
            LOGGER.log(Level.INFO, "Loaded " + orderList.size() + " orders");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading orders", e);
        }
        
        return orderList;
    }

    @Override
    public boolean updateOrderStatus(String orderId, String status) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while updating order status");
                return false;
            }
            
            String sql = "UPDATE `order` SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            
            LOGGER.log(Level.INFO, "Updated order status: " + orderId + " to " + status);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating order status", e);
            return false;
        }
    }

    @Override
    public boolean deleteOrder(String orderId) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while deleting order");
                return false;
            }
            
            String sql = "DELETE FROM `order` WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            
            int rowsAffected = stmt.executeUpdate();
            
            LOGGER.log(Level.INFO, "Deleted order: " + orderId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting order", e);
            return false;
        }
    }

    @Override
    public String createOrder(String customerId, int itemCount, double totalAmount, String status, String orderDate) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while creating order");
                return null;
            }
            
            // Generate a unique order ID
            String orderId = UUID.randomUUID().toString();
            
            String sql = "INSERT INTO `order` (order_id, customer_id, item_count, total_amount, status, order_date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            stmt.setString(2, customerId);
            stmt.setInt(3, itemCount);
            stmt.setDouble(4, totalAmount);
            stmt.setString(5, status);
            stmt.setString(6, orderDate);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Created order: " + orderId);
                return orderId;
            } else {
                LOGGER.log(Level.WARNING, "Failed to create order");
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while creating order", e);
            return null;
        }
    }
}
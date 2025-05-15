package com.example.flowermanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the InventoryService interface.
 */
public class InventoryServiceImpl implements InventoryService {

    private static final Logger LOGGER = Logger.getLogger(InventoryServiceImpl.class.getName());
    
    // Singleton instance
    private static InventoryServiceImpl instance;
    
    /**
     * Gets the singleton instance of InventoryServiceImpl.
     * 
     * @return The singleton instance
     */
    public static synchronized InventoryServiceImpl getInstance() {
        if (instance == null) {
            instance = new InventoryServiceImpl();
        }
        return instance;
    }
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private InventoryServiceImpl() {
        // Initialize if needed
    }

    @Override
    public List<InventoryData> loadInventoryData() {
        List<InventoryData> inventoryList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while loading inventory data");
                return inventoryList;
            }
            
            String query = "SELECT * FROM flower";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String productId = rs.getString("flowerId");
                String productName = rs.getString("name");
                String season = rs.getString("season");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String description = rs.getString("description");
                String imagePath = rs.getString("image");
                
                // Use current date if date field doesn't exist in database
                String date = java.time.LocalDate.now().toString();
                
                // Add to inventory list
                inventoryList.add(new InventoryData(
                    productId,
                    productName,
                    season,
                    price,
                    null, // Status is no longer used
                    stock,
                    date,
                    description,
                    imagePath
                ));
            }
            
            rs.close();
            pst.close();
            
            LOGGER.log(Level.INFO, "Loaded " + inventoryList.size() + " inventory items");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading inventory data", e);
        }
        
        return inventoryList;
    }

    @Override
    public boolean addInventory(String productId, String productName, String season, double price, int stock, String imagePath, String description) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while adding inventory");
                return false;
            }
            
            String sql = "INSERT INTO flower (flowerId, name, season, price, stock, image, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productId);
            stmt.setString(2, productName);
            stmt.setString(3, season);
            stmt.setDouble(4, price);
            stmt.setInt(5, stock);
            stmt.setString(6, imagePath);
            stmt.setString(7, description);
            
            int rowsAffected = stmt.executeUpdate();
            
            LOGGER.log(Level.INFO, "Added inventory item: " + productId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while adding inventory", e);
            return false;
        }
    }

    @Override
    public boolean updateInventory(String productId, String productName, String season, double price, int stock, String imagePath, String description) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while updating inventory");
                return false;
            }
            
            String sql = "UPDATE flower SET name=?, season=?, price=?, stock=?, image=?, description=? WHERE flowerId=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productName);
            stmt.setString(2, season);
            stmt.setDouble(3, price);
            stmt.setInt(4, stock);
            stmt.setString(5, imagePath);
            stmt.setString(6, description);
            stmt.setString(7, productId);
            
            int rowsAffected = stmt.executeUpdate();
            
            LOGGER.log(Level.INFO, "Updated inventory item: " + productId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating inventory", e);
            return false;
        }
    }

    @Override
    public boolean deleteInventory(String productId) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while deleting inventory");
                return false;
            }
            
            String sql = "DELETE FROM flower WHERE flowerId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productId);
            
            int rowsAffected = stmt.executeUpdate();
            
            LOGGER.log(Level.INFO, "Deleted inventory item: " + productId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting inventory", e);
            return false;
        }
    }
}
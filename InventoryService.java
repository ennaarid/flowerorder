package com.example.flowermanagementsystem;

import java.util.List;

public interface InventoryService {
    List<InventoryData> loadInventoryData();
    

    boolean addInventory(String productId, String productName, String season, double price, int stock, String imagePath, String description);

    boolean updateInventory(String productId, String productName, String season, double price, int stock, String imagePath, String description);
    
    boolean deleteInventory(String productId);
}
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
 * Implementation of the CartService interface that persists cart data in the database.
 * This replaces the temporary static cart solution in ItemCardController.
 */
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = Logger.getLogger(CartServiceImpl.class.getName());

    // Session ID for the current cart (using a constant value for consistency across sessions)
    private static final String SESSION_ID = "default_session";

    // Singleton instance
    private static CartServiceImpl instance;

    /**
     * Gets the singleton instance of CartServiceImpl.
     * 
     * @return The singleton instance
     */
    public static synchronized CartServiceImpl getInstance() {
        if (instance == null) {
            instance = new CartServiceImpl();
        }
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern.
     */
    private CartServiceImpl() {
        // Initialize the cart in the database if it doesn't exist
        initializeCart();
    }

    /**
     * Initializes the cart in the database if it doesn't exist.
     */
    private void initializeCart() {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during cart initialization");
                return;
            }

            // Check if cart exists
            String checkCartSql = "SELECT cartId FROM Cart WHERE customerId = ?";
            PreparedStatement checkCartStmt = conn.prepareStatement(checkCartSql);
            checkCartStmt.setString(1, SESSION_ID);
            ResultSet cartResult = checkCartStmt.executeQuery();

            if (!cartResult.next()) {
                // Create new cart
                String createCartSql = "INSERT INTO Cart (customerId) VALUES (?)";
                PreparedStatement createCartStmt = conn.prepareStatement(createCartSql);
                createCartStmt.setString(1, SESSION_ID);
                createCartStmt.executeUpdate();
                LOGGER.log(Level.INFO, "Created new cart for session: " + SESSION_ID);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing cart", e);
        }
    }

    /**
     * Gets the cart ID for the current session.
     * 
     * @return The cart ID
     * @throws SQLException If a database error occurs
     */
    private int getCartId() throws SQLException {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while getting cart ID");
                throw new SQLException("Database connection failed");
            }

            String sql = "SELECT cartId FROM Cart WHERE customerId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, SESSION_ID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("cartId");
            } else {
                // This should not happen if initializeCart was called
                throw new SQLException("Cart not found for session: " + SESSION_ID);
            }
        }
    }

    @Override
    public CartItem addToCart(String productId, String productName, double price, int quantity, String imagePath) {
        System.out.println("Adding to cart: " + quantity + " x " + productName + " (ID: " + productId + ", Price: " + price + ")");

        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while adding to cart");
                System.err.println("Failed to connect to database while adding to cart");
                return null;
            }

            int cartId = getCartId();
            System.out.println("Using cart ID: " + cartId);

            int productIdInt;

            try {
                productIdInt = Integer.parseInt(productId);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid product ID format: " + productId, e);
                System.err.println("Invalid product ID format: " + productId);
                return null;
            }

            // Check if item already exists in cart
            String checkItemSql = "SELECT itemId, quantity FROM CartItem WHERE cartId = ? AND itemType = 'Flower' AND itemRefId = ?";
            PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql);
            checkItemStmt.setInt(1, cartId);
            checkItemStmt.setInt(2, productIdInt);

            System.out.println("Checking if item exists: " + checkItemSql.replace("?", "'" + cartId + "', '" + productIdInt + "'"));

            ResultSet itemResult = checkItemStmt.executeQuery();

            if (itemResult.next()) {
                // Update existing item
                int itemId = itemResult.getInt("itemId");
                int currentQuantity = itemResult.getInt("quantity");
                int newQuantity = currentQuantity + quantity;

                System.out.println("Item exists in cart (ID: " + itemId + "), updating quantity from " + currentQuantity + " to " + newQuantity);

                String updateItemSql = "UPDATE CartItem SET quantity = ? WHERE itemId = ?";
                PreparedStatement updateItemStmt = conn.prepareStatement(updateItemSql);
                updateItemStmt.setInt(1, newQuantity);
                updateItemStmt.setInt(2, itemId);

                int rowsUpdated = updateItemStmt.executeUpdate();
                System.out.println("Updated " + rowsUpdated + " rows in CartItem table");

                LOGGER.log(Level.INFO, "Updated cart item quantity: " + productId + " to " + newQuantity);

                // Verify the update
                String verifySql = "SELECT quantity FROM CartItem WHERE itemId = ?";
                PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
                verifyStmt.setInt(1, itemId);
                ResultSet verifyResult = verifyStmt.executeQuery();

                if (verifyResult.next()) {
                    int verifiedQuantity = verifyResult.getInt("quantity");
                    System.out.println("Verified quantity after update: " + verifiedQuantity);
                }

                return new CartItem(productId, productName, price, newQuantity, imagePath);
            } else {
                // Add new item
                System.out.println("Item does not exist in cart, adding new item");

                String addItemSql = "INSERT INTO CartItem (cartId, itemType, itemRefId, quantity) VALUES (?, 'Flower', ?, ?)";
                PreparedStatement addItemStmt = conn.prepareStatement(addItemSql);
                addItemStmt.setInt(1, cartId);
                addItemStmt.setInt(2, productIdInt);
                addItemStmt.setInt(3, quantity);

                int rowsInserted = addItemStmt.executeUpdate();
                System.out.println("Inserted " + rowsInserted + " rows into CartItem table");

                LOGGER.log(Level.INFO, "Added new item to cart: " + productId);

                // Verify the insert
                String verifySql = "SELECT itemId, quantity FROM CartItem WHERE cartId = ? AND itemType = 'Flower' AND itemRefId = ?";
                PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
                verifyStmt.setInt(1, cartId);
                verifyStmt.setInt(2, productIdInt);
                ResultSet verifyResult = verifyStmt.executeQuery();

                if (verifyResult.next()) {
                    int itemId = verifyResult.getInt("itemId");
                    int verifiedQuantity = verifyResult.getInt("quantity");
                    System.out.println("Verified new item (ID: " + itemId + ") with quantity: " + verifiedQuantity);
                } else {
                    System.out.println("WARNING: Could not verify new item was added to cart!");
                }

                return new CartItem(productId, productName, price, quantity, imagePath);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding item to cart", e);
            System.err.println("SQL Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while getting cart items");
                return items;
            }

            int cartId = getCartId();

            // Log for debugging
            System.out.println("Getting cart items for cartId: " + cartId);

            // Get all items in the cart
            String sql = "SELECT ci.itemRefId, f.name, f.price, ci.quantity, f.image " +
                         "FROM CartItem ci " +
                         "JOIN Flower f ON ci.itemRefId = f.flowerId " +
                         "WHERE ci.cartId = ? AND ci.itemType = 'Flower'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);

            // Log the SQL query for debugging
            System.out.println("Executing SQL query: " + sql.replace("?", String.valueOf(cartId)));

            ResultSet rs = stmt.executeQuery();

            int itemCount = 0;
            while (rs.next()) {
                itemCount++;
                int itemRefId = rs.getInt("itemRefId");
                String productId = String.valueOf(itemRefId); // Convert int to String for CartItem
                String productName = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String imagePath = rs.getString("image");

                // Log each item found for debugging
                System.out.println("Found cart item: " + quantity + " x " + productName + " (ID: " + productId + ")");

                items.add(new CartItem(productId, productName, price, quantity, imagePath));
            }

            // Log the total number of items found
            System.out.println("Total cart items found in database: " + itemCount);

            if (itemCount == 0) {
                // If no items were found, check if the cart exists
                String checkCartSql = "SELECT COUNT(*) FROM Cart WHERE cartId = ?";
                PreparedStatement checkCartStmt = conn.prepareStatement(checkCartSql);
                checkCartStmt.setInt(1, cartId);
                ResultSet cartResult = checkCartStmt.executeQuery();

                if (cartResult.next() && cartResult.getInt(1) > 0) {
                    System.out.println("Cart exists but has no items");

                    // Check if there are any cart items at all
                    String checkItemsSql = "SELECT COUNT(*) FROM CartItem WHERE cartId = ?";
                    PreparedStatement checkItemsStmt = conn.prepareStatement(checkItemsSql);
                    checkItemsStmt.setInt(1, cartId);
                    ResultSet itemsResult = checkItemsStmt.executeQuery();

                    if (itemsResult.next()) {
                        System.out.println("Cart has " + itemsResult.getInt(1) + " items (may include non-Flower items)");
                    }
                } else {
                    System.out.println("Cart does not exist with ID: " + cartId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cart items", e);
            System.err.println("SQL Error getting cart items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public boolean removeFromCart(String productId) {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while removing from cart");
                return false;
            }

            int cartId = getCartId();
            int productIdInt;

            try {
                productIdInt = Integer.parseInt(productId);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid product ID format: " + productId, e);
                return false;
            }

            String sql = "DELETE FROM CartItem WHERE cartId = ? AND itemType = 'Flower' AND itemRefId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);
            stmt.setInt(2, productIdInt);
            int rowsAffected = stmt.executeUpdate();

            LOGGER.log(Level.INFO, "Removed item from cart: " + productId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from cart", e);
            return false;
        }
    }

    @Override
    public void clearCart() {
        try (Connection conn = DatabaseConnector.connectDB()) {
            if (conn == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database while clearing cart");
                return;
            }

            int cartId = getCartId();

            String sql = "DELETE FROM CartItem WHERE cartId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);
            stmt.executeUpdate();

            LOGGER.log(Level.INFO, "Cleared cart");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing cart", e);
        }
    }

    @Override
    public double getCartTotal() throws CartTotalException {
        // Get all cart items
        List<CartItem> items = getCartItems();

        // Calculate total by summing up individual item totals
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }

        return total;
    }

    /**
     * Exception thrown when there is an error calculating the cart total.
     */
    public static class CartTotalException extends RuntimeException {
        public CartTotalException(String message) {
            super(message);
        }

        public CartTotalException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

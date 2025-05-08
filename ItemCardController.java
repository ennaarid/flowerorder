package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemCardController {
    // Static cart to store items (temporary solution)
    private static List<CartItem> cartItems = new ArrayList<>();

    @FXML
    private VBox itemCard;

    @FXML
    private ImageView item_image;

    @FXML
    private Label item_name;

    @FXML
    private Label item_description;

    @FXML
    private Label item_season;

    @FXML
    private Label item_price;

    @FXML
    private HBox stock_container;

    @FXML
    private Label item_stock;

    @FXML
    private Button action_button;

    @FXML
    private Button buy_now_button;

    @FXML
    private Button decrease_btn;

    @FXML
    private Button increase_btn;

    @FXML
    private Label quantity_label;

    private String productId;
    private String productName;
    private String description;
    private String season;
    private double price;
    private String imagePath;
    private int stock;
    private int quantity = 1;

    // Simple class to represent a cart item
    public static class CartItem {
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

        // Getters
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getImagePath() { return imagePath; }
        public double getTotal() { return price * quantity; }
    }

    // Static method to get cart items
    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    // Static method to clear cart
    public static void clearCart() {
        cartItems.clear();
    }

    public void setData(String productId, String productName, String description, String season, double price, String imagePath, int stock, String buttonText) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.season = season;
        this.price = price;
        this.imagePath = imagePath;
        this.stock = stock;

        // Set the data to the UI components
        item_name.setText(productName);
        if (item_description != null) {
            item_description.setText(description);
        }
        if (item_season != null) {
            item_season.setText(season);
        }
        item_price.setText(String.format("₱%.2f", price));
        action_button.setText(buttonText);

        // Load the image
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/example/flowermanagementsystem/flowers/" + imagePath));
            item_image.setImage(image);
        } catch (Exception e) {
            // Load default image if the specified image is not found
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/flowermanagementsystem/flowers/rose.jpg"));
                item_image.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Show stock if it's an inventory view and stock_container exists
        if (stock >= 0 && stock_container != null && item_stock != null) {
            stock_container.setVisible(true);
            item_stock.setText("Stock: " + stock);

            // Set stock color based on quantity
            String stockColor;
            if (stock > 20) {
                stockColor = "#4caf50"; // Green for good stock
            } else if (stock > 5) {
                stockColor = "#ff9800"; // Orange for low stock
            } else {
                stockColor = "#f44336"; // Red for very low stock
            }
            item_stock.setStyle("-fx-text-fill: " + stockColor + ";");
        } else if (stock_container != null) {
            stock_container.setVisible(false);
        }
    }

    // Getters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public String getSeason() { return season; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
    public int getStock() { return stock; }
    public Button getActionButton() { return action_button; }
    public ImageView getImageView() { return item_image; }

    // Setters
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setDescription(String description) { this.description = description; }
    public void setSeason(String season) { this.season = season; }
    public void setPrice(double price) { this.price = price; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setStock(int stock) { this.stock = stock; }
    public void setActionButtonText(String text) { this.action_button.setText(text); }

    // Event handlers for quantity selector
    @FXML
    public void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            updateQuantityLabel();
        }
    }

    @FXML
    public void increaseQuantity() {
        if (stock > 0 && quantity < stock) {
            quantity++;
            updateQuantityLabel();
        }
    }

    private void updateQuantityLabel() {
        if (quantity_label != null) {
            quantity_label.setText(String.valueOf(quantity));
        }
    }

    // Event handlers for buttons
    @FXML
    public void handleAddToCart() {
        try {
            // Create a cart item
            CartItem item = new CartItem(
                productId,
                productName,
                price,
                quantity,
                imagePath
            );

            // Add to cart
            cartItems.add(item);

            showAlert("Success", quantity + " " + productName + " added to cart!", AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add item to cart: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void handleBuyNow() {
        try {
            // First add to cart
            handleAddToCart();

            // Then navigate to cart/checkout
            try {
                // Try to load the cart.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("cart.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) buy_now_button.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Shopping Cart");
                stage.show();
            } catch (IOException e) {
                // If cart.fxml doesn't exist or can't be loaded, show a message
                showAlert("Cart", "Items added to cart: " + cartItems.size() + 
                          "\nTotal: $" + calculateTotal(), AlertType.INFORMATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to proceed to checkout: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotal();
        }
        return total;
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

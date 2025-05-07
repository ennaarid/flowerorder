package com.example.final_flowerorderingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ItemCardController {
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

    private String productId;
    private String productName;
    private String description;
    private String season;
    private double price;
    private String imagePath;
    private int stock;

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
        item_description.setText(description);
        item_season.setText(season);
        item_price.setText(String.format("₱%.2f", price));
        action_button.setText(buttonText);

        // Load the image
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/example/final_flowerorderingsystem/images/" + imagePath));
            item_image.setImage(image);
        } catch (Exception e) {
            // Load default image if the specified image is not found
            Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/final_flowerorderingsystem/images/default_flower.png"));
            item_image.setImage(defaultImage);
        }

        // Show stock if it's an inventory view
        if (stock >= 0) {
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
} 
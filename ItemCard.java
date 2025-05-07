package com.example.final_flowerorderingsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ItemCard extends VBox {
    private String productId;
    private String productName;
    private String season;
    private double price;
    private String imagePath;
    private int stock;
    private Button actionButton;
    private ImageView imageView;
    private HBox priceBox;
    private VBox detailsBox;

    public ItemCard(String productId, String productName, String season, double price, String imagePath, int stock, String buttonText) {
        this.productId = productId;
        this.productName = productName;
        this.season = season;
        this.price = price;
        this.imagePath = imagePath;
        this.stock = stock;

        // Main container styling
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(15));
        setSpacing(10);
        setPrefWidth(250);
        setPrefHeight(400);
        setStyle("-fx-background-color: white; " +
                 "-fx-background-radius: 15; " +
                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 10);");

        // Image container
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: #f8f9fa; " +
                              "-fx-background-radius: 10; " +
                              "-fx-padding: 10;");
        imageContainer.setPrefHeight(200);

        // Image
        imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/example/final_flowerorderingsystem/images/" + imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/final_flowerorderingsystem/images/default_flower.png"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitHeight(180);
        imageView.setFitWidth(180);
        imageView.setPreserveRatio(true);
        imageContainer.getChildren().add(imageView);

        // Details container
        detailsBox = new VBox(8);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.setPadding(new Insets(10, 0, 0, 0));

        // Name with custom font and styling
        Label nameLabel = new Label(productName);
        nameLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Season badge
        HBox seasonBox = new HBox();
        seasonBox.setAlignment(Pos.CENTER_LEFT);
        Label seasonLabel = new Label(season);
        seasonLabel.setFont(Font.font("Montserrat", 12));
        seasonLabel.setStyle("-fx-background-color: #e8f5e9; " +
                           "-fx-text-fill: #2e7d32; " +
                           "-fx-padding: 5 10; " +
                           "-fx-background-radius: 15;");
        seasonBox.getChildren().add(seasonLabel);

        // Price container
        priceBox = new HBox();
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label(String.format("₱%.2f", price));
        priceLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        priceLabel.setStyle("-fx-text-fill: #89ac46;");
        priceBox.getChildren().add(priceLabel);

        // Stock indicator (only for inventory view)
        if (stock >= 0) {
            HBox stockBox = new HBox(5);
            stockBox.setAlignment(Pos.CENTER_LEFT);
            
            Label stockLabel = new Label("Stock: " + stock);
            stockLabel.setFont(Font.font("Montserrat", 12));
            
            // Color-coded stock status
            String stockColor;
            if (stock > 20) {
                stockColor = "#4caf50"; // Green for good stock
            } else if (stock > 5) {
                stockColor = "#ff9800"; // Orange for low stock
            } else {
                stockColor = "#f44336"; // Red for very low stock
            }
            stockLabel.setStyle("-fx-text-fill: " + stockColor + ";");
            
            stockBox.getChildren().add(stockLabel);
            detailsBox.getChildren().add(stockBox);
        }

        // Action Button with modern styling
        actionButton = new Button(buttonText);
        actionButton.setStyle("-fx-background-color: #89ac46; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-family: 'Montserrat'; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14; " +
                            "-fx-padding: 10 20; " +
                            "-fx-background-radius: 25; " +
                            "-fx-cursor: hand;");
        actionButton.setMaxWidth(Double.MAX_VALUE);
        
        // Hover effect for button
        actionButton.setOnMouseEntered(e -> 
            actionButton.setStyle("-fx-background-color: #7d9b3f; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-family: 'Montserrat'; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14; " +
                                "-fx-padding: 10 20; " +
                                "-fx-background-radius: 25; " +
                                "-fx-cursor: hand;")
        );
        actionButton.setOnMouseExited(e -> 
            actionButton.setStyle("-fx-background-color: #89ac46; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-family: 'Montserrat'; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14; " +
                                "-fx-padding: 10 20; " +
                                "-fx-background-radius: 25; " +
                                "-fx-cursor: hand;")
        );

        // Add all components to details box
        detailsBox.getChildren().addAll(nameLabel, seasonBox, priceBox);

        // Add all components to main container
        getChildren().addAll(imageContainer, detailsBox, actionButton);
    }

    // Getters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSeason() { return season; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
    public int getStock() { return stock; }
    public Button getActionButton() { return actionButton; }
    public ImageView getImageView() { return imageView; }

    // Setters
    public void setProductId(String productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setSeason(String season) { this.season = season; }
    public void setPrice(double price) { this.price = price; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setStock(int stock) { this.stock = stock; }
    public void setActionButtonText(String text) { this.actionButton.setText(text); }
} 
package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderReceiptController {

    @FXML
    private Label orderIdLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label customerLabel;

    @FXML
    private VBox itemsVBox;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Label paymentMethodLabel;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        displayOrderDetails();
    }

    private void displayOrderDetails() {
        if (order != null) {
            // Set order ID
            orderIdLabel.setText("Order ID: " + order.getOrderId());
            
            // Set date (current date and time for simplicity)
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateLabel.setText("Date: " + now.format(formatter));
            
            // Set customer name
            customerLabel.setText("Customer: " + order.getCustomer().getUsername());
            
            // Clear previous items
            itemsVBox.getChildren().clear();
            
            // Add order items
            double total = 0;
            for (OrderItem item : order.getItems()) {
                Flower flower = item.getFlower();
                int quantity = item.getQuantity();
                double price = flower.getPrice();
                double itemTotal = quantity * price;
                total += itemTotal;
                
                Label itemLabel = new Label(quantity + " x " + flower.getName() + " - ₱" + String.format("%.2f", itemTotal));
                itemsVBox.getChildren().add(itemLabel);
            }
            
            // Set total amount
            totalAmountLabel.setText("Total Amount: ₱" + String.format("%.2f", total));
            
            // Set payment method (placeholder)
            paymentMethodLabel.setText("Payment Method: Cash");
        }
    }
}
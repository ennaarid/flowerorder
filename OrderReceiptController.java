package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    @FXML
    private Label shippingInfoLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button closeButton;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        displayOrderDetails();
    }

    @FXML
    public void initialize() {
        // Add close button functionality if it exists in the FXML
        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void displayOrderDetails() {
        if (order != null) {
            // Set order ID
            orderIdLabel.setText("Order ID: " + order.getOrderId());

            // Set date from order
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateLabel.setText("Date: " + order.getOrderDate().format(formatter));

            // Set customer name
            customerLabel.setText("Customer: " + order.getCustomer().getUsername());

            // Set order status
            if (statusLabel != null) {
                statusLabel.setText("Status: " + order.getStatus());
            }

            // Clear previous items
            itemsVBox.getChildren().clear();

            // Add order items
            for (OrderItem item : order.getItems()) {
                Flower flower = item.getFlower();
                int quantity = item.getQuantity();
                double price = flower.getPrice();
                double itemTotal = quantity * price;

                Label itemLabel = new Label(quantity + " x " + flower.getName() + " - ₱" + String.format("%.2f", itemTotal));
                itemsVBox.getChildren().add(itemLabel);
            }

            // Set total amount using the order's calculate method
            totalAmountLabel.setText("Total Amount: ₱" + String.format("%.2f", order.calculateTotal()));

            // Set payment method from order
            paymentMethodLabel.setText("Payment Method: " + order.getPaymentMethod());

            // Set shipping information if available
            if (shippingInfoLabel != null) {
                ShippingInfo shipping = order.getShippingInfo();
                if (shipping != null) {
                    shippingInfoLabel.setText("Shipping To:\n" + shipping.toString());
                } else {
                    shippingInfoLabel.setText("Shipping Information: Not provided");
                }
            }
        }
    }

    @FXML
    public void closeReceipt() {
        Stage stage = (Stage) totalAmountLabel.getScene().getWindow();
        stage.close();
    }
}

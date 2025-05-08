
package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderReceiptController extends BaseController {
    @FXML private Label orderIdLabel;
    @FXML private Label dateLabel;
    @FXML private Label totalLabel;
    @FXML private Label itemsLabel;
    @FXML private Button printButton;
    @FXML private Button closeButton;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        displayOrderDetails();
    }

    @FXML
    public void initialize() {
        printButton.setOnAction(event -> printReceipt());
        closeButton.setOnAction(event -> loadView("CustomerCatalog", closeButton));
    }

    private void displayOrderDetails() {
        if (order != null) {
            orderIdLabel.setText("Order #" + order.getOrderId());
            dateLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            totalLabel.setText(String.format("Total: ₱%.2f", order.getTotal()));
            itemsLabel.setText(formatOrderItems());
        }
    }

    private String formatOrderItems() {
        StringBuilder items = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            items.append(String.format("%s x%d - ₱%.2f\n",
                    item.getFlower().getProductName(),
                    item.getQuantity(),
                    item.getQuantity() * item.getFlower().getPrice()));
        }
        return items.toString();
    }

    private void printReceipt() {
        // Implement printing logic
        showInfoAlert("Print", "Sending to printer...");
    }

    @Override
    protected void clearInputFields() {
        // No input fields to clear
    }
}

package com.example.cart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

public class CartController {

    @FXML
    private ListView<String> cartListView;

    @FXML
    private Label totalLabel;

    private Customer loggedInCustomer; // You'll need to pass the logged-in customer here
    private ObservableList<String> cartItems = FXCollections.observableArrayList();

    public void setLoggedInCustomer(Customer customer) {
        this.loggedInCustomer = customer;
        updateCartView();
    }

    @FXML
    public void initialize() {
        cartListView.setItems(cartItems);
    }

    private void updateCartView() {
        cartItems.clear();
        double total = 0;
        if (loggedInCustomer != null) {
            for (OrderItem item : loggedInCustomer.getCart()) {
                cartItems.add(item.getQuantity() + " x " + item.getFlower().getName() + " @ Php " + String.format("%.2f", item.getFlower().getPrice()));
                total += item.getQuantity() * item.getFlower().getPrice();
            }
            totalLabel.setText("Total: Php " + String.format("%.2f", total));
        } else {
            totalLabel.setText("Total: Php 0.00");
        }
    }

    @FXML
    public void removeFromCart() {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && loggedInCustomer != null) {
            // Extract the flower name from the selected string (you might need a more robust way)
            String flowerName = selectedItem.split(" x ")[1].split(" @")[0];

            // Find the corresponding Flower object in the customer's cart and remove it
            loggedInCustomer.getCart().removeIf(item -> item.getFlower().getName().equals(flowerName));
            updateCartView();
        }
    }

    @FXML
    public void checkout() {
        if (loggedInCustomer != null) {
            Order order = loggedInCustomer.checkout();
            if (order != null) {
                // Load and display the order receipt UI, passing the 'order' object
                loadOrderReceipt(order);
            } else {
                // Inform the user that the cart is empty
                // (You'd typically use an Alert dialog here)
                System.out.println("Cart is empty. Cannot checkout.");
            }
        }
    }

    private void loadOrderReceipt(Order order) {
        try {
            // Load the order_receipt.fxml
            // Get the controller for the order receipt and pass the 'order' data
            // Switch to the order receipt scene
            // This part depends on how you manage your scenes/windows
            System.out.println("Loading order receipt for Order ID: " + order.getOrderId());
            // Example (you'll need to adapt this to your application structure):
            /*
            FXMLLoader loader = new FXMLLoader(getClass().getResource("order_receipt.fxml"));
            Parent root = loader.load();
            OrderReceiptController controller = loader.getController();
            controller.setOrder(order);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Order Receipt");
            stage.show();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
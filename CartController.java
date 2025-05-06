package com.example.final_flowerorderingsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CartController {

    @FXML
    private ListView<OrderItem> cartListView;

    @FXML
    private Label totalLabel;

    private Customer currentCustomer; // You'll need to set this from your main application flow
    private ObservableList<OrderItem> cartItems = FXCollections.observableArrayList();

    public void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;
        updateCartView();
    }

    @FXML
    public void initialize() {
        cartListView.setItems(cartItems);
    }

    private void updateCartView() {
        cartItems.clear();
        double total = 0;
        if (currentCustomer != null) {
            cartItems.addAll(currentCustomer.getCart());
            for (OrderItem item : currentCustomer.getCart()) {
                total += item.getQuantity() * item.getFlower().getPrice();
            }
            totalLabel.setText(String.format("Total: ₱ %.2f", total));
        } else {
            totalLabel.setText("Total: ₱ 0.00");
        }
    }

    @FXML
    public void removeFromCart() {
        OrderItem selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && currentCustomer != null) {
            currentCustomer.removeFromCart(selectedItem.getFlower());
            updateCartView();
        }
    }

    @FXML
    public void checkout() {
        if (currentCustomer != null && !currentCustomer.getCart().isEmpty()) {
            Order order = currentCustomer.checkout();
            if (order != null) {
                loadOrderReceipt(order);
                // Optionally clear the cart after checkout
                currentCustomer.getCart().clear();
                updateCartView();
            } else {
                // Handle case where checkout fails (e.g., insufficient stock)
                System.out.println("Checkout failed.");
            }
        } else {
            System.out.println("Cart is empty. Cannot checkout.");
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("some_previous_form.fxml")); // Replace with the actual FXML file you want to go back to
            Parent root = loader.load();
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadOrderReceipt(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("order_receipt.fxml"));
            Parent root = loader.load();

            OrderReceiptController controller = loader.getController();
            controller.setOrder(order);

            Stage stage = new Stage();
            stage.setTitle("Order Receipt");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

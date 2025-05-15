package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker pickupDatePicker;

    @FXML
    private Label totalLabel;

    private double orderTotal;
    private List<CartItem> cartItems;
    private CartService cartService = CartServiceImpl.getInstance();

    @FXML
    public void initialize() {
        // Set default pickup date to tomorrow
        pickupDatePicker.setValue(LocalDate.now().plusDays(1));

        // Get cart items from cart service
        cartItems = new ArrayList<>(cartService.getCartItems());

        // Calculate order total
        calculateOrderTotal();
    }

    private void calculateOrderTotal() {
        orderTotal = cartService.getCartTotal();
        totalLabel.setText(String.format("Order Total: ₱%.2f", orderTotal));
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/cart.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not return to cart: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void placeOrder() {
        if (validateFields()) {
            try {
                // Create pickup information
                PickupInfo pickupInfo = new PickupInfo(
                        nameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        pickupDatePicker.getValue()
                );

                // Create order items
                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem cartItem : cartItems) {
                    Flower flower = new Flower(
                            cartItem.getProductName(),
                            cartItem.getPrice()
                    );
                    OrderItem orderItem = new OrderItem(flower, cartItem.getQuantity());
                    orderItems.add(orderItem);
                }

                // Create customer (in a real app, this would be the logged-in user)
                Customer customer = new Customer(nameField.getText());

                // Create the order with pickup info
                Order order = new Order(
                        "ORDER_" + System.currentTimeMillis(),
                        customer,
                        orderItems,
                        LocalDateTime.now(),
                        "Pending",
                        "Pickup",
                        pickupInfo
                );

                // Show order receipt
                loadOrderReceipt(order);

                // Clear the cart
                cartService.clearCart();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Order Error", "Could not place order: " + e.getMessage(), AlertType.ERROR);
            }
        }
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Please enter your full name.\n");
        }
        if (phoneField.getText().trim().isEmpty()) {
            errorMessage.append("Please enter your phone number.\n");
        }
        if (emailField.getText().trim().isEmpty()) {
            errorMessage.append("Please enter your email address.\n");
        }
        if (pickupDatePicker.getValue() == null) {
            errorMessage.append("Please select an estimated pickup date.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert("Validation Error", errorMessage.toString(), AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void loadOrderReceipt(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/order_reciept.fxml"));
            Parent root = loader.load();

            OrderReceiptController controller = loader.getController();
            controller.setOrder(order);

            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Receipt Error", "Could not load order receipt: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

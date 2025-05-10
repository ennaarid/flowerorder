package com.example.flowermanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartController {

    @FXML
    private ListView<ItemCardController.CartItem> cartListView;

    @FXML
    private Label totalLabel;

    private ObservableList<ItemCardController.CartItem> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load cart items from ItemCardController
        cartItems.addAll(ItemCardController.getCartItems());
        cartListView.setItems(cartItems);

        // Set cell factory to display cart items properly
        cartListView.setCellFactory(param -> new ListCell<ItemCardController.CartItem>() {
            @Override
            protected void updateItem(ItemCardController.CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getQuantity() + " x " + item.getProductName() + " - ₱" + String.format("%.2f", item.getTotal()));
                }
            }
        });

        // Update total
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (ItemCardController.CartItem item : cartItems) {
            total += item.getTotal();
        }
        totalLabel.setText(String.format("Total: ₱ %.2f", total));
    }

    @FXML
    public void removeFromCart() {
        ItemCardController.CartItem selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
            // Also remove from the static list in ItemCardController
            ItemCardController.getCartItems().remove(selectedItem);
            updateTotal();
        }
    }

    @FXML
    public void checkout() {
        if (!cartItems.isEmpty()) {
            // Navigate to the checkout page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/checkout.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) totalLabel.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Navigation Error", "Could not navigate to checkout: " + e.getMessage(), AlertType.ERROR);
            }
        } else {
            showAlert("Cart Empty", "Your cart is empty. Cannot checkout.", AlertType.WARNING);
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/catalog.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadOrderReceipt(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/order_reciept.fxml"));
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

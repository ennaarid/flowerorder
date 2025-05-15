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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartController {

    @FXML
    private ListView<CartItem> cartListView;

    @FXML
    private Label totalLabel;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    private CartService cartService = CartServiceImpl.getInstance();

    @FXML
    public void initialize() {
        cartItems.clear();

        List<CartItem> latestCartItems = cartService.getCartItems();

        cartItems.addAll(latestCartItems);

        cartListView.setItems(cartItems);

        System.out.println("Cart initialized with " + cartItems.size() + " items");
        for (CartItem item : cartItems) {
            System.out.println("  - " + item.getQuantity() + " x " + item.getProductName() + " (₱" + item.getPrice() + ")");
        }

        cartListView.setCellFactory(param -> new ListCell<CartItem>() {
            private HBox content;
            private ImageView imageView;
            private Label nameLabel;
            private Label priceLabel;

            {
                // Initialize cell components
                imageView = new ImageView();
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);

                nameLabel = new Label();
                nameLabel.setStyle("-fx-font-weight: bold;");

                priceLabel = new Label();

                VBox vbox = new VBox(nameLabel, priceLabel);
                vbox.setAlignment(Pos.CENTER_LEFT);
                vbox.setSpacing(5);
                HBox.setHgrow(vbox, Priority.ALWAYS);

                content = new HBox(10, imageView, vbox);
                content.setAlignment(Pos.CENTER_LEFT);
                content.setPadding(new Insets(5, 10, 5, 10));
            }

            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getQuantity() + " x " + item.getProductName());
                    priceLabel.setText("₱" + String.format("%.2f", item.getTotal()));

                    try {
                        String imagePath = item.getImagePath();
                        Image image = new Image(getClass().getResourceAsStream("/com/example/flowermanagementsystem/flowers/" + imagePath));
                        imageView.setImage(image);
                    } catch (Exception e) {
                        try {
                            Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/flowermanagementsystem/flowers/rose.jpg"));
                            imageView.setImage(defaultImage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    setGraphic(content);
                    setText(null);
                }
            }
        });

        updateTotal();
    }

    private void updateTotal() {
        try {
            double total = cartService.getCartTotal();
            totalLabel.setText(String.format("Total: ₱ %.2f", total));
            totalLabel.setStyle("-fx-text-fill: black;");
        } catch (CartServiceImpl.CartTotalException e) {
            totalLabel.setText("Error: Could not calculate total");
            totalLabel.setStyle("-fx-text-fill: red;");

            System.err.println("Error calculating cart total: " + e.getMessage());

            showAlert("Cart Total Error", 
                     "There was an error calculating your cart total: " + e.getMessage(), 
                     AlertType.ERROR);
        }
    }

    @FXML
    public void removeFromCart() {
        CartItem selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartService.removeFromCart(selectedItem.getProductId());
            cartItems.remove(selectedItem);
            updateTotal();
        }
    }

    @FXML
    public void checkout() {
        if (!cartItems.isEmpty()) {
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
            String fxmlPath = "/com/example/flowermanagementsystem/customerCatalog.fxml";
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Stage stage = (Stage) totalLabel.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                fxmlPath = "/com/example/flowermanagementsystem/catalog.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Stage stage = (Stage) totalLabel.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not navigate back to catalog: " + e.getMessage(), AlertType.ERROR);
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

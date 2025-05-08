package com.example.flowermanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class manageOrdersController implements Initializable {

    @FXML
    private AnchorPane main_form;

    @FXML
    private AnchorPane inventory_form;

    @FXML
    private TableView<OrderData> inventory_tableView;

    @FXML
    private TableColumn<OrderData, String> inventory_col_productID;

    @FXML
    private TableColumn<OrderData, String> inventory_col_productName;

    @FXML
    private TableColumn<OrderData, Integer> inventory_col_season;

    @FXML
    private TableColumn<OrderData, Double> inventory_col_price;

    @FXML
    private TableColumn<OrderData, String> inventory_col_status;

    @FXML
    private TableColumn<OrderData, String> inventory_col_dateOrdered;

    @FXML
    private TableColumn<OrderData, String> inventory_col_date;

    @FXML
    private Button inventory_updateBtn;

    @FXML
    private Button inventory_clearBtn;

    @FXML
    private Button inventory_deleteBtn;

    @FXML
    private TextField searchField;

    @FXML
    private Button menu_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button manageorders_btn;

    @FXML
    private Button profile_btn;

    @FXML
    private Button settings_btn;

    @FXML
    private Button logout_btn;

    private ObservableList<OrderData> orderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("user"));
        inventory_col_season.setCellValueFactory(new PropertyValueFactory<>("numberOfItems"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("amount"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_dateOrdered.setCellValueFactory(new PropertyValueFactory<>("dateOrdered"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("pickupDate"));

        // Set up the table
        inventory_tableView.setItems(orderList);

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterOrders(newValue);
        });

        // Load orders when the controller initializes
        loadOrders();
    }

    @FXML
    private void inventoryUpdateBtn() {
        OrderData selectedOrder = inventory_tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            // Implement update logic here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Update Order");
            alert.setHeaderText(null);
            alert.setContentText("Order update functionality to be implemented");
            alert.showAndWait();
        } else {
            showAlert("No Selection", "Please select an order to update.");
        }
    }

    @FXML
    private void inventoryClearBtn() {
        inventory_tableView.getSelectionModel().clearSelection();
        searchField.clear();
    }

    @FXML
    private void inventoryDeleteBtn() {
        OrderData selectedOrder = inventory_tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Order");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this order?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                orderList.remove(selectedOrder);
                // Implement database deletion here
            }
        } else {
            showAlert("No Selection", "Please select an order to delete.");
        }
    }

    private void filterOrders(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            inventory_tableView.setItems(orderList);
            return;
        }

        ObservableList<OrderData> filteredList = FXCollections.observableArrayList();
        for (OrderData order : orderList) {
            if (order.getReceiptId().toLowerCase().contains(searchText.toLowerCase()) ||
                order.getUser().toLowerCase().contains(searchText.toLowerCase()) ||
                order.getStatus().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(order);
            }
        }
        inventory_tableView.setItems(filteredList);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles switching between different forms/views in the dashboard.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == dashboard_btn) {
            // Navigate to dashboard
            inventory_form.setVisible(false);
        } else if (event.getSource() == inventory_btn) {
            // Navigate to inventory
            inventory_form.setVisible(false);
        } else if (event.getSource() == manageorders_btn) {
            // Show orders form
            inventory_form.setVisible(true);
            // Refresh the orders list
            loadOrders();
        } else if (event.getSource() == menu_btn) {
            // Navigate to catalog
            inventory_form.setVisible(false);
        } else if (event.getSource() == profile_btn) {
            // Navigate to profile
            inventory_form.setVisible(false);
        } else if (event.getSource() == settings_btn) {
            // Navigate to settings
            inventory_form.setVisible(false);
        }
    }

    // Method to load orders from database
    public void loadOrders() {
        orderList.clear();

        try {
            Connection connect = DatabaseConnector.connectDB();
            String query = "SELECT * FROM orders";
            PreparedStatement prepare = connect.prepareStatement(query);
            ResultSet result = prepare.executeQuery();

            while (result.next()) {
                String receiptId = result.getString("order_id");
                String user = result.getString("customer_id");
                int numberOfItems = result.getInt("item_count");
                double amount = result.getDouble("total_amount");
                String status = result.getString("status");
                String dateOrdered = result.getString("order_date");
                String pickupDate = "Not scheduled"; // Default value since pickup_date is not in the database

                OrderData order = new OrderData(receiptId, user, numberOfItems, amount, status, dateOrdered, pickupDate);
                orderList.add(order);
            }

            // Close resources
            result.close();
            prepare.close();
            connect.close();

            // Apply search filter if there's text in the search field
            if (searchField.getText() != null && !searchField.getText().isEmpty()) {
                filterOrders(searchField.getText());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load orders from database: " + e.getMessage());
        }
    }

}

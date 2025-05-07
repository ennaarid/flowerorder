package com.example.final_flowerorderingsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class manageOrdersController implements Initializable {

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
    private TableColumn<OrderData, String> inventory_col_date;
    
    @FXML
    private Button inventory_updateBtn;
    
    @FXML
    private Button inventory_clearBtn;
    
    @FXML
    private Button inventory_deleteBtn;
    
    @FXML
    private TextField searchField;
    
    private ObservableList<OrderData> orderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("user"));
        inventory_col_season.setCellValueFactory(new PropertyValueFactory<>("numberOfItems"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("amount"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("dateOrdered"));
        
        // Set up the table
        inventory_tableView.setItems(orderList);
        
        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterOrders(newValue);
        });
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
    
    // Method to load orders from database
    public void loadOrders() {
        // Implement database loading logic here
        // This is where you would fetch orders from your database
        // and add them to the orderList
    }
} 
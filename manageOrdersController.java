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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class manageOrdersController extends BaseController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(manageOrdersController.class.getName());
    private OrderService orderService;

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
    private Button logout_btn;

    private ObservableList<OrderData> orderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderService = OrderServiceImpl.getInstance();

        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("user"));
        inventory_col_season.setCellValueFactory(new PropertyValueFactory<>("numberOfItems"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("amount"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        inventory_col_dateOrdered.setCellValueFactory(new PropertyValueFactory<>("dateOrdered"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("pickupDate"));

        inventory_tableView.setItems(orderList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterOrders(newValue);
        });

        loadOrders();
    }

    @FXML
    private void inventoryUpdateBtn() {
        OrderData selectedOrder = inventory_tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Update Order");
            alert.setHeaderText(null);
            alert.setContentText("Order update functionality to be implemented");
            alert.showAndWait();
        } else {
            showErrorAlert("No Selection", "Please select an order to update.");
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
            }
        } else {
            showErrorAlert("No Selection", "Please select an order to delete.");
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

    @FXML
    public void switchForm(ActionEvent event) {
        try {
            String viewName = null;

            if (event.getSource() == dashboard_btn) {
                viewName = "admindash";
            } else if (event.getSource() == inventory_btn) {
                viewName = "inventory";
            } else if (event.getSource() == manageorders_btn) {
                viewName = "manageOrders";
            } else if (event.getSource() == menu_btn) {
                viewName = "catalog";
            }

            if (viewName != null) {
                loadView(viewName, event);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Navigation error", e);
            showErrorAlert("Navigation Error", "Failed to navigate to the selected page: " + e.getMessage());
        }
    }

    @FXML
    public void logout() {
        logout(main_form);
    }

    public void loadOrders() {
        orderList.clear();

        try {
            List<OrderData> orders = orderService.loadOrders();

            orderList.addAll(orders);

            if (searchField.getText() != null && !searchField.getText().isEmpty()) {
                filterOrders(searchField.getText());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load orders", e);
            showErrorAlert("Error", "Failed to load orders from database: " + e.getMessage());
        }
    }

    @Override
    protected void clearInputFields() {
        if (searchField != null) {
            searchField.clear();
        }
    }
}

package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Optional;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public abstract class AdminDashboardController extends BaseController {

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button menu_btn;

    @FXML
    private Button bouquets_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button manageorders_btn;

    @FXML
    private Button customer_view_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private AnchorPane dashboard_form;


    public void initialize() {
        loadDashboardForm();

    }

    @FXML
    private void loadDashboardForm() {
        dashboard_form.setVisible(true);
    }
    @FXML
    private void loadInventoryForm(ActionEvent event) {
        loadView("inventory", event);
    }
    @FXML
    private void loadManageOrdersForm(ActionEvent event) {
        loadView("manageOrders", event);
    }
    @FXML
    private void loadCatalogForm(ActionEvent event) {
        loadView("catalog", event);
    }

    @FXML
    public void switchToCustomerView(ActionEvent event) {
        switchToCustomerView(main_form);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleLogout() {
        logout(main_form);
    }


}

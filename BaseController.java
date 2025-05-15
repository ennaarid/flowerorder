package com.example.flowermanagementsystem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseController {

    private static final Logger LOGGER = Logger.getLogger(BaseController.class.getName());

    protected void loadView(String viewName, ActionEvent event) {
        try {
            Parent root;
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/flowermanagementsystem/" + viewName + ".fxml")));
            } catch (Exception e) {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(viewName + ".fxml")));
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading view: " + viewName, e);
            showErrorAlert("Error Loading View", "Could not load " + viewName + ".fxml");
        }
    }

    protected void loadView(String viewName, Node node) {
        try {
            Parent root;
            try {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/flowermanagementsystem/" + viewName + ".fxml")));
            } catch (Exception e) {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(viewName + ".fxml")));
            }
            Scene scene = new Scene(root);
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading view: " + viewName, e);
            showErrorAlert("Error Loading View", "Could not load " + viewName + ".fxml");
        }
    }

    protected boolean switchToAdminView(Node node) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && "Administrator".equals(currentUser.getRole())) {
            loadView("admindash", node);
            return true;
        } else {
            showErrorAlert("Access Denied", "You do not have administrator privileges.");
            return false;
        }
    }

    protected void switchToCustomerView(Node node) {
        loadView("customerCatalog", node);
    }

    protected void logout(Node node) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.isPresent() && option.get() == ButtonType.OK) {
                SessionManager.clearSession();

                Parent root = FXMLLoader.load(getClass().getResource("/com/example/flowermanagementsystem/FlowerLogin.fxml"));
                Stage stage = (Stage) node.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Flower Ordering System");
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
            showErrorAlert("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }

    protected void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected abstract void clearInputFields();
}

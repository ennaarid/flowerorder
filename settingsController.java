package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class settingsController extends BaseController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(settingsController.class.getName());
    private UserService userService;
    private String currentUsername;

    public settingsController() {
        this.userService = new UserServiceImpl();
    }

    @FXML
    private PasswordField current_password_field;

    @FXML
    private PasswordField new_password_field;

    @FXML
    private PasswordField confirm_password_field;

    @FXML
    private CheckBox email_notifications_checkbox;

    @FXML
    private CheckBox sms_notifications_checkbox;

    @FXML
    private ComboBox<String> theme_combo;

    @FXML
    private Button change_password_btn;

    @FXML
    private Button save_preferences_btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUsername = SessionManager.getCurrentUsername();
        if (currentUsername == null) {
            LOGGER.log(Level.WARNING, "No current user found, using default");
            currentUsername = "defaultUser"; 
        }

        loadSettings();

        
        current_password_field.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordFields();
        });

        new_password_field.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordFields();
        });

        confirm_password_field.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordFields();
        });

        
        theme_combo.getItems().addAll("Light", "Dark", "System Default");
        theme_combo.setValue("Light");

   
        change_password_btn.setOnAction(event -> changePassword());
        save_preferences_btn.setOnAction(event -> savePreferences());
    }

    @FXML
    private void changePassword() 
        if (!current_password_field.getText().isEmpty() || 
            !new_password_field.getText().isEmpty() || 
            !confirm_password_field.getText().isEmpty()) {

            if (!validatePasswordChange()) {
                return;
            }
        }

      
        try {
            boolean success = userService.changePassword(
                currentUsername,
                current_password_field.getText(),
                new_password_field.getText()
            );

            if (success) {
                showAlert("Success", "Password updated successfully!");

            
                current_password_field.clear();
                new_password_field.clear();
                confirm_password_field.clear();
            } else {
                showAlert("Error", "Failed to update password. Please check your current password.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void savePreferences() {
      
        try {
            boolean emailNotifications = email_notifications_checkbox.isSelected();
            boolean smsNotifications = sms_notifications_checkbox.isSelected();
            String theme = theme_combo.getValue();

            boolean success = userService.saveUserPreferences(
                currentUsername,
                emailNotifications,
                smsNotifications,
                theme
            );

            if (success) {
                showAlert("Success", "Preferences updated successfully!");
            } else {
                showAlert("Error", "Failed to update preferences. Please try again.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving preferences", e);
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void loadSettings() {
        try {
            Object[] preferences = userService.loadUserPreferences(currentUsername);

            if (preferences != null) {
                boolean emailNotifications = (Boolean) preferences[0];
                boolean smsNotifications = (Boolean) preferences[1];
                String theme = (String) preferences[2];

                email_notifications_checkbox.setSelected(emailNotifications);
                sms_notifications_checkbox.setSelected(smsNotifications);
                theme_combo.setValue(theme);

                LOGGER.log(Level.INFO, "Loaded preferences for user: " + currentUsername);
            } else {
              
                email_notifications_checkbox.setSelected(true);
                sms_notifications_checkbox.setSelected(false);
                theme_combo.setValue("Light");

                LOGGER.log(Level.INFO, "Using default preferences for user: " + currentUsername);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading preferences", e);
            
            email_notifications_checkbox.setSelected(true);
            sms_notifications_checkbox.setSelected(false);
            theme_combo.setValue("Light");
        }
    }

    private boolean validatePasswordChange() {
        try {
            User user = userService.authenticateUser(currentUsername, current_password_field.getText());
            if (user == null) {
                showAlert("Error", "Current password is incorrect.");
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying current password", e);
            showAlert("Error", "Could not verify current password: " + e.getMessage());
            return false;
        }

        if (new_password_field.getText().length() < 8) {
            showAlert("Error", "New password must be at least 8 characters long.");
            return false;
        }

        if (!new_password_field.getText().equals(confirm_password_field.getText())) {
            showAlert("Error", "New passwords do not match.");
            return false;
        }

        return true;
    }

    private void validatePasswordFields() {
        boolean allEmpty = current_password_field.getText().isEmpty() && 
                          new_password_field.getText().isEmpty() && 
                          confirm_password_field.getText().isEmpty();

        boolean allFilled = !current_password_field.getText().isEmpty() && 
                           !new_password_field.getText().isEmpty() && 
                           !confirm_password_field.getText().isEmpty();

        if (!allEmpty && !allFilled) {
            showAlert("Warning", "Please fill in all password fields to change your password.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void switchForm(ActionEvent event) {
        try {
            Button sourceButton = (Button) event.getSource();
            String buttonId = sourceButton.getId();

            switch (buttonId) {
                case "dashboard_btn":
                    loadView("dashboard", event);
                    break;
                case "profile_btn":
                    loadView("profile", event);
                    break;
                case "catalog_btn":
                    loadView("customerCatalog", event);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unknown form button: " + buttonId);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error switching form", e);
            showAlert("Error", "Could not switch form: " + e.getMessage());
        }
    }

    @FXML
    private void openOrderHistory(ActionEvent event) {
        try {
            loadView("orderHistory", event);
            LOGGER.log(Level.INFO, "Navigating to Order History");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to order history", e);
            showAlert("Error", "Could not open order history: " + e.getMessage());
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            SessionManager.clearSession();
            loadView("FlowerLogin", event);

            LOGGER.log(Level.INFO, "User logged out successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
            showAlert("Error", "Could not complete logout: " + e.getMessage());
        }
    }

    @Override
    protected void clearInputFields() {
        current_password_field.clear();
        new_password_field.clear();
        confirm_password_field.clear();
    }
}

package com.example.final_flowerorderingsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class settingsController implements Initializable {

    @FXML
    private TextField settings_currentPassword;
    
    @FXML
    private TextField settings_newPassword;
    
    @FXML
    private TextField settings_confirmPassword;
    
    @FXML
    private CheckBox settings_emailNotifications;
    
    @FXML
    private CheckBox settings_orderUpdates;
    
    @FXML
    private CheckBox settings_promotions;
    
    @FXML
    private Button settings_saveBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load current settings
        loadSettings();
        
        // Add password field listeners
        settings_currentPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordFields();
        });
        
        settings_newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordFields();
        });
        
        settings_confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordFields();
        });
    }
    
    @FXML
    private void saveSettings() {
        // Validate password change if attempted
        if (!settings_currentPassword.getText().isEmpty() || 
            !settings_newPassword.getText().isEmpty() || 
            !settings_confirmPassword.getText().isEmpty()) {
            
            if (!validatePasswordChange()) {
                return;
            }
        }
        
        // Save settings to database
        // TODO: Implement database save logic
        
        showAlert("Success", "Settings updated successfully!");
        
        // Clear password fields
        settings_currentPassword.clear();
        settings_newPassword.clear();
        settings_confirmPassword.clear();
    }
    
    private void loadSettings() {
        // TODO: Load settings from database
        // For now, we'll just set some default values
        settings_emailNotifications.setSelected(true);
        settings_orderUpdates.setSelected(true);
        settings_promotions.setSelected(false);
    }
    
    private boolean validatePasswordChange() {
        // Check if current password is correct
        // TODO: Implement actual password verification against database
        
        // Validate new password
        if (settings_newPassword.getText().length() < 8) {
            showAlert("Error", "New password must be at least 8 characters long.");
            return false;
        }
        
        // Check if passwords match
        if (!settings_newPassword.getText().equals(settings_confirmPassword.getText())) {
            showAlert("Error", "New passwords do not match.");
            return false;
        }
        
        return true;
    }
    
    private void validatePasswordFields() {
        boolean allEmpty = settings_currentPassword.getText().isEmpty() && 
                          settings_newPassword.getText().isEmpty() && 
                          settings_confirmPassword.getText().isEmpty();
        
        boolean allFilled = !settings_currentPassword.getText().isEmpty() && 
                           !settings_newPassword.getText().isEmpty() && 
                           !settings_confirmPassword.getText().isEmpty();
        
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
    private void switchForm() {
        // TODO: Implement form switching logic
    }
} 
package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.util.logging.Level;
import java.util.logging.Logger;

public class resetpasswordController extends BaseController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label messageLabel;

    private String email;
    private String token;
    private UserService userService;

    private static final Logger LOGGER = Logger.getLogger(resetpasswordController.class.getName());

    public resetpasswordController() {
        this.userService = new UserServiceImpl();
    }

    public void initialize(String email, String token) {
        this.email = email;
        this.token = token;
    }

    @FXML
    public void handleSubmitButton(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate passwords
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        if (newPassword.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters long");
            return;
        }

        try {
            boolean success = userService.resetPassword(email, token, newPassword);

            if (success) {
                showInfoAlert("Success", "Password has been reset successfully!");
                loadView("FlowerLogin", event);
            } else {
                messageLabel.setText("Failed to reset password. Token may be invalid or expired.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error resetting password", e);
            messageLabel.setText("An error occurred: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancelButton(ActionEvent event) {
        loadView("FlowerLogin", event);
    }

    @Override
    protected void clearInputFields() {
        newPasswordField.clear();
        confirmPasswordField.clear();
        messageLabel.setText("");
    }
}

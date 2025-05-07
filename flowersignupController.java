package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class flowersignupController {

    @FXML
    private Label alreadyAccountLabel;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailTextField;

    @FXML
    private Hyperlink loginHyperlink;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordLabel;

    @FXML
    private Button signupButton;

    @FXML
    private CheckBox termsOfServiceCheckBox;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final Logger LOGGER = Logger.getLogger(flowersignupController.class.getName());

    private final UserService userService;

    public flowersignupController(UserService userService) {
        this.userService = userService;
    }

    public flowersignupController() {
        this.userService = new UserServiceImpl();
    }

    @FXML
    public void initialize() {
    }

    @FXML
    public void handleSignupButtonAction() {
        String username = usernameTextField.getText(); 
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailTextField.getText();

        if (!termsOfServiceCheckBox.isSelected()) {
            showErrorAlert("Terms of Service", "Please agree to the Terms of Service.");
            return;
        }

        if (!validateInput(username, password, confirmPassword, email)) {
            return;
        }

        try {
            userService.registerUser(username, password, email);
            showSuccessAlert();
            clearInputFields();
            loadLoginView();
        } catch (UserServiceImpl.UserRegistrationException e) {
            LOGGER.log(Level.SEVERE, "User registration failed", e);
            showErrorAlert("Registration Failed", "Failed to register user: " + e.getMessage());
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword, String email) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            showErrorAlert("Input Error", "All fields are required.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showErrorAlert("Password Error", "Passwords do not match.");
            return false;
        }
        if (password.length() < 8) {
            showErrorAlert("Password Error", "Password must be at least 8 characters long.");
            return false;
        }
        if (!isValidEmail(email)) {
            showErrorAlert("Email Error", "Invalid email format.");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("You have been registered successfully. Please login.");
        alert.showAndWait();
    }

    private void clearInputFields() {
        usernameTextField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailTextField.clear();
        termsOfServiceCheckBox.setSelected(false);
    }

    @FXML
    private void handleLoginButtonAction() {
        loadLoginView();
    }

    private void loadLoginView() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FlowerLogin.fxml")));
            Scene scene = new Scene(root);
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading login view", e);
            showErrorAlert("Error", "Could not load login view.");
        }
    }
}

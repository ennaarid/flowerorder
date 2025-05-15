package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class profileController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(profileController.class.getName());

    @FXML
    private TextField profile_username;

    @FXML
    private TextField profile_email;

    @FXML
    private TextField profile_phone;

    @FXML
    private TextField address_field;

    @FXML
    private Button profile_saveBtn;

    // Store the current user's username
    private String currentUsername;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load current user profile data
        loadProfileData();
    }


    @FXML
    private void saveProfile() {
        // Validate input
        if (profile_username.getText().isEmpty() || profile_email.getText().isEmpty() || profile_phone.getText().isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        // Validate email format
        if (!isValidEmail(profile_email.getText())) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        // Validate phone number format
        if (!isValidPhone(profile_phone.getText())) {
            showAlert("Error", "Please enter a valid phone number.");
            return;
        }

        // Save profile data to database
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during profile save");
                showAlert("Error", "Database connection failed. Please try again later.");
                return;
            }

            // First, check if the phone and address columns exist in the Users table
            boolean phoneColumnExists = checkColumnExists(connection, "phone");
            boolean addressColumnExists = checkColumnExists(connection, "address");

            // If phone column doesn't exist, add it
            if (!phoneColumnExists) {
                addColumnToUsersTable(connection, "phone", "VARCHAR(20)");
            }

            // If address column doesn't exist, add it
            if (!addressColumnExists) {
                addColumnToUsersTable(connection, "address", "VARCHAR(255)");
            }

            // Update user data in the database
            String sql = "UPDATE Users SET username = ?, email = ?, phone = ?, address = ? WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, profile_username.getText());
            preparedStatement.setString(2, profile_email.getText());
            preparedStatement.setString(3, profile_phone.getText());
            preparedStatement.setString(4, address_field.getText());
            preparedStatement.setString(5, currentUsername);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Update the current username if it was changed
                currentUsername = profile_username.getText();
                showAlert("Success", "Profile updated successfully!");
            } else {
                showAlert("Error", "Failed to update profile. User not found.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during profile save", e);
            showAlert("Error", "Failed to save profile: " + e.getMessage());
        }
    }

    private boolean checkColumnExists(Connection connection, String columnName) throws SQLException {
        ResultSet columns = connection.getMetaData().getColumns(null, null, "Users", columnName);
        return columns.next(); // Returns true if the column exists
    }

    private void addColumnToUsersTable(Connection connection, String columnName, String columnType) throws SQLException {
        String sql = "ALTER TABLE Users ADD COLUMN " + columnName + " " + columnType;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.executeUpdate();
        LOGGER.log(Level.INFO, "Added " + columnName + " column to Users table");
    }

    private void loadProfileData() {
        // For demonstration purposes, we'll use a hardcoded username
        // In a real application, this would come from a login session
        currentUsername = "enaarid"; // This is the username from the sample data in the SQL file

        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during profile load");
                showAlert("Error", "Database connection failed. Please try again later.");
                return;
            }

            String sql = "SELECT username, email, phone, address FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, currentUsername);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                profile_username.setText(resultSet.getString("username"));
                profile_email.setText(resultSet.getString("email"));

                // Check if phone column exists and has data
                String phone = resultSet.getString("phone");
                profile_phone.setText(phone != null ? phone : "");

                // Check if address column exists and has data
                String address = resultSet.getString("address");
                address_field.setText(address != null ? address : "");
            } else {
                LOGGER.log(Level.WARNING, "No user found with username: " + currentUsername);
                showAlert("Error", "User not found in database.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during profile load", e);
            showAlert("Error", "Failed to load profile: " + e.getMessage());

            // Set default values if database load fails
            profile_username.setText(currentUsername);
            profile_email.setText("");
            profile_phone.setText("");
            address_field.setText("");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^\\d{3}-\\d{3}-\\d{4}$";
        return phone.matches(phoneRegex);
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
            String fxmlFile = null;
            String title = null;

            if (event.getSource() instanceof Button) {
                Button sourceButton = (Button) event.getSource();

                // Determine which form to load based on button ID
                if (sourceButton.getId().contains("menu")) {
                    fxmlFile = "/com/example/flowermanagementsystem/customerCatalog.fxml";
                    title = "Catalog";
                } else if (sourceButton.getId().contains("profile")) {
                    fxmlFile = "/com/example/flowermanagementsystem/profile.fxml";
                    title = "Profile";
                } else if (sourceButton.getId().contains("settings")) {
                    fxmlFile = "/com/example/flowermanagementsystem/settings.fxml";
                    title = "Settings";
                } else if (sourceButton.getId().contains("orders")) {
                    fxmlFile = "/com/example/flowermanagementsystem/orderHistory.fxml";
                    title = "Order History";
                }
            }

            if (fxmlFile != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                    Parent root = loader.load();
                    Stage stage = (Stage) profile_username.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setTitle(title);
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to load " + fxmlFile, e);
                    showAlert("Navigation Error", "Failed to load " + fxmlFile + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to the selected page", e);
            showAlert("Navigation Error", "Failed to navigate to the selected page: " + e.getMessage());
        }
    }

    @FXML
    private void openOrderHistory() {
        try {
            String fxmlFile = "/com/example/flowermanagementsystem/orderHistory.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) profile_username.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Order History");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not open order history", e);
            showAlert("Navigation Error", "Could not open order history: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get() == ButtonType.OK) {
                try {
                    String fxmlFile = "/com/example/flowermanagementsystem/FlowerLogin.fxml";
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                    Parent root = loader.load();
                    Stage stage = (Stage) profile_username.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setTitle("Flower Ordering System");
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to load login page", e);
                    showAlert("Navigation Error", "Failed to load login page: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to logout", e);
            showAlert("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }
}

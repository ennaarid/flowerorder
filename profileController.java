package com.example.final_flowerorderingsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class profileController implements Initializable {

    @FXML
    private ImageView profile_image;
    
    @FXML
    private TextField profile_username;
    
    @FXML
    private TextField profile_email;
    
    @FXML
    private TextField profile_phone;
    
    @FXML
    private Button change_image_btn;
    
    @FXML
    private Button profile_saveBtn;
    
    private String currentImagePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load current user profile data
        loadProfileData();
    }
    
    @FXML
    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(change_image_btn.getScene().getWindow());
        if (selectedFile != null) {
            currentImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            profile_image.setImage(image);
        }
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
        // TODO: Implement database save logic
        
        showAlert("Success", "Profile updated successfully!");
    }
    
    private void loadProfileData() {
        // TODO: Load user data from database
        // For now, we'll just set some placeholder data
        profile_username.setText("John Doe");
        profile_email.setText("john.doe@example.com");
        profile_phone.setText("123-456-7890");
        
        // Load default profile image
        Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_profile.png"));
        profile_image.setImage(defaultImage);
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
    private void switchForm() {
        // TODO: Implement form switching logic
    }
} 
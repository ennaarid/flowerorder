package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * Controller for the admin dashboard view.
 */
public class AdminDashboardController {

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
    private Button profile_btn;

    @FXML
    private Button settings_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private AnchorPane profile_form;

    @FXML
    private AnchorPane settings_form;

    @FXML
    private ImageView profile_image;

    @FXML
    private Button profile_changeImageBtn;

    @FXML
    private TextField profile_username;

    @FXML
    private TextField profile_email;

    @FXML
    private TextField profile_phone;

    @FXML
    private Button profile_saveBtn;

    @FXML
    private PasswordField settings_currentPassword;

    @FXML
    private PasswordField settings_newPassword;

    @FXML
    private PasswordField settings_confirmPassword;

    @FXML
    private CheckBox settings_emailNotif;

    @FXML
    private CheckBox settings_orderUpdates;

    @FXML
    private CheckBox settings_promotions;

    @FXML
    private Button settings_saveBtn;

    /**
     * Initializes the controller.
     */
    public void initialize() {
        // Initialization code here
    }

    /**
     * Handles switching between different forms/views in the dashboard.
     * 
     * @param event The action event triggered by the button click
     */
    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
            profile_form.setVisible(false);
            settings_form.setVisible(false);
        } else if (event.getSource() == inventory_btn) {
            dashboard_form.setVisible(false);
            profile_form.setVisible(false);
            settings_form.setVisible(false);
            // Show inventory form if it exists
        } else if (event.getSource() == manageorders_btn) {
            dashboard_form.setVisible(false);
            profile_form.setVisible(false);
            settings_form.setVisible(false);
            // Show orders form if it exists
        } else if (event.getSource() == menu_btn) {
            dashboard_form.setVisible(false);
            profile_form.setVisible(false);
            settings_form.setVisible(false);
            // Show menu form if it exists
        } else if (event.getSource() == bouquets_btn) {
            dashboard_form.setVisible(false);
            profile_form.setVisible(false);
            settings_form.setVisible(false);
            // Show bouquets form if it exists
        } else if (event.getSource() == profile_btn) {
            dashboard_form.setVisible(false);
            profile_form.setVisible(true);
            settings_form.setVisible(false);
        } else if (event.getSource() == settings_btn) {
            dashboard_form.setVisible(false);
            profile_form.setVisible(false);
            settings_form.setVisible(true);
        }
    }

    /**
     * Handles the profile image change button click.
     */
    @FXML
    public void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Logic to set the profile image
            // This would typically involve loading the image and setting it to profile_image
        }
    }

    /**
     * Handles the save profile button click.
     */
    @FXML
    public void saveProfile() {
        // Logic to save profile information
        String username = profile_username.getText();
        String email = profile_email.getText();
        String phone = profile_phone.getText();

        // Validation and saving logic would go here
    }

    /**
     * Handles the save settings button click.
     */
    @FXML
    public void saveSettings() {
        // Logic to save settings
        String currentPassword = settings_currentPassword.getText();
        String newPassword = settings_newPassword.getText();
        String confirmPassword = settings_confirmPassword.getText();

        boolean emailNotifications = settings_emailNotif.isSelected();
        boolean orderUpdates = settings_orderUpdates.isSelected();
        boolean promotions = settings_promotions.isSelected();

        // Validation and saving logic would go here
    }


    @FXML
    public void handleLogout() {
    }
}

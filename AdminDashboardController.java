package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;

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
    private Button logout_btn;

    @FXML
    private AnchorPane dashboard_form;

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
            // Hide other forms if they exist
        } else if (event.getSource() == inventory_btn) {
            dashboard_form.setVisible(false);
            // Show inventory form if it exists
        } else if (event.getSource() == manageorders_btn) {
            dashboard_form.setVisible(false);
            // Show orders form if it exists
        } else if (event.getSource() == menu_btn) {
            dashboard_form.setVisible(false);
            // Show menu form if it exists
        } else if (event.getSource() == bouquets_btn) {
            dashboard_form.setVisible(false);
            // Show bouquets form if it exists
        }
    }

    /**
     * Handles the logout button click.
     */
    @FXML
    public void handleLogout() {
        // Logout logic here
    }
}

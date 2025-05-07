package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Level;
import java.util.logging.Logger;


public class flowerloginController extends BaseController {

    @FXML
    private AnchorPane topPane;
    @FXML
    private AnchorPane bottomPane;
    @FXML
    private ImageView imageView;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private CheckBox rememberMeCheckBox;
    @FXML
    private Hyperlink forgotPasswordLink;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink signUpLink;
    @FXML
    private SplitPane fullwindow;

    private UserService userService;

    private static final Logger LOGGER = Logger.getLogger(flowerloginController.class.getName());

    public flowerloginController() {
        this.userService = new UserServiceImpl();
    }

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User authenticatedUser = userService.authenticateUser(username, password);

            if (authenticatedUser != null) {
                showWelcomeAlert(authenticatedUser.getRole());
                loadRoleView(authenticatedUser.getRole(), event);

            } else {
                showErrorAlert("Login Failed", "Invalid username or password.");
            }
            clearInputFields();
        } catch (UserServiceImpl.AuthenticationException e) {
            showErrorAlert("Authentication Error", "Failed to authenticate: " + e.getMessage());
        }
    }

    private void showWelcomeAlert(String role) {
        showInfoAlert("Login Successful", "Welcome, " + role + "!");
    }

    @Override
    protected void clearInputFields() {
        usernameField.clear();
        passwordField.clear();
    }

    private void loadRoleView(String role, ActionEvent event) {
        try{
            switch (role) {
                case "Administrator":
                    loadView("admindash", event);
                    break;
                case "Customer":
                    loadView("mainForm", event);
                    break;
                case "user":
                    loadView("mainForm", event);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unknown role: " + role);
                    showErrorAlert("Unknown Role", "Unknown role: " + role);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading view for role: " + role, e);
            showErrorAlert("Error Loading View", "Could not load view for role: " + role);
        }
    }

    @FXML
    public void handleSignUpLink(ActionEvent event) {
        loadView("FlowerSignup", event);
    }

    @FXML
    public void handleForgotPasswordLink(ActionEvent event) {
        loadView("forgotpasswordrequest", event);
    }
}

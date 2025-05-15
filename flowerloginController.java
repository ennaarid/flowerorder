package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class flowerloginController extends BaseController implements Initializable {

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
    private static final String CREDENTIALS_FILE = "user_credentials.properties";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String REMEMBER_ME_KEY = "rememberMe";

    public flowerloginController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSavedCredentials();
    }

    private void loadSavedCredentials() {
        Properties props = new Properties();
        File file = new File(CREDENTIALS_FILE);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);

                String savedUsername = props.getProperty(USERNAME_KEY);
                String savedPassword = props.getProperty(PASSWORD_KEY);
                String rememberMe = props.getProperty(REMEMBER_ME_KEY);

                if (savedUsername != null && savedPassword != null && "true".equals(rememberMe)) {
                    usernameField.setText(savedUsername);
                    passwordField.setText(savedPassword);
                    rememberMeCheckBox.setSelected(true);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to load saved credentials", e);
            }
        }
    }

    private void saveCredentials(String username, String password, boolean rememberMe) {
        Properties props = new Properties();

        if (rememberMe) {
            props.setProperty(USERNAME_KEY, username);
            props.setProperty(PASSWORD_KEY, password);
            props.setProperty(REMEMBER_ME_KEY, "true");
        } else {
            // Clear saved credentials if "Remember Me" is unchecked
            props.remove(USERNAME_KEY);
            props.remove(PASSWORD_KEY);
            props.setProperty(REMEMBER_ME_KEY, "false");
        }

        try (FileOutputStream fos = new FileOutputStream(CREDENTIALS_FILE)) {
            props.store(fos, "User Credentials");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save credentials", e);
        }
    }

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean rememberMe = rememberMeCheckBox.isSelected();

        try {
            if (username.equals("admin")) {
                User authenticatedUser = userService.authenticateUser(username, password);

                if (authenticatedUser != null) {
                    if (!authenticatedUser.getRole().equals("Administrator")) {
                        userService.updateUserRole(username, "Administrator");
                    }
                    saveCredentials(username, password, rememberMe);
                    showWelcomeAlert("Administrator");
                    loadRoleView("Administrator", event);
                } else {
                    userService.registerAdminUser(username, password, "admin@example.com");
                    saveCredentials(username, password, rememberMe);
                    showWelcomeAlert("Administrator");
                    loadRoleView("Administrator", event);
                }
            } else {
                User authenticatedUser = userService.authenticateUser(username, password);

                if (authenticatedUser != null) {
                    saveCredentials(username, password, rememberMe);
                    showWelcomeAlert(authenticatedUser.getRole());
                    loadRoleView(authenticatedUser.getRole(), event);
                } else {
                    showErrorAlert("Login Failed", "Invalid username or password.");
                }
            }

            if (!rememberMe) {
                clearInputFields();
            }
        } catch (UserServiceImpl.AuthenticationException e) {
            showErrorAlert("Authentication Error", "Failed to authenticate: " + e.getMessage());
        }
    }

    private void showWelcomeAlert(String role) {
        showInfoAlert("Login Successful", "Welcome, " + role + "!");
    }

    @Override
    protected void clearInputFields() {
        if (!rememberMeCheckBox.isSelected()) {
            usernameField.clear();
            passwordField.clear();
        }
    }

    private void loadRoleView(String role, ActionEvent event) {
        try{
            switch (role) {
                case "Administrator":
                    loadView("admindash", event);
                    break;
                case "Customer":
                    loadView("customerCatalog", event);
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

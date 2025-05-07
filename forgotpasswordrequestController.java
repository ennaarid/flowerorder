package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class forgotpasswordrequestController extends BaseController {

    @FXML
    private Button nextButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleResetRequest(MouseEvent event) {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            messageLabel.setText("Please enter your username.");
            return;
        }

        String resetToken = generateResetToken();
        String userEmail = getUserEmail(username);

        if (userEmail != null) {
            saveResetToken(userEmail, resetToken);

            sendResetLink(userEmail, resetToken);
            messageLabel.setText("Password reset link sent to your email. Check your inbox.");

            usernameField.setDisable(true);
            nextButton.setDisable(true);
        } else {
            messageLabel.setText("Username not found.");
        }
    }

    // Email configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "your-email@gmail.com"; // Update with your actual email
    private static final String EMAIL_PASSWORD = "your-app-password"; // Update with your actual app password

    private Connection getConnection() throws SQLException {
        Connection connection = DatabaseConnector.connectDB();
        if (connection == null) {
            throw new SQLException("Failed to connect to database");
        }
        return connection;
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String getUserEmail(String username) {
        String email = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT email FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving email", e);
        }

        return email;
    }

    private void saveResetToken(String email, String token) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO password_reset_tokens (email, token, created_at, expires_at) VALUES (?, ?, ?, ?)")) {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiryTime = now.plus(1, ChronoUnit.HOURS);

            stmt.setString(1, email);
            stmt.setString(2, token);
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(now));
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(expiryTime));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving reset token", e);
        }
    }

    private void sendResetLink(String email, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Password Reset Request");

            // In a real application, this would be a link to your website
            // For this demo, we'll just show a message to use the token in the app
            String content = String.format(
                    "<html><body>" +
                            "<h2>Password Reset Request</h2>" +
                            "<p>Your password reset token is: <strong>%s</strong></p>" +
                            "<p>Use this token in the application to reset your password.</p>" +
                            "</body></html>", token);

            message.setContent(content, "text/html; charset=UTF-8");
            Transport.send(message);

            // For demo purposes, we'll directly open the reset password screen
            openResetPasswordScreen(email, token);

        } catch (MessagingException e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private void openResetPasswordScreen(String email, String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resetpassword.fxml"));
            Parent root = loader.load();

            resetpasswordController controller = loader.getController();
            controller.initialize(email, token);

            Stage stage = (Stage) nextButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Error opening reset password screen");
            throw new RuntimeException("Failed to open reset password screen", e);
        }
    }

    public boolean initiatePasswordReset(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        String email = getUserEmail(username);
        if (email == null) {
            return false;
        }

        String token = generateResetToken();
        saveResetToken(email, token);
        sendResetLink(email, token);

        return true;
    }

    @Override
    protected void clearInputFields() {
        usernameField.clear();
        messageLabel.setText("");
    }
}

package com.example.flowermanagementsystem;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());

    // Custom Exception for Authentication failures
    static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Custom Exception for Registration failures
    static class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Custom Exception for Password Reset failures
    static class PasswordResetException extends RuntimeException {
        public PasswordResetException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Override
    public User authenticateUser(String username, String password) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during authentication");
                throw new AuthenticationException("Database connection failed. Please try again later.", null);
            }

            String sql = "SELECT username, password, role, email FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String dbUsername = resultSet.getString("username");
                String dbHashedPassword = resultSet.getString("password");
                String dbRole = resultSet.getString("role");
                String dbEmail = resultSet.getString("email"); // Added email
                if (BCrypt.checkpw(password, dbHashedPassword)) {
                    return new User(dbUsername, dbHashedPassword, dbRole, dbEmail);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during authentication", e);
            throw new AuthenticationException("Database error occurred. Please try again later.", e);
        }
        return null;
    }

    @Override
    public void registerUser(String username, String password, String email) { // Added registerUser method
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during registration");
                throw new UserRegistrationException("Database connection failed. Please try again later.", null);
            }

            String userId = java.util.UUID.randomUUID().toString();

            String sql = "INSERT INTO Users (userId, username, password, role, email) VALUES (?, ?, ?, ?, ?)"; // Added userId and email
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            preparedStatement.setString(4, "Customer"); // Default role is Customer
            preparedStatement.setString(5, email); // Set email
            preparedStatement.executeUpdate();

            String customerSql = "INSERT INTO Customer (userId) VALUES (?)";
            PreparedStatement customerStatement = connection.prepareStatement(customerSql);
            customerStatement.setString(1, userId);
            customerStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during registration", e);
            throw new UserRegistrationException("Failed to register user.", e);
        }
    }

    @Override
    public boolean resetPassword(String email, String token, String newPassword) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during password reset");
                throw new PasswordResetException("Database connection failed. Please try again later.", null);
            }

            if (!verifyResetToken(token)) {
                return false;
            }

            String getUsernameSql = "SELECT username FROM Users WHERE email = ?";
            PreparedStatement getUsernameStmt = connection.prepareStatement(getUsernameSql);
            getUsernameStmt.setString(1, email);
            ResultSet usernameResult = getUsernameStmt.executeQuery();

            if (!usernameResult.next()) {
                LOGGER.log(Level.WARNING, "No user found with email: " + email);
                return false;
            }

            String username = usernameResult.getString("username");

            String updatePasswordSql = "UPDATE Users SET password = ? WHERE username = ?";
            PreparedStatement updatePasswordStmt = connection.prepareStatement(updatePasswordSql);
            updatePasswordStmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            updatePasswordStmt.setString(2, username);
            int rowsAffected = updatePasswordStmt.executeUpdate();

            String deleteTokenSql = "DELETE FROM password_reset_tokens WHERE token = ?";
            PreparedStatement deleteTokenStmt = connection.prepareStatement(deleteTokenSql);
            deleteTokenStmt.setString(1, token);
            deleteTokenStmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during password reset", e);
            throw new PasswordResetException("Failed to reset password.", e);
        }
    }

    @Override
    public boolean verifyResetToken(String token) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during token verification");
                throw new PasswordResetException("Database connection failed. Please try again later.", null);
            }

            String sql = "SELECT * FROM password_reset_tokens WHERE token = ? AND expires_at > NOW()";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Token is valid if we found a row and it hasn't expired
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during token verification", e);
            throw new PasswordResetException("Failed to verify reset token.", e);
        }
    }
}

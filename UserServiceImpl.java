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

    static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }


    static class UserRegistrationException extends RuntimeException {
        public UserRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }


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
                String dbEmail = resultSet.getString("email");
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
    public void registerUser(String username, String password, String email) { 
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
            preparedStatement.setString(4, "Customer"); 
            preparedStatement.setString(5, email); 
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
    public void registerAdminUser(String username, String password, String email) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during admin registration");
                throw new UserRegistrationException("Database connection failed. Please try again later.", null);
            }

            String userId = java.util.UUID.randomUUID().toString();

            String sql = "INSERT INTO Users (userId, username, password, role, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            preparedStatement.setString(4, "Administrator");
            preparedStatement.setString(5, email);
            preparedStatement.executeUpdate();

            String adminSql = "INSERT INTO Administrator (userId) VALUES (?)";
            PreparedStatement adminStatement = connection.prepareStatement(adminSql);
            adminStatement.setString(1, userId);
            adminStatement.executeUpdate();

            LOGGER.log(Level.INFO, "Admin user registered successfully: " + username);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during admin registration", e);
            throw new UserRegistrationException("Failed to register admin user.", e);
        }
    }

    @Override
    public boolean updateUserRole(String username, String newRole) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during role update");
                throw new UserRegistrationException("Database connection failed. Please try again later.", null);
            }

        
            String getUserIdSql = "SELECT userId FROM Users WHERE username = ?";
            PreparedStatement getUserIdStmt = connection.prepareStatement(getUserIdSql);
            getUserIdStmt.setString(1, username);
            ResultSet userIdResult = getUserIdStmt.executeQuery();

            if (!userIdResult.next()) {
                LOGGER.log(Level.WARNING, "No user found with username: " + username);
                return false;
            }

            String userId = userIdResult.getString("userId");

           
            String updateRoleSql = "UPDATE Users SET role = ? WHERE username = ?";
            PreparedStatement updateRoleStmt = connection.prepareStatement(updateRoleSql);
            updateRoleStmt.setString(1, newRole);
            updateRoleStmt.setString(2, username);
            int rowsAffected = updateRoleStmt.executeUpdate();

            if (rowsAffected == 0) {
                return false;
            }

            if (newRole.equals("Administrator")) {
               
                String checkAdminSql = "SELECT userId FROM Administrator WHERE userId = ?";
                PreparedStatement checkAdminStmt = connection.prepareStatement(checkAdminSql);
                checkAdminStmt.setString(1, userId);
                ResultSet adminResult = checkAdminStmt.executeQuery();

                if (!adminResult.next()) {
                
                    String insertAdminSql = "INSERT INTO Administrator (userId) VALUES (?)";
                    PreparedStatement insertAdminStmt = connection.prepareStatement(insertAdminSql);
                    insertAdminStmt.setString(1, userId);
                    insertAdminStmt.executeUpdate();
                }

             
                String deleteCustomerSql = "DELETE FROM Customer WHERE userId = ?";
                PreparedStatement deleteCustomerStmt = connection.prepareStatement(deleteCustomerSql);
                deleteCustomerStmt.setString(1, userId);
                deleteCustomerStmt.executeUpdate();
            }
            else if (newRole.equals("Customer")) {
                String checkCustomerSql = "SELECT userId FROM Customer WHERE userId = ?";
                PreparedStatement checkCustomerStmt = connection.prepareStatement(checkCustomerSql);
                checkCustomerStmt.setString(1, userId);
                ResultSet customerResult = checkCustomerStmt.executeQuery();

                if (!customerResult.next()) {
                    String insertCustomerSql = "INSERT INTO Customer (userId) VALUES (?)";
                    PreparedStatement insertCustomerStmt = connection.prepareStatement(insertCustomerSql);
                    insertCustomerStmt.setString(1, userId);
                    insertCustomerStmt.executeUpdate();
                }
                String deleteAdminSql = "DELETE FROM Administrator WHERE userId = ?";
                PreparedStatement deleteAdminStmt = connection.prepareStatement(deleteAdminSql);
                deleteAdminStmt.setString(1, userId);
                deleteAdminStmt.executeUpdate();
            }

            LOGGER.log(Level.INFO, "User role updated successfully for: " + username + " to " + newRole);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during role update", e);
            throw new UserRegistrationException("Failed to update user role.", e);
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

            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during token verification", e);
            throw new PasswordResetException("Failed to verify reset token.", e);
        }
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during password change");
                throw new PasswordResetException("Database connection failed. Please try again later.", null);
            }

            String sql = "SELECT password FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String dbHashedPassword = resultSet.getString("password");
                if (BCrypt.checkpw(currentPassword, dbHashedPassword)) {
                  
                    String updateSql = "UPDATE Users SET password = ? WHERE username = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                    updateStmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                    updateStmt.setString(2, username);
                    int rowsAffected = updateStmt.executeUpdate();

                    return rowsAffected > 0;
                } else {
                    LOGGER.log(Level.WARNING, "Incorrect current password for user: " + username);
                    return false;
                }
            } else {
                LOGGER.log(Level.WARNING, "No user found with username: " + username);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during password change", e);
            throw new PasswordResetException("Failed to change password.", e);
        }
    }

    public boolean saveUserPreferences(String username, boolean emailNotifications, boolean smsNotifications, String theme) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during preference save");
                return false;
            }
            String checkSql = "SELECT * FROM UserPreferences WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet resultSet = checkStmt.executeQuery();

            PreparedStatement stmt;
            if (resultSet.next()) {
                String updateSql = "UPDATE UserPreferences SET email_notifications = ?, sms_notifications = ?, theme = ? WHERE username = ?";
                stmt = connection.prepareStatement(updateSql);
                stmt.setBoolean(1, emailNotifications);
                stmt.setBoolean(2, smsNotifications);
                stmt.setString(3, theme);
                stmt.setString(4, username);
            } else {
                String insertSql = "INSERT INTO UserPreferences (username, email_notifications, sms_notifications, theme) VALUES (?, ?, ?, ?)";
                stmt = connection.prepareStatement(insertSql);
                stmt.setString(1, username);
                stmt.setBoolean(2, emailNotifications);
                stmt.setBoolean(3, smsNotifications);
                stmt.setString(4, theme);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during preference save", e);
            return false;
        }
    }

    public Object[] loadUserPreferences(String username) {
        try (Connection connection = DatabaseConnector.connectDB()) {
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to database during preference load");
                return null;
            }

            String sql = "SELECT email_notifications, sms_notifications, theme FROM UserPreferences WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean emailNotifications = rs.getBoolean("email_notifications");
                boolean smsNotifications = rs.getBoolean("sms_notifications");
                String theme = rs.getString("theme");

                return new Object[]{emailNotifications, smsNotifications, theme};
            } else {
                return new Object[]{true, false, "Light"};
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during preference load", e);
            return null;
        }
    }
}

package com.example.flowermanagementsystem;

public interface UserService {
    User authenticateUser(String username, String password);

    void registerUser(String username, String password, String email);

    void registerAdminUser(String username, String password, String email);

    boolean updateUserRole(String username, String newRole);

    boolean resetPassword(String email, String token, String newPassword);

    boolean verifyResetToken(String token);

    boolean changePassword(String username, String currentPassword, String newPassword);

    boolean saveUserPreferences(String username, boolean emailNotifications, boolean smsNotifications, String theme);

    Object[] loadUserPreferences(String username);
}

package com.example.flowermanagementsystem;

public interface UserService {
    User authenticateUser(String username, String password);

    void registerUser(String username, String password, String email);

    boolean resetPassword(String email, String token, String newPassword);

    boolean verifyResetToken(String token);
}

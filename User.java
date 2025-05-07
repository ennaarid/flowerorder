package com.example.flowermanagementsystem;

public class User {
    private final String username;
    private final String hashedPassword;
    private final String role;
    private final String email;

    public User(String username, String hashedPassword, String role, String email) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public boolean checkPassword(String inputPassword) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(inputPassword, this.hashedPassword);
    }
}

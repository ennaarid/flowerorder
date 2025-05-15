package com.example.flowermanagementsystem;

public class SessionManager {
    private static String currentUsername;
    private static User currentUser;

    public static void setCurrentUser(User user) {
        if (user != null) {
            currentUser = user;
            currentUsername = user.getUsername();
        }
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
        currentUsername = null;
    }
}
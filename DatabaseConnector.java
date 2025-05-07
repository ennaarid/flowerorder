package com.example.flowermanagementsystem;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class DatabaseConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/flowerManagement_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "asdf";

    public static Connection connectDB() {
        Connection connection = null;

        String connectionString = DB_URL +
                "?useSSL=false&" +
                "allowPublicKeyRetrieval=true&" +
                "autoReconnect=true&" +
                "failOverReadOnly=false&" +
                "maxReconnects=10";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties properties = new Properties();
            properties.setProperty("user", DB_USER);
            properties.setProperty("password", DB_PASSWORD);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");

            connection = DriverManager.getConnection(connectionString, properties);
            System.out.println("Successfully connected to the database!");

        } catch (ClassNotFoundException e) {
            System.err.println("Error: JDBC Driver not found!");
            System.err.println("Driver Error: " + e.getMessage());
            e.printStackTrace();

        } catch (SQLException e) {
            System.err.println("Error: Could not connect to the database.");
            System.err.println("URL: " + DB_URL);
            System.err.println("User: " + DB_USER);
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Details: " + e.getMessage());
            e.printStackTrace();
            connection = null;
        }

        return connection;
    }
}

package com.example.flowermanagementsystem;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class DatabaseConnector {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3307/flowermanagement_db";
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
            
            initializePasswordResetTokensTable(connection);

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

    private static void initializePasswordResetTokensTable(Connection connection) {
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Cannot initialize password_reset_tokens table: No database connection");
            return;
        }

        String createTableSQL = "CREATE TABLE IF NOT EXISTS password_reset_tokens (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(255) NOT NULL, " +
                "token VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "expires_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + 
                "INDEX token_idx (token), " +
                "INDEX email_idx (email) " +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            LOGGER.log(Level.INFO, "Password reset tokens table initialized successfully");
            
            boolean tableExists = checkTableExists(connection, "password_reset_tokens");
            if (tableExists) {
                LOGGER.log(Level.INFO, "Verified password_reset_tokens table exists");
                System.out.println("Verified password_reset_tokens table exists");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to verify password_reset_tokens table exists after creation");
                System.err.println("Failed to verify password_reset_tokens table exists after creation");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize password_reset_tokens table", e);
            System.err.println("SQL Error during table creation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static boolean checkTableExists(Connection connection, String tableName) throws SQLException {
      
        String tableNameLower = tableName.toLowerCase();

        ResultSet resultSet = connection.getMetaData().getTables(
                null, null, null, new String[] {"TABLE"});

        boolean exists = false;
        while (resultSet.next()) {
            String foundTable = resultSet.getString("TABLE_NAME");
            if (foundTable.toLowerCase().equals(tableNameLower)) {
                exists = true;
                break;
            }
        }
        resultSet.close();

        if (!exists) {
            
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
                exists = rs.next();
            }
        }

        return exists;
    }
}

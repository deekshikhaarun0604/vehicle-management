package com.vehicle.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC Connection Manager for MySQL.
 * Handles database connectivity, driver registration, and connectivity state.
 */
public class DBConnection {
    private static Connection connection = null;
    private static boolean mySqlAvailable = false;
    private static boolean initialized = false;

    private static synchronized void initialize() {
        if (initialized) return;
        initialized = true;

        String url = AppConfig.get("db.url");
        String user = AppConfig.get("db.user");
        String password = AppConfig.get("db.password");

        try {
            // Register MySQL JDBC Driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    System.out.println("[INFO] MySQL JDBC Driver class not found in classpath. Operating in local in-memory storage mode.");
                    mySqlAvailable = false;
                    return;
                }
            }

            // Attempt connection
            DriverManager.setLoginTimeout(3);
            connection = DriverManager.getConnection(url, user, password);
            mySqlAvailable = true;
            System.out.println("[SUCCESS] Successfully connected to MySQL database: " + AppConfig.get("db.name"));
        } catch (SQLException e) {
            System.out.println("[INFO] MySQL server is not reachable on localhost:3306 (" + e.getMessage() + ").");
            System.out.println("[INFO] System will operate in local repository mode with sample database records pre-seeded.");
            mySqlAvailable = false;
        }
    }

    public static synchronized Connection getConnection() {
        if (!initialized) {
            initialize();
        }
        if (mySqlAvailable) {
            try {
                if (connection == null || connection.isClosed()) {
                    String url = AppConfig.get("db.url");
                    String user = AppConfig.get("db.user");
                    String password = AppConfig.get("db.password");
                    connection = DriverManager.getConnection(url, user, password);
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to re-establish MySQL connection: " + e.getMessage());
                mySqlAvailable = false;
            }
        }
        return connection;
    }

    public static boolean isMySqlAvailable() {
        if (!initialized) {
            initialize();
        }
        return mySqlAvailable;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[INFO] MySQL connection closed.");
            } catch (SQLException e) {
                System.err.println("[ERROR] Error closing MySQL connection: " + e.getMessage());
            } finally {
                connection = null;
                initialized = false;
            }
        }
    }
}

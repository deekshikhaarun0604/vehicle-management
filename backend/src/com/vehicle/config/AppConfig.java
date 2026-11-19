package com.vehicle.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Global application configuration loader.
 */
public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        // Load default values
        properties.setProperty("db.host", "localhost");
        properties.setProperty("db.port", "3306");
        properties.setProperty("db.name", "vehicle_service_db");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/vehicle_service_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        properties.setProperty("server.port", "8080");

        // Attempt to load from file
        File propFile = new File("backend/resources/db.properties");
        if (!propFile.exists()) {
            propFile = new File("resources/db.properties");
        }
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                properties.load(in);
            } catch (Exception e) {
                System.err.println("Notice: Could not read db.properties, using default configs: " + e.getMessage());
            }
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
